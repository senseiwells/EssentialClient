package me.senseiwells.essentialclient.feature.chunkdebug;

import com.mojang.blaze3d.systems.RenderSystem;
import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.ChildScreen;
import me.senseiwells.essentialclient.utils.render.RenderHelper;
import me.senseiwells.essentialclient.utils.render.Texts;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ChunkDebugScreen extends ChildScreen {
	public static final int
		HEADER_HEIGHT = 30,
		FOOTER_ROW_HEIGHT = 20,
		FOOTER_ROW_PADDING = 5,
		FOOTER_ROW_COUNT = 2,
		FOOTER_HEIGHT = FOOTER_ROW_HEIGHT * FOOTER_ROW_COUNT + FOOTER_ROW_PADDING * (FOOTER_ROW_COUNT + 1);

	private final MinecraftClient client = EssentialUtils.getClient();
	private NumberFieldWidget xPositionBox;
	private NumberFieldWidget zPositionBox;
	private boolean canClick = false;

	public ChunkDebugScreen(Screen parent) {
		super(Texts.CHUNK_SCREEN, parent);
	}

	@Override
	public void init() {
		if (ChunkGrid.instance == null) {
			ChunkGrid.instance = new ChunkGrid(this.client, this.width, this.height);
		}
		EssentialClient.CHUNK_NET_HANDLER.requestChunkData(ChunkGrid.instance.getDimension());
		int buttonWidth = (this.width - FOOTER_ROW_PADDING * 4) / 3;
		int buttonHeight = this.height - FOOTER_ROW_HEIGHT * 3 + FOOTER_ROW_PADDING * 2;
		Text dimensionText = ChunkGrid.instance.getPrettyDimension();
		ButtonWidget dimensionButton = this.addDrawableChild(new ButtonWidget(FOOTER_ROW_PADDING, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, dimensionText, button -> {
			ChunkGrid.instance.cycleDimension();
			button.setMessage(ChunkGrid.instance.getPrettyDimension());
			EssentialClient.CHUNK_NET_HANDLER.requestChunkData(ChunkGrid.instance.getDimension());
		}));
		this.addDrawableChild(new ButtonWidget(buttonWidth + FOOTER_ROW_PADDING * 2, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, Texts.RETURN_TO_PLAYER, button -> {
			if (this.client.player != null) {
				ChunkGrid.instance.setDimension(this.client.player.world);
				dimensionButton.setMessage(ChunkGrid.instance.getPrettyDimension());
				int chunkX = this.client.player.getChunkPos().x;
				int chunkZ = this.client.player.getChunkPos().z;
				ChunkGrid.instance.setCentre(chunkX, chunkZ);
				this.xPositionBox.setText(String.valueOf(chunkX));
				this.zPositionBox.setText(String.valueOf(chunkZ));
				EssentialClient.CHUNK_NET_HANDLER.requestChunkData(ChunkGrid.instance.getDimension());
			}
		}));
		Text initialMinimapText = ChunkGrid.instance.getMinimapMode().prettyName;
		this.addDrawableChild(new ButtonWidget(buttonWidth * 2 + FOOTER_ROW_PADDING * 3, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, initialMinimapText, button -> {
			ChunkGrid.instance.cycleMinimap();
			button.setMessage(ChunkGrid.instance.getMinimapMode().prettyName);
		}));
		buttonHeight = this.height - FOOTER_ROW_HEIGHT * 2 + FOOTER_ROW_PADDING * 3;
		this.xPositionBox = new NumberFieldWidget(this.textRenderer, FOOTER_ROW_PADDING + 28, buttonHeight, buttonWidth - 30, 20, Texts.X);
		this.xPositionBox.setInitialValue(ChunkGrid.instance.getCentreX());
		this.zPositionBox = new NumberFieldWidget(this.textRenderer, buttonWidth + FOOTER_ROW_PADDING * 2 + 28, buttonHeight, buttonWidth - 30, 20, Texts.Z);
		this.zPositionBox.setInitialValue(ChunkGrid.instance.getCentreZ());
		this.addDrawableChild(new ButtonWidget(buttonWidth * 2 + FOOTER_ROW_PADDING * 3, buttonHeight, buttonWidth, FOOTER_ROW_HEIGHT, Texts.REFRESH, button -> {
			if (hasControlDown()) {
				ChunkHandler.clearAllChunks();
				EssentialClient.CHUNK_NET_HANDLER.requestServerRefresh();
			} else {
				ChunkHandler.clearAllChunks();
				EssentialClient.CHUNK_NET_HANDLER.requestChunkData(ChunkGrid.instance.getDimension());
			}
		}));
		this.addDrawableChild(new ButtonWidget(5, 5, 90, 20, Texts.CHUNK_CLUSTER_SCREEN, button -> {
			this.client.setScreen(new ChunkClusterScreen(ChunkHandler.getChunkCluster(ChunkGrid.instance.getDimension()), this));
		}));

		this.addDrawableChild(this.xPositionBox);
		this.addDrawableChild(this.zPositionBox);
	}

	@Override
	public void resize(MinecraftClient client, int width, int height) {
		if (ChunkGrid.instance != null) {
			ChunkGrid.instance.onResize(width, height - HEADER_HEIGHT - FOOTER_HEIGHT);
		}
		super.resize(client, width, height);
	}

	@Override
	public void tick() {
		super.tick();
		this.xPositionBox.tick();
		this.zPositionBox.tick();
	}

	@Override
	public void close() {
		if (ChunkGrid.instance.getMinimapMode() == ChunkGrid.Minimap.NONE) {
			EssentialClient.CHUNK_NET_HANDLER.requestChunkData();
			ChunkHandler.clearAllChunks();
		}
		super.close();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);

		ChunkGrid.instance.render(0, HEADER_HEIGHT, this.width, this.height - HEADER_HEIGHT - FOOTER_HEIGHT, false);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();

		this.drawHeaderAndFooterGradient(tessellator, bufferBuilder);
		this.drawHeaderAndFooterTexture(tessellator, bufferBuilder);

		if (ChunkGrid.instance.isPanning()) {
			this.xPositionBox.setText(String.valueOf(ChunkGrid.instance.getCentreX()));
			this.zPositionBox.setText(String.valueOf(ChunkGrid.instance.getCentreZ()));
		}

		RenderHelper.drawScaledText(matrices, Texts.CHUNK_SCREEN, this.width / 2, 12, 1.5F, true);
		if (ChunkGrid.instance.getSelectionText() != null) {
			RenderHelper.drawScaledText(matrices, ChunkGrid.instance.getSelectionText(), this.width / 2, HEADER_HEIGHT + 10, 1, true);
		}

		this.xPositionBox.render(matrices, mouseX, mouseY, delta);
		this.zPositionBox.render(matrices, mouseX, mouseY, delta);

		int textHeight = this.height - 20;
		int xOffset = FOOTER_ROW_PADDING + 10;
		int zOffset = this.xPositionBox.getWidth() + 50;

		RenderHelper.drawScaledText(matrices, Texts.X, xOffset, textHeight, 1.5F, false);
		RenderHelper.drawScaledText(matrices, Texts.Z, zOffset, textHeight, 1.5F, false);

		super.render(matrices, mouseX, mouseY, delta);
	}

	private void drawHeaderAndFooterGradient(Tessellator tessellator, BufferBuilder bufferBuilder) {
		RenderSystem.disableTexture();
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

		// Header gradient
		bufferBuilder.vertex(0, HEADER_HEIGHT, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(0, HEADER_HEIGHT + 4, 0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.width, HEADER_HEIGHT + 4, 0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(this.width, HEADER_HEIGHT, 0).color(0, 0, 0, 255).next();

		// Footer gradient
		bufferBuilder.vertex(0, this.height - FOOTER_HEIGHT - 4, 0).color(0, 0, 0, 0).next();
		bufferBuilder.vertex(0, this.height - FOOTER_HEIGHT, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT, 0).color(0, 0, 0, 255).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT - 4, 0).color(0, 0, 0, 0).next();

		tessellator.draw();
	}

	private void drawHeaderAndFooterTexture(Tessellator tessellator, BufferBuilder bufferBuilder) {
		RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
		RenderSystem.setShaderTexture(0, OPTIONS_BACKGROUND_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableTexture();
		RenderSystem.disableBlend();

		bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

		// Header
		bufferBuilder.vertex(0, 0, 0).texture(0, 0).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0, HEADER_HEIGHT, 0).texture(0, HEADER_HEIGHT / 32f).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, HEADER_HEIGHT, 0).texture(this.width / 32f, HEADER_HEIGHT / 32f).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, 0, 0).texture(this.width / 32f, 0).color(64, 64, 64, 255).next();

		// Footer
		bufferBuilder.vertex(0, this.height - FOOTER_HEIGHT, 0).texture(0, (this.height - FOOTER_HEIGHT) / 32f).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(0, this.height, 0).texture(0, this.height / 32f).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height, 0).texture(this.width / 32f, this.height / 32f).color(64, 64, 64, 255).next();
		bufferBuilder.vertex(this.width, this.height - FOOTER_HEIGHT, 0).texture(this.width / 32f, (this.height - FOOTER_HEIGHT) / 32f).color(64, 64, 64, 255).next();

		tessellator.draw();
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		return ChunkGrid.instance.onScroll(mouseX, mouseY, amount);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.canClick = true;
		ChunkGrid.instance.onClicked(mouseX, mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		if (this.canClick) {
			ChunkGrid.instance.onRelease(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		ChunkGrid.instance.onDragged(mouseX, mouseY, button);
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
						ChunkGrid.instance.setCentre(
							ChunkDebugScreen.this.xPositionBox.getValue(),
							ChunkDebugScreen.this.zPositionBox.getValue()
						);
					}
				} catch (NumberFormatException e) {
					this.setText(String.valueOf(this.lastValidValue));
				}
			}
			super.setTextFieldFocused(focused);
		}
	}
}
