import java.util.Random;

/*
 * Represents a single set of weights for the atomic heuristics.
 * Acts as a single individual in the genetic algorithm.
 */
public class Individual {
    private double fitness = -1;
    private double[] weights = new double[PlayerSkeleton.Constants.FEATURE_COUNT];

    public static int PROB_PERCENT_MUTATE = 5;
    public static double WEIGHT_BOUND_UPP = 1.0;
    public static double WEIGHT_BOUND_LOW = -1.0;

    public static int RAND_BOUND_MUTATE = 2*100/PROB_PERCENT_MUTATE;


    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getWeight(int index) {
        return weights[index];
    }

    public double[] getWeights() {
        return weights;
    }

    public void setWeight(int index, double weight) {
        this.weights[index] = weight;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    /*
     * Fill with random values.
     * This is useful as an initializer.
     */
    public void randomize() {
        // TODO: Discuss what the bounds are and whether they should
        // be adhered to at all times during the genetic algorithm.
    }

    /*
     * Runs several Tetris games, each with some number of iterations.
     * Returns the average score at the end of them all.
     *
     * Should be parallelized.
     */
    public double computeFitness() {
        if (hasComputedFitness()) { // fitness was previously calculated
            return fitness;
        } else {
            // TODO
            return 0;
        }
    }

    /*
     * Change a random value in the array to something else.
     * This is meant to be used in the mutation step of a
     * genetic algorithm.
     */
    public void mutate() {
        Random randGen = new Random();
        for (int i = 0; i < weights.length; i++) {
            int randNum = randGen.nextInt(RAND_BOUND_MUTATE);
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
        // TODO: Discuss and define what does it mean to be fit
        // Or adopt a time-limit / max-iterations approach
        return false;
    }

    /**
     * Returns true if Individual has a computed fitness, otherwise false.
     *
     * @return
     */
    public boolean hasComputedFitness() {
        return fitness >= 0;
    }
}
