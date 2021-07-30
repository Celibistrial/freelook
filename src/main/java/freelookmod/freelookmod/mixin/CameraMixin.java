package freelookmod.freelookmod.mixin;


import freelookmod.freelookmod.CameraControl;
import freelookmod.freelookmod.client.FreelookmodClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Unique
    boolean firsttime = true;

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setRotation(FF)V", ordinal = 0, shift = At.Shift.AFTER))
    public void lockRotation(BlockView focusedBlock, Entity cameraEntity, boolean isThirdPerson, boolean isFrontFacing, float f, CallbackInfo ci) {
        if (!(cameraEntity instanceof ClientPlayerEntity)) return;

        if (FreelookmodClient.isFreeLooking) {
            CameraControl cameraControl = (CameraControl) cameraEntity;
            MinecraftClient client = MinecraftClient.getInstance();

            if (firsttime && client.player != null) {
                cameraControl.setCameraPitch(client.player.pitch);
                cameraControl.setCameraYaw(client.player.yaw);
                firsttime = false;
            }

            this.setRotation(cameraControl.getCameraYaw(), cameraControl.getCameraPitch());
        } else {
            firsttime = true;
        }
    }
}
