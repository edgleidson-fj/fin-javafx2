package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import model.entidade.Status;
import model.entidade.TipoPag;
import model.entidade.Usuario;
import model.servico.StatusService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;

public class Main extends Application {

	private static Scene mainScene;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();

			// Código para deixar ScrollPane ajustado a janela.
			scrollPane.setFitToHeight(true); // Altura.
			scrollPane.setFitToWidth(true); // Largura.

			mainScene = new Scene(scrollPane);
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Gerenciador de despesas");
			primaryStage.setResizable(false);
			primaryStage.show();

			// Usuário
			UsuarioService usuarioService = new UsuarioService();
			Usuario usuarioEntidade = new Usuario();
			usuarioEntidade.setLogado("N");
			usuarioService.logadoN(usuarioEntidade);
			
			// Inserir Status no BD automaticamente.
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
			} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	// Pegar MainScene da classe, para disponibilizar para outras classes.
	public static Scene pegarMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
