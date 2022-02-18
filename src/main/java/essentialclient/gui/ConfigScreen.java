package essentialclient.gui;

import essentialclient.EssentialClient;
import essentialclient.clientscript.core.ClientScriptScreen;
import essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import essentialclient.feature.chunkdebug.ChunkDebugScreen;
import essentialclient.gui.rulescreen.RulesScreen;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import static essentialclient.utils.render.Texts.*;

public class ConfigScreen extends Screen {
	private static final Text
		GAMERULE = new LiteralText("Gamerule Options"),
		WIKI = new LiteralText("Wiki Page"),
		CONFIG = new LiteralText("Open Config File");

	private final Screen parent;

	public ConfigScreen(Screen parent) {
		super(new LiteralText("Essential Client Options"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int width = this.width / 2 - 100;
		int height = this.height / 6;
		this.addButton(new ButtonWidget(width, height, 200, 20, CLIENT_SCREEN, button -> this.client.openScreen(new RulesScreen(this, false))));
		this.addButton(new ButtonWidget(width, height + 24, 200, 20, SERVER_SCREEN, button -> this.client.openScreen(new RulesScreen(this, true))));
		this.addButton(new ButtonWidget(width, height + 48, 200, 20, SCRIPT_SCREEN, button -> this.client.openScreen(new ClientScriptScreen(this))));
		this.addButton(new ButtonWidget(width, height + 72, 200, 20, CHUNK_SCREEN, button -> this.client.openScreen(new ChunkDebugScreen(this)))).active = ChunkClientNetworkHandler.chunkDebugAvailable;
		this.addButton(new ButtonWidget(width, height + 96, 200, 20, GAMERULE, button -> {
			IntegratedServer server = this.client.getServer();
			if (server != null) {
				this.client.openScreen(new EditGameRulesScreen(server.getGameRules(), rules -> {
					this.client.openScreen(this);
					rules.ifPresent(gameRules -> server.getGameRules().setAllValues(gameRules, server));
				}));
			}
		})).active = this.client.isInSingleplayer();
		this.addButton(new ButtonWidget(width, this.height - 27, 200, 20, ScreenTexts.DONE, button -> this.client.openScreen(this.parent)));
		this.addButton(new ButtonWidget(this.width - 110, this.height - 27, 100, 20, WIKI, button -> Util.getOperatingSystem().open(EssentialUtils.WIKI_URL)));
		this.addButton(new ButtonWidget(9, this.height - 27, 100, 20, CONFIG, button -> Util.getOperatingSystem().open(EssentialUtils.getEssentialConfigFile().toFile())));
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.client.openScreen(this.parent);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		int width = this.width / 2;
		TextRenderer renderer = this.textRenderer;
		drawCenteredText(matrices, renderer, "Essential Client", width, 8, 0xFFFFFF);
		drawCenteredText(matrices, renderer, "Version: " + EssentialClient.VERSION, width, 8 + renderer.fontHeight + 8, 0x949494);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
