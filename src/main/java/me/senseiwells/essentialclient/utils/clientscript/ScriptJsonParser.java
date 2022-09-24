package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.docs.parser.JsonParser;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;
import shadow.google.gson.*;

public class ScriptJsonParser extends JsonParser {
	public static JsonParser scriptOf(ArucasAPI api) {
		ScriptJsonParser parser = new ScriptJsonParser();
		parser.fromApi(api);
		return parser;
	}

	@Override
	public JsonObject toJson() {
		JsonObject object = super.toJson();
		object.add("events", this.getEventsAsJson());
		return object;
	}

	private JsonElement getEventsAsJson() {
		JsonArray allEvents = new JsonArray();
		MinecraftScriptEvents.forEachEvent(e -> {
			JsonObject event = new JsonObject();
			event.addProperty("name", e.getName());
			event.addProperty("description", e.getDescription());
			String[] params = e.getParameters();
			if (params.length % 3 == 0 && params.length != 0) {
				event.add("params", this.paramsToJson(params));
			}
			event.addProperty("cancellable", e.canCancel());
			allEvents.add(event);
		});
		return allEvents;
	}

	private JsonArray paramsToJson(String[] params) {
		JsonArray allParameters = new JsonArray();
		int i = 0;
		while (i < params.length) {
			JsonObject param = new JsonObject();
			String type = params[i++];
			String name = params[i++];
			String desc = params[i++];
			param.addProperty("name", name);
			param.addProperty("type", type);
			param.addProperty("desc", desc);
			allParameters.add(param);
		}
		return allParameters;
	}
}
