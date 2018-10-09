package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lab2.db.AuthorTableGateway;
import lab2.db.BookTableGateWay;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.*;
import model.Author;
import model.Book;

public class BookController implements Initializable, MyController {
	private Logger logger = LogManager.getLogger(BookController.class);
	@FXML
	private ListView<Book> bookList;
	private ObservableList<Book> books;
	private BookTableGateWay gateway;
	private Book book;
	private HashMap<Integer,ObservableList<Book>> viewMap;
	@FXML
	private TextField searchBox;
	@FXML
	private Button searchButton;
	//indexing used for the lazy loading 
	private int numberOfDbFetch;
	private int currView;
	private int firstId;
	private int lastId;
	private int countViews;
	private int sreachIndex;
	

	public BookController(BookTableGateWay gateway) throws SQLException {
		this.gateway = gateway;
		books = this.gateway.getBooks(0,0, "first");
		if(!(books.isEmpty())) {
		this.firstId = books.get(0).getId();
		this.lastId = books.get(49).getId();
		this.numberOfDbFetch = 1;
		this.currView = 1;
		this.viewMap = new HashMap<Integer,ObservableList<Book>>();
		this.viewMap.put(this.numberOfDbFetch, books);
		this.countViews = this.gateway.getTableSummary();
		this.sreachIndex = 600;
		}
		
	}

	public BookController(ObservableList<Book> books) {
		this.books = books;
	}

	@FXML
	void loadBookCell(MouseEvent event) throws IOException, SQLException, NamingException {
		if (event.getClickCount() == 2) {
			Book book = bookList.getSelectionModel().getSelectedItem();
			if (book != null) {
				logger.info("view Book detail");
				AppController.getInstance().changeView(AppController.BOOK_DETAIL, book);
			}
		}
	}

	@FXML
	void searchBook(ActionEvent event) throws SQLException {
		if (this.searchBox.getText().isEmpty()) {
		 this.bookList.setItems(this.viewMap.get(this.currView));
		}
		ObservableList<Book> searchResult = this.gateway.searchBookByPattern(this.searchBox.getText());
		ObservableList<Book> books = FXCollections.observableArrayList();
		System.out.println(searchResult.size());
		int searchPut = this.sreachIndex;
		 for(int i = 0;i <searchResult.size();i++ ) {
			 if(books.size() == 50 || i != searchResult.size()) {
				 books.add(searchResult.get(i));
			 }else { this.viewMap.put(searchPut, books);
			   searchPut++;
			   books.clear();
			  // books.add(searchResult.get(i));
			 }
			 }
		 this.currView = this.sreachIndex;
	    this.bookList.setItems(this.viewMap.get(currView));
	}
	
	
	@FXML
    void firstPage(ActionEvent event) throws SQLException {
	   if(currView != 1) {
		   this.currView = 1;
		   this.bookList.setItems(this.viewMap.get(this.currView));
	   }else {
		   //
	   }

    }

    @FXML
    void previousPage(ActionEvent event) {
    	this.currView --;
    	if(currView < 0 ) 
    	this.currView  = 1;
    	if(this.currView == 600)this.currView = 600;
     if(this.viewMap.containsKey(this.currView) && currView != 1 ) {
    	 this.bookList.setItems(this.viewMap.get(this.currView));
     }else{
    	 //
     }
    }

    @FXML
    void nextPage(ActionEvent event) throws SQLException {
    	//int next = this.numberOfDbFetch + 1;
    	  this.currView ++;
    	  System.out.print(this.lastId);
    		if(this.viewMap.containsKey(this.currView)) {
    			this.bookList.setItems(this.viewMap.get(currView));
    		}else {
    			this.books = this.gateway.getBooks(this.firstId,this.lastId, "next");
    			this.numberOfDbFetch ++;
    			//this.currView ++;
    			this.firstId = books.get(0).getId();
    			this.lastId = this.books.get(49).getId();
    			this.viewMap.put(this.currView,this.books); 
    			this.bookList.setItems(this.viewMap.get(currView));
    		}
    }

    @FXML
    void lastPage(ActionEvent event) {
        
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		this.bookList.setItems(this.viewMap.get(this.numberOfDbFetch));
	}
}