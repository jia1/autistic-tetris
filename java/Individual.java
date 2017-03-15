import java.util.Random;

/*
 * Represents a single set of weights for the atomic heuristics.
 * Acts as a single individual in the genetic algorithm.
 */
public class Individual {
    private double[] weights = new double[PlayerSkeleton.Constants.FEATURE_COUNT];

    public static int PROB_PERCENT_MUTATE = 5;
    public static double WEIGHT_BOUND_UPP = 1.0;
    public static double WEIGHT_BOUND_LOW = -1.0;


    public double[] getWeights() {
        return weights;
    }
    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    /*
     * Fill with random values.
     * This is useful as an initializer.
     */
    public void randomize() {
        // TODO
    }

    /*
     * Runs several Tetris games, each with some number of iterations.
     * Returns the average score at the end of them all.
     *
     * Should be parallelized.
     */
    public double computeFitness() {
        // TODO
        return 0;
    }

    /*
     * Change a random value in the array to something else.
     * This is meant to be used in the mutation step of a
     * genetic algorithm.
     */
    public void mutate() {
        Random randGen = new Random();
        for (int i = 0; i < weights.length; i++) {
            int randNum = randGen.nextInt(2*100/PROB_PERCENT_MUTATE);
            if (randNum == 0) {
                weights[i] += Math.min(WEIGHT_BOUND_UPP, randGen.nextDouble());
            } else if (randNum == 1) {
                weights[i] -= Math.max(WEIGHT_BOUND_LOW, randGen.nextDouble());
            }
        }
    }

    /*
     * Return true if this set of weights is good enough.
     */
    public boolean isFitEnough() {
        // TODO
        return false;
    }
}
