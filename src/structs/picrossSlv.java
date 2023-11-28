package structs;
import ilog.cp.IloCP;
import ilog.concert.*;

public class picrossSlv {
    private IloCP solver;
    private picross instance;

    private int[][][][] row_blocs;
    private int[][][][] col_blocs;

    public int getNbRows(){
        return instance.getNb_rows();
    }

    public AllowedTupleSearcher create_ats_from(int[] constraints, int ats_size){
        return new AllowedTupleSearcher(constraints, ats_size);
    }

    public int getNbCols(){
        return instance.getNb_cols();
    }


    public picrossSlv(String filename){
        try {
            this.instance = new picross(filename);

            this.row_blocs = new int[instance.getNb_rows()][][][];
            // Filling row_blocs
            for (int i = 0; i < instance.getNb_rows(); i++){
                AllowedTupleSearcher ats_row = create_ats_from(instance.getRow_constraints(i), instance.getNb_cols());
                int[][][] all_sols_row = ats_row.getAllSolutions();
                row_blocs[i] = all_sols_row;
            }

            this.col_blocs = new int[instance.getNb_cols()][][][];
            // Filling col_blocs
            for (int j = 0; j < instance.getNb_cols(); j++){
                AllowedTupleSearcher ats_col = create_ats_from(instance.getCol_constraints(j), instance.getNb_rows());
                int[][][] all_sols_col = ats_col.getAllSolutions();
                col_blocs[j] = all_sols_col;
            }






        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
