package essentialclient.mixins.hotbarFade;

import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    /* TODO
    @Inject(method = "renderHotbar", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;color4f(FFFF)V", ordinal = 0, shift = At.Shift.AFTER))
    private void setTransparencyHotbarPre(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.4F);
    }

    @Inject(method = "renderHotbar", at = @At(value = "TAIL"))
    private void setTransparencyHotbarPost(float tickDelta, MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "renderHotbarItem", at = @At("HEAD"))
    private void onRenderItemPre(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        RenderHelper.isRenderingHotBarGui = true;
    }

    @Inject(method = "renderHotbarItem", at = @At("TAIL"))
    private void onRenderItemPost(int x, int y, float tickDelta, PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        RenderHelper.isRenderingHotBarGui = false;
    }

    @Inject(method = "renderExperienceBar", at = @At("HEAD"))
    private void setTransparencyExperiencePre(MatrixStack matrices, int x, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.4F);
    }

    @Inject(method = "renderExperienceBar", at = @At("TAIL"))
    private void setTransparencyExperiencePost(MatrixStack matrices, int x, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "renderStatusBars", at = @At("HEAD"))
    private void setTransparencyStatusPre(MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.4F);
    }

    @Inject(method = "renderStatusBars", at = @At("TAIL"))
    private void setTransparencyStatusPost(MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "renderMountHealth", at = @At("HEAD"))
    private void setTransparencyMountHealthPre(MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.4F);
    }

    @Inject(method = "renderMountHealth", at = @At("TAIL"))
    private void setTransparencyMountHealthPost(MatrixStack matrices, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Inject(method = "renderMountJumpBar", at = @At("HEAD"))
    private void setTransparencyMountJumpPre(MatrixStack matrices, int x, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.4F);
    }

    @Inject(method = "renderMountJumpBar", at = @At("TAIL"))
    private void setTransparencyMountJumpPost(MatrixStack matrices, int x, CallbackInfo ci) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
     */
}
