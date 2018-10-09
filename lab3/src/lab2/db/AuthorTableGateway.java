package lab2.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.Launcher;
import java.text.DecimalFormat;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lab2.db.AppException;
import model.AuditTrailEntry;
import model.Author;
import model.AuthorBook;
import model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AuthorTableGateway {
	private Connection conn;

	public AuthorTableGateway(Connection conn) {
		this.conn = conn;
	}

	private static Logger logger = LogManager.getLogger(Launcher.class);

	public void insertAuthor(Author author) throws SQLException {
		PreparedStatement st = null;
		String msg =  author.getfirstName() + author.getLastName() + "inserted";
		
		ResultSet rs = null;
		st = conn.prepareStatement(
				"insert into author (first_name, last_name,dob,gender, web_site) " + "values (?, ?, ?,?, ?)",
				Statement.RETURN_GENERATED_KEYS);
		st.setString(1, author.getfirstName());
		st.setString(2, author.getLastName());
		st.setObject(3, author.getDob());
		st.setString(4, author.getGender());
		st.setString(5, author.getWebSite());
		st.executeUpdate();
		rs = st.getGeneratedKeys();
		if (rs.first()) {
			int authorId = rs.getInt(1);
			author.setId(authorId);
			AuditTrailEntry audit = new AuditTrailEntry();
			audit.setMessage(msg);
		AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
		auditGateway.AddAudit(audit, authorId, "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
		} else {
			logger.error("Didn't get the new key.");
		}
		logger.info("New author created. Id = " + author.getId());
	}

	public void deleteAuthor(Author author) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {

			String query = "delete from author " + "where id = ?";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, author.getId());

			// executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			logger.info("Author with id = " + author.getId() + " deleted from database.");
		} catch (SQLException e) {
			logger.error("Error deleting from author table in database: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
	}

	public void createTable(String tablename) {
		PreparedStatement st = null;
		try {

			String query = "CREATE TABLE IF NOT EXISTS " + tablename + "(id int(11) NOT NULL AUTO_INCREMENT, "
					+ "first_name varchar(100) NOT NULL, " + "last_name varchar(100) NOT NULL, "
					+ "dob varchar(10) NOT NULL, " + "gender varchar(1) NOT NULL, " + "web_site varchar(100) NOT NULL, "
					+ "PRIMARY KEY ( id ))";

			st = conn.prepareStatement(query);

			// execute
			st.execute();
			logger.info(tablename + " table created in database.");
		} catch (SQLException e) {
			logger.error("Error creating " + tablename + " table in database: " + e.getMessage());
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				logger.error("Statement or Result Set close error: " + e.getMessage());
			}
		}
	}

	public void close() {
		try {
			if (conn != null) {
				// close the connection
				conn.close();
				logger.info("Database connection closed.");
			}
		} catch (SQLException e) {

			logger.error("Error closing connection: " + e.getMessage());
		}
	}

	public void updateAuthor(Author author) throws AppException, SQLException {
		LocalDateTime lastModified, lastModifiedON_DB;
		PreparedStatement st = null;
		ResultSet rs = null;
		Author oldAuthor = new Author();
		String msg = "";
		st = conn.prepareStatement("select * from author where author.id = ?");
	    st.setInt(1, author.getId()); 
	    rs = st.executeQuery();
	    while(rs.next()) {
	    oldAuthor.setfirstName(rs.getString("first_name"));
		oldAuthor.setId(rs.getInt("id"));
		oldAuthor.setLastName(rs.getString("last_name"));
		oldAuthor.setDob(LocalDate.parse(rs.getString("dob")));
		oldAuthor.setGender(rs.getString("gender"));
		oldAuthor.setWebSite(rs.getString("web_site"));
	    lastModifiedON_DB  = rs.getTimestamp("last_modified").toLocalDateTime();   
	    lastModified  =  author.getLastModified();
	    if(lastModified.equals(lastModifiedON_DB)) {
		try {
			if (!(author.getfirstName().equals(oldAuthor.getfirstName()))) {
				AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
			    msg = "first name changed from " + oldAuthor.getfirstName() + " to " + author.getfirstName();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, author.getId(), "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
			}
			if (!(author.getLastName().equals(oldAuthor.getLastName()))) {
				AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
				msg = "last name changed from " + oldAuthor.getLastName() + " to " + author.getLastName();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, author.getId(), "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
			}
			if (author.getDob() != (oldAuthor.getDob())) {
				AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
				msg = "Dob published  changed from " + oldAuthor.getDob() + " to " 
						+ author.getDob();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, author.getId(), "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
			}
			if (!(author.getWebSite().equals(oldAuthor.getWebSite()))) {
				AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
				msg = "Website changed from" + oldAuthor.getWebSite() + " to " + author.getWebSite();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, author.getId(), "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
			}
			if (!(author.getGender().equals(oldAuthor.getGender()))) {
				AuditTrailGateway<Author> auditGateway = new AuditTrailGateway<Author>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
				msg = "Gender changed from" + oldAuthor.getGender() + " to " + author.getGender();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, author.getId(), "insert into author_audit_trail ( author_id, entry_msg)" + "values(?,?)");
			}
			st = conn.prepareStatement("update author set first_name = ?, "
					+ "last_name= ?, dob=?, gender = ?, web_site = ? " + "where id = ? ");
			st.setString(1, author.getfirstName());
			st.setString(2, author.getLastName());
			st.setObject(3, author.getDob());
			st.setString(4, author.getGender());
			st.setString(5, author.getWebSite());
			st.setInt(6, author.getId());
			st.executeUpdate();
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
	    }else {
	    	Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle("Information Dialog");
	    	alert.setHeaderText("Look, an Information Dialog");
	    	alert.setContentText("Changes have taken place, during your\n"
	    			+ "idle moments, please go back to the Author list and\n"
	    			+ "start the process all over again");
	    	alert.showAndWait();
	    }
	   }
	}

	public ObservableList<Author> getAuthors() throws SQLException {
		ObservableList<Author> authors = FXCollections.observableArrayList();

		PreparedStatement st = null;
		st = conn.prepareStatement("SELECT * FROM author order by first_name");
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			Author author = new Author(rs.getString("first_name"));
			author.setGateway(this);
			author.setId(rs.getInt("id"));
			author.setLastName(rs.getString("last_name"));
			author.setDob(LocalDate.parse(rs.getString("dob")));
			author.setGender(rs.getString("gender"));
			author.setWebSite(rs.getString("web_site"));
			author.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
			authors.add(author);
		}

		return authors;
	}
	public List<Author> getAuthorById(int Id) throws SQLException {
		ObservableList<Author> authors = FXCollections.observableArrayList();
		PreparedStatement st = null;
		st = conn.prepareStatement("select b.* from  author b inner join author_book a on a.author_Id = b.id where a.book_id = ?");
		st.setInt(1,Id);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			Author author = new Author();
		    author.setfirstName(rs.getString("first_name"));
			author.setGateway(this);
			author.setId(rs.getInt("id"));
			author.setLastName(rs.getString("last_name"));
			author.setDob(LocalDate.parse(rs.getString("dob")));
			author.setGender(rs.getString("gender"));
			author.setWebSite(rs.getString("web_site"));
			author.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
			authors.add(author);
		}
		
		return authors;
	}
	///frix this tomorrow
   public void  insertAuthorForBook(List<AuthorBook> authorBooks) throws SQLException {
		PreparedStatement st = null;
		int count =0 ;
		while(authorBooks.size() > count){
		st = conn.prepareStatement("insert into author_book (book_id , author_id, royalty) values(?,?,?)");
		st.setInt(1, authorBooks.get(count).getBook().getId());
		st.setInt(2, authorBooks.get(count).getAuthor().getId());
		//BigDecimal form = new  BigDecimal(authorBooks.get(count).getRoyalty());
		double x = authorBooks.get(count).getRoyalty()/100.0;
		DecimalFormat df = new DecimalFormat("#.00");
		 BigDecimal y = new BigDecimal(df.format(x));
		System.out.println(y);
		st.setBigDecimal(3,y);
	    // st.setString(3, autform.toString());
		st.executeUpdate();
		st.close();
		 AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
			AuditTrailEntry entry = new AuditTrailEntry();
			String msg = authorBooks.get(count).getBook().getTitle() + "'s author " + authorBooks.get(count).getAuthor().getfirstName() + " was added"; 
		    entry.setMessage(msg);
			auditGateway.AddAudit(entry, authorBooks.get(count).getBook().getId(),"insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
		count++;
		}
	}
	}
