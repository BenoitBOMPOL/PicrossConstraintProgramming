import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args){
        String filename = "./picross/bird.px";
        try {
            picrossInstanceSlv bird = new picrossInstanceSlv(filename);
            for (int row_id = 0; row_id < bird.getInstance().getNb_rows(); row_id++){
                System.out.println("Allowed tuples for row no." + row_id + " :");
                for (int[] allowed_row : bird.getRow_allowed_tuples(row_id)){
                    System.out.println("\t" + Arrays.toString(allowed_row));
                }
            }

            for (int col_id = 0; col_id < bird.getInstance().getNb_cols(); col_id++){
                System.out.println("Allowed tuples for col no." + col_id + " :");
                for (int[] allowed_col : bird.getCol_allowed_tuples(col_id)){
                    System.out.println("\t" + Arrays.toString(allowed_col));
                }

            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
