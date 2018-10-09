package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lab2.db.AuthorTableGateway;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import model.Author;

public class AuthorAddController implements MyController, Initializable {

	private ObservableList<String> genderList = FXCollections.observableArrayList("M", "F", "U");
	private Author author;
	private AuthorTableGateway gateway;
	private Logger logger = LogManager.getLogger(AuthorAddController.class);
	@FXML
	private Label id;
	@FXML
	private TextField fname;
	@FXML
	private TextField lname;
	@FXML
	private DatePicker pickDob;
	@FXML
	private ChoiceBox<String> gender;
	@FXML
	private TextField website;
	@FXML
	private Button save;
	boolean isValid;

	public AuthorAddController(AuthorTableGateway gateway, Author author) {

		this.gateway = gateway;

	}

	public AuthorAddController(Author author) {
		this.author = author;
	}

	@FXML
	void insert(ActionEvent event) throws IOException, SQLException, NamingException {
		Author author = new Author();
		author.setfirstName(fname.getText());
		author.setLastName(lname.getText());
		author.setDob(pickDob.getValue());
		author.setGender(gender.getValue());
		author.setWebSite(website.getText());
		author.setGateway(this.gateway);
		author.saveAuthor();
		AppController.getInstance().changeView(AppController.AUTHOR_LIST, author);
		logger.info("Author added to the list ");
	}

	@FXML
	void dontAddAuthor(ActionEvent event) throws IOException, SQLException, NamingException {
		AppController.getInstance().changeView(AppController.NEW_AUTHOR, author);
		logger.info("Cancel button was pushed");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		gender.setValue("M");
		gender.setItems(genderList);
	}
}
