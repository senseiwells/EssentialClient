package me.senseiwells.essentialclient.clientscript.definitions;

import me.senseiwells.arucas.api.docs.annotations.ClassDoc;
import me.senseiwells.arucas.api.docs.annotations.FunctionDoc;
import me.senseiwells.arucas.api.docs.annotations.ParameterDoc;
import me.senseiwells.arucas.api.docs.annotations.ReturnDoc;
import me.senseiwells.arucas.builtin.BooleanDef;
import me.senseiwells.arucas.builtin.NumberDef;
import me.senseiwells.arucas.builtin.StringDef;
import me.senseiwells.arucas.classes.CreatableDefinition;
import me.senseiwells.arucas.classes.instance.ClassInstance;
import me.senseiwells.arucas.compiler.LocatableTrace;
import me.senseiwells.arucas.functions.builtin.Arguments;
import me.senseiwells.arucas.functions.builtin.MemberFunction;
import me.senseiwells.arucas.interpreter.Interpreter;
import me.senseiwells.arucas.utils.misc.Language;
import me.senseiwells.essentialclient.clientscript.core.MinecraftAPI;
import me.senseiwells.essentialclient.utils.clientscript.impl.ScriptPos;
import me.senseiwells.essentialclient.utils.mapping.RegistryHelper;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.BIOME;

@ClassDoc(
	name = BIOME,
	desc = "This class represents biomes, and allows you to interact with things inside of them.",
	language = Language.Java
)
public class BiomeDef extends CreatableDefinition<Biome> {
	public BiomeDef(Interpreter interpreter) {
		super(MinecraftAPI.BIOME, interpreter);
	}

	@NotNull
	@Override
	public String toString(@NotNull ClassInstance instance, @NotNull Interpreter interpreter, @NotNull LocatableTrace trace) {
		Identifier id = RegistryHelper.getBiomeRegistry().getId(instance.asPrimitive(this));
		return "Biome{id=" + (id == null ? "plains" : id.getPath()) + "}";
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
			MemberFunction.of("getSkyColor", this::getSkyColor)
		);
	}

	@FunctionDoc(
		name = "canSnow",
		desc = "This function calculates whether snow will fall at given coordinates",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z coordinate")
		},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether snow will fall at given position"),
		examples = "biome.canSnow(0, 100, 0);"
	)
	private boolean canSnowFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		BlockPos blockPos = new BlockPos(x, y, z);
		return isCold(biome, blockPos);
	}

	@FunctionDoc(
		name = "canSnow",
		desc = "This function calculates whether snow will fall at given coordinates",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether snow will fall at given position"),
		examples = "biome.canSnow(new Pos(0, 100, 0));"
	)
	private boolean canSnowPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return isCold(biome, pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isHot",
		desc = "This function calculates whether a biome is hot at given position",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z coordinate")
		},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether temperature is hot at given position"),
		examples = "biome.isHot(0, 100, 0);"
	)
	private boolean isHotFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		return isHot(biome, new BlockPos(x, y, z));
	}

	@FunctionDoc(
		name = "isHot",
		desc = "This function calculates whether a biome is hot at given position",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether temperature is hot at given position"),
		examples = "biome.isHot(0, 100, 0);"
	)
	private boolean isHotPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return isHot(biome, pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isCold",
		desc = "This function calculates whether biome is cold at given position",
		params = {@ParameterDoc(type = PosDef.class, name = "pos", desc = "the position")},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether temperature is cold at given position"),
		examples = "biome.isCold(0, 100, 0);"
	)
	private boolean isColdPos(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		ScriptPos pos = arguments.nextPrimitive(PosDef.class);
		return isCold(biome, pos.getBlockPos());
	}

	@FunctionDoc(
		name = "isCold",
		desc = "This function calculates whether biome is cold at given position",
		params = {
			@ParameterDoc(type = NumberDef.class, name = "x", desc = "the x coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "y", desc = "the y coordinate"),
			@ParameterDoc(type = NumberDef.class, name = "z", desc = "the z coordinate")
		},
		returns = @ReturnDoc(type = BooleanDef.class, desc = "whether temperature is cold at given position"),
		examples = "biome.isCold(0, 100, 0);"
	)
	private boolean isColdFull(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		int x = arguments.nextPrimitive(NumberDef.class).intValue();
		int y = arguments.nextPrimitive(NumberDef.class).intValue();
		int z = arguments.nextPrimitive(NumberDef.class).intValue();
		return isCold(biome, new BlockPos(x, y, z));
	}

	@FunctionDoc(
		name = "getFogColor",
		desc = "This function returns fog color of the biome",
		returns = @ReturnDoc(type = NumberDef.class, desc = "fog color of the biome"),
		examples = "biome.getFogColor();"
	)
	private int getFogColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getFogColor();
	}

	@FunctionDoc(
		name = "getTemperature",
		desc = "This function returns temperature of the biome",
		returns = @ReturnDoc(type = NumberDef.class, desc = "temperature of the biome"),
		examples = "biome.getTemperature();"
	)
	private float getTemperature(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getTemperature();
	}

	@FunctionDoc(
		name = "getWaterColor",
		desc = "This function returns Fog color of the biome",
		returns = @ReturnDoc(type = NumberDef.class, desc = "fog color of the biome"),
		examples = "biome.getWaterColor();"
	)
	private int getWaterColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getWaterColor();
	}

	@FunctionDoc(
		name = "getWaterFogColor",
		desc = "This function returns water fog color of the biome",
		returns = @ReturnDoc(type = NumberDef.class, desc = "water fog color of the biome"),
		examples = "biome.getWaterFogColor();"
	)
	private int getWaterFogColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getWaterFogColor();
	}

	@FunctionDoc(
		name = "getId",
		desc = "This function returns the path id of the biome, e.g. 'plains'",
		returns = @ReturnDoc(type = StringDef.class, desc = "id of the biome"),
		examples = "biome.getId();"
	)
	private String getId(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		Identifier id = RegistryHelper.getBiomeRegistry().getId(biome);
		return id == null ? "plains" : id.getPath();
	}

	@FunctionDoc(
		name = "getSkyColor",
		desc = "This function returns sky color of the biome",
		returns = @ReturnDoc(type = NumberDef.class, desc = "sky color of the biome"),
		examples = "biome.getSkyColor();"
	)
	private int getSkyColor(Arguments arguments) {
		Biome biome = arguments.nextPrimitive(this);
		return biome.getSkyColor();
	}

	private static boolean isCold(Biome biome, BlockPos pos) {
		//#if MC >= 11700
		return biome.isCold(pos);
		//#else
		//$$return biome.getTemperature(pos) < 0.15F;
		//#endif
	}

	private static boolean isHot(Biome biome, BlockPos pos) {
		//#if MC >= 11904
		return biome.getTemperature() > 1.0F;
		//#elseif MC >= 11800
		//$$return biome.isHot(pos);
		//#else
		//$$return biome.getTemperature(pos) > 1.0F;
		//#endif
	}
}
