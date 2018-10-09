package model;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class AuditTrailEntry {
	private int id;
	private Timestamp dateAdded;
	private String message;

	public AuditTrailEntry() {
		id = 0;
		this.dateAdded = null;
	}

	public int getid() {
		return id;
	}

	public void setid(int id) {
		this.id = id;
	}

	public Timestamp getDateAdded() {
		return this.dateAdded;
	}

	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy MM dd HH:mm:ss");
		String text = this.dateAdded.toString();
		return text + " " + this.message;
	}

}
