package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import gui.util.Restricoes;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Lancamento;
import model.entidade.Mensagem;
import model.entidade.Usuario;
import model.servico.LancamentoService;
import model.servico.MensagemService;
import model.servico.UsuarioService;
import seguranca.Criptografia;

public class LoginController implements Initializable {

	private UsuarioService usuarioService;
	private Usuario usuarioEntidade; 
	private MensagemService mensagemService;

	@FXML
	private Label lbUsuario;
	@FXML
	private TextField txtCPF;
	@FXML
	private PasswordField txtSenha;
	@FXML
	private TextField txtID;
	@FXML
	private Button btEntrar;
	@FXML
	private Button btCriarUsuario;
	@FXML
	private Button btLimpar;
	@FXML
	private Button btEsqueciSenha;

	int usuarioId;
	String userNome;
	int id;	
	
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Criptografia c = new Criptografia();		
		String cpf = c.criptografia(txtCPF.getText());
		String senha ="";
		if(!txtSenha.getText().equals("")) {
			senha = c.criptografia(txtSenha.getText());	
		}		
		usuarioEntidade.setCpf(cpf);
		usuarioEntidade.setSenha(senha);
		Usuario user = usuarioService.login(cpf, senha);
		if(user != null) {				
				usuarioEntidade.setCpf(cpf);
				usuarioEntidade.setLogado("S");
				usuarioService.logado(usuarioEntidade);
		
				List<Usuario> lista = usuarioService.buscarTodos();
			for(Usuario u : lista) {
				 u.getLogado();		
				 if(u.getLogado().equals("S")) {
					 usuarioId = u.getId();			 
				 }				
			 }			
			
			carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.rotinasAutomaticas();
				controller.carregarTableView();
			});			
			
			Mensagem msg = new Mensagem();
			msg = mensagemService.buscarPorId(1);
			Calendar datahoje = Calendar.getInstance();
			int mesAtual = datahoje.get(Calendar.MONTH)+1;
			if(mesAtual == 12 && msg.getMostrar().equals("S")) {
			MsgProxAnoDialogForm();		
			}
			if(mesAtual != 12 && msg.getMostrar().equals("N")) {
				msg = new Mensagem();
				msg.setId(1);
				msg.setMostrar("S");
				mensagemService.atualizar(msg);
			}			
		} else{
			Alertas.mostrarAlerta(null,null , "Usuário e/ou Senha incorreto!", AlertType.WARNING);
		}			
		}
	
	@FXML
	public void onBtCriarUsuario() {
		carregarView("/gui/UsuarioView.fxml", (UsuarioController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
		});
	}
	
	@FXML
	public void onBtLimpar() {
		txtCPF.setText("");
		txtSenha.setText("");
	}
	
	@FXML
	public void onBtEsqueciASenha() {
		carregarView("/gui/EsqueciASenha.fxml", (EsqueciASenhaController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
		});
	}
	
		public void setUsuarioService(UsuarioService usuarioService) {
			this.usuarioService = usuarioService;
		}
		public void setUsuario(Usuario usuarioEntidade) {
			this.usuarioEntidade = usuarioEntidade;
		}
		public void setMensagemService(MensagemService mensagemService) {
			this.mensagemService = mensagemService;
		}
			
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setUsuario(new Usuario());
		setUsuarioService(new UsuarioService());
		setMensagemService(new MensagemService());
		Restricoes.setTextFieldInteger(txtCPF);
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 11);
		Restricoes.setTextFieldTamanhoMaximo(txtSenha, 20);
		}
		
	private synchronized <T> void carregarView(String caminhoDaView, Consumer<T> acaoDeInicializacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVBox = loader.load();

			Scene mainScene = Main.pegarMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVBox);

			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
		} catch (IOException ex) {
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar a tela.", ex.getMessage(), AlertType.ERROR);
		}
	}			
	
	public void MsgProxAnoDialogForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MsgProxAnoView.fxml"));
			Pane painel = loader.load();					
			
			MsgProxAnoController controle = loader.getController();
			controle.setMensagem(new Mensagem());
			controle.setMensagemService(new MensagemService());
			controle.carregarMensagem();					

			Stage stageDialog = new Stage();
			stageDialog.setTitle("AVISO");
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

