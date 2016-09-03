package rs.fon.ai.ner.main;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class PipelineExample {

	public static void main(String[] args) throws IOException {
		// build pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		String text = " I am a sentence.  I am another sentence.";
		
		Annotation annotation = new Annotation(text);
		pipeline.annotate(annotation);
		System.out.println(annotation.get(TextAnnotation.class));
		
		List<CoreMap> sentences = annotation.get(SentencesAnnotation.class);
		
		for (CoreMap sentence : sentences) {
			
			System.out.println(sentence.get(TokensAnnotation.class));
			
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				System.out.println(token.after() != null);
				System.out.println(token.before() != null);
				System.out.println(token.beginPosition());
				System.out.println(token.endPosition());
			}
		}
	}

}