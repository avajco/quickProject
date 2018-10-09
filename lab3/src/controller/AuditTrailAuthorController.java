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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import lab2.db.AuditTrailGateway;
import model.AuditTrailEntry;
import model.Author;

public class AuditTrailAuthorController implements Initializable, MyController {

	private static Logger logger = LogManager.getLogger(Launcher.class);
	private Author author;
	private ObservableList<AuditTrailEntry> auditRecords;
	private AuditTrailGateway<Author> gateway;
	@FXML private ListView<AuditTrailEntry> auditList;
	@FXML private Label AuthorName;
	

	public AuditTrailAuthorController(AuditTrailGateway<Author> gateway, Author author) throws SQLException {
		this.author  = author;
		this.gateway = gateway;
		String query = "select author_audit_trail.date_added, author_audit_trail.entry_msg from author inner join author_audit_trail on author_audit_trail.author_id = author.id where author.id = ?";
		this.auditRecords = this.gateway.getBookAuditRecord(author, query);	
        
	}

	@FXML
	void ViewAuthorDetail(ActionEvent event) throws IOException, SQLException, NamingException {
		AppController.getInstance().changeView(AppController.AUTHOR_LIST, author);
		logger.info("Back button pushed");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		this.AuthorName.textProperty().bindBidirectional(author.firstNameProperty());
		this.auditList.setItems(auditRecords);
	}

}
