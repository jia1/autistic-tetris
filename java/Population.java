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
        return null;
    }

    /*
     * Select two parents from the entire population
     * randomly over the population fitness distribution.
     * Breed the parents, produce a child and mutate the child.
     */
    public Individual generateChild(){
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
}
