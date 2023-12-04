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

    private IloIntVar[][][] L;
    private IloIntVar[][][] C;

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

        this.L = new IloIntVar[getNbrows()][][];
        for (int i = 0; i < getNbrows(); i++) {
            L[i] = new IloIntVar[getRow_constraints(i).length][getNbcols()];
        }
        this.C = new IloIntVar[getNbcols()][][];
        for (int j = 0; j < getNbcols(); j++) {
            C[j] = new IloIntVar[getCol_constraints(j).length][getNbrows()];
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
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    for (int j = 0; j < getNbcols(); j++){
                        L[i][k][j] = solver.intVar(0, 1, "L[" + i + ", " + k + ", " + j + "]");
                    }
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    for (int i = 0; i < getNbrows(); i++){
                        C[j][k][i] = solver.intVar(0, 1, "C[" + j + ", " + k + ", " + i + "]");
                    }
                }
            }

            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    eff_row_solution[i][k] = solver.intVar(0, getNbcols() - 1);
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    eff_col_solution[j][k] = solver.intVar(0, getNbrows() - 1);
                }
            }

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
                solver.add(solver.allowedAssignments(eff_col_solution[j], col_solution[j]));
            }

            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    for (int j = 0; j < getNbcols(); j++){
                        int p_ijk = 0;
                        for (int p = 0; (p < getRow_constraints(i)[k]) && (j + p < getNbcols()); p++){
                            p_ijk++;
                        }
                        IloIntVar[] partial_row = new IloIntVar[p_ijk];
                        System.arraycopy(L[i][k], j, partial_row, 0, p_ijk);
                        solver.add(
                                solver.ifThen(
                                        solver.eq(eff_row_solution[i][k], j),
                                        solver.eq(solver.sum(partial_row), p_ijk)
                                )
                        );
                    }
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    for (int i = 0; i < getNbrows(); i++){
                        int q_ijk = 0;
                        for (int q = 0; (q < getCol_constraints(j)[k]) && (i + q < getNbrows()); q++){
                            q_ijk++;
                        }
                        IloIntVar[] partial_col = new IloIntVar[q_ijk];
                        System.arraycopy(C[j][k], i, partial_col, 0, q_ijk);
                        solver.add(
                                solver.ifThen(
                                        solver.eq(eff_col_solution[j][k], i),
                                        solver.eq(solver.sum(partial_col), q_ijk)
                                )
                        );
                    }
                }
            }

            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    for (int j = 0; j < getNbcols(); j++){
                        IloIntVar[] temp_col = new IloIntVar[getCol_constraints(j).length];
                        for (int k_pr = 0; k_pr < getCol_constraints(j).length; k_pr++){
                            temp_col[k_pr] = C[j][k_pr][i];
                        }
                        solver.add(solver.le(L[i][k][j], solver.sum(temp_col)));
                    }
                }
            }

            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    for (int i = 0; i < getNbrows(); i++){
                        IloIntVar[] temp_row = new IloIntVar[getRow_constraints(i).length];
                        for (int k_pr = 0; k_pr < getRow_constraints(i).length; k_pr++){
                            temp_row[k_pr] = L[i][k_pr][j];
                        }
                        solver.add(solver.le(C[j][k][i], solver.sum(temp_row)));
                    }
                }
            }


            for (int i = 0; i < getNbrows(); i++){
                for (int k = 0; k < getRow_constraints(i).length; k++){
                    solver.add(solver.eq(solver.sum(L[i][k]), getRow_constraints(i)[k]));
                }
            }


            for (int j = 0; j < getNbcols(); j++){
                for (int k = 0; k < getCol_constraints(j).length; k++){
                    solver.add(solver.eq(solver.sum(C[j][k]), getCol_constraints(j)[k]));
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
    public void initEnumeration() {
        try {
            solver.startNewSearch();
        } catch (IloException e) {
            e.printStackTrace();
        }
    }

    public void displaysol(int[][] sol){
        int[][] m = new int[getNbrows()][getNbcols()];
        for (int i = 0; i < getNbrows(); i ++){
            for (int j = 0; j < getNbcols(); j++){
                m[i][j] = 0;
            }
        }

        for (int i = 0; i < getNbrows(); i++){
            for (int k = 0; k < getRow_constraints(i).length; k++){
                int start = sol[i][k];
                int length = getRow_constraints(i)[k];
                for (int j = 0; j < getNbcols(); j++){
                    if ((j >= start) && (j < start + length)){
                        m[i][j]++;
                    }
                }
            }
            for (int j = 0; j < getNbcols(); j++){
                if (m[i][j] == 1){
                    System.out.print("⬛");
                } else {
                    System.out.print("⬜");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        String filename = "./picross/chapichapo.px";
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
