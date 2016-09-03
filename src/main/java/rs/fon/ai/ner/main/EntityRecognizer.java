package rs.fon.ai.ner.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import rs.fon.ai.ner.domain.Corpus;
import rs.fon.ai.ner.domain.Doc;
import rs.fon.ai.ner.domain.RowEntity;

public class EntityRecognizer {

	public static final File PROP_FILE = new File("src/resource/java/myClassifier/parliament.prop");
	public static final String CLASSIFIER_PATH = "src/resource/java/myClassifier/ner-model-105k.ser.gz";
	public static final File DATASET_FILE = new File("C:/Users/Baki/Desktop/govori/valid/tokens.txt");

	public static void main(String[] args) throws Exception {

		 // loading properties for the new classifier
		 Properties props = new Properties();
		 props.load(new FileInputStream(PROP_FILE));
		
		 // creating classifier
		 CRFClassifier<CoreLabel> crf = new CRFClassifier<>(props);
		 crf.train();
		 crf.serializeClassifier(CLASSIFIER_PATH);
		//
		// // printing results
		// // System.out.print(crf.classifyToString(getDataset(), "tsv", false
		// ));

	}

	public static String getDataset() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(DATASET_FILE));
		String text = "";
		String line;
		Corpus corpus = new Corpus();
		List<Doc> documents = new LinkedList<>();
		List<RowEntity> entities = new LinkedList<>();

		while ((line = br.readLine()) != null) {
			if (line.length() == 0) {
				Doc doc = new Doc();
				doc.setEntities(entities);
				documents.add(doc);
				String govor = "";
				for (RowEntity rowEntity : entities) {
					govor +=rowEntity.getWord() + " ";
				}
				System.out.println(govor);
				entities = new LinkedList<>();
				continue;
			}
			String[] data = line.split("\\t");
			RowEntity re = new RowEntity(data[0], data[1]);
			entities.add(re);
		}
		corpus.setDocuments(documents);

		br.close();
		// System.out.println(text);

		return text;
	}

}
