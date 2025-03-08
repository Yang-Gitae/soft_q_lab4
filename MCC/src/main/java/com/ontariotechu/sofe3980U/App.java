package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;
import com.opencsv.*;

public class App
{
	public static void main( String[] args )
	{
		String filePath="model.csv";
		FileReader filereader;
		List<String[]> allData;
		List<Integer> actual = new ArrayList<>();
		List<double[]> predicted = new ArrayList<>();

		try{
			filereader = new FileReader(filePath);
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			allData = csvReader.readAll();
		}
		catch(Exception e){
			System.out.println( "Error reading the CSV file" );
			return;
		}

		System.out.println("First 10 rows:");
		int count=0;
		for (String[] row : allData) {
			int y_true=Integer.parseInt(row[0]);
			double[] y_predicted = new double[5];

			for(int i=0;i<5;i++){
				y_predicted[i]=Double.parseDouble(row[i+1]);
			}

			actual.add(y_true);
			predicted.add(y_predicted);

			if (count<10){
				System.out.print(y_true);
				for(double value : y_predicted){
					System.out.print("  \t  "+value);
				}
				System.out.println();
			}
			count++;
		}

		double crossEntropy = calculateCrossEntropy(actual, predicted);
		int[][] confusionMatrix = buildConfusionMatrix(actual, predicted);

		System.out.println("\n--- Evaluation Metrics ---");
		System.out.println("Cross-Entropy Loss: " + crossEntropy);
		System.out.println("\nConfusion Matrix:");
		printConfusionMatrix(confusionMatrix);
	}

	public static double calculateCrossEntropy(List<Integer> actual, List<double[]> predicted) {
		double sum = 0.0;
		int n = actual.size();
		for (int i = 0; i < n; i++) {
			int trueLabel = actual.get(i) - 1;
			sum += Math.log(predicted.get(i)[trueLabel] + 1e-10);
		}
		return -sum / n;
	}

	public static int[][] buildConfusionMatrix(List<Integer> actual, List<double[]> predicted) {
		int numClasses = predicted.get(0).length;
		int[][] confusionMatrix = new int[numClasses][numClasses];

		for (int i = 0; i < actual.size(); i++) {
			int trueLabel = actual.get(i) - 1;
			int predictedLabel = argmax(predicted.get(i));
			confusionMatrix[trueLabel][predictedLabel]++;
		}
		return confusionMatrix;
	}

	public static int argmax(double[] array) {
		int bestIndex = 0;
		double bestValue = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > bestValue) {
				bestValue = array[i];
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	public static void printConfusionMatrix(int[][] matrix) {
		for (int[] row : matrix) {
			for (int value : row) {
				System.out.print(value + "\t");
			}
			System.out.println();
		}
	}
}
