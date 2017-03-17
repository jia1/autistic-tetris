// =======================================
// === Constants ===
// =======================================

public class Constants {

    public static final int FEATURE_COUNT = 8;
    public static final int NUM_TRAIN_GAMES = 1000;
    public static final int NUM_TRAIN_ITERS = 1000;

    public static final int SEARCH_TRIALS = 100;
    public static final int SEARCH_DEPTH = 5;

    // IMPORTANT: Replace with completed weights before submission
    // public static double[] WEIGHTS =
    // new double[]{0, 0, 0, 0, 0, 0, 0, 0};
    public static double[] WEIGHTS = PlayerTraining.loadWeights();

    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int N_PIECES = 7;

    // all legal moves - first index is piece type - then a list of 2-length
    // arrays
    public static int[][][] LEGAL_MOVES = new int[N_PIECES][][];

    // indices for legalMoves
    public static final int ORIENT = 0;
    public static final int SLOT = 1;

    // possible orientations for a given piece type
    public static int[] P_ORIENTS = { 1, 2, 4, 4, 4, 2, 2 };

    // the next several arrays define the piece vocabulary in detail
    // width of the pieces [piece ID][orientation]
    public static int[][] P_WIDTH = { { 2 }, { 1, 4 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 }, { 2, 3, 2, 3 }, { 3, 2 },
            { 3, 2 } };
    // height of the pieces [piece ID][orientation]
    public static int[][] P_HEIGHT = { { 2 }, { 4, 1 }, { 3, 2, 3, 2 }, { 3, 2, 3, 2 }, { 3, 2, 3, 2 }, { 2, 3 },
            { 2, 3 } };
    public static int[][][] P_BOTTOM = { { { 0, 0 } }, { { 0 }, { 0, 0, 0, 0 } },
            { { 0, 0 }, { 0, 1, 1 }, { 2, 0 }, { 0, 0, 0 } }, { { 0, 0 }, { 0, 0, 0 }, { 0, 2 }, { 1, 1, 0 } },
            { { 0, 1 }, { 1, 0, 1 }, { 1, 0 }, { 0, 0, 0 } }, { { 0, 0, 1 }, { 1, 0 } },
            { { 1, 0, 0 }, { 0, 1 } } };
    public static int[][][] P_TOP = { { { 2, 2 } }, { { 4 }, { 1, 1, 1, 1 } },
            { { 3, 1 }, { 2, 2, 2 }, { 3, 3 }, { 1, 1, 2 } }, { { 1, 3 }, { 2, 1, 1 }, { 3, 3 }, { 2, 2, 2 } },
            { { 3, 2 }, { 2, 2, 2 }, { 2, 3 }, { 1, 2, 1 } }, { { 1, 2, 2 }, { 3, 2 } },
            { { 2, 2, 1 }, { 2, 3 } } };

    // initialize legalMoves
    {
        // for each piece type
        for (int i = 0; i < N_PIECES; i++) {
            // figure number of legal moves
            int n = 0;
            for (int j = 0; j < P_ORIENTS[i]; j++) {
                // number of locations in this orientation
                n += COLS + 1 - P_WIDTH[i][j];
            }
            // allocate space
            LEGAL_MOVES[i] = new int[n][2];
            // for each orientation
            n = 0;
            for (int j = 0; j < P_ORIENTS[i]; j++) {
                // for each slot
                for (int k = 0; k < COLS + 1 - P_WIDTH[i][j]; k++) {
                    LEGAL_MOVES[i][n][ORIENT] = j;
                    LEGAL_MOVES[i][n][SLOT] = k;
                    n++;
                }
            }
        }
    }
}
