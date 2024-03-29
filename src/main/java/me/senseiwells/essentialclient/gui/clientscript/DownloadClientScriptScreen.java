package me.senseiwells.essentialclient.gui.clientscript;

import me.senseiwells.essentialclient.utils.render.RenderContextWrapper;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;

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
		this.addDrawableChild(WidgetHelper.newButton(this.width / 2 - 100, this.height - 27, 200, 20, DONE, button -> this.close()));
	}

	@Override
	public void render(RenderContextWrapper wrapper, int mouseX, int mouseY, float delta) {
		this.renderBackground(wrapper.getContext());
		this.widget.render(wrapper.getContext(), mouseX, mouseY, delta);
		wrapper.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		super.render(wrapper, mouseX, mouseY, delta);
	}
}
