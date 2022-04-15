package me.senseiwells.essentialclient.clientscript.core;

import me.senseiwells.arucas.core.Arucas;
import me.senseiwells.arucas.utils.ExceptionUtils;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static me.senseiwells.essentialclient.utils.render.Texts.*;

public class ClientScriptScreen extends ChildScreen {
	private ClientScriptWidget scriptWidget;

	public ClientScriptScreen(Screen parent) {
		super(new LiteralText("Client Script Options"), parent);
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int height = this.height - 27;
		this.scriptWidget = new ClientScriptWidget(EssentialUtils.getClient(), this);
		this.addSelectableChild(this.scriptWidget);
		this.addDrawableChild(new ButtonWidget(10, height, 100, 20, REFRESH, button -> this.refresh()));
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, height, 200, 20, DONE, button -> this.client.setScreen(this.parent)));
		this.addDrawableChild(new ButtonWidget(this.width - 110, height, 100, 20, DOCUMENTATION, button -> Util.getOperatingSystem().open(EssentialUtils.SCRIPT_WIKI_URL)));
		this.addDrawableChild(new ButtonWidget(10, 10, 50, 20, NEW, button -> this.createNewScript()));
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

	private void createNewScript() {
		Path scriptPath = ClientScript.INSTANCE.getScriptDirectory();
		String newScriptName = "New Script";
		Path newScript = scriptPath.resolve(newScriptName + ".arucas");
		if (Files.exists(newScript)) {
			for (int i = 1; ; i++) {
				newScriptName = "New Script " + i;
				newScript = scriptPath.resolve(newScriptName + ".arucas");
				if (!Files.exists(newScript)) {
					break;
				}
			}
		}
		try {
			Files.createFile(newScript);
		}
		catch (IOException e) {
			EssentialClient.LOGGER.error(e);
			return;
		}
		ClientScriptInstance newScriptInstance = new ClientScriptInstance(newScriptName, newScript);
		ClientScript.INSTANCE.addInstance(newScriptInstance);
		this.refresh();
		this.openScriptConfigScreen(newScriptInstance);
	}

	static class ScriptConfigScreen extends Screen {
		private final ClientScriptScreen parent;
		private final ClientScriptInstance scriptInstance;
		private final TextFieldWidget nameBox;
		private final ButtonWidget openBox;
		private final ButtonWidget deleteBox;
		private final ButtonWidget keyBindBox;
		private final CheckboxWidget selectedCheck;
		private boolean editingKeyBind;
		private String newName;

		ScriptConfigScreen(ClientScriptScreen parent, ClientScriptInstance scriptInstance) {
			super(new LiteralText("Script Config for '%s'".formatted(scriptInstance.toString())));
			this.parent = parent;
			this.scriptInstance = scriptInstance;
			String scriptName = scriptInstance.toString();
			this.nameBox = new TextFieldWidget(EssentialUtils.getClient().textRenderer, 0, 0, 200, 20, new LiteralText("ScriptName"));
			this.nameBox.setText(scriptName);
			this.nameBox.setChangedListener(s -> this.newName = s);
			this.openBox = new ButtonWidget(0, 0, 200, 20, new LiteralText("Open Script"), button -> Util.getOperatingSystem().open(this.scriptInstance.getFileLocation().toFile()));
			this.deleteBox = new ButtonWidget(0, 0, 200, 20, new LiteralText("Delete Script"), button -> {
				ExceptionUtils.runSafe(() -> {
					Files.delete(this.scriptInstance.getFileLocation());
					ClientScript.INSTANCE.removeInstance(this.scriptInstance);
					this.parent.scriptWidget.clear();
					this.parent.scriptWidget.load(this.client);
					this.close();
				});
			});
			this.keyBindBox = new ButtonWidget(0, 0, 75, 20, new TranslatableText(scriptInstance.getKeyBind().getTranslationKey()), button -> this.editingKeyBind = true);
			this.selectedCheck = new CheckboxWidget(0, 0, 20, 20, new LiteralText("Selected"), ClientScript.INSTANCE.isSelected(scriptName)) {
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

		public void close() {
			EssentialUtils.getClient().setScreen(this.parent);
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
					ClientScript.INSTANCE.removeInstance(this.scriptInstance);
					ClientScript.INSTANCE.addInstance(new ClientScriptInstance(this.newName, renamed));
					this.parent.refresh();
				}
				catch (Exception exception) {
					EssentialClient.LOGGER.error(exception);
				}
			}
			this.close();
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
				this.nameBox.setText(this.newName = this.nameBox.getText());
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

			MutableText editMessage = this.keyBindBox.getMessage().shallowCopy();
			if (!keyBinding.isUnbound() && this.client != null) {
				for (KeyBinding binding : this.client.options.keysAll) {
					if (!this.editingKeyBind && binding != keyBinding && keyBinding.equals(binding)) {
						this.keyBindBox.setMessage(editMessage.formatted(Formatting.RED));
					}
				}
			}
			if (this.editingKeyBind) {
				this.keyBindBox.setMessage(
					new LiteralText("> ").append(editMessage.formatted(Formatting.YELLOW)).append(" <").formatted(Formatting.YELLOW)
				);
			}
			super.render(matrices, mouseX, mouseY, delta);
		}
	}
}
