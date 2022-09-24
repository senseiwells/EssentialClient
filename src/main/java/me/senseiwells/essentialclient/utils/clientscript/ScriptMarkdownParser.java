package me.senseiwells.essentialclient.utils.clientscript;

import me.senseiwells.arucas.api.ArucasAPI;
import me.senseiwells.arucas.api.docs.parser.MarkdownParser;
import me.senseiwells.essentialclient.clientscript.events.MinecraftScriptEvents;

public class ScriptMarkdownParser extends MarkdownParser {
	public static ScriptMarkdownParser scriptOf(ArucasAPI api) {
		ScriptMarkdownParser parser = new ScriptMarkdownParser();
		parser.fromApi(api);
		return parser;
	}

	public String parseEvents() {
		StringBuilder builder = new StringBuilder("# Events\n\n");
		builder.append(
			"""
				Events are triggers for certain events that happen in the game.
				ClientScript provides a way to hook into these triggers to be able to run your code.
				You can register multiple functions to an event and they will all get called.
				See [here]() on how to register and unregister events.
				Each event will run async by default but you are able to run it on the main game thread.
				"""
		);
		MinecraftScriptEvents.forEachEvent(e -> {
			builder.append("\n\n");
			builder.append("## `\"").append(e.getName()).append("\"`\n");
			builder.append("- ").append(e.getDescription()).append("\n");
			String[] parameters = e.getParameters();
			String params = "";
			if (parameters.length % 3 == 0 && parameters.length != 0) {
				params = this.addParameters(builder, parameters);
			}
			builder.append("- Cancellable: ").append(e.canCancel()).append("\n");
			builder.append("```kotlin\n");
			builder.append("new GameEvent(\"").append(e.getName()).append("\", fun(");
			builder.append(params).append(") {\n");
			builder.append("    // Code\n});");
			builder.append("\n```\n");
		});
		return builder.toString();
	}

	private String addParameters(StringBuilder builder, String[] parameters) {
		int i = 0;
		StringBuilder params = new StringBuilder();
		while (i < parameters.length) {
			String type = parameters[i++];
			String name = parameters[i++];
			String desc = parameters[i++];
			params.append(name);
			builder.append("- Parameter - ").append(type);
			builder.append(" (`").append(name).append("`): ");
			builder.append(desc);
			builder.append("\n");
			if (i < parameters.length) {
				params.append(", ");
			}
		}
		return params.toString();
	}
}
