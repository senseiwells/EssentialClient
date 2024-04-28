package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.ObjectDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.extensions.JsonDef;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.BuiltInFunction;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.JsonUtils;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.ClientScriptUtils;
import net.fabricmc.fabric.impl.biome.modification.BuiltInRegistryKeys;
import net.minecraft.registry.BuiltinRegistries;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.TEXT;

@ClassDoc(
	name = TEXT,
	desc = "This class is used to create formatted strings used inside Minecraft.",
	language = Language.Java
)
public class TextDef extends CreatableDefinition<MutableText> {
	public TextDef(Interpreter interpreter) {
		super(MinecraftAPI.TEXT, interpreter);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		return "Text{text=" + instance.asPrimitive(this).getString() + "}";
	}

	@Override
	public List<BuiltInFunction> defineStaticMethods() {
		return List.of(
			BuiltInFunction.of("of", 1, this::of),
			BuiltInFunction.of("parse", 1, this::parse)
		);
	}

	@FunctionDoc(
		isStatic = true,
		name = "of",
		desc = "This converts a string into a text instance",
		params = {@ParameterDoc(type = StringDef.class, name = "string", desc = "The string to convert into a text instance")},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance from the string"),
		examples = "Text.of('Hello World!');"
	)
	private Text of(Arguments arguments) {
		return Text.literal(arguments.nextPrimitive(StringDef.class));
	}

	@FunctionDoc(
		isStatic = true,
		name = "parse",
		desc = "This converts a text json into a text instance",
		params = {@ParameterDoc(type = StringDef.class, name = "textJson", desc = "The string in json format, or a Json value itself")},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance from the json"),
		examples = "Text.parse('{\"text\":\"Hello World!\",\"color\":\"white\",\"italic\":\"true\"}');"
	)
	private Object parse(Arguments arguments) {
		if (arguments.isNext(JsonDef.class)) {
			return Text.Serialization.fromJson(
				JsonUtils.GSON.toJson(arguments.nextPrimitive(JsonDef.class)),
				BuiltinRegistries.createWrapperLookup()
			);
		}
		return Text.Serialization.fromJson(
			arguments.nextPrimitive(StringDef.class),
			BuiltinRegistries.createWrapperLookup()
		);
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("withClickEvent", 2, this::withClickEvent),
			MemberFunction.of("withHoverEvent", 2, this::withHoverEvent),
			MemberFunction.of("format", 1, this::formatText),
			MemberFunction.of("append", 1, this::appendText)
		);
	}

	@FunctionDoc(
		name = "withClickEvent",
		desc = {
			"This allows you to add a click event to a text instance.",
			"The possible events are: 'open_url', 'open_file', 'run_command', 'suggest_command', 'copy_to_clipboard', 'run_function'.",
			"This will throw an error if the action is invalid"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "event", desc = "the name of the event"),
			@ParameterDoc(type = StringDef.class, name = "value", desc = "the value associated with the event")
		},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance with the click event"),
		examples = """
			text = Text.of("Hello World!");

			// Examples of click events
			text.withClickEvent("open_url", "https://youtu.be/dQw4w9WgXcQ");
			text.withClickEvent("open_file", "C:/Users/user/Desktop/thing.txt");
			text.withClickEvent("run_command", "/gamemode creative");
			text.withClickEvent("suggest_command", "/gamemode survival");
			text.withClickEvent("copy_to_clipboard", "Ooops!");
			text.withClickEvent("run_function", fun() {
				print("Text was clicked!");
			});
			"""
	)
	private ClassInstance withClickEvent(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		MutableText text = instance.asPrimitive(this);
		String actionAsString = arguments.nextConstant();
		ClickEvent clickEvent = ClientScriptUtils.stringToClickEvent(arguments.getInterpreter(), actionAsString, arguments.next());
		text.styled(style -> style.withClickEvent(clickEvent));
		return instance;
	}

	@FunctionDoc(
		name = "withHoverEvent",
		desc = {
			"This allows you to add a hover event to a text instance.",
			"The possible events are: 'show_text', 'show_item', 'show_entity'.",
			"This will throw an error if the event is invalid"
		},
		params = {
			@ParameterDoc(type = StringDef.class, name = "event", desc = "the name of the event"),
			@ParameterDoc(type = ObjectDef.class, name = "value", desc = "the value associated with the event")
		},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance with the hover event"),
		examples = """
			text = Text.of("Hello World!");

			// Examples of hover events
			text.withHoverEvent("show_text", Text.of("Hello world!"));
			text.withHoverEvent("show_item", Material.DIAMOND_SWORD.asItemStack());
			text.withHoverEvent("show_entity", Player.get());
			"""
	)
	private ClassInstance withHoverEvent(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		MutableText text = instance.asPrimitive(this);
		String action = arguments.nextConstant();
		HoverEvent hoverEvent = ClientScriptUtils.stringToHoverEvent(action, arguments.next());
		text.styled(style -> style.withHoverEvent(hoverEvent));
		return instance;
	}

	@FunctionDoc(
		name = "format",
		desc = {
			"This allows you to add a formatting to a text instance.",
			"A list of formatting names can be found [here](https://minecraft.fandom.com/wiki/Formatting_codes).",
			"This will throw an error if the formatting is invalid"
		},
		params = {@ParameterDoc(type = StringDef.class, name = "formatting", desc = "the name of the formatting")},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance with the formatting added"),
		examples = "text.format('DARK_RED').format('BOLD');"
	)
	private ClassInstance formatText(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		MutableText text = instance.asPrimitive(this);
		String string = arguments.nextConstant();
		Formatting formatting = ClientScriptUtils.stringToFormatting(string);
		text.formatted(formatting);
		return instance;
	}

	@FunctionDoc(
		name = "append",
		desc = "This allows you to append a text instance to another text instance",
		params = {@ParameterDoc(type = TextDef.class, name = "otherText", desc = "the text instance to append to")},
		returns = @ReturnDoc(type = TextDef.class, desc = "the text instance with the appended text"),
		examples = "Text.of('Hello').append(Text.of(' world!'));"
	)
	private ClassInstance appendText(Arguments arguments) {
		ClassInstance instance = arguments.next(this);
		MutableText text = instance.asPrimitive(this);
		MutableText other = arguments.nextPrimitive(this);
		text.append(other);
		return instance;
	}
}
