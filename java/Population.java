import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Population {
    Individual[] individuals;
    double[] normalizedFitness;
    double[] summedFitness;
    double totalFitness = -1;

    public static int NUM_PARENTS_PER_REPRODUCTION = 2;
    public static Random RAND_GENERATOR = new Random();

    public static ExecutorService threadPool = newFixedThreadPool(GeneticAlgorithm.POP_SIZE);
    public static Future<?>[] threadTerminationCheckers = new Future<?>[GeneticAlgorithm.POP_SIZE];

    Population() {
        this.individuals = new Individual[GeneticAlgorithm.POP_SIZE];
        //initialize individuals array
        for(int i = 0; i < individuals.length; i++){
            this.individuals[i] = new Individual();
        }
        this.normalizedFitness = new double[GeneticAlgorithm.POP_SIZE];
        //initialize summed fitness array
        this.summedFitness = new double[GeneticAlgorithm.POP_SIZE];
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
        this.computeAllFitness();
        Arrays.sort(this.individuals);

        // get rid of the weakest, replace with strongest
        individuals[individuals.length - 1] = individuals[0];
        this.computeNormalizedFitness(); // prepare for breeding

        Population nextPop = new Population();
        // keep the best half of the population
        for (int p = 0; p < individuals.length/2; p++) {
            nextPop.individuals[p] = this.individuals[p];
        }
        // breed the remainder, leave last slot empty for an immigrant
        for (int p = individuals.length/2; p < individuals.length - 1; p++) {
            nextPop.individuals[p] = makeChild();
        }
        nextPop.individuals[individuals.length - 1] = new Individual(); // introduce a mexican immigrant
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
            double alpha = RAND_GENERATOR.nextDouble();
            child.setWeight(i, alpha * parents[0].getWeight(i) + (1 - alpha) * parents[1].getWeight(i));
        }
        return child;
    }
    
    /**
     * Compute fitness values of all the individuals in parallel
     */
    public void computeAllFitness() {
        // run the threads
        for (int i = 0; i < individuals.length; i++) {
            final Individual idv = individuals[i];
            threadTerminationCheckers[i] = threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    idv.computeFitness();
                }
            });
        }

        // wait for threads to finish
        for (int i = 0; i < individuals.length; i++) {
            try {
                threadTerminationCheckers[i].get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.getCause();
                e.printStackTrace();
            }
        }
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
        accumulateFitness();
        // Normalization
        for (int i = 0; i < individuals.length; i++) {
            double currFitness = individuals[i].getFitness();
            normalizedFitness[i] = currFitness / totalFitness;
        }
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
    
    public void loadPopulation(String filePathBase) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i].loadIndividual(filePathBase + "_" + i);
        }
    }
    
    public void savePopulation(String filePathBase) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i].saveIndividual(filePathBase + "_" + i);
        }
    }
}
