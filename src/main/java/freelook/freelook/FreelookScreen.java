package freelook.freelook;

import com.google.gson.JsonObject;
import freelook.freelook.client.FreelookmodClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import freelook.freelook.VariableStorage;
import org.lwjgl.system.CallbackI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FreelookScreen extends Screen {
    public FreelookScreen(Text title) {
        super(title);
    }

    public FreelookScreen() {
        super(new LiteralText("Freelook Config"));
    }

    public void init(){
        String msg;
        if(FreelookmodClient.isToggle){
            msg = "Freelook is set to toggle";
        }
        else {
            msg = "Freelook is set to hold";
        }
        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height/2-100, 200, 20, new LiteralText(msg), (button) -> {
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
            Screen screen = new FreelookScreen();
            assert this.client != null;
            this.client.setScreen(screen);

        }));
        var wrapper = new Object(){ int perspective = 3; };
        VariableStorage variableStorage = new VariableStorage();
        try {
            wrapper.perspective = variableStorage.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        msg = switch (wrapper.perspective) {
            case 1 -> "Freelook is set to first person";
            case 2 -> "Freelook is set to second person";
            case 3 -> "Freelook is set to third person";

            default -> "something went wrong click on me to fix it";
        };


        this.addDrawableChild(new ButtonWidget(this.width / 2 - 100, this.height/2-50, 200, 20, new LiteralText(msg), (button) -> {
            if(!(wrapper.perspective >= 3)) {
                wrapper.perspective += 1;
            }else wrapper.perspective = 1;
            try {
                variableStorage.write(wrapper.perspective);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Screen screen = new FreelookScreen();
            assert this.client != null;
            this.client.setScreen(screen);

        }));

    }

    @Override
    public void render(final MatrixStack matrices, final int mouseX, final int mouseY, final float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
