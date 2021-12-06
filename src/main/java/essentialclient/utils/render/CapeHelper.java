package essentialclient.utils.render;

import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public class CapeHelper {
	public static Identifier capeTexture;
	
	private static final String NAMESPACE = "essentialclient";
	
	private static final Map<String, Identifier> capeMap = Map.ofEntries(
		Map.entry("Old Mojang", new Identifier(NAMESPACE, "textures/capes/mojangclassic.png")),
		Map.entry("Mojang", new Identifier(NAMESPACE, "textures/capes/mojangcape.png")),
		Map.entry("Mojang Studios", new Identifier(NAMESPACE, "textures/capes/mojangstudios.png")),
		Map.entry("Minecon 2011", new Identifier(NAMESPACE, "textures/capes/minecon2011.png")),
		Map.entry("Minecon 2012", new Identifier(NAMESPACE, "textures/capes/minecon2012.png")),
		Map.entry("Minecon 2013", new Identifier(NAMESPACE, "textures/capes/minecon2013.png")),
		Map.entry("Minecon 2015", new Identifier(NAMESPACE, "textures/capes/minecon2015.png")),
		Map.entry("Minecon 2016", new Identifier(NAMESPACE, "textures/capes/minecon2016.png")),
		Map.entry("Bacon", new Identifier(NAMESPACE, "textures/capes/bacon.png")),
		Map.entry("Millionth", new Identifier(NAMESPACE, "textures/capes/millionth.png")),
		Map.entry("DannyB", new Identifier(NAMESPACE, "textures/capes/dannyb.png")),
		Map.entry("Julian", new Identifier(NAMESPACE, "textures/capes/julian.png")),
		Map.entry("Cheapsh0t", new Identifier(NAMESPACE, "textures/capes/cheapsh0t.png")),
		Map.entry("MrMessiah", new Identifier(NAMESPACE, "textures/capes/mrmessiah.png")),
		Map.entry("Prismarine", new Identifier(NAMESPACE, "textures/capes/prismarine.png")),
		Map.entry("Birthday", new Identifier(NAMESPACE, "textures/capes/birthday.png")),
		Map.entry("Translator", new Identifier(NAMESPACE, "textures/capes/translator.png")),
		Map.entry("Scrolls", new Identifier(NAMESPACE, "textures/capes/scrolls.png")),
		Map.entry("Cobalt", new Identifier(NAMESPACE, "textures/capes/cobalt.png")),
		Map.entry("Mojira", new Identifier(NAMESPACE, "textures/capes/mojira.png")),
		Map.entry("Turtle", new Identifier(NAMESPACE, "textures/capes/turtle.png")),
		Map.entry("Migrator", new Identifier(NAMESPACE, "textures/capes/migrator.png")),
		Map.entry("Christmas 2010", new Identifier(NAMESPACE, "textures/capes/christmas2010.png")),
		Map.entry("New Year 2011", new Identifier(NAMESPACE, "textures/capes/newyear2011.png"))
	);

	/**
	 * Cannot do map.keySet() as the order would not be preserved
	 */
	public static final List<String> capeNames = List.of(
		"None",
		"Old Mojang",
		"Mojang",
		"Mojang Studios",
		"Minecon 2011",
		"Minecon 2012",
		"Minecon 2013",
		"Minecon 2015",
		"Minecon 2016",
		"Bacon",
		"Millionth",
		"DannyB",
		"Julian",
		"Cheapsh0t",
		"MrMessiah",
		"Prismarine",
		"Birthday",
		"Translator",
		"Scrolls",
		"Cobalt",
		"Mojira",
		"Turtle",
		"Migrator",
		"Christmas 2010",
		"New Year 2011"
	);

	public static void setCapeTexture(String capeName) {
		capeTexture = capeMap.get(capeName);
	}
}
