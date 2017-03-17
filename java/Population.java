import java.util.Arrays;
import java.util.Random;

public class Population {
    Individual[] individuals;
    double cumulativeFitness = 0;
    double[] normalizedFitness;

    public static int NUM_PARENTS_PER_REPRODUCTION = 2;
    public static Random RAND_GENERATOR = new Random();

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
        return null;
    }

    /*
     * Select two parents from the entire population
     * randomly over the population fitness distribution.
     * Breed the parents, produce a child and mutate the child.
     */
    public Individual generateChild() {
        Individual[] parents = chooseParents();
        Individual child = reproduce(parents);
        child.mutate();
        return child;
    }

    /*
     * Calculate the fitness value of every individual.
     * Normalize them so that they sum to 1.
     */
    public void computeNormalizedFitness() {
        // TODO: We can parallelize the simulation for each individual.

        // Normalization
        if (cumulativeFitness > 0) {
            for (int i = 0; i < individuals.length; i++) {
                double currFitness = individuals[i].getFitness();
                if (!individuals[i].hasComputedFitness()) {
                    individuals[i].computeFitness();
                }
                individuals[i].setFitness(currFitness / cumulativeFitness);
            }
        }
    }

    /**
     * Chooses two Individual (aka parents) with "replacement" from an array of Individual.
     *
     * 1. Transform the array into an array of cumulative sums.
     * 2. Pick a random number in the range from zero up to the cumulative total.
     * 3. Use binary search on the cumulative array to locate the index into the original array.
     *
     * @return an array of size 2 of Individual
     */
    private Individual[] chooseParents() {
        double[] fitnessSums = new double[individuals.length];
        double currSum = 0;
        for (int i = 0; i < individuals.length; i++) {
            currSum += individuals[i].getFitness();
            Arrays.fill(fitnessSums, i, individuals.length, currSum);
        }
        Individual[] parents = new Individual[2];
        for (int t = 0; t < NUM_PARENTS_PER_REPRODUCTION; t++) {
            int randNum = RAND_GENERATOR.nextInt((int)Math.ceil(currSum) + 1);
            int parentIndex = Arrays.binarySearch(fitnessSums, randNum);
            parents[t] = individuals[parentIndex];
        }
        cumulativeFitness = currSum;
        return parents;
    }

    /**
     * Reproduces a Individual (child) from two Individual (parents).
     * For each weight of the child, choose either parent to copy from.
     *
     * @param parents
     * @return
     */
    private Individual reproduce(Individual[] parents) {
        Individual child = new Individual();
        for (int i = 0; i < child.getWeights().length; i++) {
            int randIndex = RAND_GENERATOR.nextInt(NUM_PARENTS_PER_REPRODUCTION);
            child.setWeight(i, parents[randIndex].getWeight(i));
        }
        return child;
    }
}
