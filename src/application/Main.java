package application;

import java.text.SimpleDateFormat;
import java.util.Date;

import gui.util.Alertas;
import gui.util.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import model.entidade.Status;
import model.entidade.Usuario;
import model.servico.StatusService;
import model.servico.UsuarioService;

public class Main extends Application {

	private static Scene mainScene;
	private Integer prazo = 20211201; 
	private int dias = 0;
	private int x = 0;

	@Override
	public void start(Stage primaryStage) {
		expiracao();
		if (x == 1) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
				ScrollPane scrollPane = loader.load();

				// Código para deixar ScrollPane ajustado a janela.
				scrollPane.setFitToHeight(true); // Altura.
				scrollPane.setFitToWidth(true); // Largura.

				mainScene = new Scene(scrollPane);
				primaryStage.setScene(mainScene);
				primaryStage.setTitle("Minhas Despesas");
				primaryStage.setResizable(false);
				primaryStage.show();
				bd();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			Alertas.mostrarAlertaBloqueio("Atenção!", "Bloqueado.", "Favor entrar em contato para regularização.",
					AlertType.ERROR);
		}
	}

	// Pegar MainScene da classe, para disponibilizar para outras classes.
	public static Scene pegarMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void expiracao() {
		Date hoje = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String diaHoje = fmt.format(hoje);
		Integer numeroDiaHoje = Utils.stringParaInteiro(diaHoje);
		if (numeroDiaHoje < prazo) {
			x = 1;
			dias = prazo - numeroDiaHoje;
			if (dias <= 5) {
				Alertas.mostrarAlertaBloqueio("Atenção!", "Bloqueio em " + dias + " dia(s).",
						"Favor entrar em contato para regularização." + "\n Ou clique no botão(OK) para continuar.",
						AlertType.INFORMATION);
			}
		}
	}

	public void bd() {
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
	}
}
