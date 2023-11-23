package structs;
import java.io.*;
import java.util.Arrays;

public class picrossInstance {
    private int nb_rows;
    private int nb_cols;
    private int[][] row_constraints;
    private int[][] col_constraints;

    public int getNb_cols() {
        return nb_cols;
    }

    public int getNb_rows() {
        return nb_rows;
    }

    public int[] getCol_constraints(int j) {
        return col_constraints[j];
    }

    public int[] getRow_constraints(int i) {
        return row_constraints[i];
    }

    public picrossInstance(String filename) throws Exception{
        File pcross_file = new File(filename);
        BufferedReader bread = new BufferedReader(new FileReader(pcross_file));

        String chars;
        Boolean definedims = false;
        int count_rowconstraint = 0;
        int count_colconstraint = 0;
        
        while ((chars = bread.readLine()) != null){
            if (!definedims){
                this.nb_rows = Integer.parseInt(chars.split(",")[0]);
                this.nb_cols = Integer.parseInt(chars.split(",")[1]);

                this.row_constraints = new int[nb_rows][];
                this.col_constraints = new int[nb_cols][];
                
                definedims = true;
            } else{
                String[] str_constraints = chars.split(",");
                if (count_rowconstraint < nb_rows){
                    row_constraints[count_rowconstraint] = new int[str_constraints.length];
                    for (int i = 0; i < str_constraints.length; i++){
                        row_constraints[count_rowconstraint][i] = Integer.parseInt(str_constraints[i]);
                    }
                    count_rowconstraint += 1;
                } else {
                    col_constraints[count_colconstraint] = new int[str_constraints.length];
                    for (int i = 0; i < str_constraints.length; i++){
                        col_constraints[count_colconstraint][i] = Integer.parseInt(str_constraints[i]);
                    }
                    count_colconstraint += 1;
                }
            }
        }
        bread.close();
    }

    public static void main(String[] args) throws Exception {
        picrossInstance bird = new picrossInstance("./picross/bird.px");
        System.out.println("There are " + bird.getNb_rows() + " rows and " + bird.getNb_cols() + " columns.");
        System.out.println("+++ Constraint on rows +++");
        for (int i = 0; i < bird.getNb_rows(); i++){
            System.out.println("\tRow no." + i + " : " + Arrays.toString(bird.getRow_constraints(i)));
        }
        System.out.println("+++ Constraint on cols +++");
        for (int j = 0; j < bird.getNb_cols(); j++){
            System.out.println("\tCol no." + j + " : " + Arrays.toString(bird.getCol_constraints(j)));
        }
    }
}
