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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ChunkGrid {

	private int width;
	private int height;
	private int columns;
	private int rows;
	private int startX = 0;
	private int startY = 0;
	private int scale = 10;

	private final Point cornerPoint;
	private final Point cornerDragPoint = new Point();
	private final Point mouseDown = new Point();
	private final Point selectionDragPoint = new Point();
	protected final List<String> dimensions = List.of("overworld", "the_nether", "the_end");

	private Point selectionPoint = null;
	private boolean panning = false;
	private int dimensionIndex = 0;
	protected String selectionText = null;

	public ChunkGrid(MinecraftClient client, int width, int height) {
		this.width = width;
		this.height = height;
		this.updateRowsAndColumns();
		ClientPlayerEntity player = client.player;
		if (player != null) {
			this.cornerPoint = this.getCornerOfCentre(player.chunkX, player.chunkZ);
			String playerDimension = player.world.getRegistryKey().getValue().getPath();
			int dimensionIndex = this.dimensions.indexOf(playerDimension);
			this.dimensionIndex = dimensionIndex == -1 ? 0 : dimensionIndex;
		}
		else {
			this.cornerPoint = getCornerOfCentre(0, 0);
		}
		this.cornerPoint.y += 5;
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
			int x = chunkData.getPosX() - this.cornerPoint.x;
			int z = chunkData.getPosZ() - this.cornerPoint.y;
			if (x < 0 || x > this.columns || z < 0 || z > this.rows) {
				continue;
			}
			int cellX = thisX + x * this.scale;
			int cellY = thisY + z * this.scale;
			this.drawBox(bufferBuilder, cellX, cellY, x, z, chunkData.getChunkType().getColour());
		}
		tessellator.draw();

		if (this.selectionPoint != null) {
			this.drawSelectionBox(tessellator, bufferBuilder, thisX, thisY);
		}

		RenderSystem.enableTexture();
		RenderSystem.shadeModel(GL11.GL_FLAT);
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

		bufferBuilder.vertex(cellX, cellY, 0).color(red, green, blue, 100).next();
		bufferBuilder.vertex(cellX, cellY + this.scale, 0).color(red1, green1, blue1, 100).next();
		bufferBuilder.vertex(cellX + this.scale, cellY + this.scale, 0).color(red2, green2, blue2, 100).next();
		bufferBuilder.vertex(cellX + this.scale, cellY, 0).color(red1, green1, blue1, 100).next();
	}

	private void drawSelectionBox(Tessellator tessellator, BufferBuilder bufferBuilder, int thisX, int thisY) {
		int red = (-528378 & 0xff0000) >> 16;
		int green = (-528378 & 0xff00) >> 8;
		int blue = (-528378 & 0xff);

		int x = this.selectionPoint.x;
		int z = this.selectionPoint.y;
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
				if (this.selectionPoint != null) {
					this.selectionPoint.move(
						this.cornerPoint.x + this.selectionPoint.x,
						this.cornerPoint.y + this.selectionPoint.y
					);
					this.updateCornerScroll(amount);
					this.selectionPoint.move(
						this.selectionPoint.x - this.cornerPoint.x,
						this.selectionPoint.y - this.cornerPoint.y
					);
				}
				else {
					this.updateCornerScroll(amount);
				}
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
			this.cornerDragPoint.setLocation(this.cornerPoint);
			if (this.selectionPoint != null) {
				this.selectionDragPoint.setLocation(this.selectionPoint);
			}
		}
	}

	public void onRelease(double mouseX, double mouseY, int button) {
		if (button == 0 && !this.panning) {
			Point selectedPoint = this.getPointFromPosition(mouseX, mouseY);
			if (this.selectionPoint != null && this.selectionPoint.equals(selectedPoint)) {
				this.selectionPoint = null;
				this.selectionText = null;
			}
			else if (selectedPoint != null) {
				this.selectionPoint = selectedPoint;
				ChunkPos chunkPos = new ChunkPos(this.cornerPoint.x + selectedPoint.x, this.cornerPoint.y + selectedPoint.y);
				ChunkHandler.ChunkData filterData = new ChunkHandler.ChunkData(chunkPos.x, chunkPos.z, ChunkType.UNLOADED);
				Optional<ChunkHandler.ChunkData> chunkData = Arrays.stream(ChunkHandler.getChunks(this.getDimension()))
					.filter(c -> c.equals(filterData)).findFirst();
				this.selectionText = "Selected Chunk: X: %d, Z: %s Status: %s".formatted(
					chunkPos.x,
					chunkPos.z,
					chunkData.isEmpty() ? "Unloaded" : chunkData.get().getChunkType().prettyName
				);
			}
		}
		this.panning = false;
	}

	public void onDragged(double mouseX, double mouseY, int button) {
		if (button == 0 && this.isInBounds(mouseX, mouseY)) {
			int changeX = (int) (mouseX - this.mouseDown.getX()) / this.scale;
			int changeY = (int) (mouseY - this.mouseDown.getY()) / this.scale;
			if (!this.panning && changeX * changeX + changeY * changeY > 10) {
				this.panning = true;
			}
			else if (this.panning) {
				int dragX = (int) (this.cornerDragPoint.getX() - changeX);
				int dragY = (int) (this.cornerDragPoint.getY() - changeY);
				this.cornerPoint.setLocation(dragX, dragY);
				if (this.selectionPoint != null) {
					dragX = (int) (this.selectionDragPoint.getX() + changeX);
					dragY = (int) (this.selectionDragPoint.getY() + changeY);
					this.selectionPoint.setLocation(dragX, dragY);
				}
			}
		}
	}

	public void setCentre(int x, int y) {
		if (this.selectionPoint != null) {
			this.selectionPoint.move(
				this.cornerPoint.x + this.selectionPoint.x,
				this.cornerPoint.y + this.selectionPoint.y
			);
			this.cornerPoint.setLocation(this.getCornerOfCentre(x, y));
			this.selectionPoint.move(
				this.selectionPoint.x - this.cornerPoint.x,
				this.selectionPoint.y - this.cornerPoint.y
			);
		}
		else {
			this.cornerPoint.setLocation(this.getCornerOfCentre(x, y));
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

	private boolean isInBounds(double mouseX, double mouseY) {
		boolean withinX = mouseX > this.startX && mouseX < this.startX + this.width;
		boolean withinY = mouseY > this.startY && mouseY < this.startY + this.height;
		return withinX && withinY;
	}

	private Point getCentre() {
		int centreX = this.cornerPoint.x + this.columns / 2;
		int centreY = this.cornerPoint.y + this.rows / 2;
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
}
