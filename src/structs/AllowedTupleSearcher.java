package structs;
import ilog.concert.*;
import ilog.cp.IloCP;
import java.util.*;

public class AllowedTupleSearcher {
    private IloCP solver;
    private int size;          // Size of the row, column concerned
    private int[] constraints; // Either row constraint or column constraint
    private IloIntervalVar[] allowedTuple;

    public AllowedTupleSearcher(int[] constraints, int size){
        this.constraints = constraints;
        this.allowedTuple = new IloIntervalVar[constraints.length];
        this.size = size;
        stateModel();
    }

    private void stateModel() {
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            for (int i = 0; i < constraints.length; i++){
                allowedTuple[i] = solver.intervalVar(constraints[i], "C[" + i + "]");
            }

            for (int i = 0; i < constraints.length - 1; i++){
                // Ensures no overlap
                solver.add(solver.ge(
                        solver.startOf(allowedTuple[i + 1]),
                        solver.sum(1, solver.endOf(allowedTuple[i]))
                ));
            }

            for (int j = 0; j < constraints.length; j++){
                // Ensures each bloc ends before (stricly !) the end of the row
                solver.add(solver.ge(size, solver.endOf(allowedTuple[j])));
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void displaySolution(int[] sol, int nb){
        System.out.println("+++ Solution " + nb + " under constraints " + Arrays.toString(constraints) + " and size " + size + ".");
        for (int i = 0; i < sol.length; i++){
            System.out.print("\t" + i + "-th bloc : [");
            for (int j = 0; j < constraints[i]; j++){
                System.out.print(sol[i] + j);
                if (j + 1 < constraints[i]){
                    System.out.print(",");
                }
            }
            System.out.println("]");
        }
    }

    public void initEnumeration() {
        try {
            solver.propagate();
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    // sol[i] contains the position of the first completed
    // cell of the i-th constraint in the tuple
    public int[] solve(){
        int[] sol = null;
        try {
            if (solver.next()) {
                sol = new int[constraints.length];
                for (int i = 0; i < constraints.length; i++) {
                    sol[i] = solver.getStart(allowedTuple[i]);
                }
            }
        } catch (IloException e){
            e.printStackTrace();
        }
        return sol;
    }

    public void end() {
        solver.end();
    }
}
