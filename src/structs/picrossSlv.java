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

    public int[][] get_solutions(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        return ats.getAllSolutions();
    }


    public picrossSlv(String filename) throws Exception {
        super(filename);
        this.row_solution = new IloIntTupleSet[getNbrows()];
        this.col_solution = new IloIntTupleSet[getNbcols()];
        this.eff_row_solution = new IloIntVar[getNbrows()][];
        for (int i = 0; i < getNbrows(); i++){
            eff_row_solution[i] = new IloIntVar[getRow_constraints(i).length];
        }

        this.eff_col_solution = new IloIntVar[getNbcols()][];
        for (int j = 0; j < getNbcols(); j++){
            eff_col_solution[j] = new IloIntVar[getCol_constraints(j).length];
        }

        stateModel();
    }

    public void stateModel(){
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            for (int i = 0; i < getNbrows(); i++){
                row_solution[i] = solver.intTable(row_constraints[i].length);
                for (int[] sol : get_solutions(row_constraints[i], getNbcols())){
                    solver.addTuple(row_solution[i], sol);
                }

                solver.add(solver.allowedAssignments(eff_row_solution[i], row_solution[i]));
            }

            for (int j = 0; j < getNbcols(); j++){
                col_solution[j] = solver.intTable(col_constraints[j].length);
                for (int[] sol : get_solutions(col_constraints[j], getNbrows())){
                    solver.addTuple(col_solution[j], sol);
                }
                // HERE
                solver.add(solver.allowedAssignments(eff_col_solution[j], col_solution[j]));
            }

        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public int[][] solve(){
        int[][] sol = null;
        try {
            if (solver.next()){
                sol = new int[getNbrows()][];
                for (int i = 0; i < getNbrows(); i++){
                    sol[i] = new int[getRow_constraints(i).length];
                    for (int k = 0; k < sol[i].length; k++){
                        sol[i][k] = (int) solver.getValue(eff_row_solution[i][k]);
                    }
                }
            }
        } catch (IloException e){
            e.printStackTrace();
        }
        return sol;
    }

    public void show_instance_info(){
        System.out.println("There are " + getNbrows() + " rows and " + getNbcols() + " columns.");
        System.out.println("+++ Constraint on rows +++");
        for (int i = 0; i < getNbrows(); i++){
            System.out.println("\tRow no." + i + " : " + Arrays.toString(getRow_constraints(i)));
        }
        System.out.println("+++ Constraint on cols +++");
        for (int j = 0; j < getNbcols(); j++){
            System.out.println("\tCol no." + j + " : " + Arrays.toString(getCol_constraints(j)));
        }
    }
    public static void main(String[] args) {
        String filename = "./picross/bird.px";
        picrossSlv bird = null;
        try {
            bird = new picrossSlv(filename);
        } catch (Exception e) {
            System.out.println("[picrossSlv] Instance creation has failed");
            e.printStackTrace();
        }
        assert bird != null;
        bird.show_instance_info();
    }
}
