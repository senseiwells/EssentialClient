package essentialclient.config.rulescreen;

import essentialclient.config.ConfigListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;
import java.util.List;

public class RulesScreen extends Screen {
	private final Screen parent;
	private ConfigListWidget list;
	private TextFieldWidget searchBox;
	private boolean invalid;
	private boolean isEmpty;
	private final List<TextFieldWidget> stringFieldList = new ArrayList<>();
	private final List<TextFieldWidget> numberFieldList = new ArrayList<>();

	public RulesScreen(Screen parent, String name) {
		super(new LiteralText(name));
		this.parent = parent;
	}

	@Override
	protected void init() {
		if (this.client == null) {
			return;
		}
		this.searchBox = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 22, 200, 15, this.searchBox, new LiteralText("Search Rules"));
		this.searchBox.setChangedListener(this::refreshRules);
		this.list = new ConfigListWidget(this, this.client, this.searchBox.getText());
		this.addSelectableChild(this.list);
		this.addDrawableChild(this.searchBox);
		this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height - 27, 200, 20, new LiteralText(I18n.translate("gui.done")), (buttonWidget) -> this.client.setScreen(this.parent)));
		this.setInitialFocus(this.searchBox);
	}

	@Override
	public void tick() {
		this.stringFieldList.forEach(TextFieldWidget::tick);
		this.numberFieldList.forEach(TextFieldWidget::tick);
		this.searchBox.tick();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		this.list.render(matrices, mouseX, mouseY, delta);
		this.drawTooltip(mouseX, mouseY, delta);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		if (this.invalid) {
			String text = this.isEmpty ? "You can't leave the field empty!" : "Invalid value! Type a number!";
			this.fillGradient(matrices, 8, 9, 20 + this.textRenderer.getWidth(text), 14 + this.textRenderer.fontHeight, 0x68000000, 0x68000000);
			drawTexture(matrices, 10, 10, 0, 54, 3, 11);
			this.textRenderer.draw(matrices,  text, 18, 12, 16733525);
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onClose() {
		if (this.client != null) {
			this.list.updateAllEntriesOnClose();
			this.client.setScreen(this.parent);
		}
	}

	public String getSearchBoxText() {
		return this.searchBox.getText();
	}

	public void refreshRules(String filter) {
		this.children().remove(this.list);
		this.list.clear();
		this.list = new ConfigListWidget(this, this.client, filter);
		this.addSelectableChild(this.list);
	}

	public void drawTooltip(int mouseX, int mouseY, float delta) {
		this.list.drawTooltip(mouseX, mouseY, delta);
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

	public List<TextFieldWidget> getStringFieldList() {
		return stringFieldList;
	}

	public List<TextFieldWidget> getNumberFieldList() {
		return numberFieldList;
	}
}
