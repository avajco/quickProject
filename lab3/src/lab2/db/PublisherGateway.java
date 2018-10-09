package lab2.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Publisher;

public class PublisherGateway {
	private Connection conn;

	public PublisherGateway(Connection conn) {
		this.conn = conn;
	}

	public ObservableList<Publisher> getAllPublishers() throws AppException {
		ObservableList<Publisher> publisherList = FXCollections.observableArrayList();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("SELECT * FROM publisher");
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Publisher publisher = new Publisher();
				publisher.setId(rs.getInt("id"));
				publisher.setPublisherName(rs.getString("Publisher_name"));
				publisherList.add(publisher);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}
		return publisherList;
	}

	public Publisher getPublisherById(int publsiherId) throws AppException {
		Publisher publisher = new Publisher();
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"select publisher.publisher_name from book inner  join publisher on  publisher.id = book.publisher_id where publisher.id = ?",
					Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, publsiherId);
			ResultSet rs = st.executeQuery();
			if (rs.next()) {
				publisher.setPublisherName(rs.getString("Publisher_Name"));
				// publisher.setId(rs.getInt(columnIndex));
				// publisher.setId(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new AppException(e);
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
				throw new AppException(e);
			}
		}

		return publisher;
	}
}