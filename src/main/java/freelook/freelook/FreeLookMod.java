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
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.option.KeyBinding.*;


@Environment(EnvType.CLIENT)
public class FreeLookMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Freelook");
    public static final FreeLookConfig config = new FreeLookConfig();
    public static boolean isFreeLooking = false;
    private static Perspective lastPerspective;
    private KeyBinding freeLookKeyBind;
    private KeyBinding freeLookScreenKeyBind;
    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of("freelook", "freelook"));


    @Override
    public void onInitializeClient() {
        config.load();
        this.freeLookKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.activate", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY));
        this.freeLookScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyBinding("freelook.key.menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

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
