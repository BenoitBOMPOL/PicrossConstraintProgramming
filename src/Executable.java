import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args){
        int row_size = 15;
        int[] constraints = {4, 3, 5};
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, row_size);
        ats.initEnumeration();
        try {
            System.out.println("Informations on the instance : ");
            System.out.println("\t Instance has " + ats.getConstraints().length + " constraints on a row of length " + ats.getSize() +  ".");
            for (int c_id = 0; c_id < ats.getConstraints().length; c_id++){
                System.out.println("\t Constraint no." + c_id + " : " + ats.get_ith_constraint(c_id));
            }
            int[][] solutions = ats.getAllSolutions();
            int sol_no = 0;
            for (int [] sol : solutions){
                ats.displaySolution(sol, sol_no);
                sol_no++;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
