package essentialclient.utils.render;

import net.minecraft.util.Identifier;

import java.util.Map;

public class CapeHelper {
	public static Identifier capeTexture;
	
	private static final String namespace = "essentialclient";
	
	private static final Map<String, Identifier> capeMap = Map.ofEntries(
		Map.entry("Old Mojang", new Identifier(namespace, "textures/capes/mojangclassic.png")),
		Map.entry("Mojang", new Identifier(namespace, "textures/capes/mojangcape.png")),
		Map.entry("Mojang Studios", new Identifier(namespace, "textures/capes/mojangstudios.png")),
		Map.entry("Minecon 2011", new Identifier(namespace, "textures/capes/minecon2011.png")),
		Map.entry("Minecon 2012", new Identifier(namespace, "textures/capes/minecon2012.png")),
		Map.entry("Minecon 2013", new Identifier(namespace, "textures/capes/minecon2013.png")),
		Map.entry("Minecon 2015", new Identifier(namespace, "textures/capes/minecon2015.png")),
		Map.entry("Minecon 2016", new Identifier(namespace, "textures/capes/minecon2016.png")),
		Map.entry("Bacon", new Identifier(namespace, "textures/capes/bacon.png")),
		Map.entry("Millionth", new Identifier(namespace, "textures/capes/millionth.png")),
		Map.entry("DannyB", new Identifier(namespace, "textures/capes/dannyb.png")),
		Map.entry("Julian", new Identifier(namespace, "textures/capes/julian.png")),
		Map.entry("Cheapsh0t", new Identifier(namespace, "textures/capes/cheapsh0t.png")),
		Map.entry("MrMessiah", new Identifier(namespace, "textures/capes/mrmessiah.png")),
		Map.entry("Prismarine", new Identifier(namespace, "textures/capes/prismarine.png")),
		Map.entry("Birthday", new Identifier(namespace, "textures/capes/birthday.png")),
		Map.entry("Translator", new Identifier(namespace, "textures/capes/translator.png")),
		Map.entry("Scrolls", new Identifier(namespace, "textures/capes/scrolls.png")),
		Map.entry("Cobalt", new Identifier(namespace, "textures/capes/cobalt.png")),
		Map.entry("Mojira", new Identifier(namespace, "textures/capes/mojira.png")),
		Map.entry("Turtle", new Identifier(namespace, "textures/capes/turtle.png")),
		Map.entry("Migrator", new Identifier(namespace, "textures/capes/migrator.png")),
		Map.entry("Christmas 2010", new Identifier(namespace, "textures/capes/christmas2010.png")),
		Map.entry("New Year 2011", new Identifier(namespace, "textures/capes/newyear2011.png"))
	);

	public static void setCapeTexture(String capeName) {
		capeTexture = capeMap.get(capeName);
	}
}
