package me.senseiwells.essentialclient.utils.clientscript;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import kotlin.Pair;
import me.senseiwells.arucas.builtin.*;
import me.senseiwells.arucas.classes.ClassDefinition;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.EnumDefinition;
import me.senseiwells.arucas.classes.PrimitiveDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.exceptions.RuntimeError;
import me.senseiwells.arucas.utils.ArucasFunction;
import me.senseiwells.arucas.utils.impl.ArucasCollection;
import me.senseiwells.arucas.utils.impl.ArucasList;
import me.senseiwells.arucas.utils.impl.ArucasMap;
import me.senseiwells.essentialclient.clientscript.definitions.EntityDef;
import me.senseiwells.essentialclient.clientscript.definitions.ItemStackDef;
import me.senseiwells.essentialclient.clientscript.definitions.TextDef;
import me.senseiwells.essentialclient.commands.CommandRegister;
import me.senseiwells.essentialclient.mixins.clientScript.KeyBindingAccessor;
import me.senseiwells.essentialclient.rule.client.*;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.command.*;
import me.senseiwells.essentialclient.utils.misc.Events;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

//#if MC >= 11903
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
//#elseif MC >= 11901
//$$import net.minecraft.command.CommandRegistryWrapper;
//#endif

//#if MC < 11903
//$$import net.minecraft.util.registry.Registry;
//#endif

import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.*;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class ClientScriptUtils {
	private static final Set<String> WARNED = ConcurrentHashMap.newKeySet();
	private static final Map<String, TickedKey> HELD_KEYS = new ConcurrentHashMap<>();

	static {
		Events.ON_TICK_POST.register(client -> {
			for (TickedKey key : HELD_KEYS.values()) {
				key.tick();
			}
		});
	}

	public static void load() { }

	public static void warnMainThread(String name, Interpreter interpreter) {
		MinecraftClient client = EssentialUtils.getClient();
		if (!client.isOnThread() && !WARNED.contains(name)) {
			WARNED.add(name);
			interpreter.logDebug(
				"'%s' was not called on the Minecraft main thread, this may lead to unexpected behavior".formatted(name)
			);
		}
	}

	public static Future<Void> ensureMainThread(String name, Interpreter interpreter, Runnable runnable) {
		return ensureMainThread(name, interpreter, () -> {
			runnable.run();
			return null;
		});
	}

	public static <V> Future<V> ensureMainThread(String name, Interpreter interpreter, Supplier<V> callable) {
		MinecraftClient client = EssentialUtils.getClient();
		if (client.isOnThread()) {
			return CompletableFuture.completedFuture(callable.get());
		}
		if (!WARNED.contains(name)) {
			WARNED.add(name);
			interpreter.logDebug(
				"'%s' was not called on the Minecraft main thread, this may lead to unexpected behavior".formatted(name)
			);
		}
		return client.submit(() -> wrapSafe(callable, interpreter));
	}

	public static <V> V wrapSafe(Supplier<V> callable, Interpreter interpreter) {
		try {
			return callable.get();
		} catch (Exception e) {
			interpreter.getThreadHandler().handleError(e);
			return null;
		}
	}

	public static void wrapSafe(Runnable runnable, Interpreter interpreter) {
		try {
			runnable.run();
		} catch (Exception e) {
			interpreter.getThreadHandler().handleError(e);
		}
	}

	public static void holdKey(Interpreter interpreter, KeyBinding key, int interval) {
		if (interpreter.getThreadHandler().getRunning()) {
			HELD_KEYS.put(key.getTranslationKey(), new TickedKey(key, interval));
			interpreter.getThreadHandler().addShutdownEvent(() -> releaseKey(key));
		}
	}

	public static void releaseKey(KeyBinding key) {
		HELD_KEYS.remove(key.getTranslationKey());
		key.setPressed(false);
	}

	public static void modifyKey(Interpreter interpreter, boolean held, KeyBinding key) {
		if (held) {
			holdKey(interpreter, key, 0);
		} else {
			releaseKey(key);
		}
	}

	/**
	 * Default direction can be null, in which case a RuntimeError is thrown.
	 */
	public static Direction stringToDirection(String string, Direction defaultDirection) {
		return switch (string.toLowerCase()) {
			case "north", "n" -> Direction.NORTH;
			case "east", "e" -> Direction.EAST;
			case "south", "s" -> Direction.SOUTH;
			case "west", "w" -> Direction.WEST;
			case "up", "u" -> Direction.UP;
			case "down", "d" -> Direction.DOWN;
			default -> {
				if (defaultDirection != null) {
					yield defaultDirection;
				}
				throw new RuntimeError("Invalid direction '%s'".formatted(string));
			}
		};
	}

	public static Formatting stringToFormatting(String string) {
		Formatting formatting = Formatting.byName(string);
		if (formatting == null) {
			throw new RuntimeError("Invalid formatting: %s".formatted(string));
		}
		return formatting;
	}

	public static ClickEvent stringToClickEvent(Interpreter interpreter, String string, ClassInstance object) {
		return switch (string.toLowerCase()) {
			case "function", "run_function" -> {
				ArucasFunction function = object.getPrimitive(FunctionDef.class);
				if (function == null) {
					throw new RuntimeError("Invalid function value: %s".formatted(object.toString(interpreter)));
				}
				yield new FunctionClickEvent(interpreter, function);
			}
			default -> {
				ClickEvent.Action action = ClickEvent.Action.byName(string.toLowerCase());
				if (action == null) {
					throw new RuntimeError("Invalid click event action: %s".formatted(string));
				}
				String eventString = object.getPrimitive(StringDef.class);
				if (eventString == null) {
					throw new RuntimeError("Invalid event value: %s".formatted(object.toString(interpreter)));
				}
				yield new ClickEvent(action, string);
			}
		};
	}

	public static HoverEvent stringToHoverEvent(String string, ClassInstance object) {
		return switch (string.toLowerCase()) {
			case "show_text", "text" -> {
				MutableText text = object.getPrimitive(TextDef.class);
				if (text == null) {
					throw new RuntimeError("Expected 'Text' for 'show_text' hover event");
				}
				yield new HoverEvent(HoverEvent.Action.SHOW_TEXT, text);
			}
			case "show_item", "item" -> {
				ScriptItemStack stack = object.getPrimitive(ItemStackDef.class);
				if (stack == null) {
					throw new RuntimeError("Expected 'ItemStack' for 'show_item' hover event");
				}
				yield new HoverEvent(HoverEvent.Action.SHOW_ITEM, new HoverEvent.ItemStackContent(stack.stack));
			}
			case "show_entity", "entity" -> {
				Entity entity = object.getPrimitive(EntityDef.class);
				if (entity == null) {
					throw new RuntimeError("Expected 'Entity' for 'show_entity' hover event");
				}
				yield new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(entity.getType(), entity.getUuid(), entity.getName()));
			}
			default -> throw new RuntimeError("Invalid action '%s'".formatted(string));
		};
	}

	public static Identifier stringToIdentifier(String string) {
		try {
			return new Identifier(string);
		} catch (InvalidIdentifierException e) {
			throw new RuntimeError("Invalid id '%s'".formatted(string), e);
		}
	}

	public static RaycastContext.FluidHandling stringToFluidType(String string) {
		return switch (string.toLowerCase()) {
			case "none" -> RaycastContext.FluidHandling.NONE;
			case "source", "sources", "sources_only" -> RaycastContext.FluidHandling.SOURCE_ONLY;
			case "all", "any" -> RaycastContext.FluidHandling.ANY;
			default -> throw new RuntimeError("'%s' is not a valid fluid type".formatted(string));
		};
	}

	public static Hand stringToHand(String string) {
		return switch (string.toLowerCase()) {
			case "main", "main_hand" -> Hand.MAIN_HAND;
			case "off", "off_hand" -> Hand.OFF_HAND;
			default -> throw new RuntimeError("'%s' is not a valid hand".formatted(string));
		};
	}

	public static SlotActionType stringToSlotActionType(String string) {
		return switch (string.toLowerCase()) {
			case "click", "pickup" -> SlotActionType.PICKUP;
			case "shift_click", "quick_move" -> SlotActionType.QUICK_MOVE;
			case "swap" -> SlotActionType.SWAP;
			case "middle_click", "clone" -> SlotActionType.CLONE;
			case "throw" -> SlotActionType.THROW;
			case "drag", "quick_craft" -> SlotActionType.QUICK_CRAFT;
			case "double_click", "pickup_all" -> SlotActionType.PICKUP_ALL;
			default -> throw new RuntimeError("Invalid slotActionType, see Wiki");
		};
	}

	public static NbtElement stringToNbt(String string) {
		try {
			return new StringNbtReader(new StringReader(string)).parseElement();
		} catch (CommandSyntaxException cse) {
			throw new RuntimeError("'%s' couldn't be parsed".formatted(string));
		}
	}

	public static ItemStack stringToItemStack(String string) {
		try {
			//#if MC >= 11903
			RegistryWrapper<Item> wrapper = CommandRegister.getRegistryAccess().createWrapper(RegistryKeys.ITEM);
			//#elseif MC >= 11901
			//$$CommandRegistryWrapper<Item> wrapper = CommandRegistryWrapper.of(Registry.ITEM);
			//#endif

			//#if MC >= 11901
			ItemStringReader.ItemResult reader = ItemStringReader.item(wrapper, new StringReader(string));
			ItemStack itemStack = new ItemStack(reader.item());
			itemStack.setNbt(reader.nbt());
			return itemStack;
			//#else
			//$$ItemStringReader reader = new ItemStringReader(new StringReader(string), false).consume();
			//$$ItemStack itemStack = new ItemStack(reader.getItem());
			//$$itemStack.setNbt(reader.getNbt());
			//$$return itemStack;
			//#endif
		} catch (CommandSyntaxException cse) {
			NbtElement element = stringToNbt(string);
			if (element instanceof NbtCompound nbtCompound) {
				return ItemStack.fromNbt(nbtCompound);
			}
			throw new RuntimeError("'%s' couldn't be parsed".formatted(string));
		}
	}

	public static Text instanceToText(ClassInstance instance, Interpreter interpreter) {
		Text text = instance.getPrimitive(TextDef.class);
		return text != null ? text : Texts.literal(instance.toString(interpreter));
	}

	public static ArucasMap nbtToMap(Interpreter interpreter, NbtCompound compound, int depth) {
		ArucasMap nbtMap = new ArucasMap();
		depth--;
		if (compound == null || depth < 0) {
			return nbtMap;
		}
		for (String tagName : compound.getKeys()) {
			NbtElement element = compound.get(tagName);
			if (element == null) {
				continue;
			}
			nbtMap.put(interpreter, interpreter.create(StringDef.class, tagName), nbtToValue(interpreter, element, depth));
		}
		return nbtMap;
	}

	public static ArucasList nbtToList(Interpreter interpreter, AbstractNbtList<?> list, int depth) {
		ArucasList nbtList = new ArucasList();
		depth--;
		if (list == null || depth < 0) {
			return nbtList;
		}
		for (NbtElement element : list) {
			nbtList.add(nbtToValue(interpreter, element, depth));
		}
		return nbtList;
	}

	public static ClassInstance nbtToValue(Interpreter interpreter, NbtElement element, int depth) {
		if (element instanceof NbtCompound inCompound) {
			return interpreter.create(MapDef.class, nbtToMap(interpreter, inCompound, depth));
		}
		if (element instanceof AbstractNbtList<?> nbtList) {
			return interpreter.create(ListDef.class, nbtToList(interpreter, nbtList, depth));
		}
		if (element instanceof AbstractNbtNumber nbtNumber) {
			return interpreter.create(NumberDef.class, nbtNumber.doubleValue());
		}
		if (element == NbtEnd.INSTANCE) {
			return interpreter.getNull();
		}
		return interpreter.create(StringDef.class, element.asString());
	}

	public static NbtCompound mapToNbt(Interpreter interpreter, ArucasMap map, int depth) {
		NbtCompound compound = new NbtCompound();
		if (map == null || depth < 0) {
			return compound;
		}
		for (Pair<ClassInstance, ClassInstance> values : map.pairSet()) {
			compound.put(values.component1().toString(interpreter), valueToNbt(interpreter, values.component2(), depth));
		}
		return compound;
	}

	public static NbtList collectionToNbt(Interpreter interpreter, Collection<ClassInstance> collection, int depth) {
		NbtList list = new NbtList();
		if (collection == null || depth < 0) {
			return list;
		}
		for (ClassInstance instance : collection) {
			NbtElement element = valueToNbt(interpreter, instance, depth);
			// Doing it like this avoids the throwing of an error
			list.addElement(list.size(), element);
		}
		return list;
	}

	public static NbtElement valueToNbt(Interpreter interpreter, ClassInstance value, int depth) {
		Object primitive = value.getPrimitive(MapDef.class);
		if (primitive != null) {
			return mapToNbt(interpreter, (ArucasMap) primitive, depth);
		}
		if ((primitive = value.getPrimitive(CollectionDef.class)) != null) {
			return collectionToNbt(interpreter, ((ArucasCollection) primitive).asCollection(), depth);
		}
		if ((primitive = value.getPrimitive(NumberDef.class)) != null) {
			return NbtDouble.of((double) primitive);
		}
		if (value == interpreter.getNull()) {
			return NbtEnd.INSTANCE;
		}
		return NbtString.of(value.toString(interpreter));
	}

	public static ClassInstance mapToRule(ArucasMap map, Interpreter interpreter) {
		String type = getFieldInMap(map, interpreter, "type", StringDef.class);
		if (type == null) {
			throw new RuntimeError("Config map must contain 'type' that is a string");
		}
		String name = getFieldInMap(map, interpreter, "name", StringDef.class);
		if (name == null) {
			throw new RuntimeError("Config map must contain 'name' that is a string");
		}

		String description = getFieldInMap(map, interpreter, "description", StringDef.class);
		String optionalInfo = getFieldInMap(map, interpreter, "optional_info", StringDef.class);
		String currentValue = getFieldInMap(map, interpreter, "value", StringDef.class);
		String category = getFieldInMap(map, interpreter, "category", StringDef.class);
		ArucasFunction function = getFieldInMap(map, interpreter, "listener", FunctionDef.class);
		int maxLength = Objects.requireNonNullElse(getFieldInMap(map, interpreter, "max_length", NumberDef.class), 32).intValue();

		ClassInstance defaultValue = map.get(interpreter, interpreter.create(StringDef.class, "default_value"));
		ClientRule<?> clientRule = switch (type.toLowerCase()) {
			case "boolean" -> {
				Boolean bool = defaultValue == null ? null : defaultValue.getPrimitive(BooleanDef.class);
				if (bool != null) {
					yield new BooleanClientRule(name, description, bool, category);
				}
				yield new BooleanClientRule(name, description, category);
			}
			case "cycle" -> {
				ArucasList list = getFieldInMap(map, interpreter, "cycle_values", ListDef.class);
				if (list == null) {
					throw new RuntimeError("'cycle' type must have 'cycle_values' as a list");
				}

				List<String> cycles = new ArrayList<>();
				for (ClassInstance classInstance : list) {
					cycles.add(classInstance.toString(interpreter));
				}

				String defaultCycle = defaultValue == null ? null : defaultValue.getPrimitive(StringDef.class);
				if (defaultCycle != null) {
					yield new CycleClientRule(name, description, cycles, defaultCycle, category, null);
				}
				yield new CycleClientRule(name, description, cycles, category);
			}
			case "double" -> {
				Double defaultNumber = defaultValue == null ? null : defaultValue.getPrimitive(NumberDef.class);
				if (defaultNumber != null) {
					yield new DoubleClientRule(name, description, defaultNumber, category);
				}
				yield new DoubleClientRule(name, description, 0.0D, category);
			}
			case "double_slider" -> {
				Double min = getFieldInMap(map, interpreter, "min", NumberDef.class);
				if (min == null) {
					throw new RuntimeError("'double_slider' type must have 'min' as a number");
				}
				Double max = getFieldInMap(map, interpreter, "max", NumberDef.class);
				if (max == null) {
					throw new RuntimeError("'double_slider' type must have 'max' as a number");
				}

				Double defaultNumber = defaultValue == null ? null : defaultValue.getPrimitive(NumberDef.class);
				if (defaultNumber != null) {
					yield new DoubleSliderClientRule(name, description, defaultNumber, category, min, max);
				}
				yield new DoubleSliderClientRule(name, description, 0.0D, category, min, max);
			}
			case "integer" -> {
				Double defaultNumber = defaultValue == null ? null : defaultValue.getPrimitive(NumberDef.class);
				if (defaultNumber != null) {
					yield new IntegerClientRule(name, description, defaultNumber.intValue(), category);
				}
				yield new IntegerClientRule(name, description, 0, category);
			}
			case "integer_slider" -> {
				Double min = getFieldInMap(map, interpreter, "min", NumberDef.class);
				if (min == null) {
					throw new RuntimeError("'integer_slider' type must have 'min' as a number");
				}
				Double max = getFieldInMap(map, interpreter, "max", NumberDef.class);
				if (max == null) {
					throw new RuntimeError("'integer_slider' type must have 'max' as a number");
				}

				Integer defaultNumber = defaultValue == null ? null : defaultValue.getPrimitive(NumberDef.class).intValue();
				if (defaultNumber != null) {
					yield new IntegerSliderClientRule(name, description, defaultNumber, category, min.intValue(), max.intValue());
				}
				yield new IntegerSliderClientRule(name, description, 0, category, min.intValue(), max.intValue());
			}
			case "list" -> {
				List<String> configData = new ArrayList<>();
				ArucasList defaultList = defaultValue == null ? null : defaultValue.getPrimitive(ListDef.class);
				if (defaultList != null) {
					for (ClassInstance listValue : defaultList) {
						configData.add(listValue.toString(interpreter));
					}
				}
				ListClientRule listClientRule = new ListClientRule(name, description, configData, category);
				listClientRule.setMaxLength(maxLength);
				yield listClientRule;
			}
			case "string" -> {
				StringClientRule stringClientRule = new StringClientRule(name, description, defaultValue == null ? "" : defaultValue.toString(interpreter), category);
				stringClientRule.setMaxLength(maxLength);
				yield stringClientRule;
			}
			default -> throw new RuntimeError("Invalid config type '%s'".formatted(type));
		};

		ClassInstance instance = interpreter.convertValue(clientRule);
		if (function != null) {
			Interpreter parent = interpreter.branch();
			clientRule.addListener(object -> {
				parent.getThreadHandler().runAsync(() -> {
					function.invoke(parent.branch(), List.of(instance));
					return null;
				});
			});
		}
		if (currentValue != null) {
			clientRule.setValueFromString(currentValue);
		}

		clientRule.setOptionalInfo(optionalInfo);
		return instance;
	}

	public static ArgumentBuilder<ServerCommandSource, ?> mapToCommand(ArucasMap arucasMap, Interpreter interpreter) {
		return new CommandParser(arucasMap, interpreter).parse();
	}

	public static RequiredArgumentBuilder<ServerCommandSource, ?> parseArgument(String argumentName, String typeName, Collection<String> suggests, CommandParser.ArgumentGetter getter) {
		SuggestionProvider<ServerCommandSource> extraSuggestion = null;
		ArgumentType<?> type = switch (typeName.toLowerCase()) {
			case "word" -> StringArgumentType.word();
			case "boolean" -> BoolArgumentType.bool();
			//#if MC >= 11901
			case "itemstack" -> ItemStackArgumentType.itemStack(CommandRegister.getRegistryAccess());
			case "block" -> BlockStateArgumentType.blockState(CommandRegister.getRegistryAccess());
			//#else
			//$$case "itemstack" -> ItemStackArgumentType.itemStack();
			//$$case "block" -> BlockStateArgumentType.blockState();
			//#endif
			case "greedystring" -> StringArgumentType.greedyString();
			case "entity" -> ClientEntityArgumentType.entity();
			case "entities" -> ClientEntityArgumentType.entities();
			case "blockpos" -> BlockPosArgumentType.blockPos();
			case "pos" -> Vec3ArgumentType.vec3();
			//#if MC >= 11903
			case "effect" -> RegistryEntryArgumentType.registryEntry(CommandRegister.getRegistryAccess(), RegistryKeys.STATUS_EFFECT);
			case "particle" -> ParticleEffectArgumentType.particleEffect(CommandRegister.getRegistryAccess());
			case "enchantmentid" -> RegistryEntryArgumentType.registryEntry(CommandRegister.getRegistryAccess(), RegistryKeys.ENCHANTMENT);
			//#else
			//$$case "effect" -> StatusEffectArgumentType.statusEffect();
			//$$case "particle" -> ParticleEffectArgumentType.particleEffect();
			//$$case "enchantmentid" -> EnchantmentArgumentType.enchantment();
			//#endif
			case "entityid" -> {
				extraSuggestion = SuggestionProviders.SUMMONABLE_ENTITIES;
				//#if MC >= 11903
				yield RegistryEntryArgumentType.registryEntry(CommandRegister.getRegistryAccess(), RegistryKeys.ENTITY_TYPE);
				//#else
				//$$yield EntitySummonArgumentType.entitySummon();
				//#endif
			}
			case "recipeid" -> {
				extraSuggestion = SuggestionProviders.ALL_RECIPES;
				yield IdentifierArgumentType.identifier();
			}

			case "playername" -> {
				extraSuggestion = (c, b) -> CommandHelper.suggestOnlinePlayers(b);
				yield StringArgumentType.word();
			}
			case "double" -> {
				Double min = getter == null ? null : getter.get("min", NumberDef.class);
				if (min != null) {
					Double max = getter.get("max", NumberDef.class);
					if (max != null) {
						yield DoubleArgumentType.doubleArg(min, max);
					}
					yield DoubleArgumentType.doubleArg(min);
				}
				yield DoubleArgumentType.doubleArg();
			}
			case "integer" -> {
				Double min = getter == null ? null : getter.get("min", NumberDef.class);
				if (min != null) {
					Double max = getter.get("max", NumberDef.class);
					if (max != null) {
						yield IntegerArgumentType.integer(min.intValue(), max.intValue());
					}
					yield IntegerArgumentType.integer(min.intValue());
				}
				yield IntegerArgumentType.integer();
			}
			case "enum" -> {
				ClassDefinition definition = getter == null ? null : getter.get("enum", TypeDef.class);
				if (definition instanceof EnumDefinition enumDefinition) {
					yield DefinitionArgumentType.enumeration(enumDefinition);
				}
				throw new RuntimeError("Enum argument type must contain 'enum: <Type>'");
			}
			default -> throw new RuntimeError("Invalid argument type");
		};

		RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = CommandManager.argument(argumentName, type);
		if (extraSuggestion != null) {
			argumentBuilder.suggests(extraSuggestion);
		}
		if (suggests != null) {
			argumentBuilder.suggests((c, b) -> CommandSource.suggestMatching(suggests, b));
		}
		return argumentBuilder;
	}

	public static ClassInstance commandArgumentToValue(Object object, Interpreter interpreter) throws CommandSyntaxException {
		// We check for these two here since they throw CommandSyntaxExceptions
		if (object instanceof PosArgument posArgument) {
			object = posArgument.toAbsolutePos(new FakeCommandSource(EssentialUtils.getPlayer()));
		}
		if (object instanceof ClientEntitySelector selector) {
			FakeCommandSource source = new FakeCommandSource(EssentialUtils.getPlayer());
			object = selector.isSingleTarget() ? selector.getEntity(source) : selector.getEntities(source);
		}
		return interpreter.convertValue(object);
	}

	private static <T extends PrimitiveDefinition<V>, V> V getFieldInMap(ArucasMap map, Interpreter interpreter, String field, Class<T> type) {
		ClassInstance instance = map.get(interpreter, interpreter.create(StringDef.class, field));
		return instance == null ? null : instance.getPrimitive(type);
	}

	public static class TickedKey {
		private final KeyBinding binding;
		private final int interval;
		private int tick = 0;

		public TickedKey(KeyBinding binding, int interval) {
			this.binding = binding;
			this.interval = interval;
		}

		void tick() {
			if (this.tick++ >= this.interval) {
				this.binding.setPressed(true);
				InputUtil.Key key = ((KeyBindingAccessor) this.binding).getBoundKey();
				KeyBinding.onKeyPressed(key);
				this.tick = 0;
			}
		}
	}

	public static class CommandParser {
		public static final SimpleCommandExceptionType NO_ARGS = new SimpleCommandExceptionType(Texts.NO_ARGUMENTS);

		private final Interpreter interpreter;
		private final String commandName;
		private final ArucasMap subCommandMap;
		private final ArucasMap argumentMap;

		CommandParser(ArucasMap commandMap, Interpreter interpreter) {
			this.interpreter = interpreter.branch();

			this.commandName = getFieldInMap(commandMap, interpreter, "name", StringDef.class);
			if (this.commandName == null) {
				throw new RuntimeError("Command map must contain 'name: <String>'");
			}

			this.subCommandMap = getFieldInMap(commandMap, interpreter, "subcommands", MapDef.class);
			this.argumentMap = getFieldInMap(commandMap, interpreter, "arguments", MapDef.class);
		}

		ArgumentBuilder<ServerCommandSource, ?> parse() {
			LiteralArgumentBuilder<ServerCommandSource> baseCommand = CommandManager.literal(this.commandName);
			if (this.subCommandMap == null) {
				return baseCommand;
			}
			return this.command(baseCommand, this.subCommandMap);
		}

		private ArgumentBuilder<ServerCommandSource, ?> command(ArgumentBuilder<ServerCommandSource, ?> parent, ArucasMap childMap) {
			for (Pair<ClassInstance, ClassInstance> pair : childMap.pairSet()) {
				String key = pair.component1().getPrimitive(StringDef.class);
				if (key == null) {
					throw new RuntimeError("Expected string value in subcommand map");
				}
				if (key.isBlank()) {
					ArucasFunction function = pair.component2().getPrimitive(FunctionDef.class);
					if (function == null) {
						throw new RuntimeError("Expected function value");
					}
					parent.executes(c -> {
						Collection<ParsedArgument<?, ?>> arguments = CommandHelper.getArguments(c);
						if (arguments == null) {
							throw NO_ARGS.create();
						}
						List<ClassInstance> list = new ArrayList<>(arguments.size());
						for (ParsedArgument<?, ?> argument : arguments) {
							list.add(commandArgumentToValue(argument.getResult(), this.interpreter));
						}
						this.interpreter.getThreadHandler().runAsync(() -> {
							function.invoke(this.interpreter.branch(), list);
							return null;
						});
						return 1;
					});
					continue;
				}
				ArucasMap value = pair.component2().getPrimitive(MapDef.class);
				if (value == null) {
					throw new RuntimeError("Expected map value for '%s'".formatted(key));
				}
				String[] arguments = key.split(" ");
				if (arguments.length > 1) {
					ArgumentBuilder<ServerCommandSource, ?> current = this.subCommand(arguments[arguments.length - 1], value);
					for (int i = arguments.length - 2; i >= 0; i--) {
						String name = arguments[i];
						current = this.connectedCommand(name).then(current);
					}
					parent.then(current);
					continue;
				}
				parent.then(this.subCommand(key, value));
			}
			return parent;
		}

		private ArgumentBuilder<ServerCommandSource, ?> subCommand(String string, ArucasMap childMap) {
			return this.command(this.connectedCommand(string), childMap);
		}

		private ArgumentBuilder<ServerCommandSource, ?> connectedCommand(String string) {
			if (string.charAt(0) != '<' || string.charAt(string.length() - 1) != '>') {
				return CommandManager.literal(string);
			}
			string = string.substring(1, string.length() - 1);
			if (this.argumentMap != null) {
				ArucasMap subMap = getFieldInMap(this.argumentMap, this.interpreter, string, MapDef.class);
				if (subMap != null) {
					return this.getArgument(string, subMap);
				}
			}
			throw new RuntimeError("Expected map value for argument '%s'".formatted(string));
		}

		private ArgumentBuilder<ServerCommandSource, ?> getArgument(String name, ArucasMap arguments) {
			String argument = getFieldInMap(arguments, this.interpreter, "type", StringDef.class);
			if (argument == null) {
				throw new RuntimeError("Expected string for 'type' for argument '%s'".formatted(name));
			}

			List<String> suggestions = null;
			ArucasList suggests = getFieldInMap(arguments, this.interpreter, "suggests", ListDef.class);
			if (suggests != null) {
				suggestions = suggests.stream().map(i -> i.toString(this.interpreter)).toList();
			}
			RequiredArgumentBuilder<ServerCommandSource, ?> argumentBuilder = parseArgument(name, argument, suggestions, new ArgumentGetter() {
				@Override
				public <T extends PrimitiveDefinition<V>, V> V get(String fieldName, Class<T> clazz) {
					return getFieldInMap(arguments, CommandParser.this.interpreter, fieldName, clazz);
				}
			});

			ArucasFunction suggester = getFieldInMap(arguments, this.interpreter, "suggester", FunctionDef.class);
			if (suggester != null) {
				argumentBuilder.suggests((c, b) -> {
					Collection<ParsedArgument<?, ?>> commandArguments = CommandHelper.getArguments(c);
					if (commandArguments == null) {
						throw NO_ARGS.create();
					}
					Interpreter branch = this.interpreter.branch();
					List<ClassInstance> list = new ArrayList<>();
					for (ParsedArgument<?, ?> arg : commandArguments) {
						list.add(commandArgumentToValue(arg.getResult(), branch));
					}
					return branch.safe(Suggestions.empty(), () -> {
						ArucasCollection collection = suggester.invoke(branch, list).getPrimitive(CollectionDef.class);
						if (collection != null) {
							List<String> suggested = collection.asCollection().stream().map(i -> i.toString(branch)).toList();
							return CommandSource.suggestMatching(suggested, b);
						}
						throw new RuntimeError("Suggester did not return a list");
					});
				});
			}
			return argumentBuilder;
		}

		private interface ArgumentGetter {
			<T extends PrimitiveDefinition<V>, V> V get(String fieldName, Class<T> clazz);
		}
	}
}
