import java.util.Arrays;
import java.util.Random;

public class Population {
    Individual[] individuals;
    double[] normalizedFitness;
    double[] summedFitness;
    double totalFitness = -1;

    public static int NUM_PARENTS_PER_REPRODUCTION = 2;
    public static Random RAND_GENERATOR = new Random();

    Population(int pop_size) {
        this.individuals = new Individual[pop_size];
        //initialize individuals array
        for(int i = 0; i < pop_size; i++){
            this.individuals[i] = new Individual();
        }
        this.normalizedFitness = new double[pop_size];
        //initialize summed fitness array
        this.summedFitness = new double[pop_size];
    }

    /**
     * NOTE: Run this function immediately after instantiation.
     *
     * 1. Calculate the fitness value of every individual.
     * 2. Gather the sums of fitness up to index i for every index i.
     * 3. Normalize the fitness values so that they all sum to 1.
     * 4. P.S. individuals[i].getFitness() is not normalized. Normalized
     *      fitness values are stored in normalizedFitness in the Population.
     */
    public void computeNormalizedFitness() {
        // TODO: Parallelize this loop
        for (Individual idv : individuals) {
            idv.computeFitness();
        }
        accumulateFitness();

        // Normalization
        for (int i = 0; i < individuals.length; i++) {
            double currFitness = individuals[i].getFitness();
            normalizedFitness[i] = currFitness / totalFitness;
        }
    }

    /**
     * Begin breeding after deriving the fitness values via computeNormalizedFitness
     *
     * Replace the old population with a new one of the same size with better
     * characteristics. This is one full iteration of the genetic algorithm.
     *
     * Should be parallelized.
     */
    public Population breedNewGeneration() {
        this.computeNormalizedFitness();
        Population nextPop = new Population(individuals.length);
        for (int p = 0; p < individuals.length; p++) {
            nextPop.individuals[p] = makeChild();
        }
        return nextPop;
    }

    /**
     * Select two parents from the entire population randomly
     * Over the population fitness distribution.
     * Breed the parents, produce a child and mutate the child.
     */
    public Individual makeChild() {
        Individual[] parents = chooseParents();
        Individual child = reproduce(parents);
        child.mutate();
        return child;
    }

    /**
     * Chooses two Individual (aka parents) with "replacement" from an
     * Array of Individual.
     *
     * 1. Transform the array into an array of cumulative sums.
     * 2. Pick a random number in the range from zero up to the cumulative total.
     * 3. Use binary search on the cumulative array to locate the index
     *      into the original array.
     *
     * @return an array of size 2 of Individual
     */
    private Individual[] chooseParents() {
        Individual[] parents = new Individual[NUM_PARENTS_PER_REPRODUCTION];
        for (int t = 0; t < NUM_PARENTS_PER_REPRODUCTION; t++) {
            int randNum = RAND_GENERATOR.nextInt((int)Math.ceil(totalFitness) + 1);
            int parentIndex = Arrays.binarySearch(summedFitness, randNum);
            if (parentIndex < 0) {
                parentIndex = -(parentIndex + 1);
            }
            parentIndex = Math.min(parentIndex, individuals.length - 1);
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
        for (int i = 0; i < child.getWeights().length; i++) {
            int randIndex = RAND_GENERATOR.nextInt(NUM_PARENTS_PER_REPRODUCTION);
            child.setWeight(i, parents[randIndex].getWeight(i));
        }
        return child;
    }

    /**
     * Creates sums of fitness and calculate the total
     * Amount of fitness for this population instance
     */
    private void accumulateFitness() {
        if (!hasAccumulatedFitness()) {
            totalFitness = 0;
            for (int i = 0; i < individuals.length; i++) {
                totalFitness += individuals[i].getFitness();
                Arrays.fill(summedFitness, i, individuals.length, totalFitness);
            }
        }
    }

    /**
     * Returns true if accumulateFitness() was already called once
     * For this population instance
     *
     * @return
     */
    private boolean hasAccumulatedFitness() {
        return totalFitness >= 0;
    }
    
    /*
     * Get a REFERENCE to the fittest individual in the population.
     */
    public Individual getFittestIndividual() {
        Individual bestIndividual = null;
        double bestFitnessScore = -1;
        for (Individual i : this.individuals) {
            if (i.getFitness() > bestFitnessScore) {
                bestIndividual = i;
                bestFitnessScore = i.getFitness();
            }
        }
        return bestIndividual;
    }
}
