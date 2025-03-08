package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import com.opencsv.*;

public class App {
	public static void main(String[] args) {
		String[] modelFiles = {"model_1.csv", "model_2.csv", "model_3.csv"};

		for (String filePath : modelFiles) {
			System.out.println("\nProcessing file: " + filePath);
			evaluateModel(filePath);
		}
	}

	public static void evaluateModel(String filePath) {
		FileReader filereader;
		List<String[]> allData;
		List<Double> actual = new ArrayList<>();
		List<Double> predicted = new ArrayList<>();

		try {
			filereader = new FileReader(filePath);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			allData = csvReader.readAll();
		} catch (Exception e) {
			System.out.println("Error reading the CSV file: " + filePath);
			return;
		}

		System.out.println("First 10 rows:");
		int count = 0;
		for (String[] row : allData) {
			double y_true = Double.parseDouble(row[0]);
			double y_pred = Double.parseDouble(row[1]);

			actual.add(y_true);
			predicted.add(y_pred);

			if (count < 10) {
				System.out.println(y_true + "  \t  " + y_pred);
			}
			count++;
		}

		double mse = calculateMSE(actual, predicted);
		double mae = calculateMAE(actual, predicted);
		double mare = calculateMARE(actual, predicted);

		System.out.println("\n--- Evaluation Metrics ---");
		System.out.println("MSE: " + mse);
		System.out.println("MAE: " + mae);
		System.out.println("MARE: " + mare + "%\n");
	}

	public static double calculateMSE(List<Double> actual, List<Double> predicted) {
		double sum = 0.0;
		int n = actual.size();
		for (int i = 0; i < n; i++) {
			double error = actual.get(i) - predicted.get(i);
			sum += error * error;
		}
		return sum / n;
	}

	public static double calculateMAE(List<Double> actual, List<Double> predicted) {
		double sum = 0.0;
		int n = actual.size();
		for (int i = 0; i < n; i++) {
			sum += Math.abs(actual.get(i) - predicted.get(i));
		}
		return sum / n;
	}

	public static double calculateMARE(List<Double> actual, List<Double> predicted) {
		double sum = 0.0;
		int n = actual.size();
		double epsilon = 1e-10;
		for (int i = 0; i < n; i++) {
			sum += Math.abs(actual.get(i) - predicted.get(i)) / (Math.abs(actual.get(i)) + epsilon);
		}
		return (sum / n) * 100;
	}
}
