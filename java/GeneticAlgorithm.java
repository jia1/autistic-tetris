import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithm {
    
    public static final int NUM_TRAIN_GAMES = 1000;
    public static final int NUM_TRAIN_ITERS = 10;
    public static final int POP_SIZE = 1000;
    public static final int POP_ITER = 1000;
    
    public static Population population = new Population(POP_SIZE);
    public static Individual[] fittestIndividuals = new Individual[10];

    public static double[] obtainBestWeights() {
        // Process the 1st generation
        for(int i = 0; i < fittestIndividuals.length; i++){
            fittestIndividuals[i] = new Individual();
        }
        
        population.computeNormalizedFitness();
        List popList = Arrays.asList(population.individuals);
        Collections.sort(popList);
        Individual[] currFittest = (Individual[]) popList.toArray();
        fittestIndividuals = merge(fittestIndividuals, currFittest);
        for (int t = 0; t < POP_ITER; t++) {
            // Breed then process
            System.out.println("At the " + t + " iteration.");
            
            population = population.breedNewGeneration();
            population.computeNormalizedFitness();
            popList = Arrays.asList(population.individuals);
            Collections.sort(popList);
            currFittest = (Individual[]) popList.toArray();
            fittestIndividuals = merge(fittestIndividuals, currFittest);
        }
        return fittestIndividuals[0].getWeights(); // temporary solution
    }

    private static Individual[] merge(Individual[] prevFittest, Individual[] currFittest) {
        Individual[] mergedFittest = new Individual[prevFittest.length];
        int pPtr = 0, cPtr = 0;
        for (int i = 0; i < prevFittest.length; i++) {
            if (prevFittest[pPtr].compareTo(currFittest[cPtr]) > 0) {
                mergedFittest[i] = currFittest[cPtr++];
            } else {
                mergedFittest[i] = prevFittest[pPtr++];
            }
        }
        return mergedFittest;
    }
}
