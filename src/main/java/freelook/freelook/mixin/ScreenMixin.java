package freelook.freelook.mixin;


import com.google.gson.JsonObject;
import freelook.freelook.client.FreelookmodClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

@Mixin(TitleScreen.class)
public class ScreenMixin extends Screen {
//not using this class anymore

    protected ScreenMixin(Text title)  {
        super(title);

    }
    @Inject(at = @At("RETURN"),method = "initWidgetsNormal")
    private void TitleScreenmix(int y, int spacingY, CallbackInfo ci) throws FileNotFoundException {


        String msg;
        if(FreelookmodClient.isToggle){
            msg = "Freelook is set to toggle";
        }
        else {
            msg = "Freelook is set to hold";
        }
        this.addDrawableChild(new ButtonWidget.Builder(Text.of(msg), (button) -> {
            JsonObject value = new JsonObject();
            if(FreelookmodClient.isToggle){
                FreelookmodClient.isToggle = false;
                File fee = new File("t.txt");

                fee.delete();

            }else {
                FreelookmodClient.isToggle = true;

                try {
                    FileWriter fe = new FileWriter("t.txt");
                    fe.write(".");
                    fe.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
            Screen screen = new TitleScreen();
            assert this.client != null;
            this.client.setScreen(screen);

        }).dimensions(this.width / 2 - 100, y-25, 200, 20).build());
    }
}
