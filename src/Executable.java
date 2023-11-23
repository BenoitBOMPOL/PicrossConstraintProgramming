import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args) throws Exception{
        int row_size = 15;
        int[] constaints = {3, 4, 2};

        AllowedTupleSearcher ats = new AllowedTupleSearcher(constaints, row_size);
        int[][] allSolutions = ats.getAllSolutions();
        for (int sol_id = 0; sol_id < allSolutions.length; sol_id++){
            System.out.println("Solution no." + sol_id + " : " + Arrays.toString(allSolutions[sol_id]));
        }


        /**
            picrossInstance bird = new picrossInstance("./picross/bird.px");
            for (int row_id = 0; row_id < bird.getNb_rows(); row_id ++){
                System.out.println("Allowed tuples for row no." + row_id + " under constraints " + Arrays.toString(bird.getRow_constraints(row_id)) + ".");
                resolution(bird.getRow_constraints(row_id), bird.getNb_cols());
            }

            for (int col_id = 0; col_id < bird.getNb_cols(); col_id++){
                System.out.println("Allowed tuples for col no." + col_id + " under constraints " + Arrays.toString(bird.getCol_constraints(col_id)) + ".");
                resolution(bird.getCol_constraints(col_id), bird.getNb_rows());
            }
        */

    }

}
