package me.senseiwells.essentialclient.gui.clientscript;

import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;

import static me.senseiwells.essentialclient.utils.render.Texts.DONE;

public class DownloadClientScriptScreen extends ChildScreen.Typed<ClientScriptScreen> {
	private DownloadClientScriptWidget widget;

	public DownloadClientScriptScreen(ClientScriptScreen parent) {
		super(Texts.DOWNLOAD_SCRIPT, parent);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		this.widget = new DownloadClientScriptWidget(this.client, this);
		this.addSelectableChild(this.widget);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, DONE, button -> this.close()));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.widget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		super.render(matrices, mouseX, mouseY, delta);
	}
}
