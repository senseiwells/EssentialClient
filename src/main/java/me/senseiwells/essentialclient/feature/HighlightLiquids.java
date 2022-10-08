package me.senseiwells.essentialclient.feature;

import me.senseiwells.essentialclient.EssentialClient;
import me.senseiwells.essentialclient.rule.ClientRules;
import me.senseiwells.essentialclient.utils.EssentialUtils;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.world.BiomeColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

// Original code by plusls slightly modified by Sensei
public class HighlightLiquids implements SimpleSynchronousResourceReloadListener {
	public static Sprite lavaSourceFlowSprite;
	public static Sprite lavaSourceStillSprite;
	public static Sprite defaultLavaSourceFlowSprite;
	public static Sprite defaultLavaSourceStillSprite;

	public static Sprite waterSourceFlowSprite;
	public static Sprite waterSourceStillSprite;
	public static Sprite defaultWaterSourceFlowSprite;
	public static Sprite defaultWaterSourceStillSprite;

	private static final Identifier LAVA_FLOWING_SPRITE_ID = new Identifier("essentialclient", "block/lava_flow");
	private static final Identifier LAVA_STILL_SPRITE_ID = new Identifier("essentialclient", "block/lava_still");

	private static final Identifier WATER_FLOWING_SPRITE_ID = new Identifier("essentialclient", "block/water_flow");
	private static final Identifier WATER_STILL_SPRITE_ID = new Identifier("essentialclient", "block/water_still");

	public static void load() {
		if (EssentialUtils.isModInstalled("fabric-rendering-fluids-v1") && EssentialUtils.isModInstalled("fabric-textures-v0")) {
			ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
				registry.register(LAVA_FLOWING_SPRITE_ID);
				registry.register(LAVA_STILL_SPRITE_ID);
				registry.register(WATER_FLOWING_SPRITE_ID);
				registry.register(WATER_STILL_SPRITE_ID);
			});
			ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new HighlightLiquids());
		} else {
			EssentialClient.LOGGER.info("Highlight Liquids not functional - no Fabric API");
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		MinecraftClient client = EssentialUtils.getClient();
		Function<Identifier, Sprite> atlas = client.getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);

		lavaSourceFlowSprite = atlas.apply(LAVA_FLOWING_SPRITE_ID);
		lavaSourceStillSprite = atlas.apply(LAVA_STILL_SPRITE_ID);
		defaultLavaSourceStillSprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getParticleSprite();
		defaultLavaSourceFlowSprite = ModelLoader.LAVA_FLOW.getSprite();
		FluidRenderHandler lavaSourceRenderHandler = (view, pos, state) -> {
			if (view != null && pos != null && ClientRules.HIGHLIGHT_LAVA_SOURCES.getValue()) {
				BlockState blockState = view.getBlockState(pos);
				if (blockState.contains(FluidBlock.LEVEL) && blockState.get(FluidBlock.LEVEL) == 0) {
					return new Sprite[]{lavaSourceStillSprite, lavaSourceFlowSprite};
				}
			}
			return new Sprite[]{defaultLavaSourceStillSprite, defaultLavaSourceFlowSprite};
		};
		FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, lavaSourceRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_LAVA, lavaSourceRenderHandler);

		waterSourceFlowSprite = atlas.apply(WATER_FLOWING_SPRITE_ID);
		waterSourceStillSprite = atlas.apply(WATER_STILL_SPRITE_ID);
		defaultWaterSourceStillSprite = client.getBakedModelManager().getBlockModels().getModel(Blocks.WATER.getDefaultState()).getParticleSprite();
		defaultWaterSourceFlowSprite = ModelLoader.WATER_FLOW.getSprite();
		FluidRenderHandler waterSourceRenderHandler = new FluidRenderHandler() {
			@Override
			public Sprite[] getFluidSprites(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				if (view != null && pos != null && ClientRules.HIGHLIGHT_WATER_SOURCES.getValue()) {
					BlockState blockState = view.getBlockState(pos);
					if (blockState.contains(FluidBlock.LEVEL) && blockState.get(FluidBlock.LEVEL) == 0) {
						return new Sprite[]{waterSourceStillSprite, waterSourceFlowSprite};
					}
				}
				return new Sprite[]{defaultWaterSourceStillSprite, defaultWaterSourceFlowSprite};
			}

			@Override
			public int getFluidColor(@Nullable BlockRenderView view, @Nullable BlockPos pos, FluidState state) {
				return (ClientRules.HIGHLIGHT_WATER_SOURCES.getValue() && state.getLevel() == 8) || view == null ? -1 : BiomeColors.getWaterColor(view, pos);
			}
		};
		FluidRenderHandlerRegistry.INSTANCE.register(Fluids.WATER, waterSourceRenderHandler);
		FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_WATER, waterSourceRenderHandler);
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier("lava_reload_listener");
	}
}
