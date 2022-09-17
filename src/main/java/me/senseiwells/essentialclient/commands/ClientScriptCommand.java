package me.senseiwells.essentialclient.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.ScriptRepositoryManager;
import me.senseiwells.essentialclient.utils.command.CommandHelper;
import me.senseiwells.essentialclient.utils.command.EnumArgumentType;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

import static me.senseiwells.essentialclient.utils.clientscript.ScriptRepositoryManager.Category;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ClientScriptCommand {
	private static final DynamicCommandExceptionType NO_SUCH_SCRIPT = new DynamicCommandExceptionType(Texts.NO_SCRIPT::generate);

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		CommandHelper.CLIENT_COMMANDS.add("clientscript");

		LiteralArgumentBuilder<ServerCommandSource>
			root = literal("clientscript"),
			download = literal("download"),
			runFromWeb = literal("runfromweb");

		root.then(literal("run")
			.then(argument("scriptName", StringArgumentType.string())
				.suggests((c, b) -> CommandSource.suggestMatching(ClientScript.INSTANCE.getScriptInstanceNames(), b))
				.executes(context -> {
					String scriptName = StringArgumentType.getString(context, "scriptName");
					ClientScriptInstance instance = ClientScript.INSTANCE.getScriptInstance(scriptName);
					if (instance == null) {
						throw NO_SUCH_SCRIPT.create(scriptName);
					}
					instance.stopScript();
					instance.toggleScript();
					return 1;
				})
				.then(argument("script", StringArgumentType.greedyString())
					.executes(context -> {
						String scriptName = StringArgumentType.getString(context, "scriptName");
						String scriptContent = StringArgumentType.getString(context, "script");
						ClientScriptInstance.runFromContent(scriptName, scriptContent);
						return 1;
					})
				)
			)
		);

		root.then(literal("stopall")
			.executes(context -> {
				ClientScript.INSTANCE.stopAllInstances();
				return 1;
			})
		);

		download.then(argument("category", EnumArgumentType.enumeration(Category.class))
			.then(argument("scriptname", StringArgumentType.string())
				.suggests(((c, b) -> CommandSource.suggestMatching(
					ScriptRepositoryManager.INSTANCE.getChildrenNames(EnumArgumentType.getEnumeration(c, "category", Category.class)), b))
				)
				.executes(context -> {
					Category category = EnumArgumentType.getEnumeration(context, "category", Category.class);
					String scriptName = StringArgumentType.getString(context, "scriptname");
					if (ScriptRepositoryManager.INSTANCE.downloadScript(category, scriptName, false)) {
						throw NO_SUCH_SCRIPT.create(scriptName);
					}
					EssentialUtils.sendMessage(Texts.DOWNLOAD_SUCCESS.generate(scriptName).formatted(Formatting.GOLD));
					return 1;
				})
				.then(argument("shouldoverwrite", BoolArgumentType.bool())
					.executes(context -> {
						Category category = EnumArgumentType.getEnumeration(context, "category", Category.class);
						String scriptName = StringArgumentType.getString(context, "scriptname");
						boolean shouldOverwrite = BoolArgumentType.getBool(context, "shouldoverwrite");
						if (ScriptRepositoryManager.INSTANCE.downloadScript(category, scriptName, shouldOverwrite)) {
							throw NO_SUCH_SCRIPT.create(scriptName);
						}
						EssentialUtils.sendMessage(Texts.DOWNLOAD_SUCCESS.generate(scriptName).formatted(Formatting.GOLD));
						return 1;
					})
				)
			)
		);

		runFromWeb.then(argument("category", EnumArgumentType.enumeration(Category.class))
			.then(argument("scriptname", StringArgumentType.string())
				.suggests(((c, b) -> CommandSource.suggestMatching(
					ScriptRepositoryManager.INSTANCE.getChildrenNames(EnumArgumentType.getEnumeration(c, "category", Category.class)), b))
				)
				.executes(context -> {
					Category category = EnumArgumentType.getEnumeration(context, "category", Category.class);
					String scriptName = StringArgumentType.getString(context, "scriptname");
					String scriptContent = ScriptRepositoryManager.INSTANCE.getScriptFromWeb(category, scriptName, true);
					if (scriptContent == null) {
						throw NO_SUCH_SCRIPT.create(scriptName);
					}
					ClientScriptInstance.runFromContent(scriptName, scriptContent);
					return 1;
				})
				.then(argument("fromcache", BoolArgumentType.bool())
					.executes(context -> {
						Category category = EnumArgumentType.getEnumeration(context, "category", Category.class);
						String scriptName = StringArgumentType.getString(context, "scriptname");
						boolean fromCache = BoolArgumentType.getBool(context, "fromcache");
						String scriptContent = ScriptRepositoryManager.INSTANCE.getScriptFromWeb(category, scriptName, fromCache);
						if (scriptContent == null) {
							throw NO_SUCH_SCRIPT.create(scriptName);
						}
						ClientScriptInstance.runFromContent(scriptName, scriptContent);
						return 1;
					})
				)
			)
		);

		dispatcher.register(root.then(download).then(runFromWeb));
	}
}
