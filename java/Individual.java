import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;

/**
 * Represents a single set of weights for the atomic heuristics.
 * Acts as a single individual in the genetic algorithm.
 */
public class Individual implements Comparable<Individual> {
    private double fitness = -1;
    private double[] weights = new double[PlayerSkeleton.Constants.FEATURE_COUNT];

    public static final int PROB_PERCENT_MUTATE = 1;
    public static final int RAND_BOUND_MUTATE = 2*100 / PROB_PERCENT_MUTATE;
    public static final Random RAND_GENERATOR = new Random();

    public Individual() {
        this.randomize();
    }
    public Individual(Individual o) {
        this.setWeights(o.getWeights());
    }

    public double getFitness() {
        if (!hasComputedFitness()) {
            computeFitness();
        }
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public double getWeight(int index) {
        return weights[index];
    }

    /*
     * Return deep copy of the underlying weight array.
     */
    public double[] getWeights() {
        return weights.clone();
    }

    public void setWeight(int index, double weight) {
        this.weights[index] = weight;
    }

    /*
     * Set underlying weight array to a deep copy of the argument array.
     */
    public void setWeights(double[] weights) {
        this.weights = weights.clone();
    }

    /**
     * Fill the weight array with random values in the range of [-1, 1].
     * This is useful as an initializer.
     */
    public void randomize() {
        boolean[] isBad = new boolean[] { true, false, true, true, true, true, true, true, true, true, true, true, true};
        for (int i = 0; i < weights.length; i++) {
            double weight = RAND_GENERATOR.nextDouble();
            if (isBad[i]) {
                weight = -weight;
            }
            this.setWeight(i, weight);
        }
    }

    /**
     * Runs several Tetris games, each with some number of iterations (see Constants).
     * Assigns the average score at the end of them all to fitness field.
     *
     * Should be parallelized.
     */
    public void computeFitness() {
        if (!hasComputedFitness()) {
            double gameFitnessSum = 0;
            // Play the Tetris game NUM_TRAIN_GAMES times
            for (int gameNum = 0; gameNum < GeneticAlgorithm.NUM_TRAIN_GAMES; gameNum++) {
                gameFitnessSum += this.playGame();
            }
            fitness = gameFitnessSum / GeneticAlgorithm.NUM_TRAIN_GAMES;
        }
    }

    /**
     * Plays a game (of at most some fixed number of iterations)
     * using the current individual's weights to form the heuristic
     * that is used in the move decision.
     * Returns the score (number of rows cleared) at the end of the game.
     */
    public double playGame() {
        State s = new State();
        //TFrame t = new TFrame(s);
        // run the game for at most NUM_TRAIN_ITERS iterations
        while (true) { // TODO: CLEAN UP THIS FUCK SHIT
            // if game over, just end and balik kampong
            if (s.hasLost()) break;
            // make next move decision
            int[][] legalMoves = s.legalMoves();
            // this will contain the int[] representing the best move under this individual's  heuristic weights
            int[] bestMove = null;
            // this is the worst possible heuristic value given a heuristic function
            double bestHeuristicVal = Double.NEGATIVE_INFINITY;
            // try all the legal moves on clones of the current board
            for (int[] legalMove : legalMoves) {
                // clone the board
                int[][] fieldCopy = PlayerSkeleton.copyField(s.getField());
                // apply move on the board clone, using whatever the next piece was supposed to be on the original board
                fieldCopy = PlayerSkeleton.applyMove(fieldCopy, s.getNextPiece(), legalMove);
                // get the heuristic value of the cloned board after the move
                double heuristicVal = PlayerSkeleton.heuristic(fieldCopy, this.weights);
                // update the best heuristic value and the move that caused it
                if (heuristicVal > bestHeuristicVal) {
                    bestMove = legalMove;
                    bestHeuristicVal = heuristicVal;
                }
            }
            // at this point, bestMove is the actual best move

            // make the actual move
            s.makeMove(bestMove);
            //s.draw();           // [DISPLAY]
            //s.drawNext(0, 0);   // [DISPLAY]
            /*
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */
        }
        //t.dispose();
        return s.getRowsCleared();
    }

    /**
     * Change a random value in the array to something else.
     * This is meant to be used in the mutation step of the genetic algorithm.
     */
    public void mutate() {
        /*
        for (int indexToMutate = 0; indexToMutate < weights.length; indexToMutate++) {
            double mutationAmount = RAND_GENERATOR.nextDouble() * 2 - 1;
            weights[indexToMutate] += mutationAmount;
        }
        */
        weights[RAND_GENERATOR.nextInt(weights.length)] += 20.0 * (2.0*RAND_GENERATOR.nextDouble() - 1.0);
    }

    /**
     * Returns true if Individual has a computed fitness, otherwise false.
     *
     * @return
     */
    public boolean hasComputedFitness() {
        return fitness >= 0; // i.e. cleared 0 or more rows
    }
    
    /**
     * Resets the Individual's fitness to 0
     */
    public void resetFitness() {
        fitness = -1;
    }

    @Override
    public int compareTo(Individual o) {
        // Reverse compare/sort
        if (this.getFitness() < o.getFitness()) {
            return 1;
        } else if (this.getFitness() > o.getFitness()) {
            return -1;
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < weights.length; i++) {
            builder.append(weights[i]);
            builder.append(' ');
        }
        return builder.toString();
    }
    
    public void loadIndividual(String filePath) {
        Path path = Paths.get(filePath);
        try {
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            for (int i = 0; i < PlayerSkeleton.Constants.FEATURE_COUNT; i++) {
                this.weights[i] = scanner.nextDouble();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveIndividual(String filePath) {
        Path path = Paths.get(filePath);
        BufferedWriter writer;
        try {
            writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            for (int i = 0; i < PlayerSkeleton.Constants.FEATURE_COUNT; i++) {
                writer.write(Double.toString(this.weights[i]));
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Individual clone() {
        Individual ret = new Individual();
        ret.weights = this.weights.clone();
        return ret;
    }
    
    public static void main(String[] args) {
        Individual individual = new Individual();
        individual.weights = new double[]{-2.525531182203875,
                                          6.271239133059189,
                                          -0.06277814190992925,
                                          5.503462370977285,
                                          -0.9415663203740668,
                                          -1.1817283810666752,
                                          0.2760817349661713,
                                          -11.331052073843246};
        double score = individual.playGame();
        System.out.println(score);
    }
}
