package essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RenderHelper {
	private static int backwardsEntityId = Integer.MAX_VALUE;
	private static final Set<Integer> fakeEntityId = new HashSet<>();

	// Taken from Screen Class
	public static void drawGuiInfoBox(TextRenderer font, String text, int x, int y) {
		if (text == null) {
			return;
		}
		MatrixStack matrices = new MatrixStack();
		List<OrderedText> lines = wordWrap(text, 220);
		if (lines.isEmpty()) {
			return;
		}
		int i = 0;
		for (OrderedText orderedText : lines) {
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
		for (int l1 = 0; l1 < lines.size(); ++l1) {
			OrderedText orderedText = lines.get(l1);
			if (orderedText != null) {
				font.draw(orderedText, (float) mouseX, (float) mouseY, l1 == 0 ? 16760576 : -1, true, matrix4f, immediate, false, 0, 15728880);
			}
			if (l1 == 0) {
				mouseY += 2;
			}
			mouseY += 10;
		}
		immediate.draw();
		matrices.pop();
	}

	public static void drawBoxWithOutline(MatrixStack matrices, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha, float outlineRed, float outlineGreen, float outlineBlue) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		BlockPos posOrigin = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));

		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();

		Vec3d cameraPos = camera.getPos();

		matrices.push();
		matrices.translate(posOrigin.getX() - cameraPos.getX(), posOrigin.getY() - cameraPos.getY(), posOrigin.getZ() - cameraPos.getZ());

		Matrix4f model = matrices.peek().getModel();

		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableLighting();
		RenderSystem.disableCull();

		bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		drawBox(bufferBuilder, model, pos1, pos2, outlineRed, outlineGreen, outlineBlue, 1, true);
		tessellator.draw();

		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		drawBox(bufferBuilder, model, pos1, pos2, red, green, blue, alpha, false);
		tessellator.draw();

		matrices.pop();

		RenderSystem.enableCull();
		RenderSystem.enableLighting();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void drawBox(BufferBuilder bufferBuilder, Matrix4f model, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha, boolean outline) {
		float x1 = Math.abs(pos1.getX() - pos2.getX()) + 1.002F;
		float y1 = Math.abs(pos1.getY() - pos2.getY()) + 1.002F;
		float z1 = Math.abs(pos1.getZ() - pos2.getZ()) + 1.002F;

		float c0 = -0.002F;
		// is slightly outside to avoid z-fighting

		// west Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		}

		// east Face
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).next();
		if(outline) {
			bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).next();
		}

		// north Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		}

		// south Face
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).next();
		}

		// down Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).next();
		}

		// up Face
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).next();
		}
	}

	public static List<OrderedText> wordWrap(String s, int width) {
		s = s.replace("\r", "");
		return MinecraftClient.getInstance().textRenderer.wrapLines(StringVisitable.plain(s), width);
	}

	protected static void fillGradient(Matrix4f matrix, BufferBuilder bufferBuilder, int startX, int startY, int endX, int endY, int colorStart, int colorEnd) {
		float r = (float) (colorStart >> 24 & 255) / 255;
		float g = (float) (colorStart >> 16 & 255) / 255;
		float b = (float) (colorStart >> 8 & 255) / 255;
		float a = (float) (colorStart & 255) / 255;
		float j = (float) (colorEnd >> 24 & 255) / 255;
		float k = (float) (colorEnd >> 16 & 255) / 255;
		float l = (float) (colorEnd >> 8 & 255) / 255;
		float m = (float) (colorEnd & 255) / 255;
		bufferBuilder.vertex(matrix, (float) endX, (float) startY, 400.0F).color(g, b, a, r).next();
		bufferBuilder.vertex(matrix, (float) startX, (float) startY, 400.0F).color(g, b, a, r).next();
		bufferBuilder.vertex(matrix, (float) startX, (float) endY, 400.0F).color(k, l, m, j).next();
		bufferBuilder.vertex(matrix, (float) endX, (float) endY, 400.0F).color(k, l, m, j).next();
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
