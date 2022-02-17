package essentialclient.clientscript.core;

import essentialclient.EssentialClient;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static essentialclient.utils.render.Texts.*;

public class ClientScriptScreen extends Screen {
	private final Screen parent;
	private ClientScriptWidget scriptWidget;

	public ClientScriptScreen(Screen parent) {
		super(new LiteralText("Client Script Options"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		int height = this.height - 27;
		this.scriptWidget = new ClientScriptWidget(EssentialUtils.getClient(), this);
		this.children.add(this.scriptWidget);
		this.addButton(new ButtonWidget(10, height, 100, 20, REFRESH, button -> this.refresh()));
		this.addButton(new ButtonWidget(this.width / 2 - 100, height, 200, 20, DONE, button -> this.client.openScreen(this.parent)));
		this.addButton(new ButtonWidget(this.width - 110, height, 100, 20, DOCUMENTATION, button -> Util.getOperatingSystem().open(EssentialUtils.SCRIPT_WIKI_URL)));
		this.addButton(new ButtonWidget(10, 10, 50, 20, NEW, button -> this.createNewScript()));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.scriptWidget.render(matrices, mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		drawCenteredText(matrices, this.textRenderer, "Arucas Version: %s".formatted(EssentialClient.ARUCAS_VERSION), this.width / 2, 24, 0x949494);
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.client.openScreen(this.parent);
		}
	}

	public void refresh() {
		ClientScript.INSTANCE.refreshAllInstances();
		this.scriptWidget.load(this.client);
	}

	public void openScriptConfigScreen(ClientScriptInstance scriptInstance) {
		ScriptConfigScreen configScreen = new ScriptConfigScreen(this, scriptInstance);
		EssentialUtils.getClient().openScreen(configScreen);
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
		private final CheckboxWidget selectedCheck;
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
				try {
					Files.delete(this.scriptInstance.getFileLocation());
					ClientScript.INSTANCE.removeInstance(this.scriptInstance);
					this.parent.scriptWidget.clear();
					this.parent.scriptWidget.load(this.client);
					this.close();
				}
				catch (IOException ignored) {
				}
			});
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
			this.selectedCheck.x = halfWidth - 35;
			this.nameBox.y = halfHeight - 55;
			this.openBox.y = halfHeight - 25;
			this.deleteBox.y = halfHeight;
			this.selectedCheck.y = halfHeight + 25;
			this.addButton(this.nameBox);
			this.addButton(this.openBox);
			this.addButton(this.deleteBox);
			this.addButton(this.selectedCheck);
			this.addButton(new ButtonWidget(halfWidth - 100, this.height - 27, 200, 20, new LiteralText(I18n.translate("gui.done")), button -> this.onClose()));
			this.setFocused(this.nameBox);
			super.init();
		}

		@Override
		public void tick() {
			this.nameBox.tick();
			super.tick();
		}

		public void close() {
			EssentialUtils.getClient().openScreen(this.parent);
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
				catch (IOException exception) {
					EssentialClient.LOGGER.error(exception);
				}
			}
			this.close();
		}

		@Override
		public boolean charTyped(char chr, int keyCode) {
			return this.nameBox.charTyped(chr, keyCode);
		}

		@Override
		public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
			if (keyCode == GLFW.GLFW_KEY_ENTER) {
				this.nameBox.setText(this.newName = this.nameBox.getText());
				this.nameBox.changeFocus(false);
			}
			return super.keyPressed(keyCode, scanCode, modifiers) || this.nameBox.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
			this.renderBackgroundTexture(0);
			this.textRenderer.draw(matrices, "Script Name", (float) this.width / 2 - 100, (float) this.height / 2 - 68, 0x949494);
			drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
			super.render(matrices, mouseX, mouseY, delta);
		}
	}
}
