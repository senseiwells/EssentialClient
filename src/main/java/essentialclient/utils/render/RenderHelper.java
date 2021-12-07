package essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.math.Matrix4f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenderHelper {
    private static int backwardsEntityId = Integer.MAX_VALUE;
    private static final Set<Integer> fakeEntityId = new HashSet<>();
    //public static boolean isRenderingHotBarGui = false;

    // Taken from Screen Class
    public static void drawGuiInfoBox(TextRenderer font, String text, int x, int y) {
        if (text == null) {
            return;
        }
        MatrixStack matrices = new MatrixStack();
        List<OrderedText> lines = wordWrap(text, 150);
        if (lines.isEmpty()) {
            return;
        }
        int i = 0;
        for(OrderedText orderedText : lines) {
            int j = font.getWidth(orderedText);
            if (j > i) {
                i = j;
            }
        }
        int mouseX = x + 12;
        int mouseY = y - 12;
        int k = 8;
        if (lines.size() > 1) {
            k += 2 + (lines.size() - 1) * 10;
        }
        matrices.push();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        Matrix4f matrix4f = matrices.peek().getModel();
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY - 4, mouseX + i + 3, mouseY - 3, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY + k + 3, mouseX + i + 3, mouseY + k + 4, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY - 3, mouseX + i + 3, mouseY + k + 3, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, mouseX - 4, mouseY - 3, mouseX - 3, mouseY + k + 3, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, mouseX + i + 3, mouseY - 3, mouseX + i + 4, mouseY + k + 3, -267386864, -267386864);
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY - 3 + 1, mouseX - 3 + 1, mouseY + k + 3 - 1, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, mouseX + i + 2, mouseY - 3 + 1, mouseX + i + 3, mouseY + k + 3 - 1, 1347420415, 1344798847);
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY - 3, mouseX + i + 3, mouseY - 3 + 1, 1347420415, 1347420415);
        fillGradient(matrix4f, bufferBuilder, mouseX - 3, mouseY + k + 2, mouseX + i + 3, mouseY + k + 3, 1344798847, 1344798847);
        RenderSystem.enableDepthTest();
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        bufferBuilder.end();
        BufferRenderer.draw(bufferBuilder);
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();
        VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
        matrices.translate(0.0D, 0.0D, 400.0D);
        for(int l1 = 0; l1 < lines.size(); ++l1) {
            OrderedText orderedText = lines.get(l1);
            if (orderedText != null) {
                font.draw(orderedText, (float) mouseX, (float) mouseY, -1, true, matrix4f, immediate, false, 0, 15728880);
            }
            if (l1 == 0) {
                mouseY += 2;
            }
            mouseY += 10;
        }
        immediate.draw();
        matrices.pop();
    }

    
    public static List<OrderedText> wordWrap(String s, int width) {
        s = s.replace("\r", "");
        return MinecraftClient.getInstance().textRenderer.wrapLines(StringVisitable.plain(s), width);
    }

    protected static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
        float f = (float)(colorStart >> 24 & 255) / 255.0F;
        float g = (float)(colorStart >> 16 & 255) / 255.0F;
        float h = (float)(colorStart >> 8 & 255) / 255.0F;
        float i = (float)(colorStart & 255) / 255.0F;
        float j = (float)(colorEnd >> 24 & 255) / 255.0F;
        float k = (float)(colorEnd >> 16 & 255) / 255.0F;
        float l = (float)(colorEnd >> 8 & 255) / 255.0F;
        float m = (float)(colorEnd & 255) / 255.0F;
        bufferBuilder.vertex(matrix, (float)endX, (float)startY, (float) 400).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float)startX, (float)startY, (float) 400).color(g, h, i, f).next();
        bufferBuilder.vertex(matrix, (float)startX, (float)endY, (float) 400).color(k, l, m, j).next();
        bufferBuilder.vertex(matrix, (float)endX, (float)endY, (float) 400).color(k, l, m, j).next();
    }

    public static int getNextEntityId() {
        int nextId = backwardsEntityId--;
        fakeEntityId.add(nextId);
        return nextId;
    }

    public static boolean removeFakeEntity(int id) {
        return fakeEntityId.remove(id);
    }

    public static boolean isFakeEntity(int id) {
        return fakeEntityId.contains(id);
    }
}
