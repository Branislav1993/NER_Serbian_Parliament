package rs.fon.ai.ner.domain;

import java.util.Date;
import java.util.List;

public class Doc {

	private List<RowEntity> entities;
	private String memberName;
	private Date speechDate;

	public List<RowEntity> getEntities() {
		return entities;
	}

	public void setEntities(List<RowEntity> entities) {
		this.entities = entities;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public Date getSpeechDate() {
		return speechDate;
	}

	public void setSpeechDate(Date speechDate) {
		this.speechDate = speechDate;
	}

	@Override
	public String toString() {
		String doc = "";
		for (RowEntity rowEntity : entities) {
			doc += rowEntity;
		}
		return doc;
	}

}
