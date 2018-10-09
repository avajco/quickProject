package controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javax.naming.NamingException;

import clientEjb.clientConn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.text.Text;
import lab2.db.BookTableGateWay;
import model.ExcelReport;
import model.Publisher;

public class generatePublishersController implements Initializable, MyController {
	

	    @FXML
	    private Text UserName;
	    @FXML
	    private Button generateBt;
	    @FXML
	    private ChoiceBox<Publisher> publisherList;
	    private BookTableGateWay gateway;
	    private List<Publisher>  bookPublishers;
	    private ObservableList<Publisher> listOfPublisher;
	    
 public generatePublishersController(BookTableGateWay  gateway) {
	 this.gateway = gateway;
	 this.bookPublishers = gateway.getAllPublishers();
	 this.listOfPublisher = FXCollections.observableArrayList(this.bookPublishers);
 }
	    
	    @FXML
	    void report(ActionEvent event) throws IOException, SQLException, NamingException {
	    	Publisher publisher = this.publisherList.getValue();
	    	ExcelReport rp =  new ExcelReport(publisher, this.gateway);
	    }


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			if(clientConn.getInstance().getHasAccess(clientConn.getInstance().SessionId).equalsIgnoreCase("Intern")) {
				//this.(true);
				this.generateBt.setDisable(true);;
			// TODO Auto-generated method stub
			
}
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.publisherList.setItems(this.listOfPublisher);
}
}
