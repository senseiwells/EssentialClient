package me.senseiwells.essentialclient.utils.render;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Formatting;

//#if MC < 11900
//$$import net.minecraft.text.LiteralText;
//$$import net.minecraft.text.TranslatableText;
//#endif

public class Texts {
	public static final Text EMPTY = literal("");
	public static final Text ESSENTIAL_CLIENT = translatable("essentialclient");
	public static final Text CLIENT_MENU = translatable("essentialclient.menu");
	public static final Text CLIENT_SCREEN = translatable("essentialclient.options");
	public static final Text SERVER_SCREEN = translatable("essentialclient.carpetOptions");
	public static final Text GAME_RULE_SCREEN = translatable("essentialclient.gameOptions");
	public static final Text SCRIPT_SCREEN = translatable("essentialclient.scriptOptions");
	public static final Text CHUNK_SCREEN = translatable("essentialclient.chunkDebugMap");
	public static final Text CHUNK_CLUSTER_SCREEN = translatable("essentialclient.chunkClusters");
	public static final Text CONTROLS_SCREEN = translatable("essentialclient.controls");
	public static final Text TOP_SECRET = translatable("essentialclient.topSecret");
	public static final TextGenerator VERSION = o -> translatable("essentialclient.version", o);

	public static final Text RESET = translatable("essentialclient.ui.reset");
	public static final Text SELECTED = translatable("essentialclient.ui.selected");
	public static final Text REFRESH = translatable("essentialclient.ui.refresh");
	public static final Text DONE = translatable("essentialclient.ui.done");
	public static final Text DOCUMENTATION = translatable("essentialclient.ui.documentation");
	public static final Text CONFIG = translatable("essentialclient.ui.config");
	public static final Text REMOVE = translatable("essentialclient.ui.remove");
	public static final Text CREATE = translatable("essentialclient.ui.create");
	public static final Text NEW = translatable("essentialclient.ui.new");
	public static final Text DOWNLOAD = translatable("essentialclient.ui.download");
	public static final Text TRUE = translatable("essentialclient.ui.true").formatted(Formatting.DARK_GREEN);
	public static final Text FALSE = translatable("essentialclient.ui.false").formatted(Formatting.DARK_RED);
	public static final Text START = translatable("essentialclient.ui.start").formatted(Formatting.DARK_GREEN);
	public static final Text STOP = translatable("essentialclient.ui.stop").formatted(Formatting.DARK_RED);
	public static final Text CANCEL = translatable("essentialclient.ui.cancel");
	public static final Text UNKNOWN = translatable("essentialclient.ui.unknown");

	public static final TextGenerator ARUCAS_VERSION = o -> translatable("essentialclient.script.arucasVersion", o);
	public static final TextGenerator DOWNLOAD_SUCCESS = o -> translatable("essentialclient.script.downloadSuccess", o);
	public static final TextGenerator NO_SCRIPT = o -> translatable("essentialclient.script.noScript", o);
	public static final TextGenerator SCRIPT_CONFIG = o -> translatable("essentialclient.script.config", o);
	public static final Text SCRIPT_NAME = translatable("essentialclient.script.name");
	public static final Text OPEN_SCRIPT = translatable("essentialclient.script.open");
	public static final Text DELETE_SCRIPT = translatable("essentialclient.script.delete");
	public static final Text CREATE_NEW_SCRIPT = translatable("essentialclient.script.createNew");
	public static final Text DOWNLOAD_SCRIPT = translatable("essentialclient.script.download");
	public static final Text AUTOMATION = translatable("essentialclient.script.automation");
	public static final Text UTILITIES = translatable("essentialclient.script.utilities");
	public static final Text MISCELLANEOUS = translatable("essentialclient.script.miscellaneous");
	public static final Text VIEW = translatable("essentialclient.script.view");
	public static final Text DOWNLOAD_FAILED = translatable("essentialclient.script.downloadFailed");
	public static final Text DOWNLOAD_SUCCESSFUL = translatable("essentialclient.script.downloadSuccessful");
	public static final Text FINISHED = translatable("essentialclient.script.finished").formatted(Formatting.RED);
	public static final Text STARTED = translatable("essentialclient.script.started").formatted(Formatting.GREEN);
	public static final TextGenerator SCRIPT_STATUS = o -> translatable("essentialclient.script.scriptStatus", o);
	public static final TextGenerator READ_ERROR = o -> translatable("essentialclient.script.readError", o);
	public static final TextGenerator FATAL_ERROR = o -> translatable("essentialclient.script.fatalError", o);
	public static final TextGenerator CRASH_REPORT = o -> translatable("essentialclient.script.crashReport", o);
	public static final Text CRASH_BUG = translatable("essentialclient.script.crashBug").formatted(Formatting.RED);

	public static final Text X = literal("X");
	public static final Text Z = literal("Z");
	public static final Text OVERWORLD = translatable("essentialclient.chunkDebug.overworld");
	public static final Text NETHER = translatable("essentialclient.chunkDebug.nether");
	public static final Text END = translatable("essentialclient.chunkDebug.end");
	public static final Text MINIMAP_NONE = translatable("essentialclient.chunkDebug.minimapNone");
	public static final Text MINIMAP_STATIC = translatable("essentialclient.chunkDebug.minimapStatic");
	public static final Text MINIMAP_FOLLOW = translatable("essentialclient.chunkDebug.minimapFollow");
	public static final Text RETURN_TO_PLAYER = translatable("essentialclient.chunkDebug.returnToPlayer");
	public static final TextGenerator SELECTED_CHUNK = o -> translatable("essentialclient.chunkDebug.selectedChunk", o);
	public static final TextGenerator CHUNK_STATUS = o -> translatable("essentialclient.chunkDebug.status", o);
	public static final TextGenerator CHUNK_TICKET = o -> translatable("essentialclient.chunkDebug.ticket", o);
	public static final TextGenerator CHUNK_STAGE = o -> translatable("essentialclient.chunkDebug.stage", o);
	public static final Text UNLOADED = translatable("essentialclient.chunkDebug.type.unloaded");
	public static final Text BORDER = translatable("essentialclient.chunkDebug.type.border");
	public static final Text LAZY = translatable("essentialclient.chunkDebug.type.lazy");
	public static final Text ENTITY_TICKING = translatable("essentialclient.chunkDebug.type.entity");
	public static final Text EMPTY_STATUS = translatable("essentialclient.chunkDebug.status.empty");
	public static final Text STRUCTURE_STARTS = translatable("essentialclient.chunkDebug.status.structureStarts");
	public static final Text STRUCTURE_REFERENCES = translatable("essentialclient.chunkDebug.status.structureReferences");
	public static final Text BIOMES = translatable("essentialclient.chunkDebug.status.biomes");
	public static final Text NOISE = translatable("essentialclient.chunkDebug.status.noise");
	public static final Text SURFACE = translatable("essentialclient.chunkDebug.status.surface");
	public static final Text CARVERS = translatable("essentialclient.chunkDebug.status.carvers");
	public static final Text LIQUID_CARVERS = translatable("essentialclient.chunkDebug.status.liquidCarvers");
	public static final Text FEATURES = translatable("essentialclient.chunkDebug.status.features");
	public static final Text LIGHT = translatable("essentialclient.chunkDebug.status.light");
	public static final Text SPAWN = translatable("essentialclient.chunkDebug.status.spawn");
	public static final Text HEIGHTMAPS = translatable("essentialclient.chunkDebug.status.heightmaps");
	public static final Text FULL = translatable("essentialclient.chunkDebug.status.full");
	public static final Text TICKET_SPAWN = translatable("essentialclient.chunkDebug.ticket.spawn");
	public static final Text DRAGON = translatable("essentialclient.chunkDebug.ticket.dragon");
	public static final Text PLAYER = translatable("essentialclient.chunkDebug.ticket.player");
	public static final Text FORCED = translatable("essentialclient.chunkDebug.ticket.forced");
	public static final Text TICKET_LIGHT = translatable("essentialclient.chunkDebug.ticket.light");
	public static final Text PORTAL = translatable("essentialclient.chunkDebug.ticket.portal");
	public static final Text TELEPORT = translatable("essentialclient.chunkDebug.ticket.teleport");
	public static final Text CHONK = translatable("essentialclient.chunkDebug.ticket.chonk");
	public static final Text TICKET_UNKNOWN = translatable("essentialclient.chunkDebug.ticket.unknown");

	public static final TextGenerator NO_CONFIG = o -> translatable("essentialclient.configs.noConfig", o);
	public static final TextGenerator REMOVED_CONFIG = o -> translatable("essentialclient.configs.removeConfig", o);
	public static final Text KEYBIND = translatable("essentialclient.configs.keyBind");
	public static final Text NO_KEYBINDING = translatable("essentialclient.configs.noKeyBinding");
	public static final Text EDIT_LIST = translatable("essentialclient.configs.editList");
	public static final TextGenerator EDITING_LIST = o -> translatable("essentialclient.configs.editingList", o);
	public static final Text WIKI_PAGE = translatable("essentialclient.configs.wiki");
	public static final Text CONFIG_FOLDER = translatable("essentialclient.configs.configFolder");

	public static final Text INVALID_DIMENSION = translatable("essentialclient.alternateDimension.invalid");
	public static final TextGenerator COORDINATES = o -> translatable("essentialclient.alternateDimension.coordinates", o);

	public static final TextGenerator WRONG_GAMEMODE = o -> translatable("essentialclient.playerClient.wrongGamemode", o);

	public static final TextGenerator LIST_EXISTS = o -> translatable("essentialclient.playerList.exists", o);
	public static final TextGenerator LIST_NOT_EXISTS = o -> translatable("essentialclient.playerList.notExists", o);
	public static final TextGenerator LIST_EMPTY = o -> translatable("essentialclient.playerList.empty", o);
	public static final TextGenerator LIST_HAS_PLAYER = o -> translatable("essentialclient.playerList.hasPlayer", o);
	public static final TextGenerator LIST_CREATED = o -> translatable("essentialclient.playerList.created", o);
	public static final TextGenerator LIST_DELETED = o -> translatable("essentialclient.playerList.deleted", o);
	public static final TextGenerator LIST_PLAYER_ADDED = o -> translatable("essentialclient.playerList.playerAdded", o);

	public static final Text AFK = translatable("essentialclient.afkLogout.message");

	public static final Text SWAP_INVENTORY = translatable("essentialclient.swapInventories.name");

	public static final TextGenerator SET_GAME_RULE = o -> translatable("essentialclient.gameRule.set", o);

	public static final Text NO_ARGUMENTS = translatable("essentialclient.command.noArguments");
	public static final TextGenerator ENUM_NOT_FOUND = o -> translatable("essentialclient.command.enumNotFound", o);

	public static final Text UNKNOWN_UPDATE = translatable("essentialclient.clientUpdater.unknown");
	public static final Text UP_TO_DATE = translatable("essentialclient.clientUpdater.upToDate");
	public static final Text NO_VERSIONS = translatable("essentialclient.clientUpdater.noVersions");
	public static final TextGenerator SUCCESSFULLY_DOWNLOADED = o -> translatable("essentialclient.clientUpdater.successfullyDownloaded", o);
	public static final TextGenerator FAILED_TO_DOWNLOAD = o -> translatable("essentialclient.clientUpdater.failedToDownload", o);
	public static final Text OPEN_MODS_FOLDER = translatable("essentialclient.clientUpdater.openModsFolder");

	public static final TextGenerator NEW_DISPLAY = o -> translatable("essentialclient.clientNick.newDisplay", o);
	public static final TextGenerator NOT_RENAMED = o -> translatable("essentialclient.clientNick.newDisplay", o);
	public static final TextGenerator NO_LONGER_RENAMED = o -> translatable("essentialclient.clientNick.newDisplay", o);
	public static final TextGenerator IS_NOT_RENAMED = o -> translatable("essentialclient.clientNick.newDisplay", o);
	public static final TextGenerator IS_RENAMED_TO = o -> translatable("essentialclient.clientNick.newDisplay", o);

	public static final TextGenerator CURRENT_REGION = o -> translatable("essentialclient.region.currentRegion", o);
	public static final TextGenerator DISTANT_REGION = o -> translatable("essentialclient.region.distantRegion", o);

	// These allow for easy porting to later versions
	public static MutableText literal(String message) {
		//#if MC >= 11900
		return Text.literal(message);
		//#else
		//$$return new LiteralText(message);
		//#endif
	}

	public static MutableText translatable(String translatable, Object... args) {
		//#if MC >= 11900
		return Text.translatable(translatable, args);
		//#else
		//$$return new TranslatableText(translatable, args);
		//#endif
	}

	public static String getTranslatableKey(Text text) {
		//#if MC >= 11900
		if (text.getContent() instanceof TranslatableTextContent translatableText) {
			//#else
			//$$if (text instanceof TranslatableText translatableText) {
			//#endif
			return translatableText.getKey();
		}
		return null;
	}

	public interface TextGenerator {
		MutableText generate(Object... first);
	}
}
