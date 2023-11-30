package structs;
import ilog.cp.IloCP;
import ilog.concert.*;

import java.util.Arrays;

public class picrossSlv {
    private IloCP solver;
    private picross instance;

    private IloIntVar[][][] row_sols;
    private IloIntVar[][][] col_sols;

    public int[][][] get_solutions(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        return ats.getAllSolutions();
    }

    public picrossSlv(String filename){
        try {
            this.instance = new picross(filename);
            this.row_sols = new IloIntVar[instance.getNb_rows()][][];
            for (int i = 0; i < instance.getNb_rows(); i++) {
                row_sols[i] = new IloIntVar[instance.getRow_constraints(i).length][instance.getNb_cols()];
            }

            this.col_sols = new IloIntVar[instance.getNb_cols()][][];
            for (int j = 0; j < instance.getNb_rows(); j++) {
                col_sols[j] = new IloIntVar[instance.getCol_constraints(j).length][instance.getNb_rows()];
            }

            stateModel();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stateModel(){
        try{
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            for (int j = 0; j < instance.getNb_cols(); j++){
                for (int k = 0; k < instance.getCol_constraints(j).length; k++){
                    for (int i = 0; i < instance.getNb_rows(); i++) {
                        col_sols[j][k][i] = solver.intVar(0, 1, "c[j = " + j + ", i = " + i + ", k = " + k + "]");
                    }
                }
            }

            for (int i = 0; i < instance.getNb_rows(); i++){
                for (int k = 0; k < instance.getRow_constraints(i).length; k++){
                    for (int j = 0; j < instance.getNb_cols(); j++) {
                        row_sols[i][k][j] = solver.intVar(0, 1, "l[i = " + i + ", j = " + j + ", k = " + k + "]");
                    }
                }
            }

            /// L[i, k, j] <= sum(k' in j-th col constraints) c[j, k', i]
            for (int i = 0; i < instance.getNb_rows(); i++){
                for (int k = 0; k < instance.getRow_constraints(i).length; k++){
                    for (int j = 0; j < instance.getNb_cols(); j++) {
                        int nb_jth_col_csts = instance.getCol_constraints(j).length;
                        IloIntVar[] appliedconstraints_jth_col = new IloIntVar[nb_jth_col_csts];
                        for (int k_pr = 0; k_pr < nb_jth_col_csts; k_pr++){
                            appliedconstraints_jth_col[k_pr] = col_sols[j][k_pr][i];
                        }

                        solver.add(
                                solver.le(
                                        row_sols[i][k][j],
                                        solver.sum(appliedconstraints_jth_col)
                                )
                        );
                    }
                }
            }

            /* TODO : Connecting row_sols[i][k][j] to available solutions for row i
             *
             */

            /* TODO : Connecting col_sols[j][k][i] to available solutions for col j
             *
             */
        } catch (IloException e){
            e.printStackTrace();
        }
    }

    /*
     * TODO :
     *  1. Creating appropriate IloIntTupleSet for matrix corresponding to the chosen row ([][][])
     *      L[i, k, j] : Selecting first i, then k, then j
     *  2. Creating appropriate IloIntTupleSet for matrix corresponding to the chosen col ([][][])
     *      C[j, k, i] : Selecting first j, then k, then i
     *  3. Initializing such IloIntTupleSet
     *  4. Adding, for each i, j, k (in constraints) :
     *      L[i, k, j] <= sum(k' in constraints of column j) C[j, k', i]
     */

    public static void main(String[] args) {
        picrossSlv bird = new picrossSlv("./picross/bird.px");
    }
}
