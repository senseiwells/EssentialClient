package me.senseiwells.essentialclient.clientscript.core;

import com.mojang.brigadier.builder.ArgumentBuilder;
import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.essentialclient.clientscript.definitions.*;
import me.senseiwells.essentialclient.clientscript.definitions.shapes.*;
import me.senseiwells.essentialclient.clientscript.extensions.ArucasMinecraftExtension;
import me.senseiwells.essentialclient.rule.client.ClientRule;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptBlockState;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptItemStack;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptMaterial;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.render.FakeInventoryScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class MinecraftAPI {
	public static final String
		BIOME = "Biome",
		BOX_SHAPE = "BoxShape",
		CENTRED_SHAPE = "CentredShape",
		CONFIG_HANDLER = "ConfigHandler",
		CORNERED_SHAPE = "CorneredShape",
		FAKE_BLOCK = "FakeBlock",
		FAKE_ENTITY = "FakeEntity",
		GAME_EVENT = "GameEvent",
		KEY_BIND = "KeyBind",
		LINE_SHAPE = "LineShape",
		SPHERE_SHAPE = "SphereShape",
		BLOCK = "Block",
		COMMAND_BUILDER = "CommandBuilder",
		CONFIG = "Config",
		ENTITY = "Entity",
		FAKE_SCREEN = "FakeScreen",
		ITEM_ENTITY = "ItemEntity",
		ITEM_STACK = "ItemStack",
		JSON = "Json",
		LIVING_ENTITY = "LivingEntity",
		MATERIAL = "Material",
		MERCHANT_SCREEN = "MerchantScreen",
		MINECRAFT_CLIENT = "MinecraftClient",
		MINECRAFT_TASK = "MinecraftTask",
		OTHER_PLAYER = "OtherPlayer",
		OUTLINED_SHAPE = "OutlinedShape",
		PLAYER = "Player",
		POS = "Pos",
		RECIPE = "Recipe",
		SCREEN = "Screen",
		SHAPE = "Shape",
		TEXT = "Text",
		TRADE = "Trade",
		WORLD = "World";

	public static final String IMPORT_NAME = "Minecraft";

	@SuppressWarnings({"deprecation", "unchecked"})
	public static void addMinecraftAPI(ArucasAPI.Builder builder) {
		builder.addClassDefinitions(
			IMPORT_NAME,
			BiomeDef::new,
			BlockDef::new,
			BoxShapeDef::new,
			CentredShapeDef::new,
			CommandBuilderDef::new,
			ConfigDef::new,
			ConfigHandlerDef::new,
			CorneredShapeDef::new,
			EntityDef::new,
			FakeEntityDef::new,
			FakeScreenDef::new,
			FakeBlockShapeDef::new,
			GameEventDef::new,
			ItemEntityDef::new,
			ItemStackDef::new,
			KeyBindDef::new,
			LineShapeDef::new,
			LivingEntityDef::new,
			MaterialDef::new,
			MerchantScreenDef::new,
			MinecraftClientDef::new,
			MinecraftTaskDef::new,
			OtherPlayerDef::new,
			OutlinedShapeDef::new,
			PlayerDef::new,
			PosDef::new,
			RecipeDef::new,
			ScreenDef::new,
			ShapeDef::new,
			SphereShapeDef::new,
			TextDef::new,
			TradeDef::new,
			WorldDef::new
		);
		builder.addBuiltInExtension(new ArucasMinecraftExtension());

		builder.addConversion(MinecraftClient.class, (m, i) -> i.getPrimitive(MinecraftClientDef.class).instance);
		builder.addConversion(ClientPlayerEntity.class, (p, i) -> i.create(PlayerDef.class, p));
		builder.addConversion(OtherClientPlayerEntity.class, (p, i) -> i.getPrimitive(OtherPlayerDef.class).create(p));
		builder.addConversion(LivingEntity.class, (l, i) -> i.getPrimitive(LivingEntityDef.class).create(l));
		builder.addConversion(ItemEntity.class, (e, i) -> i.getPrimitive(ItemEntityDef.class).create(e));
		builder.addConversion(Entity.class, (e, i) -> i.getPrimitive(EntityDef.class).create(e));
		builder.addConversion(Block.class, (b, i) -> i.create(MaterialDef.class, ScriptMaterial.materialOf(b)));
		builder.addConversion(BlockState.class, (b, i) -> i.create(BlockDef.class, new ScriptBlockState(b, null)));
		builder.addConversion(Item.class, (m, i) -> i.create(MaterialDef.class, ScriptMaterial.materialOf(m)));
		builder.addConversion(ItemStack.class, (s, i) -> i.create(ItemStackDef.class, new ScriptItemStack(s)));
		builder.addConversion(World.class, (w, i) -> i.create(WorldDef.class, w));
		builder.addConversion(Biome.class, (b, i) -> i.create(BiomeDef.class, b));
		builder.addConversion(Screen.class, (s, i) -> i.getPrimitive(ScreenDef.class).create(s));
		builder.addConversion(FakeInventoryScreen.class, (s, i) -> i.create(FakeScreenDef.class, s));
		builder.addConversion(MerchantScreen.class, (s, i) -> i.create(MerchantScreenDef.class, s));
		builder.addConversion(MutableText.class, (t, i) -> i.create(TextDef.class, t));
		builder.addConversion(Text.class, (t, i) -> i.create(TextDef.class, t.copy()));
		builder.addConversion(Vec3d.class, (p, i) -> i.create(PosDef.class, new ScriptPos(p)));
		builder.addConversion(Vec3f.class, (p, i) -> i.create(PosDef.class, new ScriptPos(new Vec3d(p))));
		builder.addConversion(BlockPos.class, (b, i) -> i.create(PosDef.class, new ScriptPos(b)));
		builder.addConversion(Vec3i.class, (p, i) -> i.create(PosDef.class, new ScriptPos(new Vec3d(p.getX(), p.getY(), p.getZ()))));
		builder.addConversion(ScriptBlockState.class, (s, i) -> i.create(BlockDef.class, s));
		builder.addConversion(ScriptItemStack.class, (s, i) -> i.create(ItemStackDef.class, s));
		builder.addConversion(ScriptPos.class, (p, i) -> i.create(PosDef.class, p));
		builder.addConversion(ScriptMaterial.class, (m, i) -> i.create(MaterialDef.class, m));
		builder.addConversion(Recipe.class, (r, i) -> i.create(RecipeDef.class, r));
		builder.addConversion(TradeOffer.class, (t, i) -> i.create(TradeDef.class, t));
		builder.addConversion(ArgumentBuilder.class, (a, i) -> i.create(CommandBuilderDef.class, a));
		builder.addConversion(ClientRule.class, (c, i) -> i.create(ConfigDef.class, c));

		builder.addConversion(ItemStackArgument.class, (s, i) -> EssentialUtils.throwAsUnchecked(() -> i.create(ItemStackDef.class, new ScriptItemStack(s.createStack(1, false)))));
		builder.addConversion(BlockStateArgument.class, (b, i) -> i.create(BlockDef.class, new ScriptBlockState(b.getBlockState(), null)));
		builder.addConversion(Identifier.class, (id, i) -> i.create(StringDef.class, id.toString()));
		builder.addConversion(Enchantment.class, (e, i) -> i.convertValue(Registry.ENCHANTMENT.getId(e)));
	}
}
