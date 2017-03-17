import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PlayerTraining {

    public static final String WEIGHTS_PATH = "weights.txt";

    public static void main(String[] args) {
        double[] bestWeights = GeneticAlgorithm.obtainBestWeights();
        try {
            saveWeights(bestWeights);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static double[] loadWeights() {
        double[] weights = new double[Constants.FEATURE_COUNT];
        Path path = Paths.get(WEIGHTS_PATH);
        try {
            Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
            for (int i = 0; i < Constants.FEATURE_COUNT; i++) {
                weights[i] = scanner.nextDouble();
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return weights;
    }

    public static void saveWeights(double[] weights) throws IOException {
        Path path = Paths.get(WEIGHTS_PATH);
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        for (int i = 0; i < Constants.FEATURE_COUNT; i++) {
            writer.write(Double.toString(weights[i]));
            writer.write("\n");
        }
        writer.close();
    }

}
