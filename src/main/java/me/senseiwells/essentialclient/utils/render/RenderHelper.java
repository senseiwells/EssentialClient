package me.senseiwells.essentialclient.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.clientscript.extensions.BoxShapeWrapperOLD;
import me.senseiwells.essentialclient.clientscript.extensions.FakeBlockWrapper;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.LightType;

import java.util.List;

public class RenderHelper {
	public static void setupArucasBoxRendering() {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.disableTexture();
		RenderSystem.depthMask(false);
		RenderSystem.disableCull();
	}

	public static void resetArucasBoxRendering() {
		RenderSystem.lineWidth(1);
		RenderSystem.enableCull();
		RenderSystem.depthMask(true);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
	}

	public static void renderBoxes(MatrixStack matrices) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
		Vec3d cameraPos = camera.getPos();

		setupArucasBoxRendering();

		List<BoxShapeWrapperOLD> normalBoxes = BoxShapeWrapperOLD.getNormalBoxesToRender();
		List<BoxShapeWrapperOLD> renderThrough = BoxShapeWrapperOLD.getThroughBoxesToRender();

		drawOutlines(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);
		drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, normalBoxes);

		RenderSystem.disableDepthTest();
		drawOutlines(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
		drawBoxes(tessellator, bufferBuilder, matrices, cameraPos, renderThrough);
		RenderSystem.enableDepthTest();

		resetArucasBoxRendering();
	}

	private static void drawOutlines(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d camera, List<BoxShapeWrapperOLD> boxes) {
		bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
		for (BoxShapeWrapperOLD box : boxes) {
			if (box.outlineWidth >= 1) {
				RenderSystem.lineWidth(box.outlineWidth);
				addBoxToBuffer(bufferBuilder, matrices, camera, box.pos1.toBlockPos(), box.pos2.toBlockPos(), box.outlineRed, box.outlineGreen, box.outlineBlue, 255, true);
			}
		}
		tessellator.draw();
	}

	private static void drawBoxes(Tessellator tessellator, BufferBuilder bufferBuilder, MatrixStack matrices, Vec3d cameraPos, List<BoxShapeWrapperOLD> boxes) {
		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		for (BoxShapeWrapperOLD box : boxes) {
			addBoxToBuffer(bufferBuilder, matrices, cameraPos, box.pos1.toBlockPos(), box.pos2.toBlockPos(), box.red, box.green, box.blue, box.opacity, false);
		}
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

	// Renders fake blocks
	// Reference: https://github.com/ch-yx/fabric-carpet/blob/item_shape/src/main/java/carpet/script/utils/ShapesRenderer.java#L283-L403
	public static void renderBlocks(MatrixStack matrices, float tickDelta) {
		MinecraftClient client = EssentialUtils.getClient();
		VertexConsumerProvider.Immediate immediate = client.getBufferBuilders().getEntityVertexConsumers();

		FakeBlockWrapper.getAllBlocksToRender().forEach(fakeBlock -> {
			renderFakeBlock(client, matrices, fakeBlock, immediate, tickDelta);
		});
		immediate.drawCurrentLayer();
	}

	private static void renderFakeBlock(MinecraftClient client, MatrixStack matrices, FakeBlockWrapper fakeBlock, VertexConsumerProvider.Immediate immediate, float tickDelta) {
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
		if (fakeBlock.xTilt != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(fakeBlock.xTilt));
		}
		if (fakeBlock.yTilt != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(fakeBlock.yTilt));
		}
		if (fakeBlock.zTilt != 0.0F) {
			matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(fakeBlock.zTilt));
		}

		matrices.translate(-0.5, -0.5, -0.5);

		int light = LightmapTextureManager.pack(
			client.world.getLightLevel(LightType.BLOCK, fakeBlock.blockPos),
			client.world.getLightLevel(LightType.SKY, fakeBlock.blockPos)
		);

		if (fakeBlock.blockState.getRenderType() == BlockRenderType.MODEL) {
			client.getBlockRenderManager().renderBlockAsEntity(fakeBlock.blockState, matrices, immediate, light, OverlayTexture.DEFAULT_UV);
		}

		if (fakeBlock.blockEntity != null) {
			BlockEntityRenderer<BlockEntity> renderer = client.getBlockEntityRenderDispatcher().get(fakeBlock.blockEntity);
			if (renderer != null) {
				renderer.render(fakeBlock.blockEntity, tickDelta, matrices, immediate, light, OverlayTexture.DEFAULT_UV);
			}
		}

		matrices.pop();
	}
}
