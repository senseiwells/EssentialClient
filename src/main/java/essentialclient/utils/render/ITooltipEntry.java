package essentialclient.utils.render;

import net.minecraft.client.gui.ParentElement;

public interface ITooltipEntry extends ParentElement {
    void drawTooltip(int slotIndex, int x, int y, int mouseX, int mouseY, int listWidth, int listHeight, int slotWidth, int slotHeight,  float partialTicks);
}
