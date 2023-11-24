import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args){
        String filename = "./picross/bird.px";
        try {
            picrossInstance bird = new picrossInstance(filename);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
