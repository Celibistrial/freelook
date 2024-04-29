package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class FreeLookMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Freelook");
    public static boolean isFreeLooking = false;
    private static Perspective lastPerspective;
    public static final FreeLookConfig config = new FreeLookConfig();
    private KeyBinding freeLookKeyBind;
    private KeyBinding freeLookScreenKeyBind;

    @Override
    public void onInitializeClient() {
        config.load();
        this.freeLookKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.activate", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "freelook.key.category"));
        this.freeLookScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "freelook.key.category"));

        ClientTickEvents.END_CLIENT_TICK.register(this::onTickEnd);
    }

    private void onTickEnd(MinecraftClient client){
        if (freeLookScreenKeyBind.isPressed()) {
            Screen screen = new FreelookScreen();
            client.setScreen(screen);
        }
        if (!config.isToggle()) {
            if (freeLookKeyBind.isPressed()) {
                if (!isFreeLooking) { // only execute when starting to freelook
                    lastPerspective = client.options.getPerspective();

                    // switch from first to third person
                    if (lastPerspective == Perspective.FIRST_PERSON) {
                        client.options.setPerspective(config.getPerspective());
                    }
                    isFreeLooking = true;
                }
            } else if (isFreeLooking) { // only execute when stopping to freelook
                isFreeLooking = false;
                client.options.setPerspective(lastPerspective);
            }
        } else {
            if (freeLookKeyBind.wasPressed()) {
                if (!isFreeLooking) { // only execute when starting to freelook
                    lastPerspective = client.options.getPerspective();

                    // switch from first to third person
                    if (lastPerspective == Perspective.FIRST_PERSON) {
                        client.options.setPerspective(config.getPerspective());
                    }

                    isFreeLooking = true;
                } else if (freeLookKeyBind.isPressed()) {
                    isFreeLooking = false;
                    client.options.setPerspective(lastPerspective);
                }
            }
        }
    }

}
