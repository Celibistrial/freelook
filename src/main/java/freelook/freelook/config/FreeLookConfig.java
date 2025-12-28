package freelook.freelook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freelook.freelook.FreeLookMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.CameraType;
import net.minecraft.util.Mth;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class FreeLookConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean isToggle = false;
    private int perspective = 3;
    private boolean isBlocked = false;

    private List<String> blockList = new ArrayList<>(List.of(
            "play.hypixel.net",
            "mc.hypixel.net",
            "hypixel.net"
    ));

    public List<String> getBlockList() {
        return blockList;
    }

    public synchronized boolean isToggle() {
        return isToggle;
    }

    public synchronized void setToggle(boolean toggle) {
        this.isToggle = toggle;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public synchronized void nextPerspective() {
        perspective = switch (perspective) {
            case 1 -> 2;
            case 3 -> 1;
            default -> 3;
        };
    }

    public synchronized CameraType getPerspective() {
        return switch (perspective) {
            case 1 -> CameraType.FIRST_PERSON;
            case 2 -> CameraType.THIRD_PERSON_FRONT;
            default -> CameraType.THIRD_PERSON_BACK;
        };
    }

    public synchronized void setPerspective(int perspective) {
        this.perspective = Mth.clamp(perspective, 1, 3);
    }

    public void save() {
        var folder = new File(Minecraft.getInstance().gameDirectory, "config");
        if (!folder.isDirectory() && !folder.mkdirs()) {
            FreeLookMod.LOGGER.error("Failed to create missing config folder");
            return;
        }
        var file = new File(folder, "freeLook.json");
        try (var fw = new FileWriter(file)) {
            GSON.toJson(this, FreeLookConfig.class, fw);
            FreeLookMod.LOGGER.info("Saved config");
        } catch (Exception e) {
            FreeLookMod.LOGGER.error("Failed to write file {}", file.getName(), e);
        }
    }

    public void reset() {
        setToggle(false);
        setPerspective(3);
    }

    public void load() {
        var folder = new File(Minecraft.getInstance().gameDirectory, "config");
        var file = new File(folder, "freeLook.json");
        if (!file.exists()) {
            reset();
            return;
        }
        try (var fr = new FileReader(file)) {
            var obj = GSON.fromJson(fr, FreeLookConfig.class);
            setPerspective(obj.perspective);
            setToggle(obj.isToggle);
            blockList = obj.blockList != null ? new ArrayList<>(obj.blockList) : new ArrayList<>();
        } catch (Exception e) {
            FreeLookMod.LOGGER.error("Failed to read file {}", file.getName(), e);
        }
    }


}
