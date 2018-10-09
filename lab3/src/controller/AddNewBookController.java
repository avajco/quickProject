package controller;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.fxml.Initializable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lab2.db.BookTableGateWay;
import model.Book;
import model.Publisher;

public class AddNewBookController implements Initializable, MyController {
	private static Logger logger = LogManager.getLogger();
	@FXML private TextField publisherId;
  	@FXML private TextArea aboutBook;
	@FXML private TextField title;
	@FXML private TextField Ypublished;
	@FXML private TextField bookIsnb;
	private BookTableGateWay gateway;
	private Book newBook;
	private int validationNumber;
	private String inputInfoMsg;
	@FXML private ComboBox<Publisher> choosePublisher;
	private ObservableList<Publisher> listOfPublisher;
	private List<Publisher>  bookPublishers;
	//private int curr =  0;
	
	public AddNewBookController(BookTableGateWay gateway, Book book) {
		this.gateway = gateway;
	 	this.bookPublishers = gateway.getAllPublishers();
	 	this.listOfPublisher = FXCollections.observableArrayList(this.bookPublishers);
	}
	public AddNewBookController(Book book) {
	//this.book = book;
	 this.validationNumber = 0;
	 //this.curr = 0;
	 
	}
    @FXML
	    void addBook(ActionEvent event) throws SQLException, IOException, NamingException {
         Book book = new Book();
         book.setTitle(title.getText()); 
         book.setSummary(aboutBook.getText());
         int yearCurr = Calendar.getInstance().get(Calendar.YEAR);
         book.setYearPubliched(Integer.parseInt(Ypublished.getText()));
         book.setIsbn(bookIsnb.getText());
         Publisher publisher = this.choosePublisher.getValue();
         book.setPublisher(publisher);
         //validation phase
         this.inputInfoMsg = "title must be between 1 and 255 chars\r\n" + 
         					"b. summary must be less than 65536 characters\r\n" + 
         					 "c. year_published cannot be  older current year\r\n" + 
         					 "d. isbn cannot be greater than 13 characters";
        if (book.validateIsbn()) {
        	 this.validationNumber = 1;
         }
         if(book.validateSummary()) {
        	 this.validationNumber =2;
         }
         if(book.ValidationTitle(book.getTitle())) {
        	this.validationNumber =3;
        	 }
         //System.out.print(());
         if(book.getYearPubliched() < yearCurr) { 
        	 this.validationNumber = 4 ;}
         //perform book insertion into database
         System.out.print(this.validationNumber);
         if(this.validationNumber == 4) {
         book.setGateway(this.gateway);
         book.insertBook();
         //this.addCrapBooks();
         System.out.print("book is here");
         AppController.getInstance().changeView(AppController.BOOK_LIST, book);
         }else {
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("Credential Requirements");
        	alert.setContentText(this.inputInfoMsg);
        	alert.showAndWait();
         }
         }
    public void addCrapBooks() throws SQLException {
	Random random = new Random();
	for(int i = 2000; i<10000 ; i++) {
	int pbId  = random.nextInt(this.bookPublishers.size());
	String bookName = "book";
	Book book =  new Book();
	book.setTitle(bookName+i);
	System.out.println(this.bookPublishers.get(pbId));
	book.setPublisher(this.bookPublishers.get(pbId));
	book.setSummary("book" + i);
	book.setIsbn("111" + i);
	book.setYearPubliched(2016);
	book.setGateway(this.gateway);
	book.insertBook();
	}
	}
	
	@FXML
	    void DontAddBook(ActionEvent event) throws IOException, SQLException, NamingException  {
		 AppController.getInstance().changeView(AppController.NEW_BOOK,newBook);
		 logger.info("Cancel button was pushed");
	    }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		this.choosePublisher.setValue(this.bookPublishers.get(0));
		this.choosePublisher.setItems(this.listOfPublisher);

	}
	
	
	
	
	
	
	
	
	
	
	
	  



	}

