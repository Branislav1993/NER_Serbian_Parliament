package rs.fon.ai.ner.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import rs.fon.ai.ner.domain.RowEntity;

public class Util {

	public static final File DATASET_FILE = new File("C:/Users/Baki/Desktop/govori/valid/tokens.txt");
	public static final File TEST_FILE = new File("C:/Users/Baki/Desktop/govori/valid/testing/test1.txt");

	public static int countTokens(File file) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(file));
		int i = 0;
		while (in.readLine() != null) {
			i++;
		}
		in.close();
		return i;
	}

	public static void tokenizeTextFromFileToFile(File fromFile, File toFile) throws Exception {
		PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(new FileReader(fromFile), new CoreLabelTokenFactory(), "");

		if (!toFile.exists()) {
			toFile.getParentFile().mkdirs();
			toFile.createNewFile();
		}

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(toFile)));

		while (ptbt.hasNext()) {
			CoreLabel label = ptbt.next();
			out.println(label);
		}
		out.close();
	}

	public static void fixDotsInTokens(File fromFile, File toFile) throws Exception {
		String allTokens = "";
		BufferedReader in = new BufferedReader(new FileReader(fromFile));

		String line = null;

		while ((line = in.readLine()) != null) {
			String s = line;
			if (line.contains(".") && line.length() > 1) {
				s = line.replace(".", "\n.\n");
			}
			allTokens += s + System.lineSeparator();
		}
		in.close();

		if (!toFile.exists()) {
			toFile.getParentFile().mkdirs();
			toFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(toFile.getAbsoluteFile()));
		bw.write(allTokens);
		bw.close();
	}

	public static void fillTokenswithDefaultType(File fromFile, File toFile) throws Exception {
		BufferedReader in = new BufferedReader(new FileReader(fromFile));

		List<RowEntity> rows = new ArrayList<>();
		String line = null;

		while ((line = in.readLine()) != null) {
			RowEntity r = new RowEntity(line);
			rows.add(r);
		}
		in.close();

		if (!toFile.exists()) {
			toFile.getParentFile().mkdirs();
			toFile.createNewFile();
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(toFile.getAbsoluteFile()));
		for (RowEntity row : rows) {
			bw.write(row.toString());
		}
		bw.close();
	}

	public static List<List<CoreLabel>> getTokenizedSpeeches(String stringUrl) throws Exception {

		List<List<CoreLabel>> corpus = new LinkedList<>();

		URL speechesURL = new URL(stringUrl);
		URLConnection connection = speechesURL.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		String all = "";
		while ((inputLine = in.readLine()) != null) {
			all += inputLine;
		}
		in.close();

		while (all == null) {
		}

		// parsing speeches from string to JSON
		JsonObject jo = new JsonParser().parse(all).getAsJsonObject();
		JsonArray json = jo.getAsJsonArray("dataArray");

		for (int j = 0; j < json.size(); j++) {
			List<CoreLabel> tokens = new LinkedList<>();
			JsonObject data = json.get(j).getAsJsonObject();
			String text = data.get("text").toString();

			// fixing missing space after end of the sentence
			text = text.replaceAll("\\.", ". ");
			text = text.replaceAll("\\?", "? ");
			text = text.replaceAll("!", "! ");
			text = text.replaceAll(":", ": ");

			// creating string reader because of PTBTokenizer input parameter
			StringReader reader = new StringReader(text);

			// creating tokens from text
			PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(reader, new CoreLabelTokenFactory(), "tokenizeNLs=false");
			while (ptbt.hasNext()) {
				tokens.add(ptbt.next());
			}
			corpus.add(tokens);
		}
		return corpus;
	}

	public static List<List<CoreLabel>> loadTokenizedCorpus() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(DATASET_FILE));
		String line;
		List<List<CoreLabel>> corpus = new LinkedList<>();

		List<CoreLabel> entities = new LinkedList<>();
		while ((line = br.readLine()) != null) {
			if (line.length() == 0) {
				corpus.add(entities);
				entities = new LinkedList<>();
				continue;
			}
			String[] data = line.split("\\t");
			CoreLabel cl = new CoreLabel();
			cl.setWord(data[0]);
			cl.set(edu.stanford.nlp.ling.CoreAnnotations.AnswerAnnotation.class, data[1]);
			entities.add(cl);
		}
		br.close();
		return corpus;
	}

	public static List<List<CoreLabel>> loadTestTokens() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(TEST_FILE));
		String line;
		List<List<CoreLabel>> corpus = new LinkedList<>();

		List<CoreLabel> entities = new LinkedList<>();
		while ((line = br.readLine()) != null) {
			if (line.length() == 0) {
				corpus.add(entities);
				entities = new LinkedList<>();
				continue;
			}
			String[] data = line.split("\\t");
			CoreLabel cl = new CoreLabel();
			cl.setWord(data[0]);
			entities.add(cl);
		}
		br.close();
		return corpus;
	}

	public static List<List<CoreLabel>> loadDateset(String datasetPath) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(datasetPath));
		String line;
		List<List<CoreLabel>> corpus = new LinkedList<>();
		List<CoreLabel> entities = new LinkedList<>();

		while ((line = br.readLine()) != null) {
			// line length is 0 when we have an empty line in a document 
			// it splits different documents
			if (line.length() == 0) {
				corpus.add(entities);
				entities = new LinkedList<>();
				continue;
			}
			String[] data = line.split("\\t");
			CoreLabel cl = new CoreLabel();
			cl.setWord(data[0]);
			cl.set(AnswerAnnotation.class, data[1]);
			entities.add(cl);
		}
		br.close();
		return corpus;
	}

}
