import java.io.IOException;
import java.util.Random;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Collections; 

public class PlayerSkeleton {

	public static int WEIGHT_INDEX_NUM_HOLES = 0; 
	public static int WEIGHT_INDEX_NUM_ROWS_CLEARED = 1; 
	public static int WEIGHT_INDEX_MAX_HEIGHT = 2; 
	public static int WEIGHT_INDEX_AVG_HEIGHT = 3; 
	public static int WEIGHT_INDEX_BOARD_SMOOTHNESS_ABS = 4; 
	public static int WEIGHT_INDEX_BOARD_SMOOTHNESS_SQR = 5; 
	public static int WEIGHT_INDEX_MAX_ADJ_DIFF = 6;
	public static int WEIGHT_INDEX_NUM_ROWS_WITH_HOLES = 7; 
	public static int WEIGHT_INDEX_TOTAL_HOLE_DEPTHS = 8; 
	public static int WEIGHT_INDEX_MAX_HOLE_DEPTHS = 9; 
	
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

    // calculate heuristic for the resultant field after a move is applied
    // resultant field refers to field BEFORE completed rows are cleared 
    // if the resultant field terminates the game, value is -infinity
    // else calculate heuristic as per normal 
    public double heuristic(int[][] field) {
    	if (field[0][0] == -1) {
            return Double.NEGATIVE_INFINITY;
        }
    	
        return heuristic(field, weights);
    }

    public static double heuristic(int[][] field, double[] weights) {	
    	// calculate height for each col and store in top array
    	// each top element refers to height before rows are cleared 
    	int[] top = new int[Constants.COLS];
    	for(int c = 0; c < Constants.COLS; c++) {
    		top[c] = getTop(field, c); 
    	}
    	
    	// calculate absolute height different between col i and col i+1 
    	int[] topDiff = new int[top.length-1]; 
    	for(int c = 0; c < top.length-1; c++) {
    		topDiff[c] = Math.abs(top[c] - top[c+1]);  
    	}
    	
    	// calculate hole depths 
    	// hole depths refer to number of cells between the top of hole and top of the col
    	ArrayList<Integer> holeDepths = new ArrayList<>(); 
    	for (int c = 0; c < Constants.COLS; c++) {
    		for (int r = 0; r < top[c] - 1; r++) {
    			if (field[r][c] == 0 && field[r+1][c] == 1) {
    				holeDepths.add(top[c] - 1 - r); 
    			}
    		}
    	}
    
    	// calculate each feature 
    	int numHoles = numHoles(field, top); 
    	int numRowsCleared = countCompletedRows(field);  
    	int maxHeight = maxHeight(top, numRowsCleared); 
    	double avgHeight = averageHeight(top, numRowsCleared);
    	double boardSmoothnessAbs = boardSmoothnessAbsolute(topDiff); 
    	double boardSmoothnessSqr = boardSmoothnessSquared(topDiff); 
    	int maxAdjacentDiff = maxAdjacentDiff(topDiff); 
    	int numRowsWithHoles = numRowsWithHoles(field, top); 
    	int totalHoleDepths = totalHoleDepths(holeDepths); 
    	int maxHoleDepth = maxHoleDepth(holeDepths); 
    	
    	// calculate weighted sum of features 
        return numHoles * weights[WEIGHT_INDEX_NUM_HOLES] 
        	 + numRowsCleared * weights[WEIGHT_INDEX_NUM_ROWS_CLEARED] 
             + maxHeight * weights[WEIGHT_INDEX_MAX_HEIGHT]
             + avgHeight * weights[WEIGHT_INDEX_AVG_HEIGHT]
             + boardSmoothnessAbs * weights[WEIGHT_INDEX_BOARD_SMOOTHNESS_ABS]
             + boardSmoothnessSqr * weights[WEIGHT_INDEX_BOARD_SMOOTHNESS_SQR]
             + maxAdjacentDiff * weights[WEIGHT_INDEX_MAX_ADJ_DIFF]
             + numRowsWithHoles * weights[WEIGHT_INDEX_NUM_ROWS_WITH_HOLES]
             + totalHoleDepths * weights[WEIGHT_INDEX_TOTAL_HOLE_DEPTHS]
             + maxHoleDepth * weights[WEIGHT_INDEX_MAX_HOLE_DEPTHS];
    }
    
    // calculate sum of size of holes 
    public static int numHoles(int[][] field, int[] top) {
    	int totalHoles = 0; 
    	for (int c = 0; c < Constants.COLS; c++) {
    		for (int r = 0; r < top[c] - 1; r++) {
    			if (field[r][c] == 0) {
    				totalHoles++; 
    			}
    		}
    	}
    	
    	return totalHoles; 
    }
    
    public static int maxHeight(int top[], int numRowsCleared) { 
    	int maxHeight = top[0];  
    	for (int i = 1; i < top.length; i++) {
    		maxHeight = Math.max(maxHeight, top[i]); 
    	}

    	return maxHeight - numRowsCleared; 
    }
    
    public static double averageHeight(int top[], int numRowsCleared) { 
    	double totalHeight = 0;
    	for (int i = 0; i < top.length; i++) {
    		totalHeight += top[i]; 
    	}
    	return totalHeight/Constants.COLS - numRowsCleared; 
    }
    
    public static double boardSmoothnessAbsolute(int[] topDiff) {
    	double diffSum = 0; 
    	for (int i = 0; i < topDiff.length; i++) {
    		diffSum += topDiff[i]; 
    	}
    	
    	return diffSum/Constants.COLS; 
    }
    
    public static double boardSmoothnessSquared(int[] topDiff) {
    	double squaredDiffSum = 0; 
    	for (int i = 0; i < topDiff.length; i++) {
    		squaredDiffSum += (topDiff[i] * topDiff[i]); 
    	}
    	
    	return squaredDiffSum/Constants.COLS; 
    }
    
    public static int maxAdjacentDiff(int[] topDiff) {
    	int maxDiff = topDiff[0]; 
    	for (int i = 1; i < topDiff.length; i++) {
    		maxDiff = Math.max(maxDiff, topDiff[i]); 
    	}
    	
    	return maxDiff; 
    }
    
    // calculate number of rows with holes 
    public static int numRowsWithHoles(int field[][], int[] top) {
    	HashSet<Integer> rowsWithHoles = new HashSet<>(); 
    	for (int c = 0; c < Constants.COLS; c++) {
    		for (int r = 0; r < top[c] - 1; r++) {
    			if (field[r][c] == 0) {
    				rowsWithHoles.add(r);  
    			}
    		}
    	}
    	return rowsWithHoles.size(); 
    }
    
    // calculate the sum of hole depths. 
    // hole depth number of cells between the top of hole and top of the col 
    public static int totalHoleDepths(ArrayList<Integer> holeDepths) {
    	int totalHoleDepths = 0; 
    	for (int holeDepth : holeDepths) {
    		totalHoleDepths += holeDepth; 
    	}
    	return totalHoleDepths; 
    }
    
    public static int maxHoleDepth(ArrayList<Integer> holeDepths) {
    	return Collections.max(holeDepths); 
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

     public static final int FEATURE_COUNT = 10;

     public static final int SEARCH_TRIALS = 100;
     public static final int SEARCH_DEPTH = 5;

     // IMPORTANT: Replace with completed weights before submission
     // public static double[] WEIGHTS =
     // new double[]{0, 0, 0, 0, 0, 0, 0, 0};
     public static double[] WEIGHTS = PlayerTraining.loadWeights();

     public static final int COLS = 10;
     public static final int ROWS = 21;
     public static final int N_PIECES = 7;

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

     // all legal moves - first index is piece type - then a list of 2-length
     // arrays
     public static int[][][] LEGAL_MOVES = initLegalMoves();
     
     // initialize legalMoves
     private static int[][][] initLegalMoves() {
         int[][][] ret = new int[N_PIECES][][];
         // for each piece type
         for (int i = 0; i < N_PIECES; i++) {
             // figure number of legal moves
             int n = 0;
             for (int j = 0; j < P_ORIENTS[i]; j++) {
                 // number of locations in this orientation
                 n += COLS + 1 - P_WIDTH[i][j];
             }
             // allocate space
             ret[i] = new int[n][2];
             // for each orientation
             n = 0;
             for (int j = 0; j < P_ORIENTS[i]; j++) {
                 // for each slot
                 for (int k = 0; k < COLS + 1 - P_WIDTH[i][j]; k++) {
                     ret[i][n][ORIENT] = j;
                     ret[i][n][SLOT] = k;
                     n++;
                 }
             }
         }
         return ret;
     }
 }

    
}
