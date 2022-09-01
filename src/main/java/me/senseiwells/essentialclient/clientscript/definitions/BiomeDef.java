package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.classes.ClassInstance;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.core.Interpreter;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.LocatableTrace;
import me.senseiwells.arucas.utils.MemberFunction;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;

import java.util.List;

import static me.senseiwells.arucas.utils.Util.Types.*;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BIOME;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.POS;

@ClassDoc(
	name = BIOME,
	desc = "This class represents biomes, and allows you to interact with things inside of them.",
	importPath = "Minecraft",
	language = Util.Language.Java
)
public class BiomeDef extends CreatableDefinition<Biome> {
	public BiomeDef(Interpreter interpreter) {
		super(MinecraftAPI.BIOME, interpreter);
	}

	@Override
	public String toString$Arucas(ClassInstance instance, Interpreter interpreter, LocatableTrace trace) {
		Identifier key = BuiltinRegistries.BIOME.getId(instance.asPrimitive(this));
		return "Biome{id=" + (key == null ? "plains" : key.getPath()) + "}";
	}

	@Override
	public List<MemberFunction> defineMethods() {
		return List.of(
			MemberFunction.of("canSnow", 3, this::canSnowFull),
			MemberFunction.of("canSnow", 1, this::canSnowPos),
			MemberFunction.of("isHot", 3, this::isHotFull),
			MemberFunction.of("isHot", 1, this::isHotPos),
			MemberFunction.of("getFogColor", this::getFogColor),
			MemberFunction.of("getTemperature", this::getTemperature),
			MemberFunction.of("getWaterColor", this::getWaterColor),
			MemberFunction.of("getWaterFogColor", this::getWaterFogColor),
			MemberFunction.of("isCold", 1, this::isColdPos),
			MemberFunction.of("isCold", 3, this::isColdFull),
			MemberFunction.of("getId", this::getId),
			MemberFunction.of("getSkyColor", this::getSkyColor),
			MemberFunction.of("hasHighHumidity", this::hasHighHumidity)
		);
	}

	@FunctionDoc(
		name = "canSnow",
		desc = "This function calculates wheter snow will fall at given coordinates",
		params = {
			NUMBER, "x", "the x coordinate",
			NUMBER, "y", "the y coordinate",
			NUMBER, "z", "the z coordinate"
		},
		returns = {BOOLEAN, "whether snow will fall at given position"},
		examples = "biome.canSnow(0, 100, 0);"
	)
	private boolean canSnowFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		return biome.isCold(blockPos);
	}

	@FunctionDoc(
		name = "canSnow",
		desc = "This function calculates wheter snow will fall at given coordinates",
		params = {POS, "pos", "the position"},
		returns = {BOOLEAN, "whether snow will fall at given position"},
		examples = "biome.canSnow(new Pos(0, 100, 0));"
	)
	private boolean canSnowPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return biome.isCold(pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isHot",
		desc = "This function calculates wheter biome is hot at given position",
		params = {
			NUMBER, "x", "the x coordinate",
			NUMBER, "y", "the y coordinate",
			NUMBER, "z", "the z coordinate"
		},
		returns = {BOOLEAN, "whether temperature is hot at given position"},
		examples = "biome.isHot(0, 100, 0);"
	)
	private boolean isHotFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		//#if MC >= 11800
		return biome.isHot(new BlockPos(x, y, z));
		//#else
		//$$return biome.getTemperature(new BlockPos(x, y, z)) > 1.0F;
		//#endif
	}

	@FunctionDoc(
		name = "isHot",
		desc = "This function calculates wheter biome is hot at given position",
		params = {POS, "pos", "the position"},
		returns = {BOOLEAN, "whether temperature is hot at given position"},
		examples = "biome.isHot(0, 100, 0);"
	)
	private boolean isHotPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		//#if MC >= 11800
		return biome.isHot(pos.getBlockPos());
		//#else
		//$$return biome.getTemperature(pos.getBlockPos()) > 1.0F;
		//#endif
	}

	@FunctionDoc(
		name = "isCold",
		desc = "This function calculates wheter biome is cold at given position",
		params = {POS, "pos", "the position"},
		returns = {BOOLEAN, "whether temperature is cold at given position"},
		examples = "biome.isCold(0, 100, 0);"
	)
	private boolean isColdPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return biome.isCold(pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isCold",
		desc = "This function calculates wheter biome is cold at given position",
		params = {
			NUMBER, "x", "the x coordinate",
			NUMBER, "y", "the y coordinate",
			NUMBER, "z", "the z coordinate"
		},
		returns = {BOOLEAN, "whether temperature is cold at given position"},
		examples = "biome.isCold(0, 100, 0);"
	)
	private boolean isColdFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		return biome.isCold(new BlockPos(x, y, z));
	}

	@FunctionDoc(
		name = "getFogColor",
		desc = "This function returns Fog color of the biome",
		returns = {NUMBER, "fog color of the biome"},
		examples = "biome.getFogColor();"
	)
	private int getFogColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getFogColor();
	}

	@FunctionDoc(
		name = "getTemperature",
		desc = "This function returns temperature of the biome",
		returns = {NUMBER, "temperature of the biome"},
		examples = "biome.getTemperature();"
	)
	private float getTemperature(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getTemperature();
	}

	@FunctionDoc(
		name = "getWaterColor",
		desc = "This function returns Fog color of the biome",
		returns = {NUMBER, "fog color of the biome"},
		examples = "biome.getWaterColor();"
	)
	private int getWaterColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getWaterColor();
	}

	@FunctionDoc(
		name = "getWaterFogColor",
		desc = "This function returns water fog color of the biome",
		returns = {NUMBER, "water fog color of the biome"},
		examples = "biome.getWaterFogColor();"
	)
	private int getWaterFogColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getWaterFogColor();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This function returns the path id of the biome, e.g. 'plains'",
		returns = {STRING, "id of the biome"},
		examples = "biome.getId();"
	)
	private String getId(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		Identifier id = EssentialUtils.getNetworkHandler().getRegistryManager().get(Registry.BIOME_KEY).getId(biome);
		return id == null ? "plains" : id.getPath();
	}

	@FunctionDoc(
		name = "getSkyColor",
		desc = "This function returns sky color of the biome",
		returns = {NUMBER, "sky color of the biome"},
		examples = "biome.getSkyColor();"
	)
	private int getSkyColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getSkyColor();
	}

	@FunctionDoc(
		name = "hasHighHumidity",
		desc = "This function returns if biome has high humidity",
		returns = {BOOLEAN, "whether biome has high humidity"},
		examples = "biome.hasHighHumidity();"
	)
	private boolean hasHighHumidity(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.hasHighHumidity();
	}
}
