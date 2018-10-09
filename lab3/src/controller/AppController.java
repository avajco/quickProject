package controller;

import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import app.Launcher;
import clientEjb.clientConn;
import core.ServercoreLocal;
import core.ServercoreRemote;
import lab2.db.AuditTrailGateway;
import lab2.db.AuthorTableGateway;
import lab2.db.BookTableGateWay;
import misc.CryptoStuff;
import model.Author;
import model.Book;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.util.Pair;

public class AppController implements Initializable, MyController {
	private static Logger logger = LogManager.getLogger(Launcher.class);
	  @FXML
	    private MenuItem logout;

	    @FXML
	    private MenuItem authorList;

	    @FXML
	    private MenuItem addAuthor;

	    @FXML
	    private  MenuItem quit;

	    @FXML
	    private MenuItem addBook;

	    @FXML
	    private MenuItem login;

	    @FXML
	    private MenuItem bookList;
	    @FXML
	    private MenuBar generalMenu;
	    @FXML
	    private PasswordField password;
	    @FXML
	    private TextField Username;
	   public  int sessionId;
	    private ServercoreRemote auth;
	    @FXML
	    private MenuItem publisherReport;
	    
	public static final int AUTHOR_LIST = 1;
	public static final int AUTHOR_DETAIL = 2;
	public static final int NEW_AUTHOR = 3;
	public static final int BOOK_LIST = 4;
	public static final int NEW_BOOK = 5;
	public static final int BOOK_DETAIL = 6;
	public static final int AUDIT_TRAIL = 7;
	public static final int AUDIT_TRAIL_AUTHOR = 8;
	public static final int CREATE_BOOK_AUTHOR = 9;
	public static final int GENERATE_PUBLISHER_REPORT = 10;
	public static final int HOME = 11;
	public int state = 0;
	private static AppController myInstance = null;
	private BorderPane rootPane = null;

	private Connection conn;

	private AppController() throws NamingException {
		// TODO: instantiate models
		//default to no session
	    //clientConn.getInstance();
		sessionId = ServercoreLocal.INVALID_SESSION;
	}

	public void changeView(int viewType, Object arg) throws IOException, SQLException, NamingException {
		MyController controller = null;
		URL fxmlFile = null;
		switch (viewType) {
		case AUTHOR_LIST:
			state = AUTHOR_LIST;
			fxmlFile = this.getClass().getResource("/view/AuthorList.fxml");
			controller = new AuthorListController(new AuthorTableGateway(conn));
			logger.info("switched to author List pane");
			break;
		case AUTHOR_DETAIL:
			state = AUTHOR_DETAIL;
			fxmlFile = this.getClass().getResource("/view/AuthorDetailView.fxml");
			controller = new AuthorDetailController((Author) arg);
			logger.info("switched to author detail pane");
			break;
		case NEW_AUTHOR:
			state = NEW_AUTHOR;
			fxmlFile = this.getClass().getResource("/view/addauthor.fxml");
			controller = new AuthorAddController(new AuthorTableGateway(conn), (Author) arg);
			logger.info("switched to add author pane");
			break;
		case BOOK_LIST:
			state = BOOK_LIST;
			fxmlFile = this.getClass().getResource("/view/BookList.fxml");
			controller = new BookController(new BookTableGateWay(conn));
			logger.info("switched to book list pane");
			break;
		case NEW_BOOK:
			state = NEW_BOOK;
			fxmlFile = this.getClass().getResource("/view/AddNewBook.fxml");
			controller = new AddNewBookController(new BookTableGateWay(conn), (Book) arg);
			logger.info("switched to add book pane");
			break;
		case BOOK_DETAIL:
			state = BOOK_DETAIL;
			fxmlFile = this.getClass().getResource("/view/BookDetailView.fxml");
			controller = new BookDetailController((Book) arg);
			logger.info("switched to book detail pane");
			break;
		case AUDIT_TRAIL:
			state = AUDIT_TRAIL;
			fxmlFile = this.getClass().getResource("/view/AuditTrailBookView.fxml");
			controller = new AuditTrailBookController(new AuditTrailGateway<Book>(conn),(Book)arg);
			logger.info("switched to book Audit trail");
			break;
		case AUDIT_TRAIL_AUTHOR:
			state = AUDIT_TRAIL_AUTHOR;
			fxmlFile = this.getClass().getResource("/view/AuditTrailAuthorView.fxml");
			controller = new AuditTrailAuthorController(new AuditTrailGateway<Author>(conn), (Author)arg);
			logger.info("switched to Author audit trail");
			break;
		case CREATE_BOOK_AUTHOR:
			state = CREATE_BOOK_AUTHOR;
			fxmlFile = this.getClass().getResource("/view/CreateBookAuthor.fxml");
			controller = new CreateBookAuthor(new AuthorTableGateway(conn), (Book)arg);
			//logger.info("switched to Author audit trail");
			break;
		case GENERATE_PUBLISHER_REPORT:
			state = GENERATE_PUBLISHER_REPORT;
			fxmlFile = this.getClass().getResource("/view/reportGenerator.fxml");
			controller = new generatePublishersController(new BookTableGateWay(conn));
			//logger.info("switched to Author audit trail");
			break;
		case HOME:
			state =HOME;
			fxmlFile = this.getClass().getResource("/view/LaunchScreen.fxml");
			controller = new AppController();
			//logger.info("switched to Author audit trail");
			break;
		
		}
		FXMLLoader loader = new FXMLLoader(fxmlFile);
		loader.setController(controller);
		Parent viewNode = loader.load();
		rootPane.setCenter(viewNode);
		// throw new AppException(e);
	
	}

	@FXML
	void ShowAuthorList(ActionEvent event) throws IOException, SQLException, NamingException {
		logger.info("Author list item clicked");
		changeView(AUTHOR_LIST, null);
	}

	@FXML
	void closeMe(ActionEvent event) {
		logger.info("Quit application item clicked");
		Platform.exit();
	}

	@FXML
	void addNewAuthor(ActionEvent event) throws IOException, SQLException, NamingException {
		logger.info("add new author clicked");
		changeView(NEW_AUTHOR, null);
	}

	@FXML
	void showBookList(ActionEvent event) throws IOException, SQLException, NamingException {
		changeView(BOOK_LIST, null);
	}

	@FXML
	void addNewBook(ActionEvent event) throws IOException, SQLException, NamingException {
		changeView(NEW_BOOK, null);
	}
	
	/*public MasterController() {
		//create an authenticator
		auth = new AuthenticatorLocal();

		//default to no session
		sessionId = Authenticator.INVALID_SESSION;
	}
*/
	private void doLogout() {
		sessionId =  ServercoreRemote.INVALID_SESSION;
		
		//restrict access to GUI controls based on current login session
		updateGUIAccess();
	}
	
	private void doLogin() throws NamingException {
		//display login modal dialog. get login (username) and password
		//key is login, value is pw
		String userName = this.Username.getText();
		String password = this.password.getText();
		if(Username == null || password == null) //canceled
			return;
		logger.info("userName is " + userName + ", password is " + password);
		
		//hash password
		String pwHash = CryptoStuff.sha256(password);
		System.out.println(pwHash);
		
		logger.info("sha256 hash of password is " + pwHash);
		
		//if get session id back, then replace current session
		if(clientConn.getInstance() == null) {
			System.out.print("instance is null");
		}else{
		sessionId = clientConn.getInstance().getLoginsha256(userName, pwHash);
	    clientConn.getInstance().SessionId =sessionId;
		}
		logger.info("session id is " + sessionId);
		
		//restrict access to GUI controls based on current login session
		updateGUIAccess();
	}
	
	public void openAll() {
		 this.authorList.setDisable(false);
		    this.addAuthor.setDisable(false);
		    this.bookList.setDisable(false);
		    this.addBook.setDisable(false);
		    this.publisherReport.setDisable(false);
	}
	public void closeAll() {
		 this.authorList.setDisable(true);
		    this.addAuthor.setDisable(true);
		    this.bookList.setDisable(true);
		    this.addBook.setDisable(true);
		    this.publisherReport.setDisable(true);
	}
	
	private void updateGUIAccess() {
		//if logged in, login should be disabled
		if(sessionId == ServercoreRemote.INVALID_SESSION) {
			this.login.setDisable(false);
		      this.closeAll();
		      }
		else {
			this.login.setDisable(true);
		   this.openAll();
	}
		
		//if not logged in, logout should be disabled
		if(sessionId == ServercoreRemote.INVALID_SESSION) {
			this.logout.setDisable(true);
			this.closeAll();
		}
		else
			this.logout.setDisable(false);
		
		//update fxml labels
		//textLogin.setText(auth.getUserNameFromSessionId(sessionId));
		//textSessionId.setText("Session id " + sessionId);
		
		/*//update menu info based on current user's access privileges
		if(auth.hasAccess(sessionId, ABACPolicyAuthDemo.CAN_ACCESS_CHOICE_1))
			choice1.setDisable(false);
		else 
			choice1.setDisable(true);
		if(auth.hasAccess(sessionId, ABACPolicyAuthDemo.CAN_ACCESS_CHOICE_2))
			choice2.setDisable(false);
		else 
			choice2.setDisable(true);
		if(auth.hasAccess(sessionId, ABACPolicyAuthDemo.CAN_ACCESS_CHOICE_3))
			choice3.setDisable(false);
		else 
			choice3.setDisable(true);*/
	}
	
	
	
	
	
	
	
	 @FXML
	    void generatePub(ActionEvent event) throws IOException, SQLException, NamingException {
          changeView(GENERATE_PUBLISHER_REPORT, null);
         
	    }
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		updateGUIAccess();
		
	}

    @FXML
    void login(ActionEvent event) throws NamingException, IOException, SQLException {
       this.doLogin();
       try {
			if(clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Intern")) {
				this.addAuthor.setDisable(true);
				this.addBook.setDisable(true);;
				
			}
				
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       changeView(GENERATE_PUBLISHER_REPORT, null);
       this.rootPane.setBottom(null);
	}
       //this.changeView(GENERATE_PUBLISHER_REPORT, null);

    @FXML
    void logout(ActionEvent event) throws NamingException, SQLException, IOException  {
    	this.doLogout();	
    	Platform.exit();	
    }

	public static AppController getInstance() throws NamingException {
		if (myInstance == null)
			myInstance = new AppController();
		return myInstance;
	}

	public BorderPane getRootPane() {
		return rootPane;
	}

	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public Connection getConnection() {
		return conn;
	}

	public void setConnection(Connection conn) {
		this.conn = conn;
	}

}
