package structs;

import ilog.concert.*;
import ilog.cp.IloCP;
import java.util.*;

public class AllowedTupleSearcher {
    private IloCP solver;
    private final int size;
    private final int[] constraints;
    private IloIntVar[] start;

    public int getSize() {
        return size;
    }

    public int[] getConstraints() {
        return constraints;
    }

    public int get_ith_constraint(int i){
        return constraints[i];
    }

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

            // Creation of the variables
            for (int i = 0; i < constraints.length; i++){
                this.start[i] = solver.intVar(0,size-1, "C[" + i + "]");
            }

            // Adding constraints
            // Constraint 1 : "Better" overlap
            for (int i = 0; i < constraints.length - 1; i++){
                solver.add(solver.ge(
                        this.start[i + 1],
                        solver.sum(1 + constraints[i], this.start[i]))
                );
            }

            // Ensuring that each block ends before the size of the line
            for (int i = 0; i < constraints.length; i++){
                solver.add(solver.le(
                        solver.sum(this.start[i], constraints[i]),
                        size
                ));
            }

        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void displaySolution(int[] sol, int sol_no){
        System.out.println("+++ Solution " + sol_no + " under constraints " + Arrays.toString(constraints) + " and size " + size + ".");
        System.out.println("\t" + Arrays.toString(sol));
    }

    public void initEnumeration() {
        try {
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    // sol[i] = (0, 1)
    // sol[i] == 1 ssi la case (i) est coloriée
    public int[] solve(){
        int[] sol = null;
        try{
            if (solver.next()){
                sol = new int[this.size];
                for (int i = 0; i < size; i++){
                    sol[i] = 0;
                }

                for (int k = 0; k < constraints.length; k++){
                    int length = constraints[k];
                    int start_ = (int) solver.getValue(start[k]);
                    for (int i = start_; i < start_ + length; i++){
                        sol[i] = 1;
                    }
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
        int nb_sols = count_sols();
        int [][] solutions = new int[nb_sols][size];
        initEnumeration();
        int[] sol = solve();
        int sol_id = 0;
        while (sol != null){
            solutions[sol_id] = sol;
            sol_id++;
            sol = solve();
        }
        end();
        return solutions;
    }

    public void end() {
        solver.end();
    }

    public static void main(String[] args){
        int row_size = 22;
        int[] constraints = {1,1,2,1};
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, row_size);
        ats.initEnumeration();
        try {
            System.out.println("Informations on the instance : ");
            System.out.println("\t Instance has " + ats.getConstraints().length + " constraints on a row of length " + ats.getSize() +  ".");
            System.out.println("\t Constraints : " + Arrays.toString(ats.getConstraints()));

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
