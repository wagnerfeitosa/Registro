package util;

import javafx.scene.control.TextField;

public class Constraint {
	
	public static void settextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs,oldValue,newValue)->{
			if(newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs,oldValue,newValue)->{
			if(newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldMaxLength(TextField txt,int max) {
		txt.textProperty().addListener((obs,oldValue,newValue)->{
			if(newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});


}
}
