package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.docs.parser.SnippetParser;
import me.senseiwells.arucas.utils.Util;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import shadow.google.gson.Gson;
import shadow.google.gson.JsonElement;
import shadow.google.gson.JsonObject;
import shadow.kotlin.Pair;
import shadow.kotlin.TuplesKt;

import java.util.*;

public class ScriptSnippetParser extends SnippetParser {
	private static final String DEFAULT_SNIPPETS = "https://raw.githubusercontent.com/senseiwells/Arucas-VSCode-Extension/master/snippets/arucas_default.json";

	public static ScriptSnippetParser scriptOf(ArucasAPI api) {
		ScriptSnippetParser parser = new ScriptSnippetParser();
		parser.fromApi(api);
		return parser;
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = new Gson().fromJson(Util.Network.INSTANCE.getStringFromUrl(DEFAULT_SNIPPETS), JsonObject.class);
		JsonObject returnObject = this.addGameEvents(super.toJson());
		for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
			returnObject.add(entry.getKey(), entry.getValue());
		}
		return returnObject;
	}

	private JsonObject addGameEvents(JsonObject object) {
		MinecraftScriptEvents.forEachEvent(e -> {
			JsonObject gameObject = new JsonObject();
			Pair<List<String>, String> returns = this.joinParameters(e.getParameters());
			String id = "new GameEvent(\"" + e.getName() + "\", fun(" + this.listToString(returns.getFirst()) + ") { })";
			String body = "new GameEvent(\"" + e.getName() + "\", fun(" + this.listToString(returns.getFirst().stream().map(s -> "$" + s).toList()) + ") {\n\t$0\n})";
			gameObject.addProperty("prefix", id);
			gameObject.addProperty("body", body);
			gameObject.addProperty("description", e.getDescription() + "\n" + returns.getSecond());
			object.add("Game Event: " + e.getName(), gameObject);
		});
		return object;
	}

	private Pair<List<String>, String> joinParameters(String[] params) {
		if (params.length % 3 != 0) {
			throw new IllegalArgumentException("Illegal args: " + Arrays.toString(params));
		}

		ArrayList<String> paramNames = new ArrayList<>();
		StringBuilder description = new StringBuilder("\n");

		int i = 0;
		while (i < params.length) {
			String type = params[i++];
			String name = params[i++];
			String desc = params[i++];
			paramNames.add(name);
			description.append("- Argument - ").append(type).append(" (`").append(name).append("`): ").append(desc).append('\n');
		}

		return TuplesKt.to(paramNames, description.toString());
	}

	private String listToString(List<String> list) {
		StringBuilder builder = new StringBuilder();
		Iterator<String> iter = list.listIterator();
		while (iter.hasNext()) {
			builder.append(iter.next());
			if (iter.hasNext()) {
				builder.append(", ");
			}
		}
		return builder.toString();
	}
}
