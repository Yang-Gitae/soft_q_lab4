package com.ontariotechu.sofe3980U;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class evaluates binary classification models by computing various metrics including AUC-ROC.
 * It reads model predictions from CSV files and calculates metrics such as BCE, Accuracy, Precision, Recall, F1 Score, and AUC-ROC.
 */
public class App {
    public static void main(String[] args) {
        // Array of file paths for CSV files to be processed
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};

        // Iterate over each file path
        for (String filePath : files) {
            try (FileReader filereader = new FileReader(filePath);
                 CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build()) {
                // Read all data from CSV
                List<String[]> allData = csvReader.readAll();
                // Convert data into Prediction objects and sort by predicted values
                List<Prediction> predictions = convertAndSortData(allData);

                // Calculate metrics
                double bce = calculateBCE(predictions);
                int[] confusionMatrix = calculateConfusionMatrix(predictions, 0.5);
                double accuracy = calculateAccuracy(confusionMatrix);
                double precision = calculatePrecision(confusionMatrix);
                double recall = calculateRecall(confusionMatrix);
                double f1Score = calculateF1Score(precision, recall);
                double aucRoc = calculateAUCROC(predictions, 100);

                // Output the calculated metrics for each file
                System.out.println("Metrics for " + filePath + ":");
                System.out.println("BCE: " + bce);
                System.out.println("Accuracy: " + accuracy);
                System.out.println("Precision: " + precision);
                System.out.println("Recall: " + recall);
                System.out.println("F1 Score: " + f1Score);
                System.out.println("AUC-ROC: " + aucRoc);
            } catch (Exception e) {
                // Handle exceptions related to file processing
                System.out.println("Error processing the CSV file " + filePath);
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts CSV data into a sorted list of Prediction objects.
     */
    private static List<Prediction> convertAndSortData(List<String[]> data) {
        List<Prediction> predictions = new ArrayList<>();
        for (String[] row : data) {
            double actual = Double.parseDouble(row[0]);
            double predicted = Double.parseDouble(row[1]);
            predictions.add(new Prediction(actual, predicted));
        }
        // Sort predictions based on the predicted probability
        Collections.sort(predictions, (p1, p2) -> Double.compare(p1.predicted, p2.predicted));
        return predictions;
    }

    /**
     * Calculates Binary Cross-Entropy (BCE) for the predictions.
     */
    private static double calculateBCE(List<Prediction> predictions) {
        double bce = 0;
        for (Prediction p : predictions) {
            double actual = p.actual;
            double predicted = p.predicted;
            // Log terms are protected against log(0) by adding a small constant (1e-12)
            bce += actual * Math.log(predicted + 1e-12) + (1 - actual) * Math.log(1 - predicted + 1e-12);
        }
        return -bce / predictions.size();  // Average BCE over all predictions
    }

    /**
     * Builds a confusion matrix for a given threshold.
     */
    private static int[] calculateConfusionMatrix(List<Prediction> predictions, double threshold) {
        int tp = 0, tn = 0, fp = 0, fn = 0;
        for (Prediction p : predictions) {
            if (p.predicted >= threshold) {
                if (p.actual == 1.0) tp++;  // True positive
                else fp++;  // False positive
            } else {
                if (p.actual == 1.0) fn++;  // False negative
                else tn++;  // True negative
            }
        }
        return new int[]{tp, tn, fp, fn};
    }

    /**
     * Calculates the accuracy from the confusion matrix.
     */
    private static double calculateAccuracy(int[] matrix) {
        double total = matrix[0] + matrix[1] + matrix[2] + matrix[3];
        return (matrix[0] + matrix[1]) / total;
    }

    /**
     * Calculates the precision from the confusion matrix.
     */
    private static double calculatePrecision(int[] matrix) {
        return matrix[0] / (double) (matrix[0] + matrix[2]);
    }

    /**
     * Calculates the recall from the confusion matrix.
     */
    private static double calculateRecall(int[] matrix) {
        return matrix[0] / (double) (matrix[0] + matrix[3]);
    }

    /**
     * Calculates the F1 Score from precision and recall.
     */
    private static double calculateF1Score(double precision, double recall) {
        return 2 * (precision * recall) / (precision + recall);
    }

    /**
     * Calculates the Area Under the Curve (AUC) for the Receiver Operating Characteristic (ROC) curve using the trapezoidal rule.
     */
    private static double calculateAUCROC(List<Prediction> predictions, int numThresholds) {
        double aucRoc = 0;
        List<Double> tprList = new ArrayList<>();
        List<Double> fprList = new ArrayList<>();

        long totalPositives = predictions.stream().filter(p -> p.actual == 1.0).count();
        long totalNegatives = predictions.size() - totalPositives;

        for (int i = 0; i <= numThresholds; i++) {
            double threshold = i / (double) numThresholds;
            long tp = 0, fp = 0;
            for (Prediction p : predictions) {
                if (p.predicted >= threshold) {
                    if (p.actual == 1.0) tp++;
                    else fp++;
                }
            }
            double tpr = (double) tp / totalPositives;
            double fpr = (double) fp / totalNegatives;
            tprList.add(tpr);
            fprList.add(fpr);
        }

        for (int i = 1; i <= numThresholds; i++) {
            double x1 = fprList.get(i-1), x2 = fprList.get(i);
            double y1 = tprList.get(i-1), y2 = tprList.get(i);
            aucRoc += Math.abs((y1 + y2) * (x2 - x1) / 2);
        }

        return aucRoc;
    }

    /**
     * A simple class representing a prediction with an actual and predicted value.
     */
    static class Prediction implements Comparable<Prediction> {
        double actual;
        double predicted;

        Prediction(double actual, double predicted) {
            this.actual = actual;
            this.predicted = predicted;
        }

        @Override
        public int compareTo(Prediction o) {
            return Double.compare(this.predicted, o.predicted);
        }
    }
}
