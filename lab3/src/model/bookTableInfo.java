package model;

import javafx.beans.property.SimpleIntegerProperty;

public class bookTableInfo {
	 
	private SimpleIntegerProperty lastRecordId;
	private SimpleIntegerProperty firstRecordId;
	private SimpleIntegerProperty NumOfRecordOfFifty;
	public bookTableInfo() {
		this.lastRecordId = new SimpleIntegerProperty();
		this.firstRecordId = new SimpleIntegerProperty();
		this.NumOfRecordOfFifty = new SimpleIntegerProperty();
		
	}
	public Integer getLastRecordId() {
		return lastRecordId.get();
	}

	public void setLastRecordId(int lastRecordId) {
		this.lastRecordId.set(lastRecordId);
	}

	public int getFirstRecordId() {
		return firstRecordId.get();
	}

	public void setFirstRecordId(int firstRecordId) {
		this.firstRecordId.set(firstRecordId);
	}

	public int getNumOfRecordOfFifty() {
		return NumOfRecordOfFifty.get();
	}

	public void setNumOfRecordOfFifty(int numOfRecordOfFifty) {
		NumOfRecordOfFifty.set(numOfRecordOfFifty);
	}
	public SimpleIntegerProperty getFirstRecordIdProperty() {
		return this.firstRecordId;
	}
	public SimpleIntegerProperty getLastRecordIdProperty() {
		return this.lastRecordId;
	}
	public SimpleIntegerProperty getNumOfRecordOfFiftyProperty() {
		return this.NumOfRecordOfFifty;
	}

	
	
	

}
