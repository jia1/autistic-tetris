import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class PlayerTraining {

    public static void main(String[] args) {
        // Todo (T + JY): Add training for weights here
    }
    
    public static double[] loadWeights() throws IOException {
        double[] weights = new double[Constants.FEATURE_COUNT];
        Path path = Paths.get(Constants.WEIGHTS_PATH);
        Scanner scanner = new Scanner(path, StandardCharsets.UTF_8.name());
        for (int i = 0; i < Constants.FEATURE_COUNT; i++) {
            weights[i] = scanner.nextDouble();
        }
        scanner.close();
        return weights;
    }

    public static void saveWeights(double[] weights) throws IOException {
        Path path = Paths.get(Constants.WEIGHTS_PATH);
        BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        for (int i = 0; i < Constants.FEATURE_COUNT; i++) {
            writer.write(Double.toString(weights[i]));
            writer.write("\n");
        }
        writer.close();
    }

}
