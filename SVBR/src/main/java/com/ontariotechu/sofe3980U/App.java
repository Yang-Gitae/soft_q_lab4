package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import com.opencsv.*;

/**
 * Evaluate Single Variable Binary Regression
 *
 */
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
		List<Integer> actual = new ArrayList<>();
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
			int y_true = Integer.parseInt(row[0]);  // Convert actual values to integers (0 or 1)
			double y_pred = Double.parseDouble(row[1]);  // Predicted probability

			actual.add(y_true);
			predicted.add(y_pred);

			if (count < 10) {
				System.out.println(y_true + "  \t  " + y_pred);
			}
			count++;
		}

		double bce = calculateBCE(actual, predicted);
		int[] confusionMatrix = calculateConfusionMatrix(actual, predicted, 0.5);
		double accuracy = calculateAccuracy(confusionMatrix);
		double precision = calculatePrecision(confusionMatrix);
		double recall = calculateRecall(confusionMatrix);
		double f1Score = calculateF1Score(precision, recall);
		double aucRoc = calculateAUC(actual, predicted);

		System.out.println("\n--- Evaluation Metrics ---");
		System.out.println("Binary Cross-Entropy (BCE): " + bce);
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("F1 Score: " + f1Score);
		System.out.println("AUC-ROC: " + aucRoc + "\n");
	}

	public static double calculateBCE(List<Integer> actual, List<Double> predicted) {
		double sum = 0.0;
		int n = actual.size();
		for (int i = 0; i < n; i++) {
			double y_hat = predicted.get(i);
			double y = actual.get(i);
			sum += y * Math.log(y_hat + 1e-10) + (1 - y) * Math.log(1 - y_hat + 1e-10);
		}
		return -sum / n;
	}

	public static int[] calculateConfusionMatrix(List<Integer> actual, List<Double> predicted, double threshold) {
		int TP = 0, FP = 0, TN = 0, FN = 0;
		for (int i = 0; i < actual.size(); i++) {
			int predictedLabel = predicted.get(i) >= threshold ? 1 : 0;
			if (actual.get(i) == 1 && predictedLabel == 1) TP++;
			if (actual.get(i) == 0 && predictedLabel == 1) FP++;
			if (actual.get(i) == 0 && predictedLabel == 0) TN++;
			if (actual.get(i) == 1 && predictedLabel == 0) FN++;
		}
		return new int[]{TP, FP, TN, FN};
	}

	public static double calculateAccuracy(int[] cm) {
		int TP = cm[0], FP = cm[1], TN = cm[2], FN = cm[3];
		return (double) (TP + TN) / (TP + TN + FP + FN);
	}

	public static double calculatePrecision(int[] cm) {
		int TP = cm[0], FP = cm[1];
		return TP + FP == 0 ? 0 : (double) TP / (TP + FP);
	}

	public static double calculateRecall(int[] cm) {
		int TP = cm[0], FN = cm[3];
		return TP + FN == 0 ? 0 : (double) TP / (TP + FN);
	}

	public static double calculateF1Score(double precision, double recall) {
		return (precision + recall == 0) ? 0 : 2 * (precision * recall) / (precision + recall);
	}

	public static double calculateAUC(List<Integer> actual, List<Double> predicted) {
		int numPos = 0, numNeg = 0;
		for (int label : actual) {
			if (label == 1) numPos++;
			else numNeg++;
		}

		double[] tpr = new double[101];
		double[] fpr = new double[101];
		for (int i = 0; i <= 100; i++) {
			double threshold = i / 100.0;
			int[] cm = calculateConfusionMatrix(actual, predicted, threshold);
			tpr[i] = cm[0] / (double) numPos; // True Positive Rate
			fpr[i] = cm[1] / (double) numNeg; // False Positive Rate
		}

		double auc = 0;
		for (int i = 1; i <= 100; i++) {
			auc += (tpr[i - 1] + tpr[i]) * (fpr[i - 1] - fpr[i]) / 2.0;
		}
		return auc;
	}
}
