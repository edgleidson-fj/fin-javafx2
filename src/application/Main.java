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
import model.entidade.Mensagem;
import model.entidade.Status;
import model.entidade.Usuario;
import model.servico.MensagemService;
import model.servico.StatusService;
import model.servico.UsuarioService;
import seguranca.Criptografia;

public class Main extends Application {

	private static Scene mainScene;
	private int sitBd;
	private Integer ndh;
		
	@Override
	public void start(Stage primaryStage) {
		Date h = new Date();
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMM");
		String dh = fmt.format(h);
		ndh = Utils.stringParaInteiro(dh);
		sitA();
		//bd();	
		if (sitBd >= ndh) {
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
				bd();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			sitB(); 
			Alertas.mostrarAlertaBloqueio("Nome do App", "Licença expirada!", "Favor entrar em contato (xx)xxxxx-xxxx para reativação.",
					AlertType.ERROR);
		}
	}

	public static Scene pegarMainScene() {
		return mainScene;
	}

	public static void main(String[] args) {
		launch(args);
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

	public void sitA() {
		Criptografia c = new Criptografia();
		Mensagem msg = new Mensagem();
		MensagemService msgService = new MensagemService();
		msg = msgService.buscarPorId(2);
		sitBd = Utils.stringParaInteiro(c.descriptografar(msg.getDescricao()));
	}

	public void sitB() {		
		ndh += 135210 -(sitBd -= 1);
		Mensagem msg = new Mensagem();
		msg.setId(2);
		msg.setDescricao(
				"705240"+ndh+"73453123237176975012950218951220"+sitBd+"201321981720522"+sitBd+ndh+"3211498801000331150125");
		MensagemService msgService = new MensagemService();
		msgService.situacao(msg);
	}
}
