package me.senseiwells.essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.clientscript.extensions.BoxShapeWrapper;
import me.senseiwells.essentialclient.clientscript.extensions.FakeBlockWrapper;
import me.senseiwells.essentialclient.clientscript.extensions.LineShapeWrapper;
import me.senseiwells.essentialclient.clientscript.extensions.SphereShapeWrapper;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.Shape;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.*;
import net.minecraft.world.LightType;

import java.util.List;

public class RenderHelper {
	public static void renderAllShapes(MatrixStack matrices) {
		setupArucasRendering();

		renderBoxes(matrices);
		renderSpheres(matrices);
		renderLines(matrices);

		resetArucasRendering();
	}

	public static void setupArucasRendering() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableCull();
	}

	public static void resetArucasRendering() {
		RenderSystem.lineWidth(1);
		RenderSystem.enableCull();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
	}

	private static void renderBoxes(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		List<BoxShapeWrapper> normalBoxes = BoxShapeWrapper.getNormalBoxes();
		List<BoxShapeWrapper> renderThrough = BoxShapeWrapper.getThroughBoxes();

		if (!normalBoxes.isEmpty()) {
			drawBoxOutlines(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);
			drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);
		}

		if (!renderThrough.isEmpty()) {
			RenderSystem.disableDepthTest();
			drawBoxOutlines(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
			drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawBoxOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, List<BoxShapeWrapper> boxes) {
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		for (BoxShapeWrapper box : boxes) {
			if (box.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(box.getOutlineWidth());
				addBoxToBuffer(bufferBuilder, matrices, camera, box, true);
			}
		}
		tessellator.draw();
	}

	private static void drawBoxes(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, List<BoxShapeWrapper> boxes) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		for (BoxShapeWrapper box : boxes) {
			addBoxToBuffer(bufferBuilder, matrices, cameraPos, box, false);
		}
		tessellator.draw();
	}

	private static Vec3d getMinimumPos(Vec3d pos1, Vec3d pos2) {
		return new Vec3d(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}

	private static void addBoxToBuffer(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, BoxShapeWrapper box, boolean outline) {
		Vec3d pos1 = box.getPosition();
		Vec3d pos2 = box.getSecondPosition();

		int red = box.getRed(), green = box.getGreen(), blue = box.getBlue(), alpha = box.getAlpha();
		if (outline) {
			red = box.getOutlineRed();
			green = box.getOutlineGreen();
			blue = box.getOutlineBlue();
			alpha = 255;
		}

		Vec3d posOrigin = getMinimumPos(pos1, pos2);
		matrices.push();
		matrices.translate(posOrigin.getX() - cameraPos.getX(), posOrigin.getY() - cameraPos.getY(), posOrigin.getZ() - cameraPos.getZ());
		tilt(matrices, box);
		scale(matrices, box);
		addBoxVertices(bufferBuilder, matrices, pos1, pos2, red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F, outline);
		matrices.pop();
	}

	private static void addBoxVertices(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d pos1, Vec3d pos2, float red, float green, float blue, float alpha, boolean outline) {
		float c0 = -0.002F;
		float x1 = (float) (Math.abs(pos1.getX() - pos2.getX()) - c0 + 1);
		float y1 = (float) (Math.abs(pos1.getY() - pos2.getY()) - c0 + 1);
		float z1 = (float) (Math.abs(pos1.getZ() - pos2.getZ()) - c0 + 1);

		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getPositionMatrix();
		Matrix3f normal = entry.getNormalMatrix();

		if (outline) {
			// idk man
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, -1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, -1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 0.0F, -1.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 0.0F, -1.0F, 0.0F).next();
			bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, -1.0F).next();
			bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, -1.0F).next();
			bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 1.0F, 0.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 1.0F, 0.0F).next();
			bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 0.0F, 0.0F, 1.0F).next();
			return;
		}

		// West Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();

		// East Face
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();


		// North Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();

		// South Face
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();

		// Down Face
		bufferBuilder.vertex(model, c0, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, c0, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();

		// Up Face
		bufferBuilder.vertex(model, c0, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, c0, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, z1).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
		bufferBuilder.vertex(model, x1, y1, c0).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
	}

	private static void renderSpheres(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		List<SphereShapeWrapper> normalSpheres = SphereShapeWrapper.getNormalSpheres();
		List<SphereShapeWrapper> throughSpheres = SphereShapeWrapper.getThroughSpheres();

		if (!normalSpheres.isEmpty()) {
			drawSpheresOutlines(tessellator, bufferBuilder, matrices, cameraPos, normalSpheres);
			drawSpheres(tessellator, bufferBuilder, matrices, cameraPos, normalSpheres);
		}

		if (!throughSpheres.isEmpty()) {
			RenderSystem.disableDepthTest();
			drawSpheresOutlines(tessellator, bufferBuilder, matrices, cameraPos, throughSpheres);
			drawSpheres(tessellator, bufferBuilder, matrices, cameraPos, throughSpheres);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawSpheres(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, List<SphereShapeWrapper> spheres) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		for (SphereShapeWrapper sphere : spheres) {
			addSphereToBuffer(bufferBuilder, matrices, cameraPos, sphere, false);
		}
		tessellator.draw();
	}

	private static void drawSpheresOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, List<SphereShapeWrapper> spheres) {
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		for (SphereShapeWrapper sphere : spheres) {
			if (sphere.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(sphere.getOutlineWidth());
				addSphereToBuffer(bufferBuilder, matrices, camera, sphere, true);
			}
		}
		tessellator.draw();
	}

	private static void addSphereToBuffer(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, SphereShapeWrapper sphere, boolean outline) {
		Vec3d pos = sphere.getPosition();

		int red = sphere.getRed(), green = sphere.getGreen(), blue = sphere.getBlue(), alpha = sphere.getAlpha();
		if (outline) {
			red = sphere.getOutlineRed();
			green = sphere.getOutlineGreen();
			blue = sphere.getOutlineBlue();
			alpha = 255;
		}

		matrices.push();
		matrices.translate(pos.getX() - cameraPos.getX(), pos.getY() - cameraPos.getY(), pos.getZ() - cameraPos.getZ());
		tilt(matrices, sphere);
		scale(matrices, sphere);
		addSphereVertices(bufferBuilder, matrices, sphere.getWidth(), sphere.getSteps(), red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F, outline);
		matrices.pop();
	}

	private static void addSphereVertices(BufferBuilder builder, MatrixStack matrices, float radius, float steps, float red, float green, float blue, float alpha, boolean outline) {
		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getPositionMatrix();
		Matrix3f normal = entry.getNormalMatrix();

		float step = (float) (2 * Math.PI / steps);
		int halfSteps = (int) (Math.PI / step) + 1;
		int totalSteps = (int) (2 * Math.PI / step);

		if (outline) {
			for (int i = 0; i <= totalSteps; i++) {
				float theta = step * i;
				for (int j = 0; j <= totalSteps; j++) {
					float phi = step * j;
					float x = radius * MathHelper.sin(phi) * MathHelper.cos(theta);
					float z = radius * MathHelper.sin(phi) * MathHelper.sin(theta);
					float y = radius * MathHelper.cos(phi);
					Vec3f normalVector = new Vec3f(x, y, z);
					normalVector.normalize();
					builder.vertex(model, x, y, z).color(red, green, blue, alpha).normal(normal, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
				}
			}
			for (int j = 0; j <= totalSteps; j++) {
				float phi = step * j;
				for (int i = 0; i <= totalSteps; i++) {
					float theta = step * i;
					float x = radius * MathHelper.sin(phi) * MathHelper.cos(theta);
					float z = radius * MathHelper.sin(phi) * MathHelper.sin(theta);
					float y = radius * MathHelper.cos(phi);
					Vec3f normalVector = new Vec3f(x, y, z);
					normalVector.normalize();
					builder.vertex(model, x, y, z).color(red, green, blue, alpha).normal(normal, normalVector.getX(), normalVector.getY(), normalVector.getZ()).next();
				}
			}
			return;
		}

		for (int i = 0; i <= totalSteps; i++) {
			float theta = i * step;
			float thetaPrime = theta + step;
			float xb = 0;
			float zb = 0;
			float xbp = 0;
			float zbp = 0;
			float yp = radius;
			for (int j = 0; j <= halfSteps; j++) {
				float phi = j * step;
				float x = radius * MathHelper.sin(phi) * MathHelper.cos(theta);
				float z = radius * MathHelper.sin(phi) * MathHelper.sin(theta);
				float y = radius * MathHelper.cos(phi);
				float xp = radius * MathHelper.sin(phi) * MathHelper.cos(thetaPrime);
				float zp = radius * MathHelper.sin(phi) * MathHelper.sin(thetaPrime);
				builder.vertex(model, xb, yp, zb).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
				builder.vertex(model, xbp, yp, zbp).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
				builder.vertex(model, xp, y, zp).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
				builder.vertex(model, x, y, z).color(red, green, blue, alpha).normal(normal, 0, 0, 0).next();
				xb = x;
				zb = z;
				xbp = xp;
				zbp = zp;
				yp = y;
			}
		}
	}

	private static void renderLines(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		List<LineShapeWrapper> normalLines = LineShapeWrapper.getNormalLines();
		List<LineShapeWrapper> throughLines = LineShapeWrapper.getThroughLines();
		if (!normalLines.isEmpty()) {
			drawLines(tessellator, bufferBuilder, matrices, cameraPos, normalLines);
		}

		if (!throughLines.isEmpty()) {
			RenderSystem.disableDepthTest();
			drawLines(tessellator, bufferBuilder, matrices, cameraPos, throughLines);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawLines(Tessellator tessellator, BufferBuilder builder, MatrixStack matrices, Vec3d cameraPos, List<LineShapeWrapper> lines) {
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
		builder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
		for (LineShapeWrapper line : lines) {
			if (line.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(line.getOutlineWidth());
				addLineToBuffer(builder, matrices, cameraPos, line);
			}
		}
		tessellator.draw();
	}

	private static void addLineToBuffer(BufferBuilder builder, MatrixStack matrices, Vec3d cameraPos, LineShapeWrapper line) {
		Vec3d pos1 = line.getPosition();
		Vec3d pos2 = line.getSecondPosition();
		int red = line.getRed(), green = line.getGreen(), blue = line.getBlue(), alpha = line.getAlpha();

		matrices.push();
		matrices.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
		tilt(matrices, line);
		addLineVertices(builder, matrices, new Vec3f(pos1), new Vec3f(pos2), red / 255.0F, green / 255.0F, blue / 255.0F, alpha / 255.0F);
		matrices.pop();
	}

	private static void addLineVertices(BufferBuilder builder, MatrixStack matrices, Vec3f pos1, Vec3f pos2, float red, float green, float blue, float alpha) {
		float dx = pos1.getX() - pos2.getX();
		float dy = pos1.getY() - pos2.getY();
		float dz = pos1.getZ() - pos2.getZ();
		Vec3f normalVec = new Vec3f(dx, dy, dz);
		normalVec.normalize();

		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getPositionMatrix();
		Matrix3f normal = entry.getNormalMatrix();
		builder.vertex(model, pos1.getX(), pos1.getY(), pos1.getZ()).color(red, green, blue, alpha).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
		builder.vertex(model, pos2.getX(), pos2.getY(), pos2.getZ()).color(red, green, blue, alpha).normal(normal, normalVec.getX(), normalVec.getY(), normalVec.getZ()).next();
	}

	// Renders fake blocks
	// Reference: https://github.com/ch-yx/fabric-carpet/blob/item_shape/src/main/java/carpet/script/utils/ShapesRenderer.java#L283-L403
	public static void renderBlocks(MatrixStack matrices) {
		MinecraftClient client = EssentialUtils.getClient();
		VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

		FakeBlockWrapper.getAllBlocksToRender().forEach(fakeBlock -> {
			renderFakeBlock(client, matrices, fakeBlock, immediate);
		});
		immediate.drawCurrentLayer();
	}

	private static void renderFakeBlock(MinecraftClient client, MatrixStack matrices, FakeBlockWrapper fakeBlock, VertexConsumerProvider.Immediate immediate) {
		if (client.world == null) {
			return;
		}

		Vec3d cameraPos = client.gameRenderer.getCamera().getPos();

		matrices.push();
		matrices.translate(0.5D, 0.5D, 0.5D);
		matrices.translate(
			fakeBlock.blockPos.getX() - cameraPos.x,
			fakeBlock.blockPos.getY() - cameraPos.y,
			fakeBlock.blockPos.getZ() - cameraPos.z
		);
		if (fakeBlock.direction == null) {
			matrices.multiply(client.gameRenderer.getCamera().getRotation());
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
		}
		else {
			switch (fakeBlock.direction) {
				case SOUTH -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
				case EAST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
				case WEST -> matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
				case UP -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
				case DOWN -> matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(-90));
			}
		}
		tilt(matrices, fakeBlock);
		scale(matrices, fakeBlock);

		matrices.translate(-0.5, -0.5, -0.5);

		int light = LightmapTextureManager.pack(
			client.world.getLightLevel(LightType.BLOCK, fakeBlock.blockPos),
			client.world.getLightLevel(LightType.SKY, fakeBlock.blockPos)
		);

		client.getBlockRenderManager().renderBlockAsEntity(fakeBlock.blockState, matrices, immediate, light, OverlayTexture.DEFAULT_UV);

		matrices.pop();
	}

	private static void tilt(MatrixStack matrices, Shape.Tiltable tiltable) {
		if (tiltable.getXTilt() != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(tiltable.getXTilt()));
		}
		if (tiltable.getYTilt() != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(tiltable.getYTilt()));
		}
		if (tiltable.getZTilt() != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(tiltable.getZTilt()));
		}
	}

	private static void scale(MatrixStack matrices, Shape.Scalable scalable) {
		matrices.scale(scalable.getXScale(), scalable.getYScale(), scalable.getZScale());
	}
}
