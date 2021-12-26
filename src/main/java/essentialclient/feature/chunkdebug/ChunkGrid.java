package essentialclient.feature.chunkdebug;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ChunkGrid {

	private int width;
	private int height;
	private int columns;
	private int rows;
	private int startX = 0;
	private int startY = 0;
	private int scale = 10;

	private final DraggablePoint cornerPoint;
	private final Point mouseDown = new Point();
	private final List<String> dimensions;
	private final Map<String, DraggablePoint> dimensionPoints = new LinkedHashMap<>() {{
		put("overworld", null);
		put("the_nether", null);
		put("the_end", null);
	}};

	private boolean panning = false;
	private int dimensionIndex = 0;
	private String selectionText = null;
	private Minimap minimapMode = Minimap.NONE;

	public ChunkGrid(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;
		this.updateRowsAndColumns();
		this.dimensions = this.dimensionPoints.keySet().stream().toList();
		ClientPlayerEntity player = client.player;
		if (player != null) {
			this.cornerPoint = new DraggablePoint(this.getCornerOfCentre(player.chunkX, player.chunkZ));
			String playerDimension = player.world.getRegistryKey().getValue().getPath();
			int dimensionIndex = this.dimensions.indexOf(playerDimension);
			this.dimensionIndex = dimensionIndex == -1 ? 0 : dimensionIndex;
		}
		else {
			this.cornerPoint = new DraggablePoint(getCornerOfCentre(0, 0));
		}
		this.cornerPoint.mainPoint.y += 5;
	}

	private void updateRowsAndColumns() {
		this.rows = (int) Math.ceil((float) this.height / this.scale);
		this.columns = (int) Math.ceil((float) this.width / this.scale);
	}

	@SuppressWarnings("deprecation")
	public void render(int thisX, int thisY, int width, int height) {
		this.width = width;
		this.height = height;
		this.startX = thisX;
		this.startY = thisY;
		this.updateRowsAndColumns();

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		RenderSystem.disableTexture();

		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
		for (ChunkHandler.ChunkData chunkData : ChunkHandler.getChunks(this.getDimension())) {
			if (chunkData.getChunkType() == ChunkType.UNLOADED) {
				continue;
			}
			int x = chunkData.getPosX() - this.cornerPoint.getX();
			int z = chunkData.getPosZ() - this.cornerPoint.getY();
			if (x < 0 || x > this.columns || z < 0 || z > this.rows) {
				continue;
			}
			int cellX = thisX + x * this.scale;
			int cellY = thisY + z * this.scale;
			boolean shouldRenderTicket = chunkData.hasTicketType() && chunkData.getTicketType().hasColour();
			int colour = shouldRenderTicket ? chunkData.getTicketType().colour : chunkData.getChunkType().getColour();
			this.drawBox(bufferBuilder, cellX, cellY, x, z, colour);
		}
		tessellator.draw();

		DraggablePoint selectionPoint = this.getSelectionPoint();
		if (selectionPoint != null) {
			this.drawSelectionBox(tessellator, bufferBuilder, thisX, thisY, selectionPoint.mainPoint);
			this.updateSelectionInfo();
		}
		else {
			this.selectionText = null;
		}

		RenderSystem.enableTexture();
		RenderSystem.shadeModel(GL11.GL_FLAT);
	}

	public void renderMinimap(int width, int height) {
		int minimapWidth = (int) (width * 0.25F);
		int minimapHeight = (int) (height * 0.45F);
		int minimapX = this.getCentreX();
		int minimapY = this.getCentreZ();
		this.render(minimapX, minimapY, minimapWidth, minimapHeight);
	}

	private void drawBox(BufferBuilder bufferBuilder, int cellX, int cellY, int x, int y, int colour) {
		int red = (colour & 0xff0000) >> 16;
		int green = (colour & 0xff00) >> 8;
		int blue = (colour & 0xff);
		float brightness = 0.14F;

		if ((x + y) % 2 == 0) {
			colour = brighten(colour, brightness);
		}

		int red1 = (colour & 0xff0000) >> 16;
		int green1 = (colour & 0xff00) >> 8;
		int blue1 = (colour & 0xff);

		int red2 = (colour & 0xff0000) >> 16;
		int green2 = (colour & 0xff00) >> 8;
		int blue2 = (colour & 0xff);

		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red1, green1, blue1, 255).next();
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red2, green2, blue2, 255).next();
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red1, green1, blue1, 255).next();
	}

	private void drawSelectionBox(Tessellator tessellator, BufferBuilder bufferBuilder, int thisX, int thisY, Point selectionPoint) {
		int red = (-528378 & 0xff0000) >> 16;
		int green = (-528378 & 0xff00) >> 8;
		int blue = (-528378 & 0xff);

		int x = selectionPoint.x;
		int z = selectionPoint.y;
		int scaledX = x * scale;
		int scaledZ = z * scale;
		int cellX = thisX + scaledX;
		int cellY = thisY + scaledZ;

		if (cellX < thisX || cellY < thisY || cellX + scale > thisX + this.width || cellY + scale > thisY + this.height) {
			return;
		}

		bufferBuilder.begin(GL11.GL_LINES, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX, cellY + scale, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX, cellY + scale, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX + scale, cellY + scale, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX + scale, cellY + scale, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX + scale, cellY, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX + scale, cellY, 0).color(red, green, blue, 255).next();
		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 255).next();
		tessellator.draw();
	}

	public boolean onScroll(double mouseX, double mouseY, double amount) {
		if (this.isInBounds(mouseX, mouseY) && !this.panning) {
			if ((amount > 0 && this.scale < 20) || (amount < 0 && this.scale > 1)) {
				this.syncSelectionPointsWithCorner(() -> this.updateCornerScroll(amount));
				return true;
			}
		}
		return false;
	}

	private void updateCornerScroll(double scrollAmount) {
		this.scale += scrollAmount;
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
			}
			else if (newSelectionPoint != null) {
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
			}
			else if (this.panning) {
				int dragX = this.cornerPoint.dragPoint.x - changeX;
				int dragY = this.cornerPoint.dragPoint.y - changeY;
				this.cornerPoint.setLocation(dragX, dragY);
				this.updateSelectionPointDrag(changeX, changeY);
			}
		}
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
			ChunkHandler.ChunkData filterData = new ChunkHandler.ChunkData(chunkPos.x, chunkPos.z, ChunkType.UNLOADED, TicketType.UNKNOWN);
			Optional<ChunkHandler.ChunkData> chunkData = Arrays.stream(ChunkHandler.getChunks(this.getDimension()))
				.filter(c -> c.equals(filterData)).findFirst();
			boolean isEmpty = chunkData.isEmpty();
			String selectionText = "Selected Chunk: X: %d, Z: %s || Status: %s".formatted(
				chunkPos.x,
				chunkPos.z,
				isEmpty ? "Unloaded" : chunkData.get().getChunkType().prettyName
			);
			if (chunkData.isPresent() && chunkData.get().hasTicketType()) {
				selectionText += " || Type: %s".formatted(chunkData.get().getTicketType().prettyName);
			}
			this.selectionText = selectionText;
		}
	}

	public int getCentreX() {
		return this.getCentre().x;
	}

	public int getCentreZ() {
		return this.getCentre().y;
	}

	public String getDimension() {
		return this.dimensions.get(this.dimensionIndex);
	}

	private DraggablePoint getSelectionPoint() {
		return this.dimensionPoints.get(this.getDimension());
	}

	private void setSelectionPoint(DraggablePoint newPoint) {
		this.dimensionPoints.put(this.getDimension(), newPoint);
	}

	public String getSelectionText() {
		return this.selectionText;
	}

	public String getPrettyDimension() {
		return switch (this.getDimension()) {
			case "overworld" -> "The Overworld";
			case "the_nether" -> "The Nether";
			case "the_end" -> "The End";
			default -> "Unknown";
		};
	}

	public void setDimension(World world) {
		int dimensionIndex = this.dimensions.indexOf(world.getRegistryKey().getValue().getPath());
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
		NONE("None"),
		STATIC("Static"),
		FOLLOW("Follow");

		public final String prettyName;

		Minimap(String prettyName) {
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
