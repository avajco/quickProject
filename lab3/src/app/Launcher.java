package app;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import javax.naming.NamingException;
import controller.AppController;
import lab2.db.AppException;
import lab2.db.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import clientEjb.clientConn;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//luncher extends Application
public class Launcher extends Application {
	private static Logger logger = LogManager.getLogger();
	private Connection conn;

	// "launch" will run the "start" method

	@SuppressWarnings("unused")
	@Override
	public void start(Stage primaryStage) throws IOException, NamingException {
		logger.info("start() called");
		//intialize controller and get db connection
		AppController appControl = AppController.getInstance();
		appControl.setConnection(conn);
		// TODO Auto-generated method stub
		URL res = this.getClass().getResource("/view/LaunchScreen.fxml");
		FXMLLoader loader = new FXMLLoader(res);
		loader.setController(appControl);
		Parent root = loader.load();
		appControl.setRootPane((BorderPane) root);

		Scene scene = new Scene(root, 640, 480);
		primaryStage.setTitle("Library Book Inventory System");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		super.init();
		logger.info("Creating connection...");

		try {
			conn = ConnectionFactory.createConnection();
		} catch (AppException e) {
			logger.fatal("Cannot connect to db");
			Platform.exit();
		}
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();

		// TODO: find out how to attach to shutdown hook
		// logger.info("Closing connection...");
		logger.info("Closing connection...");
		conn.close();
	}

	public static void main(String[] args) throws NamingException {
		launch(args);
	}
}