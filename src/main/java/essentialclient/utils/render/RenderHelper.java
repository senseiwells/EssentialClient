package essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import essentialclient.clientscript.extensions.BoxShapeWrapper;
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
	private static final Set<Integer> fakeEntityId = new HashSet<>();
	private static int backwardsEntityId = Integer.MAX_VALUE;

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

	public static void setupArucasBoxRendering() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableLighting();
		RenderSystem.disableCull();
	}

	public static void resetArucasBoxRendering() {
		RenderSystem.lineWidth(1);
		RenderSystem.enableCull();
		RenderSystem.enableLighting();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void render(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		setupArucasBoxRendering();

		List<BoxShapeWrapper> boxesToRender = BoxShapeWrapper.getBoxesToRender();
		List<BoxShapeWrapper> normalBoxes = boxesToRender.stream().filter(box -> !box.renderThroughBlocks).toList();
		List<BoxShapeWrapper> renderThrough = boxesToRender.stream().filter(box -> box.renderThroughBlocks).toList();

		drawOutlines(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);
		drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);

		RenderSystem.disableDepthTest();
		drawOutlines(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
		drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
		RenderSystem.enableDepthTest();

		resetArucasBoxRendering();
	}

	private static void drawOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, List<BoxShapeWrapper> boxes) {
		bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		boxes
			.stream()
			.filter(box -> box.outlineWidth >= 1)
			.forEach(box -> {
				RenderSystem.lineWidth(box.outlineWidth);
				addBoxToBuffer(bufferBuilder, matrices, camera, box.pos1.toBlockPos(), box.pos2.toBlockPos(), box.outlineRed, box.outlineGreen, box.outlineBlue, 255, true);
			});
		tessellator.draw();
	}

	private static void drawBoxes(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, List<BoxShapeWrapper> boxes) {
		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		boxes
			.forEach(box -> {
				addBoxToBuffer(bufferBuilder, matrices, cameraPos, box.pos1.toBlockPos(), box.pos2.toBlockPos(), box.red, box.green, box.blue, box.opacity, false);
			});
		tessellator.draw();
	}

	private static BlockPos getMinimumBlockPos(BlockPos pos1, BlockPos pos2) {
		return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}

	private static void addBoxToBuffer(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, BlockPos pos1, BlockPos pos2, int red, int green, int blue, int opacity, boolean outline) {
		BlockPos posOrigin = getMinimumBlockPos(pos1, pos2);
		matrices.push();
		matrices.translate(posOrigin.getX() - cameraPos.getX(), posOrigin.getY() - cameraPos.getY(), posOrigin.getZ() - cameraPos.getZ());
		Matrix4f model = matrices.peek().getModel();
		addBoxVertices(bufferBuilder, model, pos1, pos2, red / 255.0F, green / 255.0F, blue / 255.0F, opacity / 255.0F, outline);
		matrices.pop();
	}

	private static void addBoxVertices(BufferBuilder bufferBuilder, Matrix4f model, BlockPos pos1, BlockPos pos2, float red, float green, float blue, float alpha, boolean outline) {
		float c0 = -0.002F;
		float x1 = Math.abs(pos1.getX() - pos2.getX()) - c0 + 1;
		float y1 = Math.abs(pos1.getY() - pos2.getY()) - c0 + 1;
		float z1 = Math.abs(pos1.getZ() - pos2.getZ()) - c0 + 1;

		// West Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		}

		// East Face
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		}

		// North Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		}

		// South Face
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		}

		// Down Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		}

		// Up Face
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
		if (outline) {
			bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(0, 0, 0).next();
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
