package structs;

import java.util.Arrays;

public class picrossInstanceSlv {
    private picrossInstance instance;
    private int[][][] row_allowed_tuples;
    private int[][][] col_allowed_tuples;

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
    }



}
