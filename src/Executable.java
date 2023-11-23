import structs.*;

import java.util.Arrays;

public class Executable {
    public static void main(String[] args) {
        int size = 15;
        int[] constraints = {4, 6};
        resolution(constraints, size);
    }

    public static void resolution(int[] constraints, int size){

        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        ats.initEnumeration();
        int[] sol = null;
        int nb_sols = 0;
        do {
            sol = ats.solve();
            if (sol != null){
                ats.displaySolution(sol, nb_sols);
                nb_sols += 1;
            }
        } while(sol != null);
        ats.end();
    }
}
