import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args){
        String filename = "./picross/bird.px";
        try {
            picrossInstanceSlv bird = new picrossInstanceSlv(filename);
            System.out.println("Instance has " + bird.getInstance().getNb_rows() + " rows and " + bird.getInstance().getNb_cols() + " columns.");
            System.out.println("Constraints on rows :");
            for (int row_id = 0; row_id < bird.getInstance().getNb_rows(); row_id++){
                System.out.println("\t Row no." + row_id + " : " + Arrays.toString(bird.getInstance().getRow_constraints(row_id)));
            }
            System.out.println("Constraints on cols :");
            for (int col_id = 0; col_id < bird.getInstance().getNb_cols(); col_id++){
                System.out.println("\t col no." + col_id + " : " + Arrays.toString(bird.getInstance().getCol_constraints(col_id)));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
