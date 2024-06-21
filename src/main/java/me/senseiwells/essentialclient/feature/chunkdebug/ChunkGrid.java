package me.senseiwells.essentialclient.feature.chunkdebug;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.RenderHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ChunkGrid {
	public static ChunkGrid instance;

	private int width;
	private int height;
	private int columns;
	private int rows;
	private int startX = 0;
	private int startY = 0;
	private int scale = 10;

	private final MinecraftClient client;
	private final DraggablePoint cornerPoint;
	private final Point staticCentrePoint = new Point();
	private final Point minimapCornerPoint = new Point();
	private final Point mouseDown = new Point();
	private final List<RegistryKey<World>> dimensions;
	private final Map<RegistryKey<World>, DraggablePoint> dimensionPoints = new LinkedHashMap<>();

	private boolean panning = false;
	private int dimensionIndex = 0;
	private Text selectionText = null;
	private Minimap minimapMode = Minimap.NONE;

	public ChunkGrid(MinecraftClient client, int width, int height) {
		this.client = client;
		this.width = width;
		this.height = height;
		this.updateRowsAndColumns();

		for (RegistryKey<World> key : EssentialUtils.getNetworkHandler().getWorldKeys()) {
			this.dimensionPoints.put(key, null);
		}

		this.dimensions = this.dimensionPoints.keySet().stream().toList();
		ClientPlayerEntity player = client.player;
		if (player != null) {
			ChunkPos pos = player.getChunkPos();
			this.cornerPoint = new DraggablePoint(this.getCornerOfCentre(pos.x, pos.z));
			int dimensionIndex = this.dimensions.indexOf(player.getEntityWorld().getRegistryKey());
			this.dimensionIndex = dimensionIndex == -1 ? 0 : dimensionIndex;
		} else {
			this.cornerPoint = new DraggablePoint(this.getCornerOfCentre(0, 0));
		}
		this.cornerPoint.mainPoint.y += 5;
	}

	private void updateRowsAndColumns() {
		this.rows = (int) Math.floor((float) this.height / this.scale);
		this.columns = (int) Math.ceil((float) this.width / this.scale);
	}

	public void render(int thisX, int thisY, int width, int height, boolean isMinimap) {
		this.width = width;
		this.height = height;
		this.startX = thisX;
		this.startY = thisY;
		this.updateRowsAndColumns();

		Tessellator tessellator = Tessellator.getInstance();

		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.setPositionColourShader();

		BufferBuilder bufferBuilder = RenderHelper.startQuads(tessellator, VertexFormats.POSITION_COLOR);

		if (isMinimap && ClientRules.CHUNK_DEBUG_MINIMAP_BACKGROUND.getValue()) {
			int thatX = thisX + this.scale * (this.columns + 1);
			int thatY = thisY + this.scale * (this.rows + 1);

			bufferBuilder.vertex(thisX - 5, thisY - 5, 0).color(53, 59, 72, 200);
			bufferBuilder.vertex(thisX - 5, thatY + 5, 0).color(53, 59, 72, 200);
			bufferBuilder.vertex(thatX + 5, thatY + 5, 0).color(53, 59, 72, 200);
			bufferBuilder.vertex(thatX + 5, thisY - 5, 0).color(53, 59, 72, 200);

			bufferBuilder.vertex(thisX, thisY, 0).color(45, 52, 54, 200);
			bufferBuilder.vertex(thisX, thatY, 0).color(45, 52, 54, 200);
			bufferBuilder.vertex(thatX, thatY, 0).color(45, 52, 54, 200);
			bufferBuilder.vertex(thatX, thisY, 0).color(45, 52, 54, 200);
		}

		for (ChunkInfo info : EssentialClient.CHUNK_NET_HANDLER.getChunks(this.getDimension())) {
			if (info.level() == ChunkLevel.UNLOADED && !ClientRules.CHUNK_DEBUG_SHOW_UNLOADED_CHUNKS.getValue()) {
				continue;
			}
			int x = info.getX() - (isMinimap ? this.minimapCornerPoint.x : this.cornerPoint.getX());
			int z = info.getZ() - (isMinimap ? this.minimapCornerPoint.y : this.cornerPoint.getY());
			if (x < 0 || x > this.columns || z < 0 || z > this.rows) {
				continue;
			}
			int cellX = thisX + x * this.scale;
			int cellY = thisY + z * this.scale;
			this.drawBox(bufferBuilder, cellX, cellY, x, z, info.getColour());
		}

		ClientPlayerEntity player = this.client.player;
		if (player != null && this.isPlayerInDimension(player)) {
			ChunkPos playerChunkPos = player.getChunkPos();
			int x = playerChunkPos.x - (isMinimap ? this.minimapCornerPoint.x : this.cornerPoint.getX());
			int z = playerChunkPos.z - (isMinimap ? this.minimapCornerPoint.y : this.cornerPoint.getY());
			if (x >= 0 && x <= this.columns && z >= 0 && z <= this.rows) {
				int cellX = thisX + x * this.scale;
				int cellY = thisY + z * this.scale;

				this.drawCross(tessellator, cellX, cellY, thisX, thisY);
			}
		}

		DraggablePoint selectionPoint = this.getSelectionPoint();
		if (selectionPoint != null) {
			Point drawingPoint = selectionPoint.mainPoint;
			if (isMinimap) {
				int minimapSelectionX = this.cornerPoint.getX() + selectionPoint.getX() - this.minimapCornerPoint.x;
				int minimapSelectionZ = this.cornerPoint.getY() + selectionPoint.getY() - this.minimapCornerPoint.y;
				drawingPoint = new Point(minimapSelectionX, minimapSelectionZ);
			}
			this.drawSelectionBox(tessellator, drawingPoint, thisX, thisY);
			this.updateSelectionInfo();
		} else {
			this.selectionText = null;
		}

		if (!isMinimap) {
			this.updateStaticCentre();
		}

		RenderSystem.disableBlend();
	}

	public void renderMinimap(int width, int height) {
		if (this.minimapMode == Minimap.NONE) {
			return;
		}
		this.width = (int) (width * 0.25F);
		this.height = (int) (height * 0.45F);
		this.updateRowsAndColumns();
		int minimapX = width - this.scale * (this.columns + 1) - 10;
		int minimapY = 10;
		switch (this.minimapMode) {
			case STATIC -> {
				Point minimapCorner = this.getCornerOfCentre(this.staticCentrePoint.x, this.staticCentrePoint.y);
				this.minimapCornerPoint.setLocation(minimapCorner);
			}
			case FOLLOW -> {
				ClientPlayerEntity player = this.client.player;
				if (player != null) {
					if (!this.isPlayerInDimension(player) && !(this.client.currentScreen instanceof ChunkDebugScreen)) {
						this.setDimension(player.getEntityWorld());
						EssentialClient.CHUNK_NET_HANDLER.requestChunkData(this.getDimension());
					}
					ChunkPos pos = player.getChunkPos();
					Point minimapCorner = this.getCornerOfCentre(pos.x, pos.z);
					this.minimapCornerPoint.setLocation(minimapCorner);
				}
			}
		}
		this.render(minimapX, minimapY, this.width, this.height, true);
	}

	private void drawBox(BufferBuilder bufferBuilder, int cellX, int cellY, int x, int y, int colour) {
		if ((x + y) % 2 == 0) {
			colour = brighten(colour, 0.14F);
		}

		int red = (colour & 0xff0000) >> 16;
		int green = (colour & 0xff00) >> 8;
		int blue = (colour & 0xff);

		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red, green, blue, 255);
	}

	private void drawSelectionBox(Tessellator tessellator, Point selectionPoint, int thisX, int thisY) {
		int red = 0xF7;
		int green = 0xF0;
		int blue = 0x06;

		int x = selectionPoint.x;
		int z = selectionPoint.y;
		int scaledX = x * this.scale;
		int scaledZ = z * this.scale;
		int cellX = thisX + scaledX;
		int cellY = thisY + scaledZ;

		if (cellX < thisX || cellY < thisY || cellX + this.scale > thisX + this.width || cellY + this.scale > thisY + this.height) {
			return;
		}

		BufferBuilder bufferBuilder = RenderHelper.startDebugLines(tessellator, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255);
	}

	private void drawCross(Tessellator tessellator, int cellX, int cellY, int x, int y) {
		int red = 0xF7;
		int green = 0xF0;
		int blue = 0x06;

		if (cellX < x || cellY < y || cellX + this.scale > x + this.width || cellY + this.scale > y + this.height) {
			return;
		}

		BufferBuilder bufferBuilder = RenderHelper.startDebugLines(tessellator, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red, green, blue, 255);
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red, green, blue, 255);
	}

	public boolean onScroll(double mouseX, double mouseY, double amount) {
		if (this.isInBounds(mouseX, mouseY) && !this.panning) {
			if ((amount > 0 && this.scale < 20) || (amount < 0 && this.scale > 1)) {
				this.syncSelectionPointsWithCorner(() -> this.updateCornerScroll(MathHelper.clamp(amount, -1, 1)));
				return true;
			}
		}
		return false;
	}

	private void updateCornerScroll(double scrollAmount) {
		this.scale += (int) scrollAmount;
		Point centre = this.getCentre();
		this.updateRowsAndColumns();
		this.cornerPoint.setLocation(this.getCornerOfCentre(centre.x, centre.y));
	}

	public void onClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && this.isInBounds(mouseX, mouseY)) {
			this.mouseDown.setLocation((int) mouseX, (int) mouseY);
			this.cornerPoint.getDragPoint().setLocation(this.cornerPoint.mainPoint);
			this.dimensionPoints.values().forEach(draggablePoint -> {
				if (draggablePoint != null) {
					draggablePoint.dragPoint.setLocation(draggablePoint.mainPoint);
				}
			});
		}
	}

	public void onRelease(double mouseX, double mouseY, int button) {
		if (button == 0 && !this.panning) {
			Point newSelectionPoint = this.getPointFromPosition(mouseX, mouseY);
			DraggablePoint oldSelectionPoint = this.getSelectionPoint();
			if (oldSelectionPoint != null && oldSelectionPoint.mainPoint.equals(newSelectionPoint)) {
				this.setSelectionPoint(null);
				this.selectionText = null;
			} else if (newSelectionPoint != null) {
				this.setSelectionPoint(new DraggablePoint(newSelectionPoint));
				this.updateSelectionInfo();
			}
		}
		this.panning = false;
	}

	public void onDragged(double mouseX, double mouseY, int button) {
		if (button == 0 && this.isInBounds(mouseX, mouseY)) {
			int changeX = (int) (mouseX - this.mouseDown.getX()) / this.scale;
			int changeY = (int) (mouseY - this.mouseDown.getY()) / this.scale;
			if (!this.panning && changeX * changeX + changeY * changeY > 2) {
				this.panning = true;
			} else if (this.panning) {
				int dragX = this.cornerPoint.dragPoint.x - changeX;
				int dragY = this.cornerPoint.dragPoint.y - changeY;
				this.cornerPoint.setLocation(dragX, dragY);
				this.updateSelectionPointDrag(changeX, changeY);
			}
		}
	}

	public void onResize(int newWidth, int newHeight) {
		Point centre = this.getCentre();
		this.width = newWidth;
		this.height = newHeight;
		this.updateRowsAndColumns();
		this.setCentre(centre.x, centre.y);
	}

	public void setCentre(int x, int y) {
		this.syncSelectionPointsWithCorner(() -> this.cornerPoint.setLocation(this.getCornerOfCentre(x, y)));
	}

	private void syncSelectionPointsWithCorner(Runnable cornerFunction) {
		Collection<DraggablePoint> draggablePoints = this.dimensionPoints.values();
		draggablePoints.forEach(draggablePoint -> {
			if (draggablePoint != null) {
				draggablePoint.setLocation(
					this.cornerPoint.getX() + draggablePoint.getX(),
					this.cornerPoint.getY() + draggablePoint.getY()
				);
			}
		});
		cornerFunction.run();
		draggablePoints.forEach(draggablePoint -> {
			if (draggablePoint != null) {
				draggablePoint.setLocation(
					draggablePoint.getX() - this.cornerPoint.getX(),
					draggablePoint.getY() - this.cornerPoint.getY()
				);
			}
		});
	}

	private void updateSelectionPointDrag(int changeX, int changeY) {
		this.dimensionPoints.values().forEach(draggablePoint -> {
			if (draggablePoint != null) {
				int dragX = draggablePoint.dragPoint.x + changeX;
				int dragY = draggablePoint.dragPoint.y + changeY;
				draggablePoint.setLocation(dragX, dragY);
			}
		});
	}

	public void updateSelectionInfo() {
		DraggablePoint selectionPoint = this.getSelectionPoint();
		if (selectionPoint != null) {
			ChunkPos chunkPos = new ChunkPos(this.cornerPoint.getX() + selectionPoint.getX(), this.cornerPoint.getY() + selectionPoint.getY());
			Optional<ChunkInfo> chunkData = EssentialClient.CHUNK_NET_HANDLER.getChunk(this.getDimension(), chunkPos);
			boolean isEmpty = chunkData.isEmpty();
			MutableText selection = Texts.SELECTED_CHUNK.generate("X: " + chunkPos.x + ", Z: " + chunkPos.z);
			selection.append(" || ");
			selection.append(Texts.CHUNK_STATUS.generate((isEmpty ? ChunkLevel.UNLOADED : chunkData.get().level()).getName()));
			if (!isEmpty) {
				ChunkInfo data = chunkData.get();
				if (data.hasTicket()) {
					selection.append(" || ");
					selection.append(Texts.CHUNK_TICKET.generate(data.ticket().getName()));
				}
				selection.append(" || ");
				selection.append(Texts.CHUNK_STAGE.generate(data.status().getName()));
			}
			this.selectionText = selection;
		}
	}

	private void updateStaticCentre() {
		this.staticCentrePoint.setLocation(this.getCentre());
	}

	public int getCentreX() {
		return this.getCentre().x;
	}

	public int getCentreZ() {
		return this.getCentre().y;
	}

	public RegistryKey<World> getDimension() {
		return this.dimensions.get(this.dimensionIndex);
	}

	public boolean isPlayerInDimension(PlayerEntity player) {
		return this.getDimension().equals(player.getEntityWorld().getRegistryKey());
	}

	private DraggablePoint getSelectionPoint() {
		return this.dimensionPoints.get(this.getDimension());
	}

	private void setSelectionPoint(DraggablePoint newPoint) {
		this.dimensionPoints.put(this.getDimension(), newPoint);
	}

	public Text getSelectionText() {
		return this.selectionText;
	}

	public Text getPrettyDimension() {
		RegistryKey<World> dimension = this.getDimension();
		if (dimension == World.OVERWORLD) {
			return Texts.OVERWORLD;
		}
		if (dimension == World.NETHER) {
			return Texts.NETHER;
		}
		if (dimension == World.END) {
			return Texts.END;
		}
		return Text.literal(dimension.getValue().getPath());
	}

	public void setDimension(World world) {
		int dimensionIndex = this.dimensions.indexOf(world.getRegistryKey());
		this.dimensionIndex = dimensionIndex == -1 ? 0 : dimensionIndex;
	}

	public void cycleDimension() {
		if (this.dimensionIndex >= this.dimensions.size() - 1) {
			this.dimensionIndex = 0;
			return;
		}
		this.dimensionIndex++;
	}

	public Minimap getMinimapMode() {
		return this.minimapMode;
	}

	public void cycleMinimap() {
		this.minimapMode = this.minimapMode.getNextMinimap();
	}

	public boolean isPanning() {
		return this.panning;
	}

	private boolean isInBounds(double mouseX, double mouseY) {
		boolean withinX = mouseX > this.startX && mouseX < this.startX + this.width;
		boolean withinY = mouseY > this.startY && mouseY < this.startY + this.height;
		return withinX && withinY;
	}

	private Point getCentre() {
		int centreX = this.cornerPoint.getX() + this.columns / 2;
		int centreY = this.cornerPoint.getY() + this.rows / 2;
		return new Point(centreX, centreY);
	}

	private Point getCornerOfCentre(int centreX, int centreY) {
		int cornerX = centreX - this.columns / 2;
		int cornerY = centreY - this.rows / 2;
		return new Point(cornerX, cornerY);
	}

	private Point getPointFromPosition(double x, double y) {
		if (!this.isInBounds(x, y)) {
			return null;
		}
		y = y - ChunkDebugScreen.HEADER_HEIGHT;
		int pointX = (int) (x / this.scale);
		int pointY = (int) (y / this.scale);
		return new Point(pointX, pointY);
	}

	@SuppressWarnings("SameParameterValue")
	private static int brighten(int colour, float factor) {
		int alpha = (colour & 0xff000000) >>> 24;
		int red = (colour & 0xff0000) >> 16;
		int green = (colour & 0xff00) >> 8;
		int blue = colour & 0xff;
		int mix = (int) ((red + green + blue) * factor / 3);
		red += mix;
		green += mix;
		blue += mix;
		int redOverflow = Integer.max(red - 255, 0);
		int greenOverflow = Integer.max(green - 255, 0);
		int blueOverflow = Integer.max(blue - 255, 0);
		red = Integer.min(red - redOverflow * 3, 255);
		green = Integer.min(green - greenOverflow * 3, 255);
		blue = Integer.min(blue - blueOverflow * 3, 255);
		return (alpha << 24) | (red << 16) | (green << 8) | (blue);
	}

	public enum Minimap {
		NONE(Texts.MINIMAP_NONE),
		STATIC(Texts.MINIMAP_STATIC),
		FOLLOW(Texts.MINIMAP_FOLLOW);

		public final Text prettyName;

		Minimap(Text prettyName) {
			this.prettyName = prettyName;
		}

		private Minimap getNextMinimap() {
			int ordinal = this.ordinal();
			if (this.ordinal() >= 2) {
				return NONE;
			}
			return Minimap.values()[ordinal + 1];
		}
	}

	private static class DraggablePoint {
		private final Point mainPoint;
		private final Point dragPoint = new Point();

		private DraggablePoint(Point main) {
			this.mainPoint = main;
		}

		private int getX() {
			return this.mainPoint.x;
		}

		private int getY() {
			return this.mainPoint.y;
		}

		private Point getDragPoint() {
			return this.dragPoint;
		}

		private void setLocation(Point newLocation) {
			this.mainPoint.setLocation(newLocation);
		}

		private void setLocation(int x, int y) {
			this.mainPoint.setLocation(x, y);
		}
	}
}
