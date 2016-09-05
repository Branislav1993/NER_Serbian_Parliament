package rs.fon.ai.ner.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import rs.fon.ai.ner.util.Util;

public class Testing {

	public static final File PROP_FILE = new File("src/resource/java/myClassifier/parliament.prop");
	public static final String PATH = "C:/Users/Baki/Desktop/testing/";
	public static final String CLASSIFIER_EXT = "classifier.ser.gz";
	public static final String DATASET_FILE = "C:/Users/Baki/Desktop/govori/valid/tokens-clean.txt";

	public static int TN_ALL = 0;
	public static int TP_ALL = 0;
	public static int FN_ALL = 0;
	public static int FP_ALL = 0;
	public static double PRECISION_ALL = 0;
	public static double RECALL_ALL = 0;
	public static double F1_ALL = 0;

	public static int NUM_FOLDS = 5;
	public static int NUM_ITERATIONS = 100;

	public static void main(String[] args) throws Exception {

		// loading properties for the new classifier
		Properties props = new Properties();
		props.load(new FileInputStream(PROP_FILE));

		List<List<CoreLabel>> dataset = Util.loadDateset(DATASET_FILE);

		for (int f = 0; f < NUM_ITERATIONS; f++) {
			for (int i = 1; i < NUM_FOLDS + 1; i++) {
				int TN = 0;
				int TP = 0;
				int FN = 0;
				int FP = 0;
				double PRECISION = 0;
				double RECALL = 0;
				double F1 = 0;
				String currentTrainingFileLocation = PATH + f + "I" + i+ "F" + "train.txt";
				String currentTestFileLocation = PATH + f + "I" + i+ "F" + "test.txt";
				String currentClassifierPath = PATH + f + "I" + i+ "F" + CLASSIFIER_EXT;

				Collections.shuffle(dataset);

				// creating test set for the current iteration
				List<List<CoreLabel>> testSet = new LinkedList<>();
				int testSize = 0;
				int numberOfDocs = 0;
				for (List<CoreLabel> document : dataset) {
					if (testSize > 24000) {
						System.out.println("Test size:" + testSize + " No of docs: " + numberOfDocs);
						testSize = 0;
						break;
					}
					testSet.add(document);
					testSize += document.size();
					numberOfDocs++;
				}

				// creating train set for the current iteration
				List<List<CoreLabel>> trainingSet = dataset.subList(numberOfDocs, dataset.size());
				System.out.println("Docs: " + numberOfDocs + " train size: " + trainingSet.size()+ " dataset size: " + dataset.size());
				numberOfDocs = 0;

				// saving training and testing data for current iteration
				serializeCoreLabels(currentTrainingFileLocation, trainingSet);
				serializeCoreLabels(currentTestFileLocation, testSet);

				// creating classifier
				props.setProperty("trainFile", currentTrainingFileLocation);
				CRFClassifier<CoreLabel> crf = new CRFClassifier<>(props);
				crf.train();
				crf.serializeClassifier(currentClassifierPath);

				// getting newly backed classifier
				AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(currentClassifierPath);

				List<List<CoreLabel>> testSetResults = new LinkedList<>();

				// classification of the test set
				for (List<CoreLabel> document : testSet) {
					List<CoreLabel> results = classifier.classifySentence(document);
					testSetResults.add(results);
				}

				// calculating the performance of the classifier on a test set
				for (int k = 0; k < testSetResults.size(); k++) {
					List<CoreLabel> testR = testSetResults.get(k);
					List<CoreLabel> testT = testSet.get(k);

					for (int j = 0; j < testR.size(); j++) {

						if (testT.get(j).getString(AnswerAnnotation.class).equals("PERSON")
								&& testR.get(j).getString(AnswerAnnotation.class).equals("PERSON")) {
							TP++;
						} else if (testT.get(j).getString(AnswerAnnotation.class).equals("PERSON")
								&& testR.get(j).getString(AnswerAnnotation.class).equals("O")) {
							FN++;
							System.out.println(
									testT.get(j).word() + " " + testT.get(j).getString(AnswerAnnotation.class));
							System.out.println(
									testR.get(j).word() + " " + testR.get(j).getString(AnswerAnnotation.class));
						} else if (testT.get(j).getString(AnswerAnnotation.class).equals("O")
								&& testR.get(j).getString(AnswerAnnotation.class).equals("PERSON")) {
							FP++;
							System.out.println(
									testT.get(j).word() + " " + testT.get(j).getString(AnswerAnnotation.class));
							System.out.println(
									testR.get(j).word() + " " + testR.get(j).getString(AnswerAnnotation.class));
						} else if (testT.get(j).getString(AnswerAnnotation.class).equals("O")
								&& testR.get(j).getString(AnswerAnnotation.class).equals("O")) {
							TN++;
						}

						// calculating evaluation measures
						PRECISION = (TP * 1.0) / (TP + FP);
						RECALL = (TP * 1.0) / (TP + FN);
						F1 = 2.0 * PRECISION * RECALL / (PRECISION + RECALL);
					}
				}
				String resultsPath = PATH + f + "I" + i+ "F" + "results.txt";
				printResults(TN, TP, FN, FP, PRECISION, RECALL, F1, 1, resultsPath);
				
				TN_ALL += TN;
				FN_ALL += FN;
				TP_ALL += TP;
				FP_ALL += FP;
				PRECISION_ALL += PRECISION;
				RECALL_ALL += RECALL;
				F1_ALL += F1;
				
				testSet = new LinkedList<>();
				testSetResults = new LinkedList<>();
				trainingSet = new LinkedList<>();
			}
		}
		
		printResults(TN_ALL, TP_ALL, FN_ALL, FP_ALL, PRECISION_ALL, RECALL_ALL, F1_ALL, NUM_ITERATIONS * NUM_FOLDS, PATH + "AVERAGE.txt");
		
	}

	public static void printResults(int TN, int TP, int FN, int FP, double PRECISION, double RECALL, double F1, int numIterations, String path) throws Exception {
		
		// printing classification performance results
		System.out.println("****************************************");
		System.out.println("TN: " + (TN*1.0)/numIterations + "\t" + "FP: " + (FP*1.0)/numIterations);
		System.out.println("FN: " + (FN*1.0)/numIterations + "\t" + "TP: " + (TP*1.0)/numIterations);
		System.out.println("Precision: " + PRECISION/numIterations);
		System.out.println("Recall: " + RECALL/numIterations);
		System.out.println("F1: " + F1/numIterations);
		System.out.println("****************************************");
		
		if(path == null) return;
		PrintWriter out = new PrintWriter(new FileWriter(path));
		out.println("TN: " + (TN*1.0)/numIterations + "\t" + "FP: " + (FP*1.0)/numIterations);
		out.println("FN: " + (FN*1.0)/numIterations + "\t" + "TP: " + (TP*1.0)/numIterations);
		out.println("Precision: " + PRECISION/numIterations);
		out.println("Recall: " + RECALL/numIterations);
		out.println("F1: " + F1/numIterations);
		out.close();
	}

	public static void serializeCoreLabels(String path, List<List<CoreLabel>> dataset) throws Exception {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path)));

		for (List<CoreLabel> document : dataset) {
			for (CoreLabel cl : document) {
				out.println(cl.word() + "\t" + cl.getString(AnswerAnnotation.class));
			}
			out.println();
		}
		out.close();
	}

}
