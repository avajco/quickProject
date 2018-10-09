package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import lab2.db.AuthorTableGateway;
import model.*;
public class CreateBookAuthor implements Initializable, MyController {

	    @FXML
	    private ListView<AuthorBook> bookAuthorCreated;
	    @FXML
	    private ListView<Author> listOfAuthors;
        private AuthorTableGateway gateway;
        private List<Author> authors;
        private List<AuthorBook> authorsBook;
        private ObservableList<AuthorBook> athBooks;
        private ObservableList<Author> authorBooks;
        private Book book;
        @FXML
        private TextField royalPercent;
        @FXML
        private Text name;
        @FXML
        private Text bookTitle;
        private Author author;
        public CreateBookAuthor(AuthorTableGateway gateway, Book book) throws SQLException {
        	this.gateway = gateway;
        	this.author = new Author();
        	this.book = book;
        	this.authorsBook = this.book.getAuthors();
        	this.authors = this.gateway.getAuthors();
        	this.authorBooks = FXCollections.observableArrayList(this.authors);
            this.bookTitle = new Text();
            this.athBooks = FXCollections.observableArrayList();
        }

		@FXML
        void setAuthor(MouseEvent event) {
           author = listOfAuthors.getSelectionModel().getSelectedItem();
        }
        @FXML
        void generateAuthorBook(ActionEvent event) throws SQLException {
           AuthorBook authorBook = new AuthorBook();
           authorBook.setAuthor(author);
           listOfAuthors.getItems().remove(author);
           authorBook.setRoyalty(Integer.parseInt(royalPercent.getText()));
           authorBook.setBook(book);
           authorBook = this.book.validateAuhtorExit(authorBook);
           if(authorBook.isNewRecord() == false) {
        	   Alert alert = new Alert(AlertType.WARNING);
        	   alert.setTitle("Warning Dialog");
        	   alert.setHeaderText("Author");
        	   alert.setContentText(authorBook.getAuthor().getfirstName() + " already an Auhor for " + this.book.getTitle());
        	   alert.showAndWait();
           }else {
           if(authorBook.getRoyalty() > 100) {
        	System.out.print("you cant have more than");   
            }else{
           this.bookAuthorCreated.setItems(this.athBooks);
           this.athBooks.add(authorBook);
           }
           }
        }
        
	    
	    @FXML
	    void done(ActionEvent event) throws SQLException, IOException, NamingException {
          this.gateway.insertAuthorForBook(this.athBooks);
          AppController.getInstance().changeView(AppController.BOOK_DETAIL, this.book); 
	    }
	       
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.name.setText(this.book.getTitle());
		this.bookTitle.setText(this.book.getTitle());
		this.listOfAuthors.setItems(this.authorBooks);
	}

}
