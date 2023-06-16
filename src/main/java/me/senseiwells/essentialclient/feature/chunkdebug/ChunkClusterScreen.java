package me.senseiwells.essentialclient.feature.chunkdebug;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.longs.LongSet;
import me.senseiwells.essentialclient.gui.entries.AbstractListEntry;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import me.senseiwells.essentialclient.utils.render.*;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class ChunkClusterScreen extends ChildScreen.Typed<ChunkDebugScreen> {
	private final ChunkCluster cluster;
	private ChunkListWidget chunkWidget;

	public ChunkClusterScreen(ChunkCluster cluster, ChunkDebugScreen parent) {
		super(Texts.CHUNK_CLUSTER_SCREEN, parent);
		this.cluster = cluster;
	}

	@Override
	protected void init() {
		super.init();
		this.chunkWidget = new ChunkListWidget();
		this.addSelectableChild(this.chunkWidget);
		ButtonWidget widget = WidgetHelper.newButton(this.width / 2 - 100, this.height - 27, 200, 20, Texts.DONE, buttonWidget -> this.close());
		this.addDrawableChild(widget);
	}


	@Override
	public void render(RenderContextWrapper wrapper, int mouseX, int mouseY, float delta) {
		this.renderBackground(wrapper.getContext());
		this.chunkWidget.render(wrapper.getContext(), mouseX, mouseY, delta);
		RenderHelper.drawScaledText(wrapper, this.title, this.width / 2, 20, 1.5F, true);

		super.render(wrapper, mouseX, mouseY, delta);
	}

	private class ChunkListWidget extends ElementListWidget<Entry> {
		public ChunkListWidget() {
			super(ChunkClusterScreen.this.client, ChunkClusterScreen.this.width + 45, ChunkClusterScreen.this.height, 43, ChunkClusterScreen.this.height - 32, 20);

			for (LongSet group : ChunkClusterScreen.this.cluster.getGroups()) {
				this.addEntry(new ChunkClusterScreen.Entry(ChunkClusterScreen.this, group));
			}
		}

		@Override
		protected int getScrollbarPositionX() {
			return this.width / 2 + this.getRowWidth() / 2 - 20;
		}

		@Override
		public int getRowWidth() {
			return 300;
		}
	}

	private static class Entry extends AbstractListEntry<Entry> {
		private final ButtonWidget viewButton;
		private final LongSet group;
		private final ChunkPos around;

		public Entry(ChunkClusterScreen parent, LongSet group) {
			this.group = group;
			//#if MC >= 11800
			this.around = new ChunkPos(group.longStream().findAny().orElse(0L));
			//#else
			//$$this.around = new ChunkPos(group.stream().findAny().orElse(0L));
			//#endif
			this.viewButton = WidgetHelper.newButton(0, 0, 75, 20, Texts.VIEW, button -> {
				ChunkGrid.instance.setCentre(this.around.x, this.around.z);
				EssentialUtils.getClient().setScreen(new ChunkDebugScreen(parent.getParent().getParent()));
			});
		}

		//#if MC >= 11700
		@Override
		public List<ButtonWidget> selectableChildren() {
			return this.children();
		}
		//#endif

		@Override
		public List<ButtonWidget> children() {
			return ImmutableList.of(this.viewButton);
		}

		@Override
		public void render(RenderContextWrapper wrapper, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			wrapper.drawTextWithShadow(EssentialUtils.getClient().textRenderer, Texts.literal("Cluster of " + this.group.size() + " chunks around " + this.around), x - 30, (int) (y + entryHeight / 2.0F - 9 / 2.0F), 0xFFFFFF);
			WidgetHelper.setPosition(this.viewButton, x + 190, y);

			this.viewButton.render(wrapper.getContext(), mouseX, mouseY, tickDelta);
		}
	}
}
