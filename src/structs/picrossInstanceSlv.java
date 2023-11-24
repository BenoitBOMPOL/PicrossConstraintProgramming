package structs;
import ilog.cp.IloCP;
import ilog.concert.*;
public class picrossInstanceSlv {
    private IloCP solver;
    private picrossInstance instance;

    private int[][][] allowedtuples_inrows;
    private int[][][] allowedtuples_incols;

    private IloIntTupleSet[] row_patterns;
    private IloIntTupleSet[] col_patterns;
    private IloIntVar[][] eff_row_pattern;
    private IloIntVar[][] eff_col_pattern;

    /*
     * Pour chaque ligne, on connaît le nombre de cases qui doivent être occupées (somme sur les contraintes)
     * Donc on peut écrire une matrice d'IloIntVar (M) M[r] : Tous les indices des cases occupées par les différents blocs de la ligne r
     * On peut faire la même chose sur les colonnes (M') M'[c] : Tous les indices des cases occupées (bla bla bla) sur la colonne c
     * et on a j € M[i] iff i € M[j]
     */

    public picrossInstance getInstance() {
        return instance;
    }

    public picrossInstanceSlv(String filename){
        try {
            // Données
            // Instance du picross, invoquée depuis la classe picrossInstance
            this.instance = new picrossInstance(filename);

            // Données (Tableau initialisé après)
            // allowedtuples_inrows[r] : Tableau qui contient tous les tuples autorisés pour la ligne r.
            // allowedtuples_incols[c] : Tableau qui contient tous les tuples autorisés pour la colonne c.
            this.allowedtuples_inrows = new int[instance.getNb_rows()][][];
            this.allowedtuples_incols = new int[instance.getNb_cols()][][];

            for (int row_id = 0; row_id < instance.getNb_rows(); row_id++) {
                AllowedTupleSearcher rowsearch = new AllowedTupleSearcher(instance.getRow_constraints(row_id), instance.getNb_cols());
                allowedtuples_inrows[row_id] = rowsearch.getAllSolutions();
            }

            for (int col_id = 0; col_id < instance.getNb_cols(); col_id++){
                AllowedTupleSearcher colsearch = new AllowedTupleSearcher(instance.getCol_constraints(col_id), instance.getNb_rows());
                allowedtuples_incols[col_id] = colsearch.getAllSolutions();
            }

            // Variables
            // (C'est a priori de là qu'on récupèrera la solution)
            // eff_row_pattern[r] : Contient *le* tuple appliqué à la ligne r
            // eff_col_pattern[c] : Contient *le* tuple appliqué à la colonne c
            this.eff_row_pattern = new IloIntVar[instance.getNb_rows()][];
            for (int r_id = 0; r_id < instance.getNb_rows(); r_id++){
                eff_row_pattern[r_id] = new IloIntVar[instance.getRow_constraints(r_id).length];
            }

            this.eff_col_pattern = new IloIntVar[instance.getNb_cols()][];
            for (int c_id = 0; c_id < instance.getNb_cols(); c_id++){
                eff_col_pattern[c_id] = new IloIntVar[instance.getCol_constraints(c_id).length];
            }

            this.row_patterns = new IloIntTupleSet[instance.getNb_rows()];
            this.col_patterns = new IloIntTupleSet[instance.getNb_cols()];

            stateModel();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stateModel(){
        try {
            solver = new IloCP();
            solver.setParameter(IloCP.IntParam.LogVerbosity, IloCP.ParameterValues.Quiet);
            solver.setParameter(IloCP.IntParam.SearchType, IloCP.ParameterValues.DepthFirst);
            solver.setParameter(IloCP.IntParam.Workers, 1);

            /// For each row, the effective_row belongs to allowed rows
            for (int r_id = 0; r_id < instance.getNb_rows(); r_id++){
                row_patterns[r_id] = solver.intTable(instance.getRow_constraints(r_id).length);
                for (int[] allowed_rowtuple : allowedtuples_inrows[r_id]){
                    solver.addTuple(row_patterns[r_id], allowed_rowtuple);
                }
                solver.add(solver.allowedAssignments(eff_row_pattern[r_id], row_patterns[r_id]));
            }

            /// For each column, the effective_col belongs to allowed cols
            for (int c_id = 0; c_id < instance.getNb_cols(); c_id++){
                col_patterns[c_id] = solver.intTable(instance.getCol_constraints(c_id).length);
                for (int[] allowed_coltuple : allowedtuples_incols[c_id]){
                    solver.addTuple(col_patterns[c_id], allowed_coltuple);
                }
                solver.add(solver.allowedAssignments(eff_col_pattern[c_id], col_patterns[c_id]));
            }

        } catch (IloException e){
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

}
