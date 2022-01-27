package gui.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Restricoes {

	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}
	
		public static void setTextFieldDouble(TextField txt) {
			txt.textProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
					txt.setText(oldValue);
				}
			});
		}

	public static void setTextFieldTamanhoMaximo(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}
	
		public static void setTextAreaTamanhoMaximo(TextArea txtArea, int max) {
			txtArea.textProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue != null && newValue.length() > max) {
					txtArea.setText(oldValue);
				}
			});
		}	
		
		public static boolean validarEmail(String email){
			Pattern p = Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._]*@[a-zA-Z0-9]+([.][a-zA-Z]+)+");
			Matcher m = p.matcher(email);
			if(m.find() && m.group().equals(email)){
			return true;
			}
			else{
			return false;
			}
		}
}
