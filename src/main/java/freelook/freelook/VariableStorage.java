package freelook.freelook;


import net.minecraft.client.option.Perspective;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VariableStorage {
    public void write(int i) throws IOException {
        PrintWriter writer = new PrintWriter("freelook.txt", StandardCharsets.UTF_8);
        writer.println(i);
        writer.close();
    }

    // 1 is first person , 2 is 2nd person 3 is 3rd person
    public int read() throws IOException {
        File file = new File("freelook.txt");
        if (!file.exists()) {
            return 3;
        }
        Scanner sc = new Scanner(file);
        if (sc.hasNextLine()) {
            return Integer.parseInt(sc.nextLine());
        } else return 3;
    }

    public Perspective getStoredPerspective() throws IOException {
        int i = read();
        return switch (i) {
            case 1 -> Perspective.FIRST_PERSON;
            case 2 -> Perspective.THIRD_PERSON_FRONT;
            default -> Perspective.THIRD_PERSON_BACK;
        };
    }
}
