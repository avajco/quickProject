package controller;

import javafx.beans.property.SimpleStringProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.util.converter.NumberStringConverter;
import model.AuditTrailEntry;
import model.AuthorBook;
import model.Book;
import model.Publisher;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import clientEjb.clientConn;
import javafx.fxml.Initializable;

public class BookDetailController implements Initializable, MyController {
	@FXML
	private DatePicker dateBookAdded;
	@FXML
	private TextField YearPublished;
	@FXML
	private TextField bookIsbn;
	@FXML
	private ComboBox<Publisher> publishers;
	@FXML
	private TextField bookTitle;
	@FXML
    private TableView<AuthorBook> bookAuthorTable;
	@FXML
	private TableColumn<AuthorBook, Number> authorRoyalty;
	@FXML
    private TableColumn<AuthorBook, String> authorLName;
    @FXML
    private TableColumn<AuthorBook, String> AutthorFName;
	@FXML
	private TextArea summarySpace;
	 @FXML
	 private TableColumn<String,String> sign;
	 @FXML
	    private Button deleteBt;

	    @FXML
	    private Button addAuthorBt;

	    @FXML
	    private Button updatebt;

	ObservableList<Publisher> listOfBooksPublisher;
	ObservableList<AuditTrailEntry> recordList;
	ObservableList<AuthorBook> listOfAuthorsInfo;
	private Publisher bookPublisher;
	private List<Publisher> allOtherPublishers;
	private List<AuthorBook> allAuthors, editedAuthors;
	Connection conn;
	private Book book;

	public BookDetailController() {

	}

	public BookDetailController(Book book) throws SQLException {
		this();
		this.book = book;
		this.bookPublisher = book.getPubLishersbyId();
		this.allOtherPublishers = book.getAllPublishers();
		this.allAuthors = this.book.getAuthors();
		this.listOfBooksPublisher = FXCollections.observableArrayList(this.allOtherPublishers);
		this.listOfAuthorsInfo = FXCollections.observableArrayList(this.allAuthors);
		this.editedAuthors = new ArrayList<AuthorBook>();
		

	}

	@FXML
	void ViewAuditTrail(ActionEvent event) throws IOException, SQLException, NamingException {
		AppController.getInstance().changeView(AppController.AUDIT_TRAIL, book);

	}

	@FXML
	void updateBookDetail(ActionEvent event) {
		this.book.updateBook(this.editedAuthors);
		
	}
	
	@FXML
    void deleteBook(ActionEvent event) throws IOException, SQLException, NamingException {
		this.book.deleteBook();
		AppController.getInstance().changeView(AppController.BOOK_LIST, this.book);
    }
	
	    @FXML
	    void showListofAuthor(ActionEvent event) throws IOException, SQLException, NamingException {
          AppController.getInstance().changeView(AppController.CREATE_BOOK_AUTHOR, book);
	    }
	    
	    @FXML
	    void getAuthorBook(MouseEvent event) throws SQLException, IOException, NamingException {
		  	if(event.getClickCount() == 3) {
		  		
		  		if(clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Intern")) {
					
					System.out.println("you are not authorised");
				}else {
		  		AuthorBook authorBook =  this.bookAuthorTable.getSelectionModel().getSelectedItem();
		  		Alert alert = new Alert(AlertType.CONFIRMATION);
		  		alert.setTitle("Confirmation Dialog");
		  		alert.setHeaderText("Delete Confirmation");
		  		alert.setContentText("Delete " + authorBook.getAuthor().getfirstName() +  "  ?");
		  		Optional<ButtonType> result = alert.showAndWait();
		  		if (result.get() == ButtonType.OK){
		  		    this.book.deleteBookAuthor(authorBook);
		  		    this.bookAuthorTable.getItems().remove(authorBook);
		  		    this.allAuthors.remove(authorBook);
		  		} else {
		  		    
		  		}
		  	}
		  	}
	    }
	  
	    @FXML
	    void onEdit(TableColumn.CellEditEvent<AuthorBook, Number> event) {
	    	AuthorBook authorsel = this.bookAuthorTable.getSelectionModel().getSelectedItem();
	    	authorsel.setRoyalty(event.getNewValue().intValue());
	    	//System.out.print(authorsel.getRoyalty());
	    	this.editedAuthors.add(authorsel);
	    	}
          
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		try {
			if(clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Data Entry") || clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Intern"))
				this.deleteBt.setDisable(true);
			if(clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Intern")) {
				this.addAuthorBt.setDisable(true);
				this.updatebt.setDisable(true);;
				
			}
				
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bookAuthorTable.setTooltip(new Tooltip("TRIPLE CLICK TO DELETE AUTHOR"));
		this.AutthorFName.setCellValueFactory(cellData -> cellData.getValue().getAuthor().firstNameProperty());
		this.authorLName.setCellValueFactory(cellData -> cellData.getValue().getAuthor().lastNameProperty());	
		this.authorRoyalty.setCellValueFactory(cellData -> cellData.getValue().royaltyProperty());
		this.authorRoyalty.setCellFactory(TextFieldTableCell.forTableColumn(new NumberStringConverter()));
		this.sign.setCellValueFactory(c-> new SimpleStringProperty("%"));
		this.bookAuthorTable.setItems(this.listOfAuthorsInfo);
		this.bookAuthorTable.setEditable(true);
		bookTitle.textProperty().bindBidirectional(book.titleproperty());
		YearPublished.textProperty().bindBidirectional(book.yearPublishedProperty(),new NumberStringConverter());
		bookIsbn.textProperty().bindBidirectional(book.isbnproperty());
		summarySpace.textProperty().bindBidirectional(book.getSummaryProperty());
		publishers.setValue(this.bookPublisher);
		publishers.setItems(this.listOfBooksPublisher);
	}

}
