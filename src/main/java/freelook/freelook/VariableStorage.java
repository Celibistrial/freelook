package freelook.freelook;


import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import net.minecraft.client.option.Perspective;
import org.xml.sax.*;
import org.w3c.dom.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class VariableStorage {
public void write(int i) throws IOException {
    PrintWriter writer = new PrintWriter("freelook.txt", StandardCharsets.UTF_8);
    writer.println(i);
    writer.close();

}
//1 is first person , 2 is 2nd person 3 is 3rd person
    public int read() throws IOException {
        File file = new File("freelook.txt");
        if(!file.exists()){
            return 3;
        }
        Scanner sc = new Scanner(file);
        if(sc.hasNextLine()) {
            return Integer.parseInt(sc.nextLine());
        }else return 3;

    }
    public Perspective getStoredPerspective() throws IOException {
    int i = read();
    switch(i){
        case 1:
            return Perspective.FIRST_PERSON;
        case 2:
            return Perspective.THIRD_PERSON_FRONT;

        case 3:
            return Perspective.THIRD_PERSON_BACK;



        }
        return Perspective.THIRD_PERSON_BACK;
    }
}
