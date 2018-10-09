package lab2.db;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.Launcher;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import lab2.db.AppException;
import model.AuditTrailEntry;
import model.Author;
import model.AuthorBook;
import model.Book;
import model.Publisher;
import model.bookTableInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookTableGateWay {
	private Connection conn;

	public BookTableGateWay(Connection conn) {
		this.conn = conn;
	}

	private static Logger logger = LogManager.getLogger(Launcher.class);

	public void AddBook(Book book) throws SQLException {
		AuditTrailEntry auditTrail = new AuditTrailEntry();
		// set the message of auditTrail.
		auditTrail.setMessage("Book Inserted");
		// call the audit trail gateway method with a audit of the inserted book to add
		// a record on the auditTrail.
		PreparedStatement st = null;
		ResultSet rs = null;
		st = conn.prepareStatement(
				"insert into book (title, summary,year_published,isbn,publisher_id) " + "values (?, ?, ?,?,?)",
				Statement.RETURN_GENERATED_KEYS);
		st.setString(1, book.getTitle());
		st.setString(2, book.getSummary());
		st.setInt(3, book.getYearPubliched());
		st.setString(4, book.getIsbn());
		st.setInt(5, book.getPublisher().getId());
		// st.setObject(5, book.getDateAdded());
		st.executeUpdate();
		rs = st.getGeneratedKeys();
		if (rs.first()) {
			int bookId = rs.getInt(1);
			book.setId(bookId);
			AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
			auditGateway.AddAudit(auditTrail, bookId, "insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
		} else {
			logger.error("Didn't get the new key.");
		}
		logger.info("New author created. Id = " + book.getId());
	}

	public void deleteBook(Book book) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			AuditTrailGateway<Book> auditGateway =  new AuditTrailGateway<Book>(this.conn);
			auditGateway.delBookAuditRecord(book);
			String query = "delete from book " + "where id = ?";
			st = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			st.setInt(1, book.getId());
			// executeUpdate is used to run insert, update, and delete statements
			st.executeUpdate();
			logger.info("Author with id = " + book.getId() + " deleted from database.");
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
	public void UpdateBookAuhtor(	Book book) {
		
	}
	public void updateBook(Book book, List<AuthorBook> authors) throws AppException {
		PreparedStatement st = null;
		try {
			String msg= "";
			st = conn.prepareStatement("SELECT * FROM book WHERE book.id = ?");
			st.setInt(1, book.getId());
			ResultSet rs = st.executeQuery();
			while (rs.next()) {
				Book oldBook = new Book();
				oldBook.setTitle(rs.getString("title"));
				oldBook.setSummary(rs.getString("summary"));
				oldBook.setYearPubliched(rs.getInt("year_published"));
				oldBook.setIsbn(rs.getString("isbn"));
				
				if (!(book.getTitle().equals(oldBook.getTitle()))) {
					AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
					AuditTrailEntry entry = new AuditTrailEntry();
					msg = "Title changed from " + oldBook.getTitle() + " to " + book.getTitle();
					entry.setMessage(msg);
					auditGateway.AddAudit(entry, book.getId(), "insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
				}
				if (!(book.getSummary().equals(oldBook.getSummary()))) {
					AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
					AuditTrailEntry entry = new AuditTrailEntry();
					msg = "summary changed from " + oldBook.getSummary() + " to " + book.getSummary();
					entry.setMessage(msg);
					auditGateway.AddAudit(entry, book.getId(), "insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
				}
				if (book.getYearPubliched() != (oldBook.getYearPubliched())) {
					AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
					AuditTrailEntry entry = new AuditTrailEntry();
					msg = "Year published  changed from " + oldBook.getYearPubliched() + " to " 
							+ book.getYearPubliched();
					entry.setMessage(msg);
					auditGateway.AddAudit(entry, book.getId(), "insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
				}
				if (!(book.getIsbn().equals(oldBook.getIsbn()))) {
					AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
					AuditTrailEntry entry = new AuditTrailEntry();
					msg = "isbn changed from" + oldBook.getIsbn() + " to " + book.getIsbn();
					entry.setMessage(msg);
					auditGateway.AddAudit(entry, book.getId(), "insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
				}
			}

			st.close();
			st = conn.prepareStatement(
					"UPDATE book SET title = ?, summary =?, year_published = ?, isbn = ? WHERE book.id = ?");
			st.setString(1, book.getTitle());
			st.setString(2, book.getSummary());
			st.setInt(3, book.getYearPubliched());
			st.setString(4, book.getIsbn());
			st.setInt(5, book.getId());
			st.executeUpdate();
			st.close();
			int count = 0;
			while(!authors.isEmpty()) {
				AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
				AuditTrailEntry entry = new AuditTrailEntry();
				double x = authors.get(count).getRoyalty()/100.0;
				DecimalFormat df = new DecimalFormat("#.00");
				 BigDecimal y = new BigDecimal(df.format(x));
				System.out.println(y);
				//st.setBigDecimal(3,y);
				msg = book.getTitle() + "'s Auhtors Royalty changed to " + authors.get(count).getRoyalty(); 
				st = conn.prepareStatement("UPDATE author_book SET royalty = ? WHERE author_book.author_id = ? and author_book.book_id = ?");
				st.setBigDecimal(1, new BigDecimal(x));
				st.setInt(2, authors.get(count).getAuthor().getId() );
				st.setInt(3, book.getId());
				st.executeUpdate();
				st.close();
				entry.setMessage(msg);
				auditGateway.AddAudit(entry, book.getId(),"insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
				authors.remove(authors.get(count));
				count ++;
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
	}

	
	//work in proress 
	public ObservableList<Book> getBooks(int firstId, int lastId, String type) throws SQLException {
		ObservableList<Book> books = FXCollections.observableArrayList();
		PreparedStatement st = null;
		String query = "";
		String firstQuery = "SELECT * FROM book order by book.id limit 50";
		String nextQuery = "select * From book  where book.id > ? order by book.id limit 50 ";
		String last = "SELECT * FROM book ORDER BY book.id DESC LIMIT 50";
		if(type.equalsIgnoreCase("FIRST")) {
			query = firstQuery;
			}
		else if(type.equalsIgnoreCase("last")) {
			query = last;
		}else {
			query = nextQuery;
		}
		
		st = conn.prepareStatement(query);
		if(type.equalsIgnoreCase("next"))st.setInt(1, lastId);
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			Publisher publisher = new Publisher();
			Book book = new Book(rs.getString("title"));
			book.setPublisher(publisher);
			publisher.setId(rs.getInt("publisher_id"));
			book.setGateway(this);
			book.setId(rs.getInt("id"));
			book.setSummary(rs.getString("summary"));
			book.setYearPubliched(rs.getInt("year_published"));
			book.setIsbn(rs.getString("isbn"));
			books.add(book);
		}
		st.close();
		return books;
	}
	
	
	
 public  int  getTableSummary() throws SQLException {
	 String QueryCount  = "select count(*) from book";
	 //String QuerylastId = "SELECT book.id FROM book " + 
	 	//	"ORDER BY bookId DESC" + 
	 		//"LIMIT 1";
	 //String QueryFirstId = "select book.id from book limit 1";
	 int countDb = 0;
	 PreparedStatement st = conn.prepareStatement(QueryCount);
	 ResultSet rs = st.executeQuery();
	 while(rs.next()) {
		   countDb = rs.getInt(1);
	 }
  int countTot = countDb/50;
	 
	 return countTot;
 }
	
//	public ObservableList<Book> getRequiredBooks(int lastRetrivedId){
//		ObservableList<Book> books = FXCollections.observableArrayList();
//		PreparedStatement st  = conn.prepareStatement("select book.id, book.title from <table> where <TableId> > ? group by book.id limit = <number>");
//		st.setInt(1, lastRetrivedId);
//		ResultSet rs = st.executeQuery();
//		while(rs.next()){
//		Book book = new Book(rs.getString("title"));
//		book.setId(rs.getInt("id"));;
//		books.add(book);
//		}
//		}

		

	public Publisher getPublishersById(int Id) {
		PublisherGateway gateway = new PublisherGateway(this.conn);
		return gateway.getPublisherById(Id);
	}

	public List<Publisher> getAllPublishers() {
		PublisherGateway gateway = new PublisherGateway(this.conn);
		return gateway.getAllPublishers();
	}
    public  List<AuthorBook> getAuthorsForBook(Book book) throws SQLException{	
    	List<AuthorBook> Bookauthors = FXCollections.observableArrayList();
    	PreparedStatement st = null;
    	ResultSet rs = null;
    	st = conn.prepareStatement("select b.* , a.royalty from author_book a inner join author b WHERE a.author_Id = b.id and book_id = ?");
    	st.setInt(1,book.getId());
    	rs = st.executeQuery();
    	while(rs.next()) {
    		AuthorBook Bookauthor = new AuthorBook();
    		Author author = new Author(rs.getString("first_name"));
			author.setId(rs.getInt("id"));
			author.setLastName(rs.getString("last_name"));
			author.setDob(LocalDate.parse(rs.getString("dob")));
			author.setGender(rs.getString("gender"));
			author.setWebSite(rs.getString("web_site"));
			author.setLastModified(rs.getTimestamp("last_modified").toLocalDateTime());
    		Bookauthor.setBook(book);
    		Bookauthor.setNewRecord(false);
    		Bookauthor.setAuthor(author);
    		BigDecimal  x = rs.getBigDecimal("royalty");
    		System.out.println(x.multiply(new BigDecimal(100)).intValue());
    		Bookauthor.setRoyalty(x.multiply(new BigDecimal(100)).intValue());
    		//double  y = x.doubleValue();
    	    Bookauthors.add(Bookauthor);
    	}
    	return Bookauthors;
    }
    public  AuthorBook getAuthorBook(AuthorBook newAuthorBook) throws SQLException{	

    	PreparedStatement st = null;
    	ResultSet rs = null;
    	st = conn.prepareStatement("select *  from author_book WHERE author_book.author_Id = ? and book_id = ?");
    	st.setInt(1,newAuthorBook.getAuthor().getId());
    	st.setInt(2 , newAuthorBook.getBook().getId());
    	rs = st.executeQuery();
    	while(rs.next()) {
    		newAuthorBook.setNewRecord(false);
    	}
    	return newAuthorBook;
    }
    
    public void  deleteAuthorForBook(AuthorBook authorBooks) throws SQLException {
		PreparedStatement st = null;
		st = conn.prepareStatement("delete from author_book where author_book.author_id = ? and author_book.book_id = ?");
		st.setInt(2, authorBooks.getBook().getId());
		st.setInt(1, authorBooks.getAuthor().getId());
	    st.executeUpdate();
	    st.close();
	    AuditTrailGateway<Book> auditGateway = new AuditTrailGateway<Book>(conn);
		AuditTrailEntry entry = new AuditTrailEntry();
		String msg = authorBooks.getBook().getTitle() + "'s author " + authorBooks.getAuthor().getfirstName() + " was deleted"; 
	    entry.setMessage(msg);
		auditGateway.AddAudit(entry, authorBooks.getBook().getId(),"insert into book_audit_trail ( book_id, entry_msg)" + "values(?,?)");
		}
	public ObservableList<Book> searchBookByPattern(String pattern) throws SQLException {
		ObservableList<Book> books = FXCollections.observableArrayList();
		PreparedStatement st = null;
		st = conn.prepareStatement("select * from book where title like ?");
		st.setString(1, "%" + pattern + "%");
		ResultSet rs = st.executeQuery();
		while (rs.next()) {
			Publisher publisher = new Publisher();
			Book book = new Book(rs.getString("title"));
			book.setPublisher(publisher);
			publisher.setId(rs.getInt("publisher_id"));
			book.setGateway(this);
			book.setId(rs.getInt("id"));
			book.setSummary(rs.getString("summary"));
			book.setYearPubliched(rs.getInt("year_published"));
			book.setIsbn(rs.getString("isbn"));
			books.add(book);
		}

		return books;
	}
	public List<Book> getBooksByPublisher(int id) {
		List<Book> books = FXCollections.observableArrayList();
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("Select * from book where publisher_id = '" + id + "'");
			rs = st.executeQuery();

			while (rs.next()) {
				// create an author object from the record
				Book book = new Book(rs.getString("title"));
				book.setId(rs.getInt("id"));
			    book.setSummary(rs.getString("summary"));
				book.setYearPubliched(rs.getInt("year_published"));
			    //book.setArs.getInt("publisher_id"));
				book.setIsbn(rs.getString("isbn"));
				//book.setDateAdded(rs.getTimestamp("date_added"));
				books.add(book);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return books;
	}
}