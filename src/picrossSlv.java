import ilog.cp.IloCP;
import ilog.concert.*;

public class picrossSlv extends picross{
    private IloCP solver;

    private IloIntVar[][] grid;
    private IloIntVar[][] X; // X[i][k] is the starting position of the k-th bloc in row i
    private IloIntVar[][] Y; // Y[j][l] is the starting position of the l-th bloc in column j

    public IloIntVar[] get_jth_col(int j){
        IloIntVar[] col = new IloIntVar[getNbrows()];
        for (int i = 0; i < getNbrows(); i++){
            col[i] = grid[i][j];
        }
        return col;
    }


    public picrossSlv(String filename) throws Exception {
        super(filename);

        this.grid = new IloIntVar[getNbrows()][getNbcols()];
        this.X = new IloIntVar[getNbrows()][];
        for (int i = 0; i < getNbrows(); i++){
            X[i] = new IloIntVar[getRow_constraints(i).length];
        }

        this.Y = new IloIntVar[getNbcols()][];
        for (int j = 0; j < getNbcols(); j++){
            Y[j] = new IloIntVar[getCol_constraints(j).length];
        }

        stateModel();
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
                    grid[i][j] = solver.intVar(0, 1);
                }
            }

            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    X[i][k] = solver.intVar(0, getNbcols() - 1);
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int l = 0; l < getCol_constraints(j).length; l++){
                    Y[j][l] = solver.intVar(0, getNbrows() - 1);
                }
            }

            // NOTE : (1A)
            for (int i = 0; i < getNbrows(); i++){
                int blocs_in_row_i = 0;
                for (int p : getRow_constraints(i)){
                    blocs_in_row_i += p;
                }
                solver.add(solver.eq(solver.sum(grid[i]), blocs_in_row_i));
            }

            // NOTE : (1B)
            for (int j = 0; j < getNbcols(); j++){
                int blocs_in_col_j = 0;
                for (int q : getCol_constraints(j)){
                    blocs_in_col_j += q;
                }
                solver.add(solver.eq(solver.sum(get_jth_col(j)), blocs_in_col_j));
            }

            // NOTE : (1C)
            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length - 1; k++){
                    solver.add(solver.le(solver.sum(X[i][k], getRow_constraints(i)[k] + 1), X[i][k + 1]));
                }
            }

            // NOTE : (1D)
            for (int j = 0; j < getNbcols(); j++){
                for (int l = 0; l < getCol_constraints(j).length - 1; l++){
                    solver.add(solver.le(solver.sum(Y[j][l], getCol_constraints(j)[l] + 1), Y[j][l + 1]));
                }
            }



            // NOTE : (1E)
            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    IloConstraint[] activ = new IloConstraint[getRow_constraints(i).length];
                    for (int s = 0; s < getRow_constraints(i).length; s++){
                        activ[s] = solver.and(solver.le(X[i][s], j), solver.ge(X[i][s], j - getRow_constraints(i)[s] + 1));
                    }
                    solver.add(solver.ifThen(solver.eq(grid[i][j], 1), solver.or(activ)));
                    solver.add(solver.ifThen(solver.or(activ), solver.eq(grid[i][j], 1)));
                }
            }

            // NOTE : (1F)
            for (int j = 0; j < getNbcols(); j++){
                for (int i = 0; i < getNbrows(); i++){
                    IloConstraint[] cactiv = new IloConstraint[getCol_constraints(j).length];
                    for (int t = 0; t < getCol_constraints(j).length; t++){
                        cactiv[t] = solver.and(solver.le(Y[j][t], i), solver.ge(Y[j][t], i - getCol_constraints(j)[t] + 1));
                    }
                    solver.add(solver.ifThen(solver.eq(grid[i][j], 1), solver.or(cactiv)));
                    solver.add(solver.ifThen(solver.or(cactiv), solver.eq(grid[i][j], 1)));
                }
            }


            // NOTE : (1G)
            // Test : stack leftmost task
            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    int sum_proc_before = 0;
                    for (int k_pr = 0; k_pr < k; k_pr++){
                        sum_proc_before += getRow_constraints(i)[k_pr];
                    }
                    solver.add(solver.ge(X[i][k], sum_proc_before + k));
                }
            }

            // NOTE : (1H)
            // Test : stack leftmost task
            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    int sum_proc_before = 0;
                    for (int k_pr = 0; k_pr < k; k_pr++){
                        sum_proc_before += getCol_constraints(j)[k_pr];
                    }
                    solver.add(solver.ge(Y[j][k], sum_proc_before + k));
                }
            }

            // NOTE : (1I)
            // Test : stack rightmost
            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    int sumrow = 0;
                    for (int kpr = k; kpr < getRow_constraints(i).length; kpr++){
                        sumrow += getRow_constraints(i)[kpr];
                    }
                    solver.add(solver.le(X[i][k], getNbcols() - getRow_constraints(i).length + k + 1 - sumrow));
                }
            }

            // NOTE : (1J)
            // Test : stack rightmost
            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    int sumcol = 0;
                    for (int kpr = k; kpr < getCol_constraints(j).length; kpr++){
                        sumcol += getCol_constraints(j)[kpr];
                    }
                    solver.add(solver.le(Y[j][k], getNbrows() - getCol_constraints(j).length + k + 1 - sumcol));
                }
            }


            for (int i = 0; i < getNbrows(); i++){
                for (int ki = 0; ki < getRow_constraints(i).length; ki++){
                    for (int j = 0; j < getNbcols(); j++){
                        if (j > 0) {
                            // NOTE : (1K)
                            solver.add(
                                    solver.ifThen(
                                            solver.eq(X[i][ki], j),
                                            solver.eq(grid[i][j - 1], 0)
                                    )
                            );
                        }
                        // NOTE : (1M)
                        if (j + getRow_constraints(i)[ki] < getNbcols()){
                            solver.add(solver.ifThen(
                                    solver.eq(X[i][ki], j),
                                    solver.eq(grid[i][j + getRow_constraints(i)[ki]], 0)
                            ));
                        }
                    }
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int kj = 0; kj < getCol_constraints(j).length; kj++){
                    for (int i = 0; i < getNbrows(); i++){
                        if (i > 0){
                            // NOTE : (1L)
                            solver.add(solver.ifThen(
                                    solver.eq(Y[j][kj], i),
                                    solver.eq(grid[i-1][j], 0)
                            ));
                        }

                        // NOTE : (1N)
                        if (i + getCol_constraints(j)[kj] < getNbrows()){
                            solver.add(solver.ifThen(
                                    solver.eq(Y[j][kj], i),
                                    solver.eq(grid[i + getCol_constraints(j)[kj]][j], 0)
                            ));
                        }
                    }
                }
            }



        } catch (IloException e){
            e.printStackTrace();
        }
    }

    public void propagate(){
        try {
            initEnumeration();
            solver.propagate();
            int count_unfixed_vars = 0;
            for (int i = 0; i < getNbrows(); i++){
                for (int j = 0; j < getNbcols(); j++){
                    if (solver.isFixed(grid[i][j])){
                        int value = (int) solver.getValue(grid[i][j]);
                        System.out.print(value == 1 ? "⬛" : "⬜");
                    } else {
                        // Print a red square
                        count_unfixed_vars++;
                        System.out.print("\uD83D\uDFE5");
                    }
                }
                System.out.println();
            }
            System.out.println(count_unfixed_vars + " cells are undetermined.");
            double ratio = (double) count_unfixed_vars / (grid.length * grid[0].length);
            System.out.println("Undetermined ratio : " + ratio);

            solver.printInformation();
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
                        sol[i][j] = (int) solver.getValue(grid[i][j]);
                    }
                }
            }

            int nbFails = solver.getInfo(IloCP.IntInfo.NumberOfFails);
            System.out.println("Number of fails : " + nbFails);
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

    public int count_sols(){
        int count = 0;
        initEnumeration();
        int[][] sol = solve();
        displaysol(sol);
        while (sol != null) {
            count += 1;
            sol = solve();
            if (sol != null) {
                displaysol(sol);
            }
        }
        return count;
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
