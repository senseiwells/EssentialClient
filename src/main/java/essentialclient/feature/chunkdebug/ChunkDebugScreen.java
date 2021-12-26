package essentialclient.feature.chunkdebug;

import com.mojang.blaze3d.systems.RenderSystem;
import essentialclient.EssentialClient;
import essentialclient.feature.ClientKeybinds;
import essentialclient.utils.EssentialUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.lwjgl.opengl.GL11;

public class ChunkDebugScreen extends Screen {
	public static final int
		HEADER_HEIGHT = 20,
		FOOTER_ROW_HEIGHT = 20,
		FOOTER_ROW_PADDING = 5,
		FOOTER_ROW_COUNT = 2,
		FOOTER_HEIGHT = FOOTER_ROW_HEIGHT * FOOTER_ROW_COUNT + FOOTER_ROW_PADDING * (FOOTER_ROW_COUNT + 1);
	public static ChunkGrid chunkGrid;

	private final MinecraftClient client = EssentialUtils.getClient();
	private final Screen parent;
	private NumberFieldWidget xPositionBox;
	private NumberFieldWidget zPositionBox;
	private boolean canClick = false;

	public ChunkDebugScreen(Screen parent) {
		super(new LiteralText("Chunk Debug Screen"));
		this.parent = parent;
	}

	@Override
	public void init(MinecraftClient client, int width, int height) {
		super.init(client, width, height);
		if (chunkGrid == null) {
			chunkGrid = new ChunkGrid(client, width, height);
		}
		EssentialClient.chunkNetHandler.requestChunkData(chunkGrid.getDimension());
		int buttonWidth = (width - FOOTER_ROW_PADDING * 4) / 3 ;
		int buttonHeight = height - FOOTER_ROW_HEIGHT * 3 + FOOTER_ROW_PADDING * 2;
		Text dimensionText = new LiteralText(chunkGrid.getPrettyDimension());
		ButtonWidget dimensionButton = this.addButton(new ButtonWidget(FOOTER_ROW_PADDING, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, dimensionText, button -> {
			chunkGrid.cycleDimension();
			button.setMessage(new LiteralText(chunkGrid.getPrettyDimension()));
			EssentialClient.chunkNetHandler.requestChunkData(chunkGrid.getDimension());
		}));
		this.addButton(new ButtonWidget(buttonWidth + FOOTER_ROW_PADDING * 2, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, new LiteralText("Return to player"), button -> {
			if (this.client.player != null) {
				chunkGrid.setDimension(this.client.player.world);
				dimensionButton.setMessage(new LiteralText(chunkGrid.getPrettyDimension()));
				int chunkX = this.client.player.chunkX;
				int chunkZ = this.client.player.chunkZ;
				chunkGrid.setCentre(chunkX, chunkZ);
				this.xPositionBox.setText(String.valueOf(chunkX));
				this.zPositionBox.setText(String.valueOf(chunkZ));
				EssentialClient.chunkNetHandler.requestChunkData(chunkGrid.getDimension());
			}
		}));
		Text initialMinimapText = new LiteralText("Minimap: %s".formatted(chunkGrid.getMinimapMode().prettyName));
		ButtonWidget minimapButton = this.addButton(new ButtonWidget(buttonWidth * 2 + FOOTER_ROW_PADDING * 3, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, initialMinimapText, button -> {
			chunkGrid.cycleMinimap();
			Text minimapText = new LiteralText("Minimap: %s".formatted(chunkGrid.getMinimapMode().prettyName));
			button.setMessage(minimapText);
		}));
		minimapButton.active = false;
		buttonHeight = height - FOOTER_ROW_HEIGHT * 2 + FOOTER_ROW_PADDING * 3;
		this.xPositionBox = new NumberFieldWidget(this.textRenderer, FOOTER_ROW_PADDING + 28, buttonHeight, buttonWidth - 30, 20, new LiteralText("X"));
		this.xPositionBox.setInitialValue(chunkGrid.getCentreX());
		this.zPositionBox = new NumberFieldWidget(this.textRenderer, buttonWidth + FOOTER_ROW_PADDING * 2 + 28, buttonHeight, buttonWidth - 30, 20, new LiteralText("Z"));
		this.zPositionBox.setInitialValue(chunkGrid.getCentreZ());
		this.addButton(new ButtonWidget(buttonWidth * 2 + FOOTER_ROW_PADDING * 3, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, new LiteralText("Refresh"), button -> {
			if (hasControlDown()) {
				ChunkHandler.clearAllChunks();
				EssentialClient.chunkNetHandler.requestServerRefresh();
			}
			else {
				ChunkHandler.clearAllChunks();
				EssentialClient.chunkNetHandler.requestChunkData(chunkGrid.getDimension());
			}
		}));

		this.addChild(this.xPositionBox);
		this.addChild(this.zPositionBox);
	}

	@Override
	public void tick() {
		super.tick();
		this.xPositionBox.tick();
		this.zPositionBox.tick();
	}

	@Override
	public void onClose() {
		if (chunkGrid.getMinimapMode() == ChunkGrid.Minimap.NONE) {
			EssentialClient.chunkNetHandler.requestChunkData();
			ChunkHandler.clearAllChunks();
		}
		this.client.openScreen(this.parent);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		chunkGrid.render(0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT - FOOTER_HEIGHT);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		this.drawHeaderAndFooterGradient(tessellator, bufferBuilder);
		this.drawHeaderAndFooterTexture(tessellator, bufferBuilder);

		if (chunkGrid.isPanning()) {
			this.xPositionBox.setText(String.valueOf(chunkGrid.getCentreX()));
			this.zPositionBox.setText(String.valueOf(chunkGrid.getCentreZ()));
		}

		this.drawText(matrices, "Chunk Debug Map", this.width / 2, 8, 1, true);
		if (chunkGrid.getSelectionText() != null) {
			this.drawText(matrices, chunkGrid.getSelectionText(), this.width / 2, 30, 1, true);
		}

		this.xPositionBox.render(matrices, mouseX, mouseY, delta);
		this.zPositionBox.render(matrices, mouseX, mouseY, delta);

		int textHeight = this.height - 20;
		int xOffset = FOOTER_ROW_PADDING + 10;
		int zOffset = xPositionBox.getWidth() + 50;

		this.drawText(matrices, "X", xOffset, textHeight, 1.5F, false);
		this.drawText(matrices, "Z", zOffset, textHeight, 1.5F, false);

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void drawText(MatrixStack matrices, String text, int x, int y, float scale, boolean center) {
		matrices.push();
		matrices.translate(x, y, 0.0D);
		matrices.scale(scale, scale, 0.0F);
		if (center) {
			DrawableHelper.drawCenteredText(matrices, this.textRenderer, new LiteralText(text), 0, 0, 0xFFFFFF);
		} else {
			DrawableHelper.drawTextWithShadow(matrices, this.textRenderer, new LiteralText(text), 0, 0, 0xFFFFFF);
		}
		matrices.pop();
	}

	@SuppressWarnings({"PointlessArithmeticExpression", "deprecation"})
	private void drawHeaderAndFooterGradient(Tessellator tessellator, BufferBuilder bufferBuilder) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.shadeModel(GL11.GL_SMOOTH);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);

		// Header gradient
		bufferBuilder.vertex(         0,               HEADER_HEIGHT + 0, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(         0,               HEADER_HEIGHT + 4, 0).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(this.width,               HEADER_HEIGHT + 4, 0).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(this.width,               HEADER_HEIGHT + 0, 0).color(0, 0, 0, 255).next();

		// Footer gradient
		bufferBuilder.vertex(         0, this.height - FOOTER_HEIGHT - 4, 0).color(0, 0, 0,   0).next();
		bufferBuilder.vertex(         0, this.height - FOOTER_HEIGHT - 0, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT - 0, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT - 4, 0).color(0, 0, 0,   0).next();

		tessellator.draw();
	}

	@SuppressWarnings("deprecation")
	private void drawHeaderAndFooterTexture(Tessellator tessellator, BufferBuilder bufferBuilder) {
		this.client.getTextureManager().bindTexture(OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();
		RenderSystem.shadeModel(GL11.GL_FLAT);

		bufferBuilder.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

		// Header
		bufferBuilder.vertex(         0,                           0, 0).color(64, 64, 64, 255).texture(               0,                                   0).next();
		bufferBuilder.vertex(         0,               HEADER_HEIGHT, 0).color(64, 64, 64, 255).texture(               0,                 HEADER_HEIGHT / 32f).next();
		bufferBuilder.vertex(this.width,               HEADER_HEIGHT, 0).color(64, 64, 64, 255).texture(this.width / 32f,                 HEADER_HEIGHT / 32f).next();
		bufferBuilder.vertex(this.width,                           0, 0).color(64, 64, 64, 255).texture(this.width / 32f,                                   0).next();

		// Footer
		bufferBuilder.vertex(         0, this.height - FOOTER_HEIGHT, 0).color(64, 64, 64, 255).texture(               0, (this.height - FOOTER_HEIGHT) / 32f).next();
		bufferBuilder.vertex(         0, this.height                , 0).color(64, 64, 64, 255).texture(               0,                   this.height / 32f).next();
		bufferBuilder.vertex(this.width, this.height                , 0).color(64, 64, 64, 255).texture(this.width / 32f,                   this.height / 32f).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT, 0).color(64, 64, 64, 255).texture(this.width / 32f, (this.height - FOOTER_HEIGHT) / 32f).next();

		tessellator.draw();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (ClientKeybinds.OPEN_CHUNK_DEBUG.getKeyBinding().matchesKey(keyCode, scanCode)) {
			this.onClose();
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return chunkGrid.onScroll(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.canClick = true;
		chunkGrid.onClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.canClick) {
			chunkGrid.onRelease(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		chunkGrid.onDragged(mouseX, mouseY, button);
		return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
	}

	private class NumberFieldWidget extends TextFieldWidget {
		private int lastValidValue;

		private NumberFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
			super(textRenderer, x, y, width, height, text);
		}

		private void setInitialValue(int value) {
			this.lastValidValue = value;
			this.setText(String.valueOf(value));
		}

		private int getValue() {
			return this.lastValidValue;
		}

		@Override
		public void setTextFieldFocused(boolean focused) {
			if (this.isFocused() && !focused) {
				try {
					int newValue = Integer.parseInt(this.getText());
					if (this.lastValidValue != newValue) {
						this.lastValidValue = newValue;
						ChunkDebugScreen.chunkGrid.setCentre(
							ChunkDebugScreen.this.xPositionBox.getValue(),
							ChunkDebugScreen.this.zPositionBox.getValue()
						);
					}
				}
				catch (NumberFormatException e) {
					this.setText(String.valueOf(this.lastValidValue));
				}
			}
			super.setTextFieldFocused(focused);
		}
	}
}
