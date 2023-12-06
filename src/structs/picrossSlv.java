package structs;

import ilog.cp.IloCP;
import ilog.concert.*;

import java.util.Arrays;

public class picrossSlv extends picross{
    private IloCP solver;
    private IloIntTupleSet[] row_solution;
    private IloIntVar[][] eff_row_solution;
    private IloIntTupleSet[] col_solution;
    private IloIntVar[][] eff_col_solution;

    // private IloIntVar[][] L;
    // private IloIntVar[][] C;

    public int[][] get_solutions(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        return ats.getAllSolutions();
    }


    public picrossSlv(String filename) throws Exception {
        super(filename);

        this.row_solution = new IloIntTupleSet[getNbrows()];
        this.col_solution = new IloIntTupleSet[getNbcols()];
        this.eff_row_solution = new IloIntVar[getNbrows()][getNbcols()];
        this.eff_col_solution = new IloIntVar[getNbcols()][getNbrows()];

        // this.L = new IloIntVar[getNbrows()][getNbcols()];
        // this.C = new IloIntVar[getNbcols()][getNbrows()];

        stateModel();
    }

    public void stateModel(){
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            /* for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    L[i][j] = solver.intVar(0, 1, "L[" + i + ", " + j + "]");
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int i = 0; i < getNbrows(); i++){
                    C[j][i] = solver.intVar(0, 1, "C[" + j + ", " + i + "]");
                }
            } */

            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    eff_row_solution[i][j] = solver.intVar(0,  1);
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int i = 0; i < getNbrows(); i++){
                    eff_col_solution[j][i] = solver.intVar(0, 1);
                }
            }

            for (int i = 0; i < getNbrows(); i++){
                row_solution[i] = solver.intTable(getNbcols());
                for (int[] sol : get_solutions(row_constraints[i], getNbcols())){
                    solver.addTuple(row_solution[i], sol);
                }
                solver.add(solver.allowedAssignments(eff_row_solution[i], row_solution[i]));
            }

            for (int j = 0; j < getNbcols(); j++){
                col_solution[j] = solver.intTable(getNbrows());
                for (int[] sol : get_solutions(col_constraints[j], getNbrows())){
                    solver.addTuple(col_solution[j], sol);
                }
                solver.add(solver.allowedAssignments(eff_col_solution[j], col_solution[j]));
            }

            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    solver.add(solver.eq(eff_row_solution[i][j], eff_col_solution[j][i]));
                }
            }

        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public int[][] solve(){
        int[][] sol = null;
        try {
            if (solver.next()){
                sol = new int[getNbrows()][getNbcols()];
                for (int i = 0; i < getNbrows(); i++){
                    for (int j = 0; j < getNbcols(); j++){
                        sol[i][j] = (int) solver.getValue(eff_row_solution[i][j]);
                    }
                }
            }
        } catch (IloException e){
            e.printStackTrace();
        }
        return sol;
    }
    public void initEnumeration() {
        try {
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void displaysol(int[][] sol){
        // sol.length = nb_rows
        // sol[0].length = nb_cols
        for (int i = 0; i < getNbrows(); i++){
            for (int j = 0; j < getNbcols(); j++){
                if (sol[i][j] == 1){
                    System.out.print("⬛");
                } else {
                    System.out.print("⬜");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        String filename = "./picross/bird.px";
        picrossSlv picross = null;
        try {
            picross = new picrossSlv(filename);
            picross.initEnumeration();
            int[][] sol = picross.solve();
            picross.displaysol(sol);
        } catch (Exception e) {
            System.out.println("[picrossSlv] Instance creation has failed");
            e.printStackTrace();
        }

    }
}
