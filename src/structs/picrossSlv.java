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

            IloIntVar[] index_sol_row = new IloIntVar[instance.getNb_rows()];
            IloIntVar[][][] solution_row = new IloIntVar[instance.getNb_rows()][][];
            for (int i = 0; i < instance.getNb_rows(); i++){
                index_sol_row[i] = solver.intVar(0, get_solutions(instance.getRow_constraints(i), instance.getNb_cols()).length - 1);
                solution_row[i] = new IloIntVar[instance.getRow_constraints(i).length][instance.getNb_cols()];
                // TODO Element constraint on solution_row[i] : solution_row[i] contains the solution at index index_sol_row[i];
                for (int k = 0; k < instance.getRow_constraints(i).length; k++){
                    for (int j = 0; j < instance.getNb_cols(); j++){
                        row_sols[i][k][j] = solution_row[i][k][j];
                    }
                }
            }

            IloIntVar[] index_sol_col = new IloIntVar[instance.getNb_cols()];
            IloIntVar[][][] solution_col = new IloIntVar[instance.getNb_cols()][][];
            for (int j = 0; j < instance.getNb_cols(); j++){
                index_sol_col[j] = solver.intVar(0, get_solutions(instance.getCol_constraints(j), instance.getNb_rows()).length - 1);
                solution_col[j] = new IloIntVar[instance.getCol_constraints(j).length][instance.getNb_rows()];
                // TODO Element constraint on solution_col[j] : solution_col[j] contains the solution at index index_sol_col[j];
                for (int k = 0; k < instance.getCol_constraints(j).length; k++){
                    for (int i = 0; j < instance.getNb_rows(); i++){
                        col_sols[j][k][i] = solution_col[j][k][i];
                    }
                }
            }
            /* TODO : Connecting col_sols[j][k][i] to available solutions for col j
             *      What can be done ?
             *          1. index_sol_col_j (IloIntVar) is the index of the solution in get_solutions(instance.getCol_constraints(j), instance.getNb_rows());
             *              1.1 index_sol_col_j = solver.intVar(0, get_solutions(instance.getCol_constraints(j), instance.getNb_rows()).length - 1)
             *          2. solution_col_j (IloIntVar[][]) is the ELEMENT of get_solutions(instance.getCol_constraints(j), instance.getNb_rows()) of index index_sol_col_j;
             *          2.1 solver.element(...)
             *          3. col_sols[j][k][i] = solution_col_j[k][i] (loop over k and i)
             *              for (int k = 0; k < instance.getCol_constraints(j).length; k++){
             *                  for (int i = 0; i < instance.getNb_rows(); j++){
             *                      col_sols[j][k][i] = solution_col_j[k][i]
             *                  }
             *              }
             */
        } catch (IloException e){
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        picrossSlv bird = new picrossSlv("./picross/bird.px");
    }
}
