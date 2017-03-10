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
        double[] weights = PlayerTraining.loadWeights();
        PlayerSkeleton p = new PlayerSkeleton(weights);
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

}
