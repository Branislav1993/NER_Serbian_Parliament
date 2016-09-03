package rs.fon.ai.ner.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class SpeechesDefaultTokenizer {

	public static final String API_URL = "http://localhost:8080/api/api/sessions/2083/speeches?limit=5";
	public static final File TRAINING_FILE = new File("C:/Users/Baki/Desktop/govori/valid/tokens.txt");

	public static void main(String[] args) throws Exception {

		// getting all speeches
		URL speechesURL = new URL(API_URL);
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

		// writing tokens in a file with default O type
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(TRAINING_FILE, true)));

		for (int j = 0; j < json.size(); j++) {
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
				CoreLabel label = ptbt.next();
				System.out.println(label + "\tO");
			}
			System.out.println();
		}
		out.close();
	}

}
