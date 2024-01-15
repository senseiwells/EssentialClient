package me.senseiwells.essentialclient.gui.clientscript;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.gui.entries.AbstractListEntry;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.text.Text;

import java.util.List;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ClientScriptWidget extends ElementListWidget<ClientScriptWidget.ScriptListEntry> {
	private final ClientScriptScreen parent;

	public ClientScriptWidget(MinecraftClient minecraftClient, ClientScriptScreen scriptScreen) {
		super(minecraftClient, scriptScreen.width + 45, scriptScreen.height - 43 - 32, 43, 20);
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

	class ScriptListEntry extends AbstractListEntry<ScriptListEntry> {
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
			this.configButton = WidgetHelper.newButton(0, 0, 45, 20, isTemporary ? REMOVE : CONFIG, buttonWidget -> {
				if (!isTemporary) {
					ClientScriptWidget.this.parent.openScriptConfigScreen(this.scriptInstance);
					return;
				}
				ClientScript.INSTANCE.removeInstance(this.scriptInstance);
				ClientScriptWidget.this.clear();
				ClientScriptWidget.this.load(this.client);
			});
			this.startButton = WidgetHelper.newButton(0, 0, 45, 20, instance.isScriptRunning() ? STOP : START, buttonWidget -> {
				if (this.scriptInstance.isScriptRunning()) {
					this.scriptInstance.stopScript();
					return;
				}
				this.scriptInstance.toggleScript();
			});
			this.checkButton = CheckboxWidget.builder(EMPTY, client.textRenderer)
				.pos(0, 0)
				.checked(ClientScript.INSTANCE.isSelected(this.name))
				.callback((w, b) -> {
					if (b) {
						ClientScript.INSTANCE.removeSelectedInstance(this.name);
					} else {
						ClientScript.INSTANCE.addSelectedInstance(this.name);
					}
				})
				.build();
			this.checkButton.active = !isTemporary;
		}

		@Override
		public List<ClickableWidget> children() {
			return ImmutableList.of(this.configButton, this.startButton, this.checkButton);
		}

		@Override
		public List<ClickableWidget> selectableChildren() {
			return this.children();
		}

		@Override
		public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			context.drawTextWithShadow(this.client.textRenderer, Text.literal(this.name), x - 50, y + height / 2 - 9 / 2, 16777215);
			WidgetHelper.setPosition(this.checkButton, x + width - 20, y);
			WidgetHelper.setPosition(this.startButton, x + width - 70, y);
			WidgetHelper.setPosition(this.configButton, x + width - 120, y);

			this.startButton.active = this.client.player != null;
			this.startButton.setMessage(this.scriptInstance.isScriptRunning() ? STOP : START);
			this.configButton.render(context, mouseX, mouseY, tickDelta);
			this.startButton.render(context, mouseX, mouseY, tickDelta);
			this.checkButton.render(context, mouseX, mouseY, tickDelta);
		}
	}
}
