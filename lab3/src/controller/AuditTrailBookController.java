package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.Launcher;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lab2.db.AuditTrailGateway;
import model.AuditTrailEntry;
import model.Book;

public class AuditTrailBookController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(Launcher.class);

	@FXML
	private int bookId;
	@FXML
	private ListView<AuditTrailEntry> auditList;
	private Book book;
	private ObservableList<AuditTrailEntry> auditRecords;
	private AuditTrailGateway<Book> gateway;
	@FXML
	private Button backButton;
	@FXML
	private Label bookTitle;

	public AuditTrailBookController(AuditTrailGateway<Book> gateway, Book book) throws SQLException {
		this.book = book;
		this.gateway = gateway;
		String query = "select book_audit_trail.date_added, book_audit_trail.entry_msg from book inner  join book_audit_trail on  book_audit_trail.book_id = book.id where book.id = ?";
		this.auditRecords = this.gateway.getBookAuditRecord(book, query);	
        
	}

	@FXML
	void ViewBookDetail(ActionEvent event) throws IOException, SQLException, NamingException {
		AppController.getInstance().changeView(AppController.BOOK_DETAIL, book);
		logger.info("Back button pushed");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		this.bookTitle.textProperty().bindBidirectional(book.titleproperty());
		this.auditList.setItems(auditRecords);
	}

}
