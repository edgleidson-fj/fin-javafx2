package gui.util;

import javafx.scene.control.TextField;

// Restri��es = Constraints.
public class Restricoes {

	// Restrigir o campo TextField apenas com n�meros Inteiros.
	// Express�o regular ("\\d*") = Para n�meros inteiros.
	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	// Restrigir o campo TextField com quantidade m�xima de caracteres definido.
	public static void setTextFieldTamanhoMaximo(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	// Rstrigir o campo TextField apenas com n�meros Double (0.00).
	// Express�o regular ("\\d*([\\.]\\d*)?") = Para n�meros Double.
	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}

}
