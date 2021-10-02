package essentialclient.feature;
import essentialclient.gui.clientrule.ClientRules;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.event.client.ClientSpriteRegistryCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.texture.Sprite;
import net.minecraft.fluid.Fluids;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;

import java.util.function.Function;

//Original code by plusls slightly modified by Sensei
public class HighlightLavaSources implements SimpleSynchronousResourceReloadListener {

    public static Sprite lavaSourceFlowSprite;
    public static Sprite lavaSourceStillSprite;
    public static Sprite defaultLavaSourceFlowSprite;
    public static Sprite defaultLavaSourceStillSprite;

    private static final Identifier flowingSpriteId = new Identifier("essentialclient", "block/lava_flow");
    private static final Identifier stillSpriteId = new Identifier("essentialclient", "block/lava_still");

    public static void init() {
        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register((atlasTexture, registry) -> {
            registry.register(flowingSpriteId);
            registry.register(stillSpriteId);
        });
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new HighlightLavaSources());
    }

    @Override
    public void reload(ResourceManager manager) {
        final Function<Identifier, Sprite> atlas = MinecraftClient.getInstance().getSpriteAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE);
        lavaSourceFlowSprite = atlas.apply(flowingSpriteId);
        lavaSourceStillSprite = atlas.apply(stillSpriteId);
        defaultLavaSourceStillSprite = MinecraftClient.getInstance().getBakedModelManager().getBlockModels().getModel(Blocks.LAVA.getDefaultState()).getParticleSprite();
        defaultLavaSourceFlowSprite = ModelLoader.LAVA_FLOW.getSprite();
        FluidRenderHandler lavaSourceRenderHandler = (view, pos, state) -> {
            if (view != null && pos != null && ClientRules.HIGHLIGHT_LAVA_SOURCES.getBoolean()) {
                BlockState blockState = view.getBlockState(pos);
                if (blockState.contains(FluidBlock.LEVEL) && blockState.get(FluidBlock.LEVEL) == 0)
                    return new Sprite[]{lavaSourceStillSprite, lavaSourceFlowSprite};
            }
            return new Sprite[]{defaultLavaSourceStillSprite, defaultLavaSourceFlowSprite};
        };
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.LAVA, lavaSourceRenderHandler);
        FluidRenderHandlerRegistry.INSTANCE.register(Fluids.FLOWING_LAVA, lavaSourceRenderHandler);
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier("lava_reload_listener");
    }
}