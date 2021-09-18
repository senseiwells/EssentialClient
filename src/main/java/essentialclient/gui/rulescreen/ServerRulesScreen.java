package essentialclient.gui.rulescreen;

import essentialclient.gui.ConfigListWidget;
import essentialclient.utils.carpet.CarpetSettingsServerNetworkHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.ArrayList;

public class ServerRulesScreen extends Screen
{
    private final Screen parent;
    private ConfigListWidget list;
    private boolean invalid;
    private boolean isEmpty;
    private final ArrayList<TextFieldWidget> stringFieldList = new ArrayList<>();
    private final ArrayList<TextFieldWidget> numberFieldList = new ArrayList<>();
    
    public ServerRulesScreen(Screen parent) {
        super(new LiteralText("Carpet Server Options"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        if (this.client == null)
            return;
        this.list = new ConfigListWidget(this, this.client);
        this.addSelectableChild(this.list);
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155, this.height - 29, 150, 20, new LiteralText("Refresh"), (buttonWidget) -> CarpetSettingsServerNetworkHandler.requestUpdate(this.client)));
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 155 + 160, this.height - 29, 150, 20, new LiteralText(I18n.translate("gui.done")), (buttonWidget) -> this.client.setScreen(this.parent)));
    }
    
    @Override
    public void tick() {
        this.stringFieldList.forEach(TextFieldWidget::tick);
        this.numberFieldList.forEach(TextFieldWidget::tick);
    }
    
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        this.list.render(matrices, mouseX, mouseY, delta);
        this.drawTooltip(mouseX, mouseY, delta);
        drawCenteredText(matrices, this.textRenderer,"Carpet Settings", this.width / 2, 8, 0xFFFFFF);
        if (this.invalid) {
            String text = this.isEmpty ? "You can't leave the field empty!" : "Invalid value! Type an integer!";
            this.fillGradient(matrices, 8, 9, 20 + this.textRenderer.getWidth(text), 14 + this.textRenderer.fontHeight, 0x68000000, 0x68000000);
            drawTexture(matrices, 10, 10, 0, 54, 3, 11);
            this.textRenderer.draw(matrices,  text, 18, 12, 16733525);
        }
        super.render(matrices, mouseX, mouseY, delta);
    }
    
    public void drawTooltip(int mouseX, int mouseY, float delta)
    {
        this.list.drawTooltip(mouseX, mouseY, delta);
    }
    
    public void setInvalid(boolean invalid)
    {
        this.invalid = invalid;
    }
    
    public void setEmpty(boolean isEmpty)
    {
        this.isEmpty = isEmpty;
    }
    
    public ArrayList<TextFieldWidget> getStringFieldList()
    {
        return stringFieldList;
    }
    
    public ArrayList<TextFieldWidget> getNumberFieldList()
    {
        return numberFieldList;
    }
}