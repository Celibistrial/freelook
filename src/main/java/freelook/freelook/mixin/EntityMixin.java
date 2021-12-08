package freelook.freelook.mixin;




import freelook.freelook.CameraOverriddenEntity;
import freelook.freelook.client.FreelookmodClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements CameraOverriddenEntity {
	@Shadow private float yaw;
	@Shadow private float pitch;
	@Unique
	private float cameraPitch;

	@Unique
	private float cameraYaw;

	@Inject(method = "changeLookDirection", at = @At("HEAD"), cancellable = true)
	public void changeCameraLookDirection(double xDelta, double yDelta, CallbackInfo ci) {
		if (!FreelookmodClient.isFreeLooking || !((Object) this instanceof ClientPlayerEntity)) return;

		double pitchDelta = (yDelta * 0.15);
		double yawDelta = (xDelta * 0.15);

		this.cameraPitch = MathHelper.clamp(this.cameraPitch + (float) pitchDelta, -90.0f, 90.0f);
		this.cameraYaw += (float) yawDelta;
	
		ci.cancel();
	}

	@Override
	@Unique
	public float getCameraPitch() {
		return this.cameraPitch;
	}

	@Override
	@Unique
	public float getCameraYaw() {
		return this.cameraYaw;
	}

	@Override
	@Unique
	public void setCameraPitch(float pitch) {
		this.cameraPitch = pitch;
	}

	@Override
	@Unique
	public void setCameraYaw(float yaw) {
		this.cameraYaw = yaw;
	}


	@Unique
	public float getYaw(){return this.yaw;}

	@Unique
	public float getPitch(){return this.pitch;}
}