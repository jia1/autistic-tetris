import java.util.Arrays;
import java.util.Random;

public class Population {
    Individual[] individuals;
    double[] normalizedFitness;

    public static int NUM_PARENTS_PER_REPRODUCTION = 2;

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
        // TODO
        return null;
    }

    /*
     * Calculate the fitness value of every individual.
     * Normalize them so that they sum to 1.
     */
    public void computeNormalizedFitness() {
        // TODO
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
        int[] cumulativeFitness = new int[individuals.length];
        int currSum = 0;
        int lastIndex = individuals.length - 1;
        for (int i = 0; i <= lastIndex; i++) {
            currSum += individuals[i].getFitness();
            Arrays.fill(cumulativeFitness, i, lastIndex, currSum);
        }
        Random randGen = new Random();
        Individual[] parents = new Individual[2];
        for (int t = 0; t < NUM_PARENTS_PER_REPRODUCTION; t++) {
            int randNum = randGen.nextInt(currSum + 1);
            int parentIndex = Arrays.binarySearch(cumulativeFitness, randNum);
            parents[t] = individuals[parentIndex];
        }
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
        Random randGen = new Random();
        for (int i = 0; i < child.getWeights().length; i++) {
            int randIndex = randGen.nextInt(NUM_PARENTS_PER_REPRODUCTION);
            child.setWeight(i, parents[randIndex].getWeight(i));
        }
        return child;
    }
}
