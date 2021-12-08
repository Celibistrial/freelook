package freelook.freelook.mixin;


import com.google.gson.JsonObject;
import freelook.freelook.client.FreelookmodClient;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.*;
import java.util.Scanner;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {


    protected TitleScreenMixin(Text title)  {
        super(title);

    }
    @Inject(at = @At("RETURN"),method = "initWidgetsNormal")
    private void TitleScreenmix(int y, int spacingY, CallbackInfo ci) throws FileNotFoundException {

        String msg;
        if(FreelookmodClient.isToggle == true){
            msg = "Freelook is set to toggle";
        }
        else {
            msg = "Freelook is set to hold";
        }
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, y-25, 200, 20, new LiteralText(msg), (button) -> {
            JsonObject value = new JsonObject();
            if(FreelookmodClient.isToggle){
                FreelookmodClient.isToggle = false;


            }else {
                FreelookmodClient.isToggle = true;

            }
            Screen screen = new TitleScreen();
            this.client.setScreen(screen);

        }));
    }
}
