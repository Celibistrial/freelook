package freelook.freelook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import freelook.freelook.FreeLookMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.Perspective;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FreeLookConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private boolean isToggle = false;
    private int perspective = 3;

    public synchronized boolean isToggle() {
        return isToggle;
    }

    public synchronized void setToggle(boolean toggle) {
        this.isToggle = toggle;
    }

    public synchronized void setPerspective(int perspective) {
        this.perspective = MathHelper.clamp(perspective, 1, 3);
    }

    public synchronized void nextPerspective(){
        perspective = switch (perspective) {
            case 1 -> 2;
            case 3 -> 1;
            default -> 3;
        };
    }

    public synchronized Perspective getPerspective() {
        return switch (perspective) {
            case 1 -> Perspective.FIRST_PERSON;
            case 2 -> Perspective.THIRD_PERSON_FRONT;
            default -> Perspective.THIRD_PERSON_BACK;
        };
    }

    public void save(){
        var folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        if (!folder.isDirectory() && !folder.mkdirs()){
            FreeLookMod.LOGGER.error("Failed to create missing config folder");
            return;
        }
        var file = new File(folder, "freeLook.json");
        try (var fw = new FileWriter(file)){
            GSON.toJson(this, FreeLookConfig.class, fw);
            FreeLookMod.LOGGER.info("Saved config");
        } catch (Exception e){
            FreeLookMod.LOGGER.error("Failed to write file {}", file.getName(), e);
        }
    }

    public void reset(){
        setToggle(false);
        setPerspective(3);
    }

    public void load(){
        var folder = new File(MinecraftClient.getInstance().runDirectory, "config");
        var file = new File(folder, "freeLook.json");
        if (!file.exists()){
            reset();
            return;
        }
        try (var fr = new FileReader(file)){
            var obj = GSON.fromJson(fr, FreeLookConfig.class);
            setPerspective(obj.perspective);
            setToggle(obj.isToggle);
        } catch (Exception e){
            FreeLookMod.LOGGER.error("Failed to read file {}", file.getName(), e);
        }
    }
}
