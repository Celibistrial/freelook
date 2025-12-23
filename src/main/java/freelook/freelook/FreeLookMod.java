package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.CameraType;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.client.KeyMapping.*;


@Environment(EnvType.CLIENT)
public class FreeLookMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Freelook");
    public static final FreeLookConfig config = new FreeLookConfig();
    public static boolean isFreeLooking = false;
    private static CameraType lastPerspective;
    private KeyMapping freeLookKeyBind;
    private KeyMapping freeLookScreenKeyBind;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("freelook", "freelook"));


    @Override
    public void onInitializeClient() {
        config.load();
        this.freeLookKeyBind = KeyBindingHelper.registerKeyBinding(new KeyMapping("freelook.key.activate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY));
        this.freeLookScreenKeyBind = KeyBindingHelper.registerKeyBinding(new KeyMapping("freelook.key.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerData server = Minecraft.getInstance().getCurrentServer();
            if (server != null) {
                String currentIP = server.ip.toLowerCase();
                for (String blocked : config.getBlockList()) {
                    if (currentIP.contains(blocked.toLowerCase())) {
                        config.setBlocked(true);
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(this::onTickEnd);
    }

    private void onTickEnd(Minecraft client) {
        if (freeLookScreenKeyBind.isDown()) {
            Screen screen = new FreelookScreen();
            client.setScreen(screen);
        }
        if (!config.isBlocked()) {
            if (!config.isToggle()) {
                if (freeLookKeyBind.isDown() && !isFreeLooking) {
                    startFreeLooking(client);
                } else if (!freeLookKeyBind.isDown() && isFreeLooking) {
                    stopFreeLooking(client);
                }
            } else if (freeLookKeyBind.consumeClick()) {
                if (!isFreeLooking) {
                    startFreeLooking(client);
                } else if (freeLookKeyBind.isDown()) {
                    stopFreeLooking(client);
                }
            }
        }
    }

    private void startFreeLooking(Minecraft client) {
        lastPerspective = client.options.getCameraType();
        // only switch to configured perspective if in first person, looks weird otherwise
        if (lastPerspective == CameraType.FIRST_PERSON) {
            client.options.setCameraType(config.getPerspective());
        }
        isFreeLooking = true;
    }

    private void stopFreeLooking(Minecraft client) {
        isFreeLooking = false;
        client.options.setCameraType(lastPerspective);
    }

}
