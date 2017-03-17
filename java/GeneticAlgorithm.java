import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithm {
    public static Population population = new Population(Constants.POP_SIZE);
    public static Individual[] fittestIndividuals = new Individual[10];

    public static double[] obtainBestWeights() {
        for (int t = 0; t < Constants.POP_ITER; t++) {
            population.computeNormalizedFitness();
            List popList = Arrays.asList(population.individuals);
            Collections.sort(popList);
            Individual[] currFittest = (Individual[]) popList.toArray();
            fittestIndividuals = merge(fittestIndividuals, currFittest);
            population = population.breedNewGeneration();
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
