package freelook.freelook.mixin;


import freelook.freelook.CameraOverriddenEntity;
import freelook.freelook.FreeLookMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
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
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;setRotation(FF)V", ordinal = 1, shift = At.Shift.AFTER))
    public void lockRotation(BlockGetter focusedBlock, Entity cameraEntity, boolean isThirdPerson, boolean isFrontFacing, float tickDelta, CallbackInfo ci) {
        if (FreeLookMod.isFreeLooking && cameraEntity instanceof LocalPlayer) {
            CameraOverriddenEntity cameraOverriddenEntity = (CameraOverriddenEntity) cameraEntity;

            if (firstTime && Minecraft.getInstance().player != null) {
                cameraOverriddenEntity.freelook$setCameraPitch(Minecraft.getInstance().player.getXRot());
                cameraOverriddenEntity.freelook$setCameraYaw(Minecraft.getInstance().player.getYRot());
                firstTime = false;
            }
            this.setRotation(cameraOverriddenEntity.freelook$getCameraYaw(), cameraOverriddenEntity.freelook$getCameraPitch());

        }
        if (!FreeLookMod.isFreeLooking && cameraEntity instanceof LocalPlayer) {
            firstTime = true;
        }
    }

}
