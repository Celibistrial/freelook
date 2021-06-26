package freelookmod.freelookmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class FreelookmodClient implements ClientModInitializer {

    public static boolean isFreeLooking = false;

    @Override
    public void onInitializeClient() {

        KeyBinding freeLook = KeyBindingHelper.registerKeyBinding(new KeyBinding("FreeLook", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "FreeLookMod"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            float yaw_o= 0;
            float pitch_o = 0;
            if(freeLook.isPressed()){
                MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
                isFreeLooking = true;
            }else{
                isFreeLooking = false;
                MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
            }
//        while(freeLook.wasPressed()){
//
//
//            if(isFreeLooking){
//                MinecraftClient.getInstance().options.setPerspective(Perspective.FIRST_PERSON);
//
//                isFreeLooking = false;
//            }
//            else if (!isFreeLooking){
//                MinecraftClient.getInstance().options.setPerspective(Perspective.THIRD_PERSON_BACK);
//
//
//                isFreeLooking = true;
//            }
//        }


        });


    }


}
