package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FreelookScreen extends Screen {
    private final FreeLookConfig config;

    public FreelookScreen() {
        super(Component.translatable("freelook.menu.title"));
        this.config = FreeLookMod.config;
    }

    private @NotNull Component getModeText() {
        String key = config.isToggle() ? "freelook.menu.toggle" : "freelook.menu.hold";
        return Component.translatable(key);
    }

    private @NotNull Component getPerspectiveText() {
        var msg = switch (config.getPerspective()) {
            case FIRST_PERSON -> "freelook.menu.perspective.first";
            case THIRD_PERSON_FRONT -> "freelook.menu.perspective.second";
            case THIRD_PERSON_BACK -> "freelook.menu.perspective.third";
        };
        return Component.translatable(msg);
    }

    @Override
    public void init() {
        config.load();
        this.clearWidgets();

        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;
        int lineHeight = 25;

        this.addRenderableWidget(new Button.Builder(getModeText(), button -> {
            config.setToggle(!config.isToggle());
            button.setMessage(getModeText());
        }).bounds(centerX - 100, baseY, 200, 20).build());

        this.addRenderableWidget(new Button.Builder(getPerspectiveText(), button -> {
            config.nextPerspective();
            button.setMessage(getPerspectiveText());
        }).bounds(centerX - 100, baseY + lineHeight, 200, 20).build());
        this.addRenderableWidget(new Button.Builder(Component.translatable("freelook.menu.disabled_servers"), button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new DisabledServerScreen(this));
        }).bounds(centerX - 100, baseY + 2 * lineHeight, 200, 20).build());


    }


    @Override
    public void removed() {
        config.save();
    }
}
