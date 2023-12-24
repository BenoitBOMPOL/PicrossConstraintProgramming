import ilog.concert.*;
import ilog.cp.IloCP;
import java.util.*;


public class OneLineSolver {
    private IloCP solver;
    private final int size;
    private final int[] constraints;

    private IloIntVar[] b; // b[i] in {0, 1}
    private IloIntVar[] x; // x[j] in {1..n}

    public OneLineSolver(int[] constraints, int size){
        this.constraints = constraints;
        this.size = size;

        this.x = new IloIntVar[constraints.length];
        this.b = new IloIntVar[size];

        stateModel();
    }

    private void stateModel() {
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            // Creation of the variables
            for (int j = 0; j < constraints.length; j++) {
                x[j] = solver.intVar(0, this.size - 1);
            }
            for (int i = 0; i < size; i++) {
                b[i] = solver.intVar(0, 1);
            }

            // Adding constraints
            int somme_p = 0;
            for (int p : constraints) {
                somme_p += p;
            }
            solver.add(solver.eq(solver.sum(b), somme_p));

            for (int j = 0; j < constraints.length - 1; j++) {
                solver.add(solver.le(solver.sum(x[j], constraints[j] + 1), x[j + 1]));
            }

            for (int i = 0; i < size; i++) {
                IloConstraint[] or_constraints = new IloConstraint[constraints.length];
                for (int j = 0; j < constraints.length; j++) {
                    or_constraints[j] = solver.and(solver.le(x[j], i), solver.ge(x[j], i - constraints[j] + 1));
                }
                solver.add(solver.ifThen(solver.eq(b[i], 1), solver.or(or_constraints)));
                solver.add(solver.ifThen(solver.or(or_constraints), solver.eq(b[i], 1)));
            }


        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void initEnumeration() {
        try {
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public boolean is_valid(int[] word){
        if (word.length != this.size){
            return false;
        }

        int index = 0;
        // Trimming leftmost 0s
        while (word[index] == 0){
            index += 1;
        }
        for (int i = 0; i < constraints.length - 1; i++){
            // Checking consecutive 1s
            for (int j = 0; j < constraints[i]; j++){
                if (word[index + j] == 0){
                    return false;
                }
            }
            index += constraints[i];
            if (word[index] == 1){
                return false;
            }
            while (word[index] == 0){
                index++;
            }
        }

        for (int j = 0; j < constraints[constraints.length - 1]; j++){
            if (word[index + j] == 0){
                return false;
            }
        }
        index += constraints[constraints.length - 1];
        while (index < word.length){
            if (word[index] == 1){
                return false;
            }
            index++;
        }
        return true;
    }

    // sol[i] = (0, 1)
    // sol[i] == 1 ssi la case (i) est coloriée
    public int[] solve(){
        int[] sol = null;
        try{
            if (solver.next()){
                sol = new int[this.size];
                for (int i = 0; i < size; i++){
                    sol[i] = (int) solver.getValue(b[i]);
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

    public static void main(String[] args) {
        int[] constraints = {1, 4, 2};
        int size = 12;
        OneLineSolver ols = new OneLineSolver(constraints, size);
        ols.initEnumeration();
        int[][] all_sol = ols.getAllSolutions();

        System.out.println(all_sol.length + " (potential) soutions have been found.");
        /* for (int[] sol : all_sol){
            if (!ols.is_valid(sol)){
                System.out.println(Arrays.toString(sol) + " is tested invalid but is valid.");
            } else {
                System.out.println(Arrays.toString(sol) + " is a valid solution.");
            }
        } */
    }

}
