package freelook.freelook;

import freelook.freelook.client.FreeLookModClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FreelookScreen extends Screen {

    public FreelookScreen() {
        super(Text.of("Freelook Config"));
    }

    private Text getModeText(){
        String msg;
        if (FreeLookModClient.isToggle) {
            msg = "Freelook is set to toggle";
        } else {
            msg = "Freelook is set to hold";
        }
        return Text.of(msg);
    }

    private Text getPerspectiveText(int perspective){
        var msg = switch (perspective) {
            case 1 -> "Freelook is set to first person";
            case 2 -> "Freelook is set to second person";
            case 3 -> "Freelook is set to third person";
            default -> "something went wrong click on me to fix it";
        };
        return Text.of(msg);
    }

    public void init() {
        var btn = new ButtonWidget.Builder(getModeText(), button -> {
            if (FreeLookModClient.isToggle) {
                FreeLookModClient.isToggle = false;
                File fee = new File("t.txt");
                fee.delete();
            } else {
                FreeLookModClient.isToggle = true;
                try (var fw = new FileWriter("t.txt")) {
                    fw.write(".");
                    fw.flush();
                } catch (IOException e) {
                    FreeLookMod.LOGGER.error("Failed to write toggle true", e);
                }
            }
            button.setMessage(getModeText());

        }).dimensions(this.width / 2 - 100, this.height / 2 - 100, 200, 20).build();
        this.addDrawableChild(btn);
        var wrapper = new Object() {
            int perspective = 3;
        };
        VariableStorage variableStorage = new VariableStorage();
        try {
            wrapper.perspective = variableStorage.read();
        } catch (IOException e) {
            FreeLookMod.LOGGER.error("Failed to read perspective", e);
        }

        this.addDrawableChild(new ButtonWidget.Builder(getPerspectiveText(wrapper.perspective), button -> {
            if (!(wrapper.perspective >= 3)) {
                wrapper.perspective += 1;
            } else wrapper.perspective = 1;
            try {
                variableStorage.write(wrapper.perspective);
            } catch (IOException e) {
                FreeLookMod.LOGGER.error("Failed to write perspective", e);
            }
            button.setMessage(getPerspectiveText(wrapper.perspective));
        }).dimensions(this.width / 2 - 100, this.height / 2 - 50, 200, 20).build());
    }
}
