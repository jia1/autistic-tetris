import java.util.Random;

/**
 * Represents a single set of weights for the atomic heuristics.
 * Acts as a single individual in the genetic algorithm.
 */
public class Individual implements Comparable<Individual> {
    private double fitness = -1;
    private double[] weights = new double[PlayerSkeleton.Constants.FEATURE_COUNT];

    public static int PROB_PERCENT_MUTATE = 5;
    public static int RAND_BOUND_MUTATE = 2*100 / PROB_PERCENT_MUTATE;
    public static Random RAND_GENERATOR = new Random();

    public Individual() {
        this.randomize();
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

    public double[] getWeights() {
        return weights;
    }

    public void setWeight(int index, double weight) {
        this.weights[index] = weight;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    /**
     * Fill the weight array with random values in the range of [-1, 1].
     * This is useful as an initializer.
     */
    public void randomize() {
        for (int i = 0; i < weights.length; i++) {
            this.setWeight(i, RAND_GENERATOR.nextDouble() * 2 - 1);
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
            System.out.println("fitness " + fitness);
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
        for (int numIterations = 0; numIterations < GeneticAlgorithm.NUM_TRAIN_ITERS; numIterations++) {
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
                Thread.sleep(100);
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
        for (int i = 0; i < weights.length; i++) {
            int randNum = RAND_GENERATOR.nextInt(RAND_BOUND_MUTATE);
            if (randNum == 0) {
                weights[i] += RAND_GENERATOR.nextDouble();
            } else if (randNum == 1) {
                weights[i] -= RAND_GENERATOR.nextDouble();
            }
        }
    }

    /**
     * Returns true if Individual has a computed fitness, otherwise false.
     *
     * @return
     */
    public boolean hasComputedFitness() {
        return fitness >= 0; // i.e. cleared 0 or more rows
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
    
    public static void main(String[] args) {
        Individual individual = new Individual();
        double score = individual.playGame();
        System.out.println(score);
    }
}
