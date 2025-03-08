package com.ontariotechu.sofe3980U;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import java.io.FileReader;
import java.util.List;

/**
 * Evaluate Single Variable Continuous Regression
 */
public class App {
    public static void main(String[] args) {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        for (String filePath : files) {
            try (FileReader filereader = new FileReader(filePath);
                 CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build()) {
                List<String[]> allData = csvReader.readAll();
                double mse = calculateMSE(allData);
                double mae = calculateMAE(allData);
                double mare = calculateMARE(allData);

                System.out.println("Metrics for " + filePath + ":");
                System.out.println("MSE: " + mse);
                System.out.println("MAE: " + mae);
                System.out.println("MARE: " + mare);
            } catch (Exception e) {
                System.out.println("Error processing the CSV file " + filePath);
                e.printStackTrace();
            }
        }
    }

    public static double calculateMSE(List<String[]> data) {
        double sum = 0;
        for (String[] row : data) {
            float trueValue = Float.parseFloat(row[0]);
            float predictedValue = Float.parseFloat(row[1]);
            sum += Math.pow(trueValue - predictedValue, 2);
        }
        return sum / data.size();
    }

    public static double calculateMAE(List<String[]> data) {
        double sum = 0;
        for (String[] row : data) {
            float trueValue = Float.parseFloat(row[0]);
            float predictedValue = Float.parseFloat(row[1]);
            sum += Math.abs(trueValue - predictedValue);
        }
        return sum / data.size();
    }

    public static double calculateMARE(List<String[]> data) {
        double sum = 0;
        double epsilon = 0.0001;  // To avoid division by zero
        for (String[] row : data) {
            float trueValue = Float.parseFloat(row[0]);
            float predictedValue = Float.parseFloat(row[1]);
            sum += Math.abs(trueValue - predictedValue) / (Math.abs(trueValue) + epsilon);
        }
        return (sum / data.size()) * 100;
    }
}
