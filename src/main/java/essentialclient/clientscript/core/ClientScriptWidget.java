package essentialclient.clientscript.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;

import java.util.List;

public class ClientScriptWidget extends ElementListWidget<ClientScriptWidget.ClientListEntry> {
	public ClientScriptWidget(MinecraftClient minecraftClient, ClientScriptScreen scriptScreen) {
		super(minecraftClient, scriptScreen.width + 45, scriptScreen.height, 43, scriptScreen.height - 32, 20);
		this.load(minecraftClient);
	}

	public void load(MinecraftClient client) {
		for (ClientScriptInstance instance : ClientScript.INSTANCE.getScriptInstancesInOrder()) {
			this.addEntry(new ClientListEntry(client, instance, this));
		}
	}

	public void clear() {
		this.clearEntries();
	}

	public static class ClientListEntry extends ElementListWidget.Entry<ClientScriptWidget.ClientListEntry> {
		private final MinecraftClient client;
		private final String name;
		private final ClientScriptInstance scriptInstance;
		private final ButtonWidget openButton;
		private final ButtonWidget startButton;
		private final CheckboxWidget checkButton;

		ClientListEntry(MinecraftClient client, ClientScriptInstance instance, ClientScriptWidget widget) {
			this.client = client;
			this.name = instance.toString();
			this.scriptInstance = instance;
			boolean isTemporary = instance.isTemporary();
			this.openButton = new ButtonWidget(0, 0, 45, 20, new LiteralText(isTemporary ? "Remove" : "Open"), buttonWidget -> {
				if (!isTemporary) {
					Util.getOperatingSystem().open(this.scriptInstance.getFileLocation().toFile());
					return;
				}
				ClientScript.INSTANCE.removeInstance(this.scriptInstance);
				widget.clear();
				widget.load(this.client);
			});
			this.startButton = new ButtonWidget(0, 0, 45, 20, new LiteralText(instance.isScriptRunning() ? "Stop" : "Start"), buttonWidget -> {
				if (this.scriptInstance.isScriptRunning()) {
					this.scriptInstance.stopScript();
					return;
				}
				this.scriptInstance.startScript();
			});
			this.checkButton = new CheckboxWidget(0, 0, 20, 20, new LiteralText(""), ClientScript.INSTANCE.isSelected(this.name)) {
				@Override
				public void onPress() {
					String instanceName = ClientListEntry.this.name;
					if (this.isChecked()) {
						ClientScript.INSTANCE.removeSelectedInstance(instanceName);
					}
					else {
						ClientScript.INSTANCE.addSelectedInstance(instanceName);
					}
					super.onPress();
				}
			};
			this.checkButton.active = !isTemporary;
		}

		@Override
		public List<? extends Element> children() {
			return List.of(this.openButton, this.startButton, this.checkButton);
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer font = this.client.textRenderer;
			float fontY = (float) (y + height / 2 - 9 / 2);
			font.draw(matrices, this.name, (float) x - 50, fontY, 16777215);
			this.checkButton.x = x + width - 20;
			this.startButton.x = x + width - 70;
			this.openButton.x = x + width - 120;
			this.openButton.y = this.startButton.y = this.checkButton.y = y;
			this.startButton.active = this.client.player != null;
			this.startButton.setMessage(new LiteralText(this.scriptInstance.isScriptRunning() ? "Stop" : "Start"));
			this.openButton.render(matrices, mouseX, mouseY, tickDelta);
			this.startButton.render(matrices, mouseX, mouseY, tickDelta);
			this.checkButton.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
}
