package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.CameraType;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
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
    private static CameraType freeLookPerspective;
    private KeyMapping freeLookKeyBind;
    private KeyMapping freeLookFirstPersonKeyBind;
    private KeyMapping freeLookSecondPersonKeyBind;
    private KeyMapping freeLookThirdPersonKeyBind;
    private KeyMapping freeLookScreenKeyBind;
    private KeyMapping currentHoldKeyBind;
    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("freelook", "freelook"));


    @Override
    public void onInitializeClient() {
        config.load();
        this.freeLookKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("freelook.key.activate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, CATEGORY));
        this.freeLookFirstPersonKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("freelook.key.activate_first_person", InputConstants.UNKNOWN.getValue(), CATEGORY));
        this.freeLookSecondPersonKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("freelook.key.activate_second_person", InputConstants.UNKNOWN.getValue(), CATEGORY));
        this.freeLookThirdPersonKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("freelook.key.activate_third_person", InputConstants.UNKNOWN.getValue(), CATEGORY));
        this.freeLookScreenKeyBind = KeyMappingHelper.registerKeyMapping(new KeyMapping("freelook.key.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY));

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            ServerData server = Minecraft.getInstance().getCurrentServer();
            if (server != null) {
                String currentIP = server.ip.toLowerCase();
                for (String blocked : config.getBlockList()) {
                    if (currentIP.contains(blocked.toLowerCase())) {
                        config.setBlocked(true);
                        break;
                    }
                }
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(this::onTickEnd);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            config.setBlocked(false);
        });
    }

    private void onTickEnd(Minecraft client) {
        if (freeLookScreenKeyBind.consumeClick()) {
            Screen screen = new FreelookScreen();
            client.setScreen(screen);
        }
        if (!config.isBlocked()) {
            if (!config.isToggle()) {
                KeyMapping requestedHoldKey = getPressedHoldKeyBind();
                if (!isFreeLooking && requestedHoldKey != null) {
                    startFreeLooking(client, getPerspectiveFromKeyBind(requestedHoldKey));
                    currentHoldKeyBind = requestedHoldKey;
                } else if (isFreeLooking && currentHoldKeyBind != null && !currentHoldKeyBind.isDown()) {
                    stopFreeLooking(client);
                    currentHoldKeyBind = null;
                }
            } else {
                CameraType requestedPerspective = consumeTogglePerspectiveKey();
                if (requestedPerspective != null) {
                    if (!isFreeLooking) {
                        startFreeLooking(client, requestedPerspective);
                    } else if (freeLookPerspective == requestedPerspective) {
                        stopFreeLooking(client);
                    } else {
                        setFreeLookPerspective(client, requestedPerspective);
                    }
                }
            }
        }
    }

    private KeyMapping getPressedHoldKeyBind() {
        if (freeLookFirstPersonKeyBind.isDown()) {
            return freeLookFirstPersonKeyBind;
        }
        if (freeLookSecondPersonKeyBind.isDown()) {
            return freeLookSecondPersonKeyBind;
        }
        if (freeLookThirdPersonKeyBind.isDown()) {
            return freeLookThirdPersonKeyBind;
        }
        if (freeLookKeyBind.isDown()) {
            return freeLookKeyBind;
        }
        return null;
    }

    private CameraType consumeTogglePerspectiveKey() {
        if (freeLookFirstPersonKeyBind.consumeClick()) {
            return CameraType.FIRST_PERSON;
        }
        if (freeLookSecondPersonKeyBind.consumeClick()) {
            return CameraType.THIRD_PERSON_FRONT;
        }
        if (freeLookThirdPersonKeyBind.consumeClick()) {
            return CameraType.THIRD_PERSON_BACK;
        }
        if (freeLookKeyBind.consumeClick()) {
            return config.getPerspective();
        }
        return null;
    }

    private CameraType getPerspectiveFromKeyBind(KeyMapping keyBind) {
        if (keyBind == freeLookFirstPersonKeyBind) {
            return CameraType.FIRST_PERSON;
        }
        if (keyBind == freeLookSecondPersonKeyBind) {
            return CameraType.THIRD_PERSON_FRONT;
        }
        if (keyBind == freeLookThirdPersonKeyBind) {
            return CameraType.THIRD_PERSON_BACK;
        }
        return config.getPerspective();
    }

    private void startFreeLooking(Minecraft client, CameraType requestedPerspective) {
        lastPerspective = client.options.getCameraType();
        setFreeLookPerspective(client, requestedPerspective);
        isFreeLooking = true;
    }

    private void setFreeLookPerspective(Minecraft client, CameraType requestedPerspective) {
        freeLookPerspective = requestedPerspective;
        // only switch if currently in first person, looks weird otherwise
        if (lastPerspective == CameraType.FIRST_PERSON) {
            client.options.setCameraType(requestedPerspective);
        }
    }

    private void stopFreeLooking(Minecraft client) {
        isFreeLooking = false;
        freeLookPerspective = null;
        currentHoldKeyBind = null;
        client.options.setCameraType(lastPerspective);
    }

    public static CameraType getFreeLookPerspective() {
        return freeLookPerspective;
    }
}