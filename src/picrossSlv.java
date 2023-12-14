import ilog.cp.IloCP;
import ilog.concert.*;

public class picrossSlv extends picross{
    private IloCP solver;
    private IloIntTupleSet[] row_solution;
    private IloIntTupleSet[] col_solution;
    private IloIntVar[][] grid;

    int nbFails;

    public int[][] get_solutions(int[] constraints, int size){
        AllowedTupleSearcher ats = new AllowedTupleSearcher(constraints, size);
        return ats.getAllSolutions();
    }


    public picrossSlv(String filename) throws Exception {
        super(filename);

        this.row_solution = new IloIntTupleSet[getNbrows()];
        this.col_solution = new IloIntTupleSet[getNbcols()];
        this.grid = new IloIntVar[getNbrows()][getNbcols()];

        stateModel();
    }

    public IloIntVar[] get_jth_col(int j){
        IloIntVar[] col = new IloIntVar[getNbrows()];
        for (int i = 0; i < getNbrows(); i++){
            col[i] = grid[i][j];
        }
        return col;
    }

    public void stateModel(){
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            // Boolean variables in grid
            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    grid[i][j] = solver.intVar(0,  1);
                }
            }

            // Enumerating solutions for each row
            for (int i = 0; i < getNbrows(); i++){
                row_solution[i] = solver.intTable(getNbcols());
                for (int[] sol : get_solutions(row_constraints[i], getNbcols())){
                    solver.addTuple(row_solution[i], sol);
                }
                solver.add(solver.allowedAssignments(grid[i], row_solution[i]));
            }

            // Enumerating solutions for each column
            for (int j = 0; j < getNbcols(); j++){
                col_solution[j] = solver.intTable(getNbrows());
                for (int[] sol : get_solutions(col_constraints[j], getNbrows())){
                    solver.addTuple(col_solution[j], sol);
                }
                solver.add(solver.allowedAssignments(get_jth_col(j), col_solution[j]));
            }

        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public void propagate(){
        try {
            initEnumeration();
            solver.propagate();
            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    if (solver.isFixed(grid[i][j])){
                        int value = (int) solver.getValue(grid[i][j]);
                        System.out.print(value == 1 ? "⬛" : "⬜");
                    } else {
                        // Print a red square
                        System.out.print("\uD83D\uDFE5");
                    }
                }
                System.out.println();
            }

            solver.printInformation();
        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public int[][] solve(){
        initEnumeration();
        int[][] sol = null;
        try {
            if (solver.next()){
                sol = new int[getNbrows()][getNbcols()];
                for (int i = 0; i < getNbrows(); i++){
                    for (int j = 0; j < getNbcols(); j++){
                        sol[i][j] = (int) solver.getValue(grid[i][j]);
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
        if (sol == null){
            System.out.println("+++ null solution found. +++");
            return;
        }
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
        String filename = args[0];
        picrossSlv picross = null;
        try {
            picross = new picrossSlv(filename);
            picross.initEnumeration();
            picross.propagate();
            picross.initEnumeration();
            int[][] sol = picross.solve();
            picross.displaysol(sol);
            if (picross.is_a_valid_sol(sol)){
                System.out.println("A valid solution has been found.");
            } else {
                System.out.println("The answer provided is incorrect.");
            }

        } catch (Exception e) {
            System.out.println("[picrossSlv] Instance creation has failed");
            e.printStackTrace();
        }
    }
}
