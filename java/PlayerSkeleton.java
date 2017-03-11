import java.io.IOException;
import java.util.Random;

public class PlayerSkeleton {

    public int pickMove(State s, int[][] legalMoves) {
        int[][] field = copyField(s.getField());
        // convert field to 0s and 1s
        for (int row = 0; row < field.length; row++) {
            for (int col = 0; col < field[row].length; col++) {
                field[row][col] = field[row][col] == 0 ? 0 : 1;
            }   
        }
        int nextPiece = s.getNextPiece();
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestMove = 0;
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

    // field NEED NOT need to have its completed rows cleared
    // Original field will not be kept
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
        return heuristic(field, weights);
    }

    public static double heuristic(int[][] field, double[] weights) {
        // Todo (YY): Implement
        return 0;
    }

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
            for (int h = height + Constants.P_BOTTOM[piece][orient][i]; h < height + Constants.P_TOP[piece][orient][i]; h++) {
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

    public static class Constants {

        public static final int FEATURE_COUNT = 8;

        public static final int SEARCH_TRIALS = 100;
        public static final int SEARCH_DEPTH = 5;

        // IMPORTANT: Replace with completed weights before submission
        // public static double[] WEIGHTS =
        //          new double[]{0, 0, 0, 0, 0, 0, 0, 0};
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
