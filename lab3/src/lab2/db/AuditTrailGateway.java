package lab2.db;

import java.sql.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AuditTrailEntry;
import model.Author;
import model.Book;

public class AuditTrailGateway <T> {
	private Connection conn;

	public AuditTrailGateway(Connection conn) {
		this.conn = conn;
	}

	public void AddAudit(AuditTrailEntry audit, int Id, String query) throws SQLException {
		PreparedStatement st = null;
		ResultSet rs = null;
		st = conn.prepareStatement( query,
				Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, Id);
		st.setString(2, audit.getMessage());
		st.executeUpdate();
		rs = st.getGeneratedKeys();
		if (rs.first()) {
			audit.setid(rs.getInt(1));
		} else {
			System.out.print("nothing is happening");
		}

	}

	public ObservableList<AuditTrailEntry> getBookAuditRecord(T obj, String query) throws SQLException {
		ObservableList<AuditTrailEntry> entries = FXCollections.observableArrayList();
		PreparedStatement st = null;
		// ResultSet rs = null;
        Book bookDummie = new Book() ;
		st = conn.prepareStatement(
				query,
				Statement.RETURN_GENERATED_KEYS);
		if(bookDummie.getClass().getName().equalsIgnoreCase(obj.getClass().getName()))
		{
		st.setInt(1, ((Book) obj).getId());
		}else {
		 st.setInt(1,((Author)obj).getId());
		}
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			AuditTrailEntry entry = new AuditTrailEntry();
			entry.setDateAdded(rs.getTimestamp("date_added"));
			entry.setMessage(rs.getString(2));
			entries.add(entry);
		}
		return entries;

	}
	// deletes an audit trial.
	public void delBookAuditRecord(Book book) throws SQLException {
		PreparedStatement st = null;
		st = conn.prepareStatement(
				"DELETE FROM book_audit_trail WHERE book_audit_trail.book_id = ?",
				Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, book.getId());
	    st.executeUpdate();

	}
}