package rs.fon.ai.ner.domain;

import rs.fon.ai.ner.constants.Constants;

public class RowEntity {

	private String word;
	private String type;

	public RowEntity(String word, String type) {
		this.word = word;
		if (type.equals(Constants.PERSON_TYPE) || type.equals(Constants.DEFAULT_TYPE))
			this.type = type;
		else
			throw new RuntimeException("Wrong type: " + type);
	}

	public RowEntity(String word) {
		this.word = word;
		this.type = Constants.DEFAULT_TYPE;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RowEntity other = (RowEntity) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return word + "\t" + type + "\n";
	}

}
