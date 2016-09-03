package rs.fon.ai.ner.domain;

import java.util.List;

public class Corpus {
	private List<Doc> documents;

	public List<Doc> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Doc> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		String corpus = "";
		for (Doc document : documents) {
			corpus += document + "\n";
		}
		return corpus;
	}

}
