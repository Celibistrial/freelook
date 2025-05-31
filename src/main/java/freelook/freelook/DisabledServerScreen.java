package freelook.freelook;

import freelook.freelook.config.FreeLookConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class DisabledServerScreen extends Screen {
    private final FreeLookConfig config;
    private final Screen parent;
    private TextFieldWidget ipField;

    public DisabledServerScreen(Screen parent) {
        super(Text.translatable("freelook.menu.disabled_servers"));
        this.parent = parent;
        this.config = FreeLookMod.config;
    }

    @Override
    protected void init() {
        this.clearChildren();
        int centerX = this.width / 2;
        int baseY = this.height / 2 - 100;
        int lineHeight = 25;

        ipField = new TextFieldWidget(this.textRenderer, centerX - 100, baseY, 200, 20, Text.translatable("freelook.menu.enter_ip"));
        this.addSelectableChild(ipField);
        this.addDrawableChild(ipField);

        this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("freelook.menu.add_ip"), button -> {
            String ip = ipField.getText().trim();
            if (!ip.isEmpty() && !config.getBlockList().contains(ip)) {
                config.getBlockList().add(ip);
                config.save();
                ipField.setText("");
                this.init();
            }
        }).dimensions(centerX - 100, baseY + lineHeight, 200, 20).build());

        int listStartY = baseY + 2 * lineHeight;
        int i = 0;
        for (String ip : config.getBlockList()) {
            int y = listStartY + i * lineHeight;

            this.addDrawableChild(new ButtonWidget.Builder(Text.literal(ip), b -> {
            }).dimensions(centerX - 100, y, 140, 20).build());

            this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("freelook.menu.remove"), button -> {
                config.getBlockList().remove(ip);
                config.save();
                this.init();
            }).dimensions(centerX + 45, y, 55, 20).build());

            i++;
        }

        this.addDrawableChild(new ButtonWidget.Builder(Text.translatable("gui.back"), button -> {
            assert this.client != null;
            this.client.setScreen(parent);
        }).dimensions(centerX - 100, this.height - 40, 200, 20).build());
    }


}
