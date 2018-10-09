package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lab2.db.AuthorTableGateway;
import javafx.collections.*;
import javafx.fxml.*;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import model.Author;

public class AuthorListController implements Initializable, MyController {
	private Logger logger = LogManager.getLogger(AuthorListController.class);

	@FXML
	private ListView<Author> authorList;
	private ObservableList<Author> authors;
	private AuthorTableGateway gateway;

	public AuthorListController(AuthorTableGateway gateway) throws SQLException {
		this.gateway = gateway;
		authors = this.gateway.getAuthors();
	}

	public AuthorListController(ObservableList<Author> authors) {
		this.authors = authors;
	}

	@FXML
	void loadAuthorCell(MouseEvent event) throws IOException, SQLException, NamingException {
		if (event.getClickCount() == 2) {
			Author author = authorList.getSelectionModel().getSelectedItem();
			if (author != null) {
				logger.info("view Author detail");
				AppController.getInstance().changeView(AppController.AUTHOR_DETAIL, author);
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.authorList.setItems(authors);
	}
}