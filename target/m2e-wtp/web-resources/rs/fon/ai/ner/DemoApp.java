package rs.fon.ai.ner;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

public class DemoApp {

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "src/resource/java/classifiers/english.all.3class.distsim.crf.ser.gz";

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

		String[] example = { "Good afternoon Rajat Raina, how are you today?",
				"I go to school at Stanford University, which is located in California." };

		for (String str : example) {
			System.out.print(classifier.classifyToString(str, "tsv", false));
		}

	}

}
