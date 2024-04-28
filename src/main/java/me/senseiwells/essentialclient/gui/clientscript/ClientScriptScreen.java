package me.senseiwells.essentialclient.gui.clientscript;

import me.senseiwells.arucas.Arucas;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.feature.keybinds.ClientKeyBind;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.WidgetHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ClientScriptScreen extends ChildScreen {
	private ClientScriptWidget scriptWidget;

	public ClientScriptScreen(Screen parent) {
		super(SCRIPT_SCREEN, parent);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int height = this.height - 27;
		this.scriptWidget = new ClientScriptWidget(this.client, this);
		this.addDrawableChild(this.scriptWidget);
		this.addDrawableChild(WidgetHelper.newButton(10, height, 100, 20, REFRESH, button -> this.refresh()));
		this.addDrawableChild(WidgetHelper.newButton(this.width / 2 - 100, height, 200, 20, DONE, button -> this.close()));
		this.addDrawableChild(WidgetHelper.newButton(this.width - 110, height, 100, 20, DOCUMENTATION, button -> Util.getOperatingSystem().open(EssentialUtils.SCRIPT_WIKI_URL)));
		this.addDrawableChild(WidgetHelper.newButton(10, 10, 80, 20, NEW, button -> this.client.setScreen(new CreateClientScriptScreen(this))));
		this.addDrawableChild(WidgetHelper.newButton(this.width - 90, 10, 80, 20, DOWNLOAD, button -> this.client.setScreen(new DownloadClientScriptScreen(this))));
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		this.scriptWidget.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer, ARUCAS_VERSION.generate(Arucas.VERSION), this.width / 2, 24, 0x949494);
	}

	public void refresh() {
		ClientScript.INSTANCE.refresh();
		this.scriptWidget.load(this.client);
	}

	public void openScriptConfigScreen(ClientScriptInstance scriptInstance) {
		ScriptConfigScreen configScreen = new ScriptConfigScreen(this, scriptInstance);
		EssentialUtils.getClient().setScreen(configScreen);
	}

	static class ScriptConfigScreen extends ChildScreen.Typed<ClientScriptScreen> {
		private final ClientScriptInstance scriptInstance;
		private final TextFieldWidget nameBox;
		private final ButtonWidget openBox;
		private final ButtonWidget deleteBox;
		private final ButtonWidget keyBindBox;
		private final CheckboxWidget selectedCheck;
		private boolean firstKey;
		private boolean editingKeyBind;
		private String newName;

		ScriptConfigScreen(ClientScriptScreen parent, ClientScriptInstance scriptInstance) {
			super(SCRIPT_CONFIG.generate(scriptInstance.getName()), parent);
			this.scriptInstance = scriptInstance;
			String scriptName = scriptInstance.getName();
			this.nameBox = new TextFieldWidget(EssentialUtils.getClient().textRenderer, 0, 0, 200, 20, SCRIPT_NAME);
			this.nameBox.setText(scriptName);
			this.nameBox.setChangedListener(s -> this.newName = s);
			this.openBox = WidgetHelper.newButton(0, 0, 200, 20, OPEN_SCRIPT, button -> Util.getOperatingSystem().open(this.scriptInstance.getFileLocation().toFile()));
			this.deleteBox = WidgetHelper.newButton(0, 0, 200, 20, DELETE_SCRIPT, button -> {
				try {
					this.scriptInstance.delete();
					this.getParent().scriptWidget.clear();
					this.getParent().scriptWidget.load(this.client);
					super.close();
				} catch (IOException e) {
					EssentialClient.LOGGER.error("Failed to delete script", e);
				}
			});
			this.keyBindBox = WidgetHelper.newButton(0, 0, 75, 20, Text.translatable(scriptInstance.getKeyBind().getDisplay()), button -> this.firstKey = this.editingKeyBind = true);
			this.selectedCheck = CheckboxWidget.builder(SELECTED, EssentialUtils.getClient().textRenderer)
				.pos(0, 0)
				.checked(ClientScript.INSTANCE.isSelected(scriptName))
				.callback((w, b) -> {
					if (b) {
						ClientScript.INSTANCE.removeSelectedInstance(scriptName);
					} else {
						ClientScript.INSTANCE.addSelectedInstance(scriptName);
					}
				})
				.build();
			this.newName = "";
		}

		@Override
		protected void init() {
			int halfHeight = this.height / 2;
			int halfWidth = this.width / 2;
			this.nameBox.setPosition(halfWidth - 100, halfHeight - 55);
			this.openBox.setPosition(halfWidth - 100, halfHeight - 25);
			this.deleteBox.setPosition(halfWidth - 100, halfHeight);
			this.keyBindBox.setPosition(halfWidth - 50, halfHeight + 25);
			this.selectedCheck.setPosition(this.keyBindBox.getX() + 80, halfHeight + 25);
			this.addDrawableChild(this.nameBox);
			this.addDrawableChild(this.openBox);
			this.addDrawableChild(this.deleteBox);
			this.addDrawableChild(this.keyBindBox);
			this.addDrawableChild(this.selectedCheck);
			this.addDrawableChild(WidgetHelper.newButton(halfWidth - 100, this.height - 27, 200, 20, DONE, button -> this.close()));
			this.setFocused(this.nameBox);
			super.init();
		}

		@Override
		public void close() {
			if (!this.newName.isEmpty() && !this.newName.equals(this.scriptInstance.getName())) {
				try {
					Path original = this.scriptInstance.getFileLocation();
					Path newLocation = original.resolveSibling(this.newName + ".arucas");
					if (Files.exists(newLocation)) {
						throw new FileAlreadyExistsException("Tried to rename to an existing file");
					}
					this.scriptInstance.renameScript(this.newName, newLocation);
					Files.move(original, newLocation);
					this.getParent().refresh();
				} catch (Exception exception) {
					EssentialClient.LOGGER.error(exception);
				}
			}
			super.close();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.editingKeyBind) {
				this.editingKeyBind = false;
				return true;
			}
			return super.mouseClicked(mouseX, mouseY, button);
		}

		@Override
		public boolean charTyped(char chr, int keyCode) {
			return this.nameBox.charTyped(chr, keyCode);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (this.editingKeyBind) {
				ClientKeyBind keyBind = this.scriptInstance.getKeyBind();
				if (keyBind.isSingleKey()) {
					InputUtil.Key key = keyCode == GLFW.GLFW_KEY_ESCAPE ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode);
					keyBind.addKey(key);
					this.editingKeyBind = false;
					return true;
				}
				if (this.firstKey) {
					this.firstKey = false;
					keyBind.clearKey();
				}
				if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
					this.editingKeyBind = false;
					return true;
				}
				keyBind.addKey(InputUtil.fromKeyCode(keyCode, scanCode));
				return true;
			}
			if (keyCode == GLFW.GLFW_KEY_ENTER && this.nameBox.isFocused()) {
				this.newName = this.nameBox.getText();
				this.nameBox.setFocused(false);
			}
			return super.keyPressed(keyCode, scanCode, modifiers) || this.nameBox.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void render(DrawContext context, int mouseX, int mouseY, float delta) {
			super.render(context, mouseX, mouseY, delta);
			context.drawTextWithShadow(this.textRenderer, SCRIPT_NAME, (int) (this.width / 2.0F - 100), (int) (this.height / 2.0F - 68), 0x949494);
			context.drawTextWithShadow(this.textRenderer, KEYBIND, (int) (this.width / 2.0F - 100), (int) (this.height / 2.0F + 30), 0xE0E0E0);
			context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

			ClientKeyBind keyBinding = this.scriptInstance.getKeyBind();

			MutableText editMessage = Text.literal(keyBinding.getDisplay());
			if (this.client != null && this.client.textRenderer.getWidth(editMessage) > 70) {
				editMessage = Text.literal("...");
			}

			if (this.editingKeyBind) {
				this.keyBindBox.setMessage(
					Text.literal("> ").append(editMessage.formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
				);
			} else {
				this.keyBindBox.setMessage(editMessage);
			}

			if (this.keyBindBox.isMouseOver(mouseX, mouseY)) {
				String display = keyBinding.getDisplay();
				List<Text> textList = List.of(
					Text.literal(keyBinding.getName()).formatted(Formatting.GOLD),
					display.isEmpty() ? NO_KEYBINDING : Text.literal(display)
				);
				context.drawTooltip(this.textRenderer, textList, mouseX, mouseY);
			}
		}
	}
}
