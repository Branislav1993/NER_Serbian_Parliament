package rs.fon.ai.ner.main;

import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import rs.fon.ai.ner.util.Util;

public class NERParliament {
	public static final String API_URL = "http://localhost:8080/api/api/sessions/2083/speeches?limit=1&page=100";
	public static final String CLASSIFIER = "src/resource/java/myClassifier/ner-model-105k.ser.gz";

	public static void main(String[] args) throws Exception {

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(CLASSIFIER);

		// getting all speeches
//		List<List<CoreLabel>> corpus = Util.getTokenizedSpeeches(API_URL);
		List<List<CoreLabel>> corpus = Util.loadTokenizedCorpus();

		for (List<CoreLabel> document : corpus) {
			System.out.println(document.size());
			List<CoreLabel> results = classifier.classifySentence(document);
			for (CoreLabel cl : results) {
//				System.out.println(cl.originalText() + " " + cl.getString(edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation.class));
				System.out.println(cl.toShorterString());
			}
		}
	}

}
