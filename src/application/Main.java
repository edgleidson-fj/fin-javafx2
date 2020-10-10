package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entidade.Usuario;
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
			usuarioEntidade.setId(1);
			usuarioEntidade.setLogado("N");
			usuarioService.logado(usuarioEntidade);
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
