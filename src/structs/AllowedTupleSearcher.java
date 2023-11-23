package structs;

import ilog.concert.*;
import ilog.cp.IloCP;
import java.util.*;

public class AllowedTupleSearcher {
    private IloCP solver;
    private int size;          // Size of the row, column concerned
    private int[] constraints; // Either row constraint or column constraint
    //private IloIntervalVar[] allowedTuple;
    private IloIntVar[] start;

    public AllowedTupleSearcher(int[] constraints, int size){
        this.constraints = constraints;
        this.start = new IloIntVar[constraints.length];
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
                this.start[i] = solver.intVar(0,size-1, "C[" + i + "]");
            }

            for (int i = 0; i < constraints.length - 1; i++){
                // Ensures no overlap
                solver.add(solver.ge(
                        this.start[i + 1],
                        solver.sum(1 + constraints[i], this.start[i]))
                );
            }

            for (int i = 0; i < constraints.length; i++){
                // Ensures ends before ending date
                solver.add(solver.le(
                        solver.sum(this.start[i], constraints[i]),
                        size
                ));
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
                    sol[i] = (int) solver.getValue(start[i]);
                }
            }
        } catch (IloException e){
            e.printStackTrace();
        }
        return sol;
    }

    public int count_sols(){
        int count = 0;
        initEnumeration();
        int[] sol = solve();
        while (sol != null){
            count += 1;
            sol = solve();
        }
        return count;
    }

    public int[][] getAllSolutions(){
        int nb_solutions = count_sols();
        int[][] allsolutions = new int[nb_solutions][constraints.length];
        initEnumeration();
        int[] sol = solve();
        int sol_id = 0;
        while (sol != null){
            allsolutions[sol_id] = sol;
            sol_id += 1;
            sol = solve();
        }
        end();
        return allsolutions;
    }

    public void end() {
        try {
            solver.printInformation();
        } catch (IloException e) {
            e.printStackTrace();
        }
        solver.end();
    }

}
