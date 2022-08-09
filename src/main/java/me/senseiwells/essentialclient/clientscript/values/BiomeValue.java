package me.senseiwells.essentialclient.clientscript.values;

import me.senseiwells.arucas.api.ArucasClassExtension;
import me.senseiwells.arucas.api.docs.ClassDoc;
import me.senseiwells.arucas.api.docs.FunctionDoc;
import me.senseiwells.arucas.throwables.CodeError;
import me.senseiwells.arucas.utils.Arguments;
import me.senseiwells.arucas.utils.ArucasFunctionMap;
import me.senseiwells.arucas.utils.Context;
import me.senseiwells.arucas.values.*;
import me.senseiwells.arucas.values.functions.MemberFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import java.util.Optional;

import static me.senseiwells.arucas.utils.ValueTypes.NUMBER;
import static me.senseiwells.essentialclient.clientscript.core.MinecraftAPI.*;

public class BiomeValue extends GenericValue<Biome> {
	public BiomeValue(Biome biome) {
		super(biome);
	}

	@Override
	public GenericValue<Biome> copy(Context context) {
		return this;
	}

	@Override
	public String getAsString(Context context) {
		return "Biome@" + this.value;
	}

	@Override
	public int getHashCode(Context context) {
		return this.value.hashCode();
	}

	@Override
	public boolean isEquals(Context context, Value value) {
		return this.value == value.getValue();
	}

	@Override
	public String getTypeName() {
		return BIOME;
	}

	@ClassDoc(
		name = BIOME,
		desc = "This class represents biomes, and allows you to interact with things inside of them.",
		importPath = "Minecraft"
	)
	public static class ArucasBiomeClass extends ArucasClassExtension {
		public ArucasBiomeClass() {
			super(BIOME);
		}

		@Override
		public ArucasFunctionMap<MemberFunction> getDefinedMethods() {
			return ArucasFunctionMap.of(
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
			example = "biome.canSnow(0, 100, 0);"
		)
		private Value canSnowFull(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return BooleanValue.of(!biome.doesNotSnow(blockPos));
		}

		@FunctionDoc(
			name = "canSnow",
			desc = "This function calculates wheter snow will fall at given coordinates",
			params = {POS, "pos", "the position"},
			returns = {BOOLEAN, "whether snow will fall at given position"},
			example = "biome.canSnow(new Pos(0, 100, 0));"
		)
		private Value canSnowPos(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return BooleanValue.of(!biome.doesNotSnow(blockPos));
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
			example = "biome.isHot(0, 100, 0);"
		)
		private Value isHotFull(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return BooleanValue.of(biome.isHot(blockPos));
		}

		@FunctionDoc(
			name = "isHot",
			desc = "This function calculates wheter biome is hot at given position",
			params = {POS, "pos", "the position"},
			returns = {BOOLEAN, "whether temperature is hot at given position"},
			example = "biome.isHot(0, 100, 0);"
		)
		private Value isHotPos(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return BooleanValue.of(biome.isHot(blockPos));
		}

		@FunctionDoc(
			name = "isCold",
			desc = "This function calculates wheter biome is cold at given position",
			params = {POS, "pos", "the position"},
			returns = {BOOLEAN, "whether temperature is cold at given position"},
			example = "biome.isCold(0, 100, 0);"
		)
		private Value isColdPos(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			PosValue posValue = arguments.getNext(PosValue.class);
			BlockPos blockPos = new BlockPos(posValue.value);
			return BooleanValue.of(biome.isCold(blockPos));
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
			example = "biome.isCold(0, 100, 0);"
		)
		private Value isColdFull(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			NumberValue num1 = arguments.getNextNumber();
			NumberValue num2 = arguments.getNextNumber();
			NumberValue num3 = arguments.getNextNumber();
			BlockPos blockPos = new BlockPos(num1.value, num2.value, num3.value);
			return BooleanValue.of(biome.isCold(blockPos));
		}

		@FunctionDoc(
			name = "getFogColor",
			desc = "This function returns Fog color of the biome",
			returns = {NUMBER, "fog color of the biome"},
			example = "biome.getFogColor();"
		)
		private Value getFogColor(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return NumberValue.of(biome.getFogColor());
		}

		@FunctionDoc(
			name = "getTemperature",
			desc = "This function returns temperature of the biome",
			returns = {NUMBER, "temperature of the biome"},
			example = "biome.getTemperature();"
		)
		private Value getTemperature(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return NumberValue.of(biome.getTemperature());
		}

		@FunctionDoc(
			name = "getWaterColor",
			desc = "This function returns Fog color of the biome",
			returns = {NUMBER, "fog color of the biome"},
			example = "biome.getWaterColor();"
		)
		private Value getWaterColor(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return NumberValue.of(biome.getFogColor());
		}

		@FunctionDoc(
			name = "getWaterFogColor",
			desc = "This function returns water fog color of the biome",
			returns = {NUMBER, "water fog color of the biome"},
			example = "biome.getWaterFogColor();"
		)
		private Value getWaterFogColor(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return NumberValue.of(biome.getWaterFogColor());
		}

		@FunctionDoc(
			name = "getId",
			desc = "This function returns Fog color of the biome",
			returns = {STRING, "id of the biome"},
			example = "biome.getId();"
		)
		private Value getId(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			Optional<RegistryKey<Biome>> biomeKey = BuiltinRegistries.BIOME.getKey(biome);
			return biomeKey.map(biomeRegistryKey -> StringValue.of(biomeRegistryKey.getValue().getPath())).orElseGet(() -> StringValue.of(BiomeKeys.PLAINS.getValue().getPath()));
		}

		@FunctionDoc(
			name = "getSkyColor",
			desc = "This function returns sky color of the biome",
			returns = {NUMBER, "sky color of the biome"},
			example = "biome.getSkyColor();"
		)
		private Value getSkyColor(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return NumberValue.of(biome.getSkyColor());
		}

		@FunctionDoc(
			name = "hasHighHumidity",
			desc = "This function returns if biome has high humidity",
			returns = {BOOLEAN, "whether biome has high humidity"},
			example = "biome.hasHighHumidity();"
		)
		private Value hasHighHumidity(Arguments arguments) throws CodeError {
			Biome biome = this.getBiome(arguments);
			return BooleanValue.of(biome.hasHighHumidity());
		}

		private Biome getBiome(Arguments arguments) throws CodeError {
			Biome biome = arguments.getNextGeneric(BiomeValue.class);
			if (biome == null) {
				throw arguments.getError("Biome was null");
			}
			return biome;
		}

		@Override
		public Class<BiomeValue> getValueClass() {
			return BiomeValue.class;
		}
	}
}
