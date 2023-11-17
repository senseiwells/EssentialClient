package me.senseiwells.essentialclient.gui.config;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.feature.chunkdebug.ChunkDebugScreen;
import me.senseiwells.essentialclient.gui.RulesScreen;
import me.senseiwells.essentialclient.gui.clientscript.ClientScriptScreen;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ConfigScreen extends ChildScreen {
	public ConfigScreen(Screen parent) {
		super(CLIENT_SCREEN, parent);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int width = this.width / 2 - 100;
		int height = this.height / 6;
		this.addDrawableChild(WidgetHelper.newButton(width, height, 200, 20, CLIENT_SCREEN, button -> this.client.setScreen(RulesScreen.getClientRulesScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(width, height + 24, 200, 20, SERVER_SCREEN, button -> this.client.setScreen(RulesScreen.getCarpetRulesScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(width, height + 48, 200, 20, GAME_RULE_SCREEN, button -> this.client.setScreen(RulesScreen.getGameRulesScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(width, height + 72, 200, 20, SCRIPT_SCREEN, button -> this.client.setScreen(new ClientScriptScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(width, height + 96, 200, 20, CONTROLS_SCREEN, button -> this.client.setScreen(new ControlsScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(width, height + 120, 200, 20, CHUNK_SCREEN, button -> this.client.setScreen(new ChunkDebugScreen(this)))).active = EssentialClient.CHUNK_NET_HANDLER.isAvailable();
		this.addDrawableChild(WidgetHelper.newButton(width, this.height - 27, 200, 20, DONE, button -> this.close()));
		this.addDrawableChild(WidgetHelper.newButton(this.width - 89, this.height - 27, 80, 20, WIKI_PAGE, button -> Util.getOperatingSystem().open(EssentialUtils.WIKI_URL)));
		this.addDrawableChild(WidgetHelper.newButton(9, this.height - 27, 80, 20, CONFIG_FOLDER, button -> Util.getOperatingSystem().open(EssentialUtils.getEssentialConfigFile().toFile())));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		int width = this.width / 2;
		TextRenderer renderer = this.textRenderer;
		context.drawCenteredTextWithShadow(renderer, ESSENTIAL_CLIENT, width, 8, 0xFFFFFF);
		context.drawCenteredTextWithShadow(renderer, VERSION.generate(EssentialClient.VERSION), width, 8 + renderer.fontHeight + 8, 0x949494);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.client != null && mouseX > this.width - 30 && mouseY < 30) {
			this.client.setScreen(new Secret(Texts.EMPTY, this));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	private static class Secret extends ChildScreen {
		protected Secret(Text title, Screen parent) {
			super(title, parent);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			super.render(context, mouseX, mouseY, delta);
			MatrixStack matrices = context.getMatrices();
			matrices.push();
			matrices.scale(2.0F, 2.0F, 2.0F);
			context.drawCenteredTextWithShadow(this.textRenderer, TOP_SECRET, this.width / 2, 8, 0xFFFFFF);
			matrices.pop();
		}
	}
}
