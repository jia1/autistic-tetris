import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class GeneticAlgorithm {
    /*
    Iteration 12652
    Best Weights: -2.525531182203875 6.271239133059189 -0.06277814190992925 5.503462370977285 -0.9415663203740668 -1.1817283810666752 0.2760817349661713 -11.331052073843246 
    Best Fitness: 14293.000000
    Population Best Fitness: 14293.000000
     */
    
    public static final int NUM_TRAIN_GAMES = 5;
    public static final int POP_SIZE = 50;
    public static final int NUM_BREEDING_ITER = Integer.MAX_VALUE;
    public static final String WEIGHTS_PATH = "weights.txt";
    public static final String SECOND_WEIGHTS_PATH = "weights2.txt";
    public static final String ITERATION_PATH = "iteration.txt";
    
    public static Population population = new Population();
    public static Individual[] fittestIndividuals = new Individual[10]; // TODO: maybe no need so many
    public static Individual overallFittestIndividual = null;


    public static void train(String toLoadPopFilePathBase, String toSavePopFilePathBase) {
        int lastSavedIterationCount = loadIterationCount();
        if (toLoadPopFilePathBase != null) {
            //population.loadPopulation(toLoadPopFilePathBase);
            population.unifiedLoadPopulation(toLoadPopFilePathBase, lastSavedIterationCount);
        }

        for (int i = lastSavedIterationCount; i < NUM_BREEDING_ITER; i++) {            
            Population newGeneration = null;
            boolean isCheckpointTime = i % 25 == 0;
            boolean useSecondFile = i % 100 == 0;
            boolean toSavePopulation = i % 100 == 0;;

            if (isCheckpointTime) {
                long startTime = System.nanoTime();
                newGeneration = population.breedNewGeneration();
                long endTime = System.nanoTime();
                //System.out.printf("Time Spent: %d ns%n", endTime - startTime);

                //population.savePopulation(toSavePopFilePathBase);

                Individual fittestIndividual = population.getFittestIndividual();
                if (overallFittestIndividual == null || fittestIndividual.getFitness() > overallFittestIndividual.getFitness()) {
                    overallFittestIndividual = fittestIndividual;
                }
                int individualCount = 0;
                double totalFitness = 0;
                for (Individual individual : population.getIndividuals()) {
                    totalFitness += individual.getFitness();
                    individualCount++;
                }
                System.out.printf("Iteration %d\tBest Fitness: %f\tAverage Fitness: %f%n",
                        i,
                        overallFittestIndividual.getFitness(),
                        totalFitness / individualCount);
                double[] bestWeights = overallFittestIndividual.getWeights();
                //saving population to unified file once every 100 iterations
                if (toSavePopulation) {
                    population.unifiedSavePopulation(toSavePopFilePathBase, i);
                    saveIterationCount(i);
                }
                
                try {
					saveWeights(bestWeights, useSecondFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("Error writing to weight file");
					e.printStackTrace();
				}
            } else {
                newGeneration = population.breedNewGeneration();
            }

            population = newGeneration; // garbage collect the old one
        }
    }
    
    public static void testTraining() {
        population = new Population();
        for (int i = 0;; i++) {
            Population newGeneration = population.breedNewGeneration();

            Individual fittestIndividual = population.getFittestIndividual();
            if (overallFittestIndividual == null || fittestIndividual.getFitness() > overallFittestIndividual.getFitness()) {
                overallFittestIndividual = fittestIndividual;
            }
            //System.out.printf("Iteration %d%nBest Weights: %s%nBest Fitness: %f%nPopulation Best Fitness: %f%n%n",
            //        i,
            //        overallFittestIndividual.toString(),
            //        overallFittestIndividual.getFitness(),
            //        fittestIndividual.getFitness());
            System.out.printf("Iteration %d%nBest Weights: %s%nBest Fitness: %f%nPopulation Best Fitness: %f%n%n",
                    i,
                    overallFittestIndividual.toString(),
                    overallFittestIndividual.getFitness(),
                    fittestIndividual.getFitness());

            population = newGeneration; // garbage collect the old one
        }
    }
    
    public static void saveWeights(double[] weights, boolean useSecondFile) throws IOException {
    	Path path;
    	if(!useSecondFile){
    		path = Paths.get(WEIGHTS_PATH);
    	} else {
    		path = Paths.get(SECOND_WEIGHTS_PATH);
    	}
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        for (int i = 0; i < PlayerSkeleton.Constants.FEATURE_COUNT; i++) {
            writer.write(Double.toString(weights[i]));
            writer.write("\n");
        }
        writer.close();
    }
    
    public static int loadIterationCount() {
        Path path = Paths.get(ITERATION_PATH);
        int iterationCount = 0;
        try {
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            iterationCount = scanner.nextInt();
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iterationCount;
    }
    
    public static void saveIterationCount(int iterationCount) {
        Path path = Paths.get(ITERATION_PATH);
        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            writer.write(iterationCount);
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
    public static double[] train() {
        // Process the 1st generation
        for(int i = 0; i < fittestIndividuals.length; i++){
            fittestIndividuals[i] = new Individual();
        }
        
        population.computeNormalizedFitness();
        List<Individual> popList = Arrays.asList(population.individuals);
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
    */
    
    public static void main(String[] args) {
        //GeneticAlgorithm.testTraining();
        GeneticAlgorithm.train(null, "TESTPOP0");
    }
}
