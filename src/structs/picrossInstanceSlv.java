package structs;
import ilog.cp.IloCP;
import ilog.concert.*;
public class picrossInstanceSlv {
    private IloCP solver;
    private picrossInstance instance;

    private int[][][] allowedtuples_inrows;
    private int[][][] allowedtuples_incols;

    public picrossInstance getInstance() {
        return instance;
    }
    public int [][] get_row_allowedtuples(int i){
        return allowedtuples_inrows[i];
    }
    public int [][] get_col_allowedtuples(int j){
        return allowedtuples_incols[j];
    }

    public picrossInstanceSlv(String filename){
        try {
            this.instance = new picrossInstance(filename);
            this.allowedtuples_inrows = new int[instance.getNb_rows()][][];
            this.allowedtuples_incols = new int[instance.getNb_cols()][][];

            for (int row_id = 0; row_id < instance.getNb_rows(); row_id++){
                AllowedTupleSearcher rowsearch = new AllowedTupleSearcher(instance.getRow_constraints(row_id), instance.getNb_cols());
                allowedtuples_inrows[row_id] = rowsearch.getAllSolutions();
            }

            for (int col_id = 0; col_id < instance.getNb_cols(); col_id++){
                AllowedTupleSearcher colsearch = new AllowedTupleSearcher(instance.getCol_constraints(col_id), instance.getNb_rows());
                allowedtuples_incols[col_id] = colsearch.getAllSolutions();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
