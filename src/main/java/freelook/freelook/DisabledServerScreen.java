package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class DisabledServerScreen extends Screen {
    private final FreeLookConfig config;
    private final Screen parent;
    private EditBox ipField;

    public DisabledServerScreen(Screen parent) {
        super(Component.translatable("freelook.menu.disabled_servers"));
        this.parent = parent;
        this.config = FreeLookMod.config;
    }

    @Override
    protected void init() {
        this.clearWidgets();
        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;
        int lineHeight = 25;

        ipField = new EditBox(this.font, centerX - 100, baseY, 200, 20, Component.translatable("freelook.menu.enter_ip"));
        this.addWidget(ipField);
        this.addRenderableWidget(ipField);

        this.addRenderableWidget(new Button.Builder(Component.translatable("freelook.menu.add_ip"), button -> {
            String ip = ipField.getValue().trim();
            if (!ip.isEmpty() && !config.getBlockList().contains(ip)) {
                config.getBlockList().add(ip);
                config.save();
                ipField.setValue("");
                this.init();
            }
        }).bounds(centerX - 100, baseY + lineHeight, 200, 20).build());

        int listStartY = baseY + 2 * lineHeight;
        int i = 0;
        for (String ip : config.getBlockList()) {
            int y = listStartY + i * lineHeight;

            this.addRenderableWidget(new Button.Builder(Component.literal(ip), b -> {
            }).bounds(centerX - 100, y, 140, 20).build());

            this.addRenderableWidget(new Button.Builder(Component.translatable("freelook.menu.remove"), button -> {
                config.getBlockList().remove(ip);
                config.save();
                this.init();
            }).bounds(centerX + 45, y, 55, 20).build());

            i++;
        }

        this.addRenderableWidget(new Button.Builder(Component.translatable("gui.back"), button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(parent);
        }).bounds(centerX - 100, this.height - 40, 200, 20).build());
    }


}
