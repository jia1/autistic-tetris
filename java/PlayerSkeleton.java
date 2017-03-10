import java.io.IOException;

public class PlayerSkeleton {

    private final double[] weights;

    public PlayerSkeleton(double[] weights) {
        this.weights = weights;
    }

    public int pickMove(State s, int[][] legalMoves) {
        // Todo (K): Implement
        return 0;
    }

    public double metaHeuristic(int[][] field) {
        // Todo (K): Implement
        return 0;
    }

    public double heuristic(int[][] field) {
        return heuristic(field, weights);
    }

    public static double heuristic(int[][] field, double[] weights) {
        // Todo (YY): Implement
        return 0;
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


    public static class Constants {
    
        public static final int FEATURE_COUNT = 8;
        
        public static final int SEARCH_TRIALS = 100;
        public static final int SEARCH_DEPTH = 5;
        
        // IMPORTANT: Replace with completed weights before submission
        //public static double[] WEIGHTS = new double[]{0, 0, 0, 0, 0, 0, 0, 0};
        public static double[] WEIGHTS = PlayerTraining.loadWeights();
        
    }
}
