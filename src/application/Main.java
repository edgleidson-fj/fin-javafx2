package application;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gui.util.Alertas;
import gui.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Status;
import model.entidade.Usuario;
import model.servico.StatusService;
import model.servico.UsuarioService;

public class Main extends Application {

	private static Scene mainScene;
	private Integer pz = 20220201;
	private int d = 0;
	private int x = 0;

	@Override
	public void start(Stage primaryStage) {
		expiracao();
		bd();
		if (x == 1) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
				ScrollPane scrollPane = loader.load();

				scrollPane.setFitToHeight(true);
				scrollPane.setFitToWidth(true);

				mainScene = new Scene(scrollPane);
				primaryStage.setScene(mainScene);
				primaryStage.setTitle("Minhas Despesas");
				primaryStage.setResizable(false);
				primaryStage.show();
				// bd();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Alertas.mostrarAlertaBloqueio("Aten��o!", "Bloqueado.", "Favor entrar em contato para regulariza��o.",
					AlertType.ERROR);
		}
	}

	public static Scene pegarMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void expiracao() {
		Date h = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String dh = fmt.format(h);
		Integer ndh = Utils.stringParaInteiro(dh);
		if (ndh < pz) {
			x = 1;
			d = pz - ndh;
			if (d <= 5) {
				Alertas.mostrarAlertaBloqueio("Aten��o!", "Bloqueio em " + d + " dia(s).",
						"Favor entrar em contato para regulariza��o." + "\n Ou clique no bot�o(OK) para continuar.",
						AlertType.INFORMATION);
			}
		}
	}

	public void bd() {
		try {
			UsuarioService usuarioService = new UsuarioService();
			Usuario usuarioEntidade = new Usuario();
			usuarioEntidade.setLogado("N");
			usuarioService.logadoN(usuarioEntidade);

			StatusService statusService = new StatusService();
			Status statusEntidade = new Status();
			statusEntidade.setId(1);
			statusEntidade.setNome("EM ABERTO");
			statusService.salvar(statusEntidade);
			statusEntidade.setId(2);
			statusEntidade.setNome("PAGO");
			statusService.salvar(statusEntidade);
			statusEntidade.setId(3);
			statusEntidade.setNome("VENCIDO");
			statusService.salvar(statusEntidade);
			statusEntidade.setId(4);
			statusEntidade.setNome("CANCELADO");
			statusService.salvar(statusEntidade);
		} catch (Exception e) {
			BDConfigDialogForm();
		}
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
