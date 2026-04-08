package freelook.freelook.mixin;

import freelook.freelook.CameraOverriddenEntity;
import freelook.freelook.FreeLookMod;
import net.minecraft.client.CameraType;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Unique
    private float freelook$cachedYaw;

    @Unique
    private boolean freelook$overrodeYaw;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void freelook$applyCameraRelativeMovement(CallbackInfo ci) {
        if (!FreeLookMod.isFreeLooking || !FreeLookMod.config.isBetterThirdPersonControls()) {
            return;
        }
        CameraType activePerspective = FreeLookMod.getFreeLookPerspective();
        if (activePerspective == CameraType.FIRST_PERSON) {
            return;
        }

        LocalPlayer player = (LocalPlayer) (Object) this;
        CameraOverriddenEntity cameraOverriddenEntity = (CameraOverriddenEntity) player;
        freelook$cachedYaw = player.getYRot();
        player.setYRot(cameraOverriddenEntity.freelook$getCameraYaw());
        freelook$overrodeYaw = true;
    }

    @Inject(method = "aiStep", at = @At("RETURN"))
    private void freelook$restoreYawAfterMovement(CallbackInfo ci) {
        if (!freelook$overrodeYaw) {
            return;
        }
        LocalPlayer player = (LocalPlayer) (Object) this;
        player.setYRot(freelook$cachedYaw);
        freelook$overrodeYaw = false;
    }
}