package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class BDConfigDialogFormController implements Initializable {

	@FXML
	private TextField txtDiretorio;
	@FXML
	private TextField txtBD;
	@FXML
	private TextField txtSenha;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btSair;

	@FXML
	public void onBtSalvar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		apagarTxt();
		criarTxt();
		parentStage.close();
	}

	@FXML
	public void onBtSair(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		parentStage.close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	public void criarTxt() {
		try (FileWriter fileWrite = new FileWriter("C:\\App - FinJFX\\db.properties", false);
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {

			printWrite.append("user=root\r\n" + "password=" + txtSenha.getText() + "\r\n" + "dburl=jdbc:mysql://"
					+ txtDiretorio.getText() + ":3306/" + txtBD.getText() + "\r\n" + "useSSL=false");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void apagarTxt() {
		try {
			Path path = Path.of("C:\\App - FinJFX\\db.properties");
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}


//Referencia: https://www.youtube.com/watch?v=sBoAecYJN0w
