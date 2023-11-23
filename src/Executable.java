import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args) throws Exception{
        int row_size = 15;
        int[] constaints = {3, 4, 2};

        resolution(constaints, row_size);

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

    public static void resolution(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        ats.initEnumeration();
        int[] sol = ats.solve();
        int nb_sols = 0;

        while (sol != null){
            ats.displaySolution(sol, nb_sols);
            nb_sols += 1;
            sol = ats.solve();
        }

        ats.end();

        System.out.println("Number of solutions found : " + nb_sols);
    }
}
