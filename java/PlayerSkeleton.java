import java.io.IOException;
import java.util.Random;

public class PlayerSkeleton {

    // Picks a move to carry out based on the current state of the board.
    //
    // What it does:
    // For every legal move, evaluate the resultant state of the board with a
    // heuristic. The move which gives rise to the highest score is picked as
    // the next move.
    public int pickMove(State s, int[][] legalMoves) {
        int[][] field = copyField(s.getField());
        // Convert field to 0s and 1s
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                field[row][col] = field[row][col] == 0 ? 0 : 1;
            }
        }
        int nextPiece = s.getNextPiece();
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestMove = 0;
        // Among all possible moves, find the best move which gives
        // rise to the highest meta-heuristic score.
        for (int i = 0; i < legalMoves.length; i++) {
            int[][] nextField = applyMove(copyField(field), nextPiece, legalMoves[i]);
            double score = metaHeuristic(nextField);
            if (score > bestScore) {
                bestScore = score;
                bestMove = i;
            }
        }
        return bestMove;
    }

    // =======================================
    // === Heuristics ===
    // =======================================

    // Meta-heuristic
    // This function serves as a "booster" to improve the performance of the
    // original heuristic we trained.
    //
    // What it does:
    // For a board, use the original heuristic to play the game for a certain
    // number of moves, recording the number of rows cleared.
    // Repeat this trial a certain number of times, then return the total number
    // of rows cleared as the score.
    //
    // The number of trials is specified by SEARCH_TRIALS, and the number of
    // moves played per trial is defined by SEARCH_DEPTH. These are parameters
    // that can be changed to tweak the running time as well as the accuracy of
    // the heuristic
    public double metaHeuristic(int[][] field) {
        if (field[0][0] == -1) {
            // game over
            return Double.NEGATIVE_INFINITY;
        }
        int initialRowsCleared = countCompletedRows(field);
        field = clearCompletedRows(field);
        int totalRowsCleared = 0;
        for (int trials = 0; trials < Constants.SEARCH_TRIALS; trials++) {
            int[][] curField = copyField(field);
            int rowsCleared = initialRowsCleared;
            for (int depth = 0; depth < Constants.SEARCH_DEPTH; depth++) {
                int nextPiece = rand.nextInt(Constants.N_PIECES);
                curField = moveByHeuristic(curField, nextPiece);
                rowsCleared += countCompletedRows(curField);
                curField = clearCompletedRows(curField);
            }
            totalRowsCleared += rowsCleared;
        }
        return totalRowsCleared;
    }

    // Chooses the legal move which gives rise to the best heuristic score, then
    // apply it to the board
    public int[][] moveByHeuristic(int[][] field, int piece) {
        int[][] legalMoves = Constants.LEGAL_MOVES[piece];

        double bestScore = Double.NEGATIVE_INFINITY;
        int[][] bestField = null;

        for (int[] move : legalMoves) {
            int[][] newField = applyMove(copyField(field), piece, move);
            double score = heuristic(newField);
            if (score > bestScore) {
                bestScore = score;
                bestField = newField;
            }
        }

        return bestField;
    }

    public double heuristic(int[][] field) {
    	int[] top = new int[Constants.COLS];
    	for(int c = 0; c < Constants.COLS; c++) {
    		top[c] = getTop(field, c); 
    	}
    	
        return heuristic(field, top, weights);
    }

    public static double heuristic(int[][] field, int[] top, double[] weights) {
        // Todo (YY): Implement
    	
        return 0;
    }
    
    public static int numHoles() {
    	return 0; 
    }
    
    public static int numRowsCleared(int[][] field) { 
    	
    	return 0; 
    }
    
    public static int maxHeight() { 
    	
    	return 0; 
    }
    
    public static int averageHeight() { 
    	return 0; 
    }
    
    public static int surfaceArea() {
    	return 0; 
    }
    
    public static int boardSmoothnessAbsolute() {
    	return 0; 
    }
    
    public static int boardSmoothnessSquared() {
    	return 0; 
    }
    
    public static int maxAdjacentDiff() {
    	return 0; 
    }
    
    public static void heightDiff(int[] top, int col1, int col2) {
    	// return height diff? 
    	// maybe move into heuristics 
    }
    

    // =======================================
    // === Helper Functions ===
    // =======================================

    // For a given field, piece and move, return a new field as a result
    // of that move.
    // Original field will not be kept
    public static int[][] applyMove(int[][] field, int piece, int[] move) {
        int orient = move[Constants.ORIENT];
        int slot = move[Constants.SLOT];
        int height = getTop(field, slot) - Constants.P_BOTTOM[piece][orient][0];
        // for each column beyond the first in the piece
        for (int c = 1; c < Constants.P_WIDTH[piece][orient]; c++) {
            height = Math.max(height, getTop(field, slot + c) - Constants.P_BOTTOM[piece][orient][c]);
        }

        // check if game ended
        if (height + Constants.P_HEIGHT[piece][orient] >= Constants.ROWS) {
            // Game over
            field[0][0] = -1;
            return field;
        }

        // for each column in the piece - fill in the appropriate blocks
        for (int i = 0; i < Constants.P_WIDTH[piece][orient]; i++) {

            // from bottom to top of brick
            for (int h = height + Constants.P_BOTTOM[piece][orient][i]; h < height
                    + Constants.P_TOP[piece][orient][i]; h++) {
                field[h][i + slot] = 1;
            }
        }
        return field;
    }

    // Returns the height of the field in a specific column
    public static int getTop(int[][] field, int col) {
        int height = field.length;
        while (height >= 1 && field[height - 1][col] == 0) {
            height--;
        }
        return height;
    }

    // Counts the number of completed rows in a field
    public static int countCompletedRows(int[][] field) {
        int completedRows = 0;
        for (int row = 0; row < field.length; row++) {
            boolean complete = true;
            for (int col = 0; col < field[row].length; col++) {
                if (field[row][col] == 0) {
                    complete = false;
                    break;
                }
            }
            if (complete) {
                completedRows++;
            }
        }
        return completedRows;
    }

    // Returns a new field with completed rows cleared.
    // Original field will not be kept
    public static int[][] clearCompletedRows(int[][] field) {
        for (int r = field.length - 1; r >= 0; r--) {
            // check all columns in the row
            boolean full = true;
            for (int c = 0; c < field[r].length; c++) {
                if (field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            // if the row was full - remove it and slide above stuff down
            if (full) {
                for (int c = 0; c < field[r].length; c++) {
                    for (int i = r; i < field.length - 1; i++) {
                        field[i][c] = field[i + 1][c];
                        field[i + 1][c] = 0;
                    }
                }
            }
        }
        return field;
    }

    public static int[][] copyField(int[][] field) {
        int[][] result = new int[field.length][];
        for (int i = 0; i < field.length; i++) {
            result[i] = field[i].clone();
        }
        return result;
    }

    // =======================================
    // === Main Functions ===
    // =======================================

    public static void main(String[] args) throws IOException {
        State s = new State();
        new TFrame(s);
        PlayerSkeleton p = new PlayerSkeleton(Constants.WEIGHTS);
        while (!s.hasLost()) {
            s.makeMove(p.pickMove(s, s.legalMoves()));
            s.draw();
            s.drawNext(0, 0);
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("You have completed " + s.getRowsCleared() + " rows.");
    }

    public PlayerSkeleton(double[] weights) {
        this.weights = weights;
    }

    private final double[] weights;
    private final Random rand = new Random();

    // =======================================
    // === Constants ===
    // =======================================

    public static class Constants {

        public static final int FEATURE_COUNT = 8;

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
}
