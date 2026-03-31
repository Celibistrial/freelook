package freelook.freelook.mixin;

import freelook.freelook.CameraOverriddenEntity;
import freelook.freelook.FreeLookMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    boolean firstTime = true;

    @Shadow
    private Entity entity;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "alignWithEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1, shift = At.Shift.AFTER))
    public void lockRotation(float f, CallbackInfo ci) {
        if (FreeLookMod.isFreeLooking && this.entity instanceof LocalPlayer) {
            CameraOverriddenEntity cameraOverriddenEntity = (CameraOverriddenEntity) this.entity;

            if (firstTime && Minecraft.getInstance().player != null) {
                cameraOverriddenEntity.freelook$setCameraPitch(Minecraft.getInstance().player.getXRot());
                cameraOverriddenEntity.freelook$setCameraYaw(Minecraft.getInstance().player.getYRot());
                firstTime = false;
            }
            this.setRotation(cameraOverriddenEntity.freelook$getCameraYaw(), cameraOverriddenEntity.freelook$getCameraPitch());

        }
        if (!FreeLookMod.isFreeLooking && this.entity instanceof LocalPlayer) {
            firstTime = true;
        }
    }
}