package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Alerts {
	
	public static void alert(String message, String title, AlertType alertType) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Info");
		alert.setContentText(message);
		alert.show();
	}
	
	public static void info(String message) {
		alert(message, "Info", AlertType.INFORMATION);
	}
	
	public static void error(String message) {
		alert(message, "Info", AlertType.ERROR);
	}
	
	

}
