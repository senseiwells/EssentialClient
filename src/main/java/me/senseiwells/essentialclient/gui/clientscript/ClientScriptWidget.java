package me.senseiwells.essentialclient.gui.clientscript;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.List;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ClientScriptWidget extends ElementListWidget<ClientScriptWidget.ScriptListEntry> {
	private final ClientScriptScreen parent;

	public ClientScriptWidget(MinecraftClient minecraftClient, ClientScriptScreen scriptScreen) {
		super(minecraftClient, scriptScreen.width + 45, scriptScreen.height, 43, scriptScreen.height - 32, 20);
		this.parent = scriptScreen;
		this.load(minecraftClient);
	}

	public void load(MinecraftClient client) {
		this.clear();
		for (ClientScriptInstance instance : ClientScript.INSTANCE.getScriptInstancesInOrder()) {
			this.addEntry(new ScriptListEntry(client, instance));
		}
	}

	public void clear() {
		this.clearEntries();
	}

	class ScriptListEntry extends ElementListWidget.Entry<ScriptListEntry> {
		private final MinecraftClient client;
		private final String name;
		private final ClientScriptInstance scriptInstance;
		private final ButtonWidget configButton;
		private final ButtonWidget startButton;
		private final CheckboxWidget checkButton;

		ScriptListEntry(MinecraftClient client, ClientScriptInstance instance) {
			this.client = client;
			this.name = instance.getName();
			this.scriptInstance = instance;
			boolean isTemporary = instance.isTemporary();
			this.configButton = new ButtonWidget(0, 0, 45, 20, isTemporary ? REMOVE : CONFIG, buttonWidget -> {
				if (!isTemporary) {
					ClientScriptWidget.this.parent.openScriptConfigScreen(this.scriptInstance);
					return;
				}
				ClientScript.INSTANCE.removeInstance(this.scriptInstance);
				ClientScriptWidget.this.clear();
				ClientScriptWidget.this.load(this.client);
			});
			this.startButton = new ButtonWidget(0, 0, 45, 20, instance.isScriptRunning() ? STOP : START, buttonWidget -> {
				if (this.scriptInstance.isScriptRunning()) {
					this.scriptInstance.stopScript();
					return;
				}
				this.scriptInstance.toggleScript();
			});
			this.checkButton = new CheckboxWidget(0, 0, 20, 20, EMPTY, ClientScript.INSTANCE.isSelected(this.name)) {
				@Override
				public void onPress() {
					String instanceName = ScriptListEntry.this.name;
					if (this.isChecked()) {
						ClientScript.INSTANCE.removeSelectedInstance(instanceName);
					} else {
						ClientScript.INSTANCE.addSelectedInstance(instanceName);
					}
					super.onPress();
				}
			};
			this.checkButton.active = !isTemporary;
		}

		@Override
		public List<ClickableWidget> children() {
			return this.selectableChildren();
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return ImmutableList.of(this.configButton, this.startButton, this.checkButton);
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer font = this.client.textRenderer;
			float fontY = (float) (y + height / 2 - 9 / 2);
			font.draw(matrices, this.name, (float) x - 50, fontY, 16777215);
			this.checkButton.x = x + width - 20;
			this.startButton.x = x + width - 70;
			this.configButton.x = x + width - 120;
			this.configButton.y = this.startButton.y = this.checkButton.y = y;
			this.startButton.active = this.client.player != null;
			this.startButton.setMessage(this.scriptInstance.isScriptRunning() ? STOP : START);
			this.configButton.render(matrices, mouseX, mouseY, tickDelta);
			this.startButton.render(matrices, mouseX, mouseY, tickDelta);
			this.checkButton.render(matrices, mouseX, mouseY, tickDelta);
		}
	}
}
