package bd;

import java.io.IOException;

import gui.util.Alertas;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BDException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public BDException(String mensagem) {
		super(mensagem);
		BDConfigDialogForm();
	}
	
	public void BDConfigDialogForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/BDConfigDialogFormView.fxml"));
			Pane painel = loader.load();

			Stage stageDialog = new Stage();
			stageDialog.setTitle("Banco de Dados");
			stageDialog.setScene(new Scene(painel));
			stageDialog.setResizable(false);
			stageDialog.initModality(Modality.WINDOW_MODAL);
			stageDialog.showAndWait();
		} catch (IOException ex) {
			ex.printStackTrace();
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar View", ex.getMessage(), AlertType.ERROR);
		}
	}
}
