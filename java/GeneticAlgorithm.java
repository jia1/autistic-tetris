public class GeneticAlgorithm {
    public static double obtainBestWeights() {
        // TODO
    }
}

public class Population {
    Individual[] individuals;
    double[] normalizedFitness;

    Population(int pop_size) {
        this.individuals = new Individual[pop_size];
        this.normalizedFitness = new double[pop_size];
    }

    /*
     * Replace the old population with a new one
     * of the same size, with better characteristics.
     * This is a full iteration of the genetic algorithm.
     *
     * Should be parallelized.
     */
    public Population breedNewGeneration() {
        // TODO
    }

    /*
     * Select two parents from the entire population
     * randomly over the population fitness distribution.
     * Breed the parents, produce a child and mutate the child.
     */
    public Individual generateChild(){
        // TODO
    }

    /*
     * Calculate the fitness value of every individual.
     * Normalize them so that they sum to 1.
     */
    public void computeNormalizedFitness() {
        // TODO
    }
}

/*
 * Represents a single set of weights for the atomic heuristics.
 * Acts as a single individual in the genetic algorithm.
 */
public class Individual {
    private double[] weights = new double[FEATURE_COUNT];


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
    }

    /*
     * Change a random value in the array to something else.
     * This is meant to be used in the mutation step of a
     * genetic algorithm.
     */
    public void mutate() {
        // TODO
    }

    /*
     * Return true if this set of weights is good enough.
     */
    public boolean isFitEnough() {
        // TODO
    }
}
