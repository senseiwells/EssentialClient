package me.senseiwells.essentialclient.feature;

import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CustomClientCape {
	private static Identifier currentCape;

	private static final Map<String, Identifier> CAPE_MAP = Util.make(new LinkedHashMap<>(), map -> {
		map.put("Old Mojang", id("mojangclassic"));
		map.put("Mojang", id("mojangcape"));
		map.put("Mojang Studios", id("mojangstudios"));
		map.put("Minecon 2011", id("minecon2011"));
		map.put("Minecon 2012", id("minecon2012"));
		map.put("Minecon 2013", id("minecon2013"));
		map.put("Minecon 2015", id("minecon2015"));
		map.put("Minecon 2016", id("minecon2016"));
		map.put("Bacon", id("bacon"));
		map.put("Millionth", id("millionth"));
		map.put("DannyB", id("dannyb"));
		map.put("Julian", id("julian"));
		map.put("Cheapsh0t", id("cheapsh0t"));
		map.put("MrMessiah", id("mrmessiah"));
		map.put("Prismarine", id("prismarine"));
		map.put("Birthday", id("birthday"));
		map.put("Translator", id("translator"));
		map.put("Scrolls", id("scrolls"));
		map.put("Cobalt", id("cobalt"));
		map.put("Mojira", id("mojira"));
		map.put("Turtle", id("turtle"));
		map.put("Migrator", id("migrator"));
		map.put("Christmas 2010", id("christmas2010"));
		map.put("New Year 2011", id("newyear2011"));
	});

	private static final List<String> CAPE_NAMES = CAPE_MAP.keySet().stream().toList();

	public static List<String> getCapeNames() {
		return CAPE_NAMES;
	}

	public static void setCurrentCape(String capeName) {
		currentCape = CAPE_MAP.get(capeName);
	}

	public static Identifier getCurrentCape() {
		return currentCape;
	}

	private static Identifier id(String name) {
		return new Identifier("essentialclient", "textures/capes/" + name + ".png");
	}
}
