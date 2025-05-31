package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class FreeLookMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Freelook");
    public static final FreeLookConfig config = new FreeLookConfig();
    public static boolean isFreeLooking = false;
    private static Perspective lastPerspective;
    private KeyBinding freeLookKeyBind;
    private KeyBinding freeLookScreenKeyBind;
    private KeyBinding freelookFirstKeyBind;
    private KeyBinding freelookThirdFrontKeyBind;
    private KeyBinding freelookThirdBackKeyBind;


    @Override
    public void onInitializeClient() {
        config.load();
        this.freeLookKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.activate", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "freelook.key.category"));
        this.freeLookScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "freelook.key.category"));
        this.freelookFirstKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.first", InputUtil.Type.KEYSM, GLFW.GLFW_KEY_UNKNOWN, "freelook.key.category"));
        this.freelookThirdFrontKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.thirdfront", InputUtil.Type.KEYSM, GLFW.GLFW_KEY_UNKNOWN, "freelook.key.category"));       
        this.freelookThirdBackKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.thirdback", InputUtil.Type.KEYSM, GLFW.GLFW_KEY_UNKNOWN, "freelook.key.category"));



        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
            if (server != null) {
                String currentIP = server.address.toLowerCase();
                for (String blocked : config.getBlockList()) {
                    if (currentIP.contains(blocked.toLowerCase())) {
                        config.setBlocked(true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(this::onTickEnd);
    }

    private void onTickEnd(MinecraftClient client) {
        if (freeLookScreenKeyBind.isPressed()) {
            Screen screen = new FreelookScreen();
            client.setScreen(screen);
        }
        if (freeLookFirstKeyBind.wasPressed()) {
            client.options.setPerspective(1);        
        }
        if (freeLookThirdFrontKeyBind.wasPressed()) {
            client.options.setPerspective(2);        
        }
        if (freeLookThirdBackKeyBind.wasPressed()) {
            client.options.setPerspective(3);        
        }
        if (!config.isBlocked()) {
            if (!config.isToggle()) {
                if (freeLookKeyBind.isPressed() && !isFreeLooking) {
                    startFreeLooking(client);
                } else if (!freeLookKeyBind.isPressed() && isFreeLooking) {
                    stopFreeLooking(client);
                }
            } else if (freeLookKeyBind.wasPressed()) {
                if (!isFreeLooking) {
                    startFreeLooking(client);
                } else if (freeLookKeyBind.isPressed()) {
                    stopFreeLooking(client);
                }
            }
        }
    }

    private void startFreeLooking(MinecraftClient client) {
        lastPerspective = client.options.getPerspective();
        // only switch to configured perspective if in first person, looks weird otherwise
        if (lastPerspective == Perspective.FIRST_PERSON) {
            client.options.setPerspective(config.getPerspective());
        }
        isFreeLooking = true;
    }

    private void stopFreeLooking(MinecraftClient client) {
        isFreeLooking = false;
        client.options.setPerspective(lastPerspective);
    }

}
