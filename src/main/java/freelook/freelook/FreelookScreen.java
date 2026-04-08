package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.minecraft.client.gui.components.AbstractSliderButton;
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

    private @NotNull Component getControlModeText() {
        String key = config.isBetterThirdPersonControls()
                ? "freelook.menu.style.better_third_person"
                : "freelook.menu.style.classic";
        return Component.translatable(key);
    }

    private @NotNull Component getHeadYawLimitText() {
        if (config.getMaxHeadYaw() >= 360.0f) {
            return Component.translatable("freelook.menu.head_yaw_limit_full");
        }
        return Component.translatable("freelook.menu.head_yaw_limit", (int) config.getMaxHeadYaw());
    }

    private static double toSliderValue(float maxHeadYaw) {
        int step = switch ((int) maxHeadYaw) {
            case 30 -> 0;
            case 60 -> 1;
            case 90 -> 2;
            case 120 -> 3;
            case 360 -> 4;
            default -> 3;
        };
        return step / 4.0d;
    }

    private static float fromSliderValue(double sliderValue) {
        int step = (int) Math.round(sliderValue * 4.0d);
        return switch (step) {
            case 0 -> 30.0f;
            case 1 -> 60.0f;
            case 2 -> 90.0f;
            case 3 -> 120.0f;
            default -> 360.0f;
        };
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

        this.addRenderableWidget(new Button.Builder(getControlModeText(), button -> {
            int nextMode = config.isBetterThirdPersonControls()
                    ? FreeLookConfig.CONTROL_MODE_CLASSIC
                    : FreeLookConfig.CONTROL_MODE_BETTER_THIRD_PERSON;
            config.setControlMode(nextMode);
            button.setMessage(getControlModeText());
        }).bounds(centerX - 100, baseY + 2 * lineHeight, 200, 20).build());

        this.addRenderableWidget(new AbstractSliderButton(centerX - 100, baseY + 3 * lineHeight, 200, 20, getHeadYawLimitText(), toSliderValue(config.getMaxHeadYaw())) {
            @Override
            protected void updateMessage() {
                this.setMessage(getHeadYawLimitText());
            }

            @Override
            protected void applyValue() {
                config.setMaxHeadYaw(fromSliderValue(this.value));
                updateMessage();
            }
        });

        this.addRenderableWidget(new Button.Builder(Component.translatable("freelook.menu.disabled_servers"), button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new DisabledServerScreen(this));
        }).bounds(centerX - 100, baseY + 4 * lineHeight, 200, 20).build());


    }

    @Override
    public void removed() {
        config.save();
    }
}