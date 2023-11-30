package structs;
import ilog.cp.IloCP;
import ilog.concert.*;

import java.util.Arrays;

public class picrossSlv {
    private IloCP solver;
    private picross instance;

    public int get_nb_rows(){
        return instance.getNb_rows();
    }

    public int get_nb_cols(){
        return instance.getNb_cols();
    }

    public int[] get_constraints_on_row(int i){
        return instance.getRow_constraints(i);
    }

    public int get_nb_constraints_on_row(int i){
        return instance.getRow_constraints(i).length;
    }

    private int[][][][] row_all_solutions;
    // NOTE : row_all_solutions[i][s][k][j] == 1 iff the s-th solution for row i is active on the j-th column for the k-th constraint

    private int[][][][] col_all_solutions;
    // NOTE : col_all_solutions[j][s][k][i] == 1 iff the s-th solution for col j is active on the i-th row for the k-th constraint

    private IloIntVar[][][] row_sols;
    // NOTE : row_sols[i][k][j] == 1 iff the j-th bloc is active for the k-th constraint of row i
    // NOTE : This is what will be returned in the solve function

    private IloIntVar[][][] col_sols;
    // NOTE : col_sols[j][k][i] == 1 iff the i-th bloc is active for the k-th constraint of col j

    public int[][][] get_solutions(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        return ats.getAllSolutions();
    }

    public picrossSlv(String filename){
        // Building the instance using the location
        try {
            this.instance = new picross(filename);
        } catch (Exception e){
            System.out.println("[Exception] : Picross instance creation has failed.");
        }

        // Enumerating all allowed solutions for each row
        this.row_all_solutions = new int[instance.getNb_rows()][][][];
        for (int i = 0; i < instance.getNb_rows(); i++){
            row_all_solutions[i] = get_solutions(instance.getRow_constraints(i), instance.getNb_cols());
        }

        // Enumerating all allowed solutions for each column
        this.col_all_solutions = new int[instance.getNb_cols()][][][];
        for (int j = 0; j < instance.getNb_cols(); j++){
            col_all_solutions[j] = get_solutions(instance.getCol_constraints(j), instance.getNb_rows());
        }

        // Creating the table for blocs for each row
        this.row_sols = new IloIntVar[instance.getNb_rows()][][];
        for (int i = 0; i < instance.getNb_rows(); i++) {
            row_sols[i] = new IloIntVar[instance.getRow_constraints(i).length][instance.getNb_cols()];
        }

        // Creating the table for blocs for each column
        this.col_sols = new IloIntVar[instance.getNb_cols()][][];
        for (int j = 0; j < instance.getNb_rows(); j++) {
            col_sols[j] = new IloIntVar[instance.getCol_constraints(j).length][instance.getNb_rows()];
        }

        stateModel();
    }

    public void stateModel(){
        try{
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            // Initializing solutions for columns
            for (int j = 0; j < instance.getNb_cols(); j++){
                for (int k = 0; k < instance.getCol_constraints(j).length; k++){
                    for (int i = 0; i < instance.getNb_rows(); i++) {
                        col_sols[j][k][i] = solver.intVar(0, 1, "c[j = " + j + ", i = " + i + ", k = " + k + "]");
                    }
                }
            }

            // Initializing solutions for rows
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
                    solver.add(
                            solver.eq(
                                    instance.getRow_constraints(i)[k],
                                    solver.sum(row_sols[i][k])
                            )
                    );

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

        } catch (IloException e){
            System.out.println("[Exception] : Variable creation and/or constraint creation has failed.");
        }
    }

    public void initEnumeration() {
        try {
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public int[][][] solve(){
        int[][][] sol = null;
        try{
            if (solver.next()){
                sol = new int[instance.getNb_rows()][][];
                for (int i = 0; i < instance.getNb_rows(); i++) {
                    sol[i] = new int[instance.getRow_constraints(i).length][instance.getNb_cols()];
                    for (int k = 0; k < instance.getRow_constraints(i).length; k++){
                        for (int j = 0; j < instance.getNb_cols(); j++){
                            sol[i][k][j] = (int) solver.getValue(row_sols[i][k][j]);
                        }
                    }
                }
            }
        } catch (IloException e){
            System.out.println("[Exception] : Solving process has failed.");
            e.printStackTrace();
        }
        return sol;
    }

    public void end(){
        solver.end();
    }

    public static void main(String[] args) {
        picrossSlv bird = new picrossSlv("./picross/bird.px");
        bird.initEnumeration();

        int[][][] bird_sol = bird.solve();
        for (int i = 0; i < bird.get_nb_rows(); i++) {
            System.out.println("Solution for row no. " + i + " under constraints " + Arrays.toString(bird.get_constraints_on_row(i)));
            for (int k = 0; k < bird.get_nb_constraints_on_row(i); k++) {
                System.out.println("\tConstraint no." + k + " : " + Arrays.toString(bird_sol[i][k]));
            }
        }

    }
}
