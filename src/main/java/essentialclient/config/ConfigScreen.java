package essentialclient.config;

import essentialclient.clientscript.ClientScript;
import essentialclient.config.rulescreen.ClientRulesScreen;
import essentialclient.config.rulescreen.ServerRulesScreen;
import essentialclient.feature.EssentialCarpetClient;
import essentialclient.feature.chunkdebug.ChunkClientNetworkHandler;
import essentialclient.feature.chunkdebug.ChunkDebugScreen;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.EditGameRulesScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

public class ConfigScreen extends Screen {
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
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6, 200, 20, new LiteralText("Essential Client Options"), (button) -> this.client.setScreen(new ClientRulesScreen(this))));
		ButtonWidget serverRuleButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 24, 200, 20, new LiteralText("Carpet Server Options"), button -> this.client.setScreen(new ServerRulesScreen(this))));
		ButtonWidget chunkDebugButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 48, 200, 20, new LiteralText("Chunk Debug Map"), button -> {
			this.client.setScreen(new ChunkDebugScreen(this));
		}));
		ButtonWidget gameRuleButton = this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height / 6 + 72, 200, 20, new LiteralText("Gamerule Options"), button -> {
			if (this.client.getServer() == null) {
				return;
			}
			this.client.setScreen(new EditGameRulesScreen(this.client.getServer().getGameRules(), (rules) -> {
				this.client.setScreen(this);
				rules.ifPresent((gameRules -> this.client.getServer().getGameRules().setAllValues(gameRules, this.client.getServer())));
			}));
		}));
		if (!client.isInSingleplayer()) {
			if (!EssentialCarpetClient.serverIsCarpet) {
				serverRuleButton.active = false;
			}
			gameRuleButton.active = false;
		}
		if (!ChunkClientNetworkHandler.chunkDebugAvailable) {
			chunkDebugButton.active = false;
		}
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, new LiteralText(I18n.translate("gui.done")), (button) -> this.client.setScreen(this.parent)));
		this.addDrawableChild(new ButtonWidget(this.width - 110, this.height - 27, 100, 20, new LiteralText("Open Script File"), (button) -> {
			EssentialUtils.checkifScriptFileExists();
			Util.getOperatingSystem().open(ClientScript.getFile().toFile());
		}));
		this.addDrawableChild(new ButtonWidget(9, this.height - 27, 100, 20, new LiteralText("Open Config File"), (button) -> Util.getOperatingSystem().open(EssentialUtils.getEssentialConfigFile().toFile())));
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, "Essential Client", this.width / 2, 8, 0xFFFFFF);
		if (this.client != null && this.client.getCurrentServerEntry() != null) {
			drawCenteredText(matrices, this.textRenderer, String.format("You are currently connected to: %s", this.client.getCurrentServerEntry().name), this.width / 2, 8 + this.textRenderer.fontHeight + 8, 0xFFFFFF);
		}
		super.render(matrices, mouseX, mouseY, delta);
	}
}