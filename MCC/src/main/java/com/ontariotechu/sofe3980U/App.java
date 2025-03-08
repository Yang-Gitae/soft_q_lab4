package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.*;

/**
 * Evaluates multiclass classification models by computing cross entropy and constructing a confusion matrix.
 */
public class App {
    public static void main(String[] args) {
        String filePath = "model.csv";
        FileReader filereader;
        List<String[]> allData;
        try {
            filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            allData = csvReader.readAll();
        } catch (Exception e) {
            System.out.println("Error reading the CSV file");
            return;
        }

        // Variables to hold computed values
        double crossEntropy = 0;
        int numClasses = 5; // Assuming there are 5 classes
        int[][] confusionMatrix = new int[numClasses][numClasses];

        // Parse the CSV data
        for (String[] row : allData) {
            int actualClass = Integer.parseInt(row[0]) - 1; // Convert 1-based index to 0-based
            double[] predictedProbs = new double[numClasses];
            for (int i = 0; i < numClasses; i++) {
                predictedProbs[i] = Double.parseDouble(row[i + 1]);
            }
            
            // Update cross entropy
            crossEntropy += -Math.log(predictedProbs[actualClass] + 1e-12); // Add small value to avoid log(0)

            // Determine the predicted class
            int predictedClass = argMax(predictedProbs);
            confusionMatrix[actualClass][predictedClass]++;
        }

        // Finalize the calculation of cross entropy
        crossEntropy /= allData.size();

        // Print the results
        System.out.println("Cross-Entropy: " + crossEntropy);
        System.out.println("Confusion Matrix:");
        for (int i = 0; i < numClasses; i++) {
            for (int j = 0; j < numClasses; j++) {
                System.out.print(confusionMatrix[i][j] + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Finds the index of the largest element in the array.
     */
    private static int argMax(double[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
