package structs;

import ilog.cp.IloCP;
import ilog.concert.*;

public class picrossInstanceSlv {
    private IloCP solver;
    private picrossInstance instance;
    private int[][][] row_allowed_tuples;
    private int[][][] col_allowed_tuples;

    private IloIntTupleSet[] rows;
    private IloIntTupleSet[] cols;
    private IloIntVar[][] effectiveRows;
    private IloIntVar[][] effectiveCols;

    public picrossInstance getInstance() {
        return instance;
    }

    public int[][] getCol_allowed_tuples(int j) {
        return col_allowed_tuples[j];
    }

    public int[][] getRow_allowed_tuples(int i) {
        return row_allowed_tuples[i];
    }

    public picrossInstanceSlv(String filename) throws Exception{
        this.instance = new picrossInstance(filename);
        this.row_allowed_tuples = new int[instance.getNb_rows()][][];
        this.col_allowed_tuples = new int[instance.getNb_cols()][][];

        for (int row_id = 0; row_id < instance.getNb_rows(); row_id++){
            AllowedTupleSearcher row_search =
                    new AllowedTupleSearcher(instance.getRow_constraints(row_id),instance.getNb_cols());
            row_allowed_tuples[row_id] = row_search.getAllSolutions();
        }

        for (int col_id = 0; col_id < instance.getNb_cols(); col_id++){
            AllowedTupleSearcher col_search =
                    new AllowedTupleSearcher(instance.getCol_constraints(col_id), instance.getNb_rows());
            col_allowed_tuples[col_id] = col_search.getAllSolutions();
        }

        this.rows = new IloIntTupleSet[instance.getNb_rows()];
        this.cols = new IloIntTupleSet[instance.getNb_cols()];

        this.effectiveRows = new IloIntVar[instance.getNb_rows()][];
        for (int row_id = 0; row_id < instance.getNb_rows(); row_id++){
            this.effectiveRows[row_id] = new IloIntVar[instance.getRow_constraints(row_id).length];
        }
        this.effectiveCols = new IloIntVar[instance.getNb_cols()][];
        for (int col_id = 0; col_id < instance.getNb_rows(); col_id++){
            this.effectiveRows[col_id] = new IloIntVar[instance.getCol_constraints(col_id).length];
        }
        stateModel();
    }

    public void stateModel(){
        try{
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            for (int row_id = 0; row_id < instance.getNb_rows(); row_id++){
                rows[row_id] = solver.intTable(instance.getRow_constraints(row_id).length);
                for (int[] valid_row : row_allowed_tuples[row_id]){
                    solver.addTuple(rows[row_id], valid_row);
                }
                solver.add(solver.allowedAssignments(effectiveRows[row_id], rows[row_id]));
            }

            for (int col_id = 0; col_id < instance.getNb_rows(); col_id++){
                rows[col_id] = solver.intTable(instance.getCol_constraints(col_id).length);
                for (int[] valid_col : row_allowed_tuples[col_id]){
                    solver.addTuple(rows[col_id], valid_col);
                }
                solver.add(solver.allowedAssignments(effectiveCols[col_id], cols[col_id]));
            }

        } catch (IloException e){
            e.printStackTrace();
        }

    }
}
