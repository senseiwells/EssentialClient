package me.senseiwells.essentialclient.gui.clientscript;

import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.clientscript.core.ClientScript;
import me.senseiwells.essentialclient.clientscript.core.ClientScriptInstance;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ClientScriptScreen extends ChildScreen {
	private ClientScriptWidget scriptWidget;

	public ClientScriptScreen(Screen parent) {
		super(Texts.literal("Client Script Options"), parent);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int height = this.height - 27;
		this.scriptWidget = new ClientScriptWidget(this.client, this);
		this.addSelectableChild(this.scriptWidget);
		this.addDrawableChild(new ButtonWidget(10, height, 100, 20, REFRESH, button -> this.refresh()));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, height, 200, 20, DONE, button -> this.onClose()));
		this.addDrawableChild(new ButtonWidget(this.width - 110, height, 100, 20, DOCUMENTATION, button -> Util.getOperatingSystem().open(EssentialUtils.SCRIPT_WIKI_URL)));
		this.addDrawableChild(new ButtonWidget(10, 10, 80, 20, NEW, button -> this.client.setScreen(new CreateClientScriptScreen(this))));
		this.addDrawableChild(new ButtonWidget(this.width - 90, 10, 80, 20, DOWNLOAD, button -> this.client.setScreen(new DownloadClientScriptScreen(this))));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.scriptWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		drawCenteredText(matrices, this.textRenderer, "Arucas Version: %s".formatted(Arucas.VERSION), this.width / 2, 24, 0x949494);
		super.render(matrices, mouseX, mouseY, delta);
	}

	public void refresh() {
		ClientScript.INSTANCE.refreshAllInstances();
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
		private boolean editingKeyBind;
		private String newName;

		ScriptConfigScreen(ClientScriptScreen parent, ClientScriptInstance scriptInstance) {
			super(Texts.literal("Script Config for '%s'".formatted(scriptInstance.toString())), parent);
			this.scriptInstance = scriptInstance;
			String scriptName = scriptInstance.toString();
			this.nameBox = new TextFieldWidget(EssentialUtils.getClient().textRenderer, 0, 0, 200, 20, Texts.literal("ScriptName"));
			this.nameBox.setText(scriptName);
			this.nameBox.setChangedListener(s -> this.newName = s);
			this.openBox = new ButtonWidget(0, 0, 200, 20, Texts.literal("Open Script"), button -> Util.getOperatingSystem().open(this.scriptInstance.getFileLocation().toFile()));
			this.deleteBox = new ButtonWidget(0, 0, 200, 20, Texts.literal("Delete Script"), button -> ExceptionUtils.runSafe(() -> {
				Files.delete(this.scriptInstance.getFileLocation());
				ClientScript.INSTANCE.removeInstance(this.scriptInstance);
				this.getParent().scriptWidget.clear();
				this.getParent().scriptWidget.load(this.client);
				super.onClose();
			}));
			this.keyBindBox = new ButtonWidget(0, 0, 75, 20, Texts.translatable(scriptInstance.getKeyBind().getTranslationKey()), button -> this.editingKeyBind = true);
			this.selectedCheck = new CheckboxWidget(0, 0, 20, 20, Texts.literal("Selected"), ClientScript.INSTANCE.isSelected(scriptName)) {
				@Override
				public void onPress() {
					if (this.isChecked()) {
						ClientScript.INSTANCE.removeSelectedInstance(scriptName);
					}
					else {
						ClientScript.INSTANCE.addSelectedInstance(scriptName);
					}
					super.onPress();
				}
			};
			this.newName = "";
		}

		@Override
		protected void init() {
			int halfHeight = this.height / 2;
			int halfWidth = this.width / 2;
			this.nameBox.x = this.openBox.x = this.deleteBox.x = halfWidth - 100;
			this.keyBindBox.x = halfWidth - 50;
			this.selectedCheck.x = this.keyBindBox.x + 80;
			this.nameBox.y = halfHeight - 55;
			this.openBox.y = halfHeight - 25;
			this.deleteBox.y = halfHeight;
			this.keyBindBox.y = halfHeight + 25;
			this.selectedCheck.y = halfHeight + 25;
			this.addDrawableChild(this.nameBox);
			this.addDrawableChild(this.openBox);
			this.addDrawableChild(this.deleteBox);
			this.addDrawableChild(this.keyBindBox);
			this.addDrawableChild(this.selectedCheck);
			this.addDrawableChild(new ButtonWidget(halfWidth - 100, this.height - 27, 200, 20, DONE, button -> this.onClose()));
			this.setFocused(this.nameBox);
			super.init();
		}

		@Override
		public void tick() {
			this.nameBox.tick();
			super.tick();
		}

		@Override
		public void onClose() {
			if (!this.newName.isEmpty() && !this.newName.equals(this.scriptInstance.toString())) {
				try {
					Path original = this.scriptInstance.getFileLocation();
					Path renamed = original.resolveSibling(this.newName + ".arucas");
					if (Files.exists(renamed)) {
						throw new FileAlreadyExistsException("Tried to rename to an existing file");
					}
					Files.move(original, renamed);
					String oldName = this.scriptInstance.toString();
					if (ClientScript.INSTANCE.isSelected(oldName)) {
						ClientScript.INSTANCE.removeSelectedInstance(oldName);
						ClientScript.INSTANCE.addSelectedInstance(this.newName);
					}
					ClientScript.INSTANCE.removeInstance(this.scriptInstance);
					ClientScript.INSTANCE.addInstance(new ClientScriptInstance(this.newName, renamed));
					this.getParent().refresh();
				}
				catch (Exception exception) {
					EssentialClient.LOGGER.error(exception);
				}
			}
			super.onClose();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.editingKeyBind) {
				this.scriptInstance.getKeyBind().setBoundKey(InputUtil.Type.MOUSE.createFromCode(button));
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
				InputUtil.Key key = keyCode == GLFW.GLFW_KEY_ESCAPE ? InputUtil.UNKNOWN_KEY : InputUtil.fromKeyCode(keyCode, scanCode);
				this.scriptInstance.getKeyBind().setBoundKey(key);
				this.editingKeyBind = false;
				return true;
			}
			if (keyCode == GLFW.GLFW_KEY_ENTER && this.nameBox.isFocused()) {
				this.newName = this.nameBox.getText();
				this.nameBox.changeFocus(false);
			}
			return super.keyPressed(keyCode, scanCode, modifiers) || this.nameBox.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			this.renderBackgroundTexture(0);
			this.textRenderer.draw(matrices, "Script Name", this.width / 2.0F - 100, this.height / 2.0F - 68, 0x949494);
			this.textRenderer.draw(matrices, "KeyBind", this.width / 2.0F - 100, this.height / 2.0F + 30, 0xE0E0E0);
			drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);

			KeyBinding keyBinding = this.scriptInstance.getKeyBind();
			this.keyBindBox.setMessage(keyBinding.getBoundKeyLocalizedText());

			MutableText editMessage = this.keyBindBox.getMessage().copy();
			if (!keyBinding.isUnbound() && this.client != null) {
				for (KeyBinding binding : this.client.options.keysAll) {
					if (!this.editingKeyBind && binding != keyBinding && keyBinding.equals(binding)) {
						this.keyBindBox.setMessage(editMessage.formatted(Formatting.RED));
					}
				}
			}
			if (this.editingKeyBind) {
				this.keyBindBox.setMessage(
					Texts.literal("> ").append(editMessage.formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
				);
			}
			super.render(matrices, mouseX, mouseY, delta);
		}
	}
}
