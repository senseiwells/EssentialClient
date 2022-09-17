package me.senseiwells.essentialclient.gui.clientscript;

import com.google.common.collect.ImmutableList;
import me.senseiwells.essentialclient.utils.clientscript.ScriptRepositoryManager;
import me.senseiwells.essentialclient.utils.clientscript.ScriptRepositoryManager.Category;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import java.util.List;

public class DownloadClientScriptWidget extends ElementListWidget<DownloadClientScriptWidget.Entry> {
	private final DownloadClientScriptScreen downloadScreen;
	private int maxScriptNameLength;

	public DownloadClientScriptWidget(MinecraftClient minecraftClient, DownloadClientScriptScreen downloadScreen) {
		super(minecraftClient, downloadScreen.width + 45, downloadScreen.height, 43, downloadScreen.height - 32, 20);
		this.downloadScreen = downloadScreen;

		for (Category category : Category.values()) {
			this.addEntry(new CategoryEntry(category.getPrettyName()));
			for (String script : ScriptRepositoryManager.INSTANCE.getChildrenNames(category)) {
				int length = script.length();
				if (length > this.maxScriptNameLength) {
					this.maxScriptNameLength = length;
				}
				this.addEntry(new ScriptListEntry(category, script));
			}
		}
	}

	abstract static class Entry extends ElementListWidget.Entry<Entry> { }

	class CategoryEntry extends Entry {
		private final int textWidth;
		final Text text;

		CategoryEntry(Text text) {
			this.textWidth = DownloadClientScriptWidget.this.client.textRenderer.getWidth(text);
			this.text = text;
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer renderer = DownloadClientScriptWidget.this.client.textRenderer;
			renderer.draw(matrices, this.text, DownloadClientScriptWidget.this.downloadScreen.width / 2.0F - this.textWidth / 2.0F, y + entryHeight - 9 - 1, 0xFFFFFF);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ButtonWidget> children() {
			return ImmutableList.of();
		}
	}

	class ScriptListEntry extends Entry {
		private final String scriptName;
		private final ButtonWidget viewButton;
		private final ButtonWidget downloadButton;

		ScriptListEntry(Category category, String scriptName) {
			this.scriptName = scriptName;
			this.viewButton = new ButtonWidget(0, 0, 50, 20, Texts.VIEW, button -> {
				Util.getOperatingSystem().open(ScriptRepositoryManager.INSTANCE.getViewableLink(category, scriptName));
			});
			this.downloadButton = new ButtonWidget(0, 0, 60, 20, Texts.DOWNLOAD.copy().formatted(Formatting.DARK_GREEN), button -> {
				ToastManager toastManager = DownloadClientScriptWidget.this.client.getToastManager();
				if (ScriptRepositoryManager.INSTANCE.downloadScript(category, scriptName, true)) {
					SystemToast.show(
						toastManager, SystemToast.Type.PACK_COPY_FAILURE,
						Texts.DOWNLOAD_FAILED, null
					);
					return;
				}
				SystemToast.show(
					toastManager, SystemToast.Type.WORLD_BACKUP,
					Texts.DOWNLOAD_SUCCESSFUL, null
				);
				DownloadClientScriptWidget.this.downloadScreen.getParent().refresh();
			});
		}

		@Override
		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			float width = x - 20 - DownloadClientScriptWidget.this.maxScriptNameLength;
			DownloadClientScriptWidget.this.client.textRenderer.draw(matrices, this.scriptName, width, y + entryHeight / 2.0F - 9 / 2.0F, 0xFFFFFF);
			this.downloadButton.x = x + 155;
			this.viewButton.x = x + 100;
			this.downloadButton.y = this.viewButton.y = y;
			this.viewButton.render(matrices, mouseX, mouseY, tickDelta);
			this.downloadButton.render(matrices, mouseX, mouseY, tickDelta);
		}

		@Override
		public List<? extends Selectable> selectableChildren() {
			return this.children();
		}

		@Override
		public List<ButtonWidget> children() {
			return ImmutableList.of(this.viewButton, this.downloadButton);
		}
	}
}
