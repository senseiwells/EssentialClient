package essentialclient.clientscript.core;

import essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

public class ClientScriptScreen extends Screen {
	private final Screen parent;
	private ClientScriptWidget scriptWidget;

	public ClientScriptScreen(Screen parent) {
		super(new LiteralText("Client Script Options"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		this.scriptWidget = new ClientScriptWidget(EssentialUtils.getClient(), this);
		this.children.add(this.scriptWidget);
		this.addButton(new ButtonWidget(10, this.height - 27, 100, 20, new LiteralText("Refresh"), this::refresh));
		this.addButton(new ButtonWidget(this.width - 110, this.height - 27, 100, 20, new LiteralText(I18n.translate("gui.done")), button -> {
			if (this.client != null) {
				this.client.openScreen(this.parent);
			}
		}));
		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.scriptWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		drawCenteredText(matrices, this.textRenderer, "Arucas Version: %s".formatted(EssentialUtils.getArucasVersion()), this.width / 2, 24, 0x949494);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.client.openScreen(this.parent);
		}
	}

	public void refresh(ButtonWidget button) {
		ClientScript.INSTANCE.refreshAllInstances();
		this.children.remove(this.scriptWidget);
		this.scriptWidget.clear();
		this.scriptWidget = new ClientScriptWidget(this.client, this);
		this.children.add(this.scriptWidget);
	}
}
