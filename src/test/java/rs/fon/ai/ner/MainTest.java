package rs.fon.ai.ner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MainTest {

	public static void main(String[] args) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader("C:/Users/Baki/Desktop/govori/valid/tokens.txt"));
		int i = 0;
		int personsNum = 0;
		int oNum = 0;
		int docsNum = 1;
		String line;
		while ((line = in.readLine()) != null) {
			if (line.contains("PERSON")) {
				personsNum++;
			}
			if (line.contains("\tO")) {
				oNum++;
			}
			if (line.length() == 0) {
				docsNum++;
			}
			i++;
		}
		in.close();

		System.out.println("No of documents: " + docsNum);
		System.out.println("No of persons: " + personsNum);
		System.out.println("No of Os: " + oNum);
		System.out.println("Check: " + (oNum + personsNum + docsNum - 1) + " number of lines " + i);
	}

}
