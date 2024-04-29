package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

public class FreelookScreen extends Screen {
    private final FreeLookConfig config;

    public FreelookScreen() {
        super(Text.translatable("freelook.menu.title"));
        this.config = FreeLookMod.config;
    }

    private @NotNull Text getModeText(){
        String key = config.isToggle() ? "freelook.menu.toggle" : "freelook.menu.hold";
        return Text.translatable(key);
    }

    private @NotNull Text getPerspectiveText(){
        var msg = switch (config.getPerspective()) {
            case FIRST_PERSON -> "freelook.menu.perspective.first";
            case THIRD_PERSON_FRONT -> "freelook.menu.perspective.second";
            case THIRD_PERSON_BACK -> "freelook.menu.perspective.third";
        };
        return Text.translatable(msg);
    }

    public void init() {
        config.load();
        this.addDrawableChild(new ButtonWidget.Builder(getModeText(), button -> {
            config.setToggle(!config.isToggle());
            button.setMessage(getModeText());
        }).dimensions(this.width / 2 - 100, this.height / 2 - 100, 200, 20).build());
        this.addDrawableChild(new ButtonWidget.Builder(getPerspectiveText(), button -> {
            config.nextPerspective();
            button.setMessage(getPerspectiveText());
        }).dimensions(this.width / 2 - 100, this.height / 2 - 50, 200, 20).build());
    }

    @Override
    public void removed() {
        config.save();
    }
}
