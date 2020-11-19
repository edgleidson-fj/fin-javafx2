package gui.util;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

// Restrições = Constraints.
public class Restricoes {

	// Restrigir o campo TextField apenas com números Inteiros.
	// Expressão regular ("\\d*") = Para números inteiros.
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	// Restrigir o campo TextField com quantidade máxima de caracteres definido.
	public static void setTextFieldTamanhoMaximo(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}
	
	// Restrigir o campo TextArea com quantidade máxima de caracteres definido.
		public static void setTextAreaTamanhoMaximo(TextArea txtArea, int max) {
			txtArea.textProperty().addListener((obs, oldValue, newValue) -> {
				if (newValue != null && newValue.length() > max) {
					txtArea.setText(oldValue);
				}
			});
		}

	// Rstrigir o campo TextField apenas com números Double (0.00).
	// Expressão regular ("\\d*([\\.]\\d*)?") = Para números Double.
	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	

}
