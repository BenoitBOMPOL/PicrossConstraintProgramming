import structs.*;

public class Executable {
    public static void main(String[] args) throws Exception{
        String filename = "./picross/bird.px";
        picrossInstanceSlv bird = new picrossInstanceSlv(filename);
        for (int row_id = 0; row_id < bird.getInstance().getNb_rows(); row_id++){
            System.out.println("Number of solutions for row no. " + row_id + " : " + bird.getRow_allowed_tuples(row_id).length);
        }
        for (int col_id = 0; col_id < bird.getInstance().getNb_cols(); col_id++){
            System.out.println("Number of solutions for col no. " + col_id + " : " + bird.getCol_allowed_tuples(col_id).length);
        }
    }

}
