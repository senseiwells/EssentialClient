package me.senseiwells.essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.clientscript.impl.*;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.LightType;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.function.Consumer;

public class RenderHelper {
	@SuppressWarnings("deprecation")
	public static final Identifier BLOCK_ATLAS_TEXTURE = SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	public static final RenderLayer CULL_BLOCK_LAYER = RenderLayer.getEntityTranslucentCull(BLOCK_ATLAS_TEXTURE);
	public static final RenderLayer NO_CULL_BLOCK_LAYER = RenderLayer.getEntityTranslucent(BLOCK_ATLAS_TEXTURE);

	public static void drawScaledText(DrawContext context, Text text, int x, int y, float scale, boolean center) {
		MinecraftClient client = EssentialUtils.getClient();
		MatrixStack matrices = context.getMatrices();
		matrices.push();
		matrices.translate(x, y, 0.0D);
		matrices.scale(scale, scale, 0.0F);
		if (center) {
			context.drawCenteredTextWithShadow(client.textRenderer, text, 0, 0, 0xFFFFFF);
		} else {
			context.drawTextWithShadow(client.textRenderer, text, 0, 0, 0xFFFFFF);
		}
		matrices.pop();
	}

	public static void renderAllShapes(MatrixStack matrices) {
		setupArucasRendering();

		renderBoxes(matrices);
		renderSpheres(matrices);
		renderLines(matrices);
		// renderBlocks(matrices);

		resetArucasRendering();
	}

	public static void setupArucasRendering() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.depthMask(false);
		RenderSystem.disableCull();
	}

	public static void resetArucasRendering() {
		RenderSystem.lineWidth(1);
		RenderSystem.enableCull();
		RenderSystem.depthMask(true);
		RenderSystem.disableBlend();
		setPositionColourShader();
	}

	private static void renderBoxes(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		if (ScriptBox.hasRegular()) {
			drawBoxOutlines(tessellator, bufferBuilder, matrices, cameraPos, ScriptBox::forEachRegular);
			drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, ScriptBox::forEachRegular);
		}

		if (ScriptBox.hasIgnoreDepth()) {
			RenderSystem.disableDepthTest();
			drawBoxOutlines(tessellator, bufferBuilder, matrices, cameraPos, ScriptBox::forEachIgnoreDepth);
			drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, ScriptBox::forEachIgnoreDepth);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawBoxOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, Consumer<Consumer<ScriptBox>> forEach) {
		setLineShader();
		startLines(bufferBuilder);
		forEach.accept(box -> {
			if (box.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(box.getOutlineWidth());
				addBoxToBuffer(bufferBuilder, matrices, camera, box, true);
			}
		});
		tessellator.draw();
	}

	private static void drawBoxes(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, Consumer<Consumer<ScriptBox>> forEach) {
		setPositionColourShader();
		startQuads(bufferBuilder, VertexFormats.POSITION_COLOR);
		forEach.accept(box -> {
			addBoxToBuffer(bufferBuilder, matrices, cameraPos, box, false);
		});
		tessellator.draw();
	}

	private static Vec3d getMinimumPos(Vec3d pos1, Vec3d pos2) {
		return new Vec3d(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
	}

	private static void addBoxToBuffer(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, ScriptBox box, boolean outline) {
		Vec3d pos1 = box.getCornerA();
		Vec3d pos2 = box.getCornerB();

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

		if (ScriptSphere.hasRegular()) {
			drawSpheresOutlines(tessellator, bufferBuilder, matrices, cameraPos, ScriptSphere::forEachRegular);
			drawSpheres(tessellator, bufferBuilder, matrices, cameraPos, ScriptSphere::forEachRegular);
		}

		if (ScriptSphere.hasIgnoreDepth()) {
			RenderSystem.disableDepthTest();
			drawSpheresOutlines(tessellator, bufferBuilder, matrices, cameraPos, ScriptSphere::forEachIgnoreDepth);
			drawSpheres(tessellator, bufferBuilder, matrices, cameraPos, ScriptSphere::forEachIgnoreDepth);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawSpheresOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, Consumer<Consumer<ScriptSphere>> forEach) {
		setLineShader();
		startLines(bufferBuilder);
		forEach.accept(sphere -> {
			if (sphere.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(sphere.getOutlineWidth());
				addSphereToBuffer(bufferBuilder, matrices, camera, sphere, true);
			}
		});
		tessellator.draw();
	}

	private static void drawSpheres(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, Consumer<Consumer<ScriptSphere>> forEach) {
		setPositionColourShader();
		startQuads(bufferBuilder, VertexFormats.POSITION_COLOR);
		forEach.accept(sphere -> {
			addSphereToBuffer(bufferBuilder, matrices, cameraPos, sphere, false);
		});
		tessellator.draw();
	}

	private static void addSphereToBuffer(BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, ScriptSphere sphere, boolean outline) {
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
					Vector3f normalVector = new Vector3f(x, y, z);
					normalVector.normalize();
					builder.vertex(model, x, y, z).color(red, green, blue, alpha).normal(normal, normalVector.x, normalVector.y, normalVector.z).next();
				}
			}
			for (int j = 0; j <= totalSteps; j++) {
				float phi = step * j;
				for (int i = 0; i <= totalSteps; i++) {
					float theta = step * i;
					float x = radius * MathHelper.sin(phi) * MathHelper.cos(theta);
					float z = radius * MathHelper.sin(phi) * MathHelper.sin(theta);
					float y = radius * MathHelper.cos(phi);
					Vector3f normalVector = new Vector3f(x, y, z);
					normalVector.normalize();
					builder.vertex(model, x, y, z).color(red, green, blue, alpha).normal(normal, normalVector.x, normalVector.y, normalVector.z).next();
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

		if (ScriptLine.hasRegular()) {
			RenderSystem.enableDepthTest();
			drawLines(tessellator, bufferBuilder, matrices, cameraPos, ScriptLine::forEachRegular);
		}

		if (ScriptLine.hasIgnoreDepth()) {
			RenderSystem.disableDepthTest();
			drawLines(tessellator, bufferBuilder, matrices, cameraPos, ScriptLine::forEachIgnoreDepth);
			RenderSystem.enableDepthTest();
		}
	}

	private static void drawLines(Tessellator tessellator, BufferBuilder builder, MatrixStack matrices, Vec3d cameraPos, Consumer<Consumer<ScriptLine>> forEach) {
		setLineShader();
		startLines(builder);
		forEach.accept(line -> {
			if (line.getOutlineWidth() >= 1) {
				RenderSystem.lineWidth(line.getOutlineWidth());
				addLineToBuffer(builder, matrices, cameraPos, line);
			}
		});
		tessellator.draw();
	}

	private static void addLineToBuffer(BufferBuilder builder, MatrixStack matrices, Vec3d cameraPos, ScriptLine line) {
		Vec3d pos1 = line.getCornerA();
		Vec3d pos2 = line.getCornerB();
		int red = line.getRed(), green = line.getGreen(), blue = line.getBlue(), alpha = line.getAlpha();

		matrices.push();
		matrices.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
		tilt(matrices, line);
		addLineVertices(
			builder, matrices,
			pos1.toVector3f(), pos2.toVector3f(),
			red / 255.0F,
			green / 255.0F,
			blue / 255.0F,
			alpha / 255.0F
		);
		matrices.pop();
	}

	private static void addLineVertices(BufferBuilder builder, MatrixStack matrices, Vector3f pos1, Vector3f pos2, float red, float green, float blue, float alpha) {
		float dx = pos1.x - pos2.x;
		float dy = pos1.y - pos2.y;
		float dz = pos1.z - pos2.z;
		Vector3f normalVec = new Vector3f(dx, dy, dz);
		normalVec.normalize();

		MatrixStack.Entry entry = matrices.peek();
		Matrix4f model = entry.getPositionMatrix();
		Matrix3f normal = entry.getNormalMatrix();
		builder.vertex(model, pos1.x, pos1.y, pos1.z).color(red, green, blue, alpha).normal(normal, normalVec.x, normalVec.y, normalVec.z).next();
		builder.vertex(model, pos2.x, pos2.y, pos2.z).color(red, green, blue, alpha).normal(normal, normalVec.x, normalVec.y, normalVec.z).next();
	}

	public static void renderBlocks(MatrixStack matrices) {
		MinecraftClient client = EssentialUtils.getClient();
		VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

		ScriptFakeBlock.forEachRegular(f -> renderFakeBlock(client, matrices, f, immediate));
		immediate.draw(NO_CULL_BLOCK_LAYER);
		immediate.draw(CULL_BLOCK_LAYER);

		// TODO: Fix this :P
		// RenderSystem.clear(GlConst.GL_DEPTH_BUFFER_BIT, MinecraftClient.IS_SYSTEM_MAC);

		ScriptFakeBlock.forEachIgnoreDepth(f -> renderFakeBlock(client, matrices, f, immediate));
		immediate.draw(NO_CULL_BLOCK_LAYER);
		immediate.draw(CULL_BLOCK_LAYER);
	}

	private static void renderFakeBlock(MinecraftClient client, MatrixStack matrices, ScriptFakeBlock fakeBlock, VertexConsumerProvider.Immediate immediate) {
		if (client.world == null) {
			return;
		}

		Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
		Vec3d pos = fakeBlock.getPosition();

		matrices.push();
		matrices.translate(0.5D, 0.5D, 0.5D);
		matrices.translate(
			pos.getX() - cameraPos.x,
			pos.getY() - cameraPos.y,
			pos.getZ() - cameraPos.z
		);
		if (fakeBlock.getDirection() == null) {
			matrices.multiply(client.gameRenderer.getCamera().getRotation());
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
		} else {
			switch (fakeBlock.getDirection()) {
				case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
				case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270));
				case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
				case UP -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
				case DOWN -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
			}
		}
		tilt(matrices, fakeBlock);
		scale(matrices, fakeBlock);

		matrices.translate(-0.5, -0.5, -0.5);

		BlockPos blockPos = EssentialUtils.vec3dToBlockPos(pos);
		int light = LightmapTextureManager.pack(
			client.world.getLightLevel(LightType.BLOCK, blockPos),
			client.world.getLightLevel(LightType.SKY, blockPos)
		);

		renderBlockAsEntity(fakeBlock.getState(), matrices, immediate, fakeBlock.getAlpha() / 255.0F, light, OverlayTexture.DEFAULT_UV, fakeBlock.shouldCull());

		matrices.pop();
	}

	private static void tilt(MatrixStack matrices, ScriptShape tiltable) {
		if (tiltable.getXTilt() != 0.0F) {
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tiltable.getXTilt()));
		}
		if (tiltable.getYTilt() != 0.0F) {
			matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tiltable.getYTilt()));
		}
		if (tiltable.getZTilt() != 0.0F) {
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tiltable.getZTilt()));
		}
	}

	private static void scale(MatrixStack matrices, ScriptShape scalable) {
		matrices.scale(scalable.getXScale(), scalable.getYScale(), scalable.getZScale());
	}

	public static void renderBlockAsEntity(BlockState state, MatrixStack matrices, VertexConsumerProvider vertexConsumer, float alpha, int light, int overlay, boolean cull) {
		MinecraftClient client = EssentialUtils.getClient();
		BlockRenderManager manager = client.getBlockRenderManager();
		switch (state.getRenderType()) {
			case MODEL -> {
				BakedModel bakedModel = manager.getModel(state);
				int i = client.getBlockColors().getColor(state, null, null, 0);
				float red = (i >> 16 & 0xFF) / 255.0F;
				float green = (i >> 8 & 0xFF) / 255.0F;
				float blue = (i & 0xFF) / 255.0F;
				RenderLayer layer = cull ? CULL_BLOCK_LAYER : NO_CULL_BLOCK_LAYER;
				renderBlock(matrices.peek(), vertexConsumer.getBuffer(layer), state, bakedModel, red, green, blue, alpha, light, overlay);
			}
			// Not supported
			case ENTITYBLOCK_ANIMATED -> manager.renderBlockAsEntity(state, matrices, vertexConsumer, light, overlay);
		}
	}

	public static void renderBlock(MatrixStack.Entry entry, VertexConsumer vertexConsumer, BlockState state, BakedModel bakedModel, float red, float green, float blue, float alpha, int light, int overlay) {
		Random random = Random.create();
		long seed = 42L;
		for (Direction direction : Direction.values()) {
			random.setSeed(seed);
			renderQuads(entry, vertexConsumer, red, green, blue, alpha, bakedModel.getQuads(state, direction, random), light, overlay);
		}
		random.setSeed(seed);
		renderQuads(entry, vertexConsumer, red, green, blue, alpha, bakedModel.getQuads(state, null, random), light, overlay);
	}

	private static void renderQuads(MatrixStack.Entry entry, VertexConsumer vertexConsumer, float red, float green, float blue, float alpha, List<BakedQuad> quads, int light, int overlay) {
		for (BakedQuad bakedQuad : quads) {
			float r = 1.0F;
			float g = 1.0F;
			float b = 1.0F;
			float a = MathHelper.clamp(alpha, 0.0F, 1.0F);
			if (bakedQuad.hasColor()) {
				r = MathHelper.clamp(red, 0.0f, 1.0F);
				g = MathHelper.clamp(green, 0.0f, 1.0F);
				b = MathHelper.clamp(blue, 0.0f, 1.0F);
			}
			vertexConsumerQuad(vertexConsumer, entry, bakedQuad, r, g, b, a, light, overlay);
		}
	}

	private static void vertexConsumerQuad(VertexConsumer consumer, MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, float alpha, int light, int overlay) {
		vertexConsumerQuad(consumer, matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, alpha, new int[]{light, light, light, light}, overlay, false);
	}

	private static void vertexConsumerQuad(VertexConsumer consumer, MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, float alpha, int[] lights, int overlay, @SuppressWarnings("SameParameterValue") boolean useQuadColorData) {
		float[] fs = {brightnesses[0], brightnesses[1], brightnesses[2], brightnesses[3]};
		int[] is = {lights[0], lights[1], lights[2], lights[3]};
		int[] js = quad.getVertexData();
		Vec3i vec3i = quad.getFace().getVector();
		Matrix4f matrix4f = matrixEntry.getPositionMatrix();
		Vector3f vec3f = matrixEntry.getNormalMatrix().transform(new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ()));
		int i = 8;
		int j = js.length / i;
		try (MemoryStack memoryStack = MemoryStack.stackPush()) {
			ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			for (int k = 0; k < j; ++k) {
				float q;
				float p;
				float o;
				float n;
				float m;
				intBuffer.clear();
				intBuffer.put(js, k * 8, 8);
				float f = byteBuffer.getFloat(0);
				float g = byteBuffer.getFloat(4);
				float h = byteBuffer.getFloat(8);
				if (useQuadColorData) {
					float l = (float) (byteBuffer.get(12) & 0xFF) / 255.0f;
					m = (float) (byteBuffer.get(13) & 0xFF) / 255.0f;
					n = (float) (byteBuffer.get(14) & 0xFF) / 255.0f;
					o = l * fs[k] * red;
					p = m * fs[k] * green;
					q = n * fs[k] * blue;
				} else {
					o = fs[k] * red;
					p = fs[k] * green;
					q = fs[k] * blue;
				}
				int r = is[k];
				m = byteBuffer.getFloat(16);
				n = byteBuffer.getFloat(20);
				Vector4f vector4f = matrix4f.transform(new Vector4f(f, g, h, 1.0F));
				consumer.vertex(vector4f.x, vector4f.y, vector4f.z, o, p, q, alpha, m, n, overlay, r, vec3f.x, vec3f.y, vec3f.z);
			}
		}
	}

	public static void startLines(BufferBuilder builder) {
		builder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
	}

	public static void startDebugLines(BufferBuilder builder, VertexFormat format) {
		builder.begin(VertexFormat.DrawMode.DEBUG_LINES, format);
	}

	public static void startQuads(BufferBuilder builder, VertexFormat format) {
		builder.begin(VertexFormat.DrawMode.QUADS, format);
	}

	public static void setLineShader() {
		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
	}

	public static void setPositionColourShader() {
		RenderSystem.setShader(GameRenderer::getPositionColorProgram);
	}

	public static void setPositionTextureColourShader() {
		RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
	}

	public static void bindTexture(Identifier texture) {
		RenderSystem.setShaderTexture(0, texture);
	}
}
