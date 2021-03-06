package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import bd.BDException;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.LancamentoService;
import model.servico.UsuarioService;

public class EsqueciASenhaController implements Initializable/*, DataChangerListener*/ {

	private UsuarioService service;
	private Usuario entidade;
	
	@FXML
	private TextField txtNome;	
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtCPF;
	@FXML
	private TextField txtNovaSenha;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btRecuperar;
	@FXML
	private Button btLimpar;
	
	// Inje��o da depend�ncia.
	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}

	public void setUsuario(Usuario entidade) {
		this.entidade = entidade;
	}
	

	int x;
	public void onBtRecuperarSenha() {
		try {
			String nome = txtNome.getText();
			String cpf = txtCPF.getText();
			String email = txtEmail.getText();
			String novaSenha = txtNovaSenha.getText();	
			entidade.setNome(nome);
			entidade.setCpf(cpf);
			entidade.setEmail(email);
			entidade.setSenha(novaSenha);
			Usuario user = service.recuperarSenha(nome, cpf, email, novaSenha);
				entidade.setCpf(cpf);
					entidade.setLogado("S");
					service.logado(entidade);			
			} catch (BDException ex) {
			Alertas.mostrarAlerta("Favor revisar campos", null, ex.getMessage(), AlertType.ERROR);
		}		
	}

	public void onBtVoltar() {	
		x=1;
		atualizarPropriaView(null, "/gui/LoginView.fxml");
	}
	
	public void onBtLimpar() {
		txtNome.setText("");
		txtCPF.setText("");
		txtEmail.setText("");
		txtNovaSenha.setText("");
	}

		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComportamento();
		}

	private void inicializarComportamento() {
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 20);
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 14);
	}

	// ---------------------------------------------

	public void carregarCamposDeCadastro() {
		List<Usuario> lista = service.buscarTodos();
		for(Usuario u : lista) {
			 u.getLogado();
			
			 if(u.getLogado().equals("S")) {
					txtNome.setText(u.getNome());
					txtEmail.setText(u.getEmail());
					txtCPF.setText(u.getCpf());
			 }
		 }		
	}
	
	private  void atualizarPropriaView(Usuario obj, String caminhoDaView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVBox = loader.load();			
			
			if(x == 1) {
				LoginController controller = loader.getController();
				controller.setUsuario(new Usuario());
				controller.setUsuarioService(new UsuarioService());
			}
			else {
			EsqueciASenhaController controller = loader.getController();
			controller.setUsuario(obj);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarCamposDeCadastro();
			}
			
			Scene mainScene = Main.pegarMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVBox);
		} catch (IOException ex) {
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar a tela.", ex.getMessage(), AlertType.ERROR);
		}
	}
	
	
	//Mascara 999.999.999-99
		@FXML
		private void mascaraCPF() {
		   txtCPF.setOnKeyTyped((KeyEvent evento) -> {
	            if (!"01234567891234".contains(evento.getCharacter())) {
	                evento.consume();
	            }
	            if (evento.getCharacter().trim().length() == 0) {

	            } else if (txtCPF.getText().length() == 16) {
	                evento.consume();
	            }
	            switch (txtCPF.getText().length()) {
	                case 3:
	                	txtCPF.setText(txtCPF.getText() + ".");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 7:
	                	txtCPF.setText(txtCPF.getText() + ".");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 11:
	                	txtCPF.setText(txtCPF.getText() + "-");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 14:
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	            }
	        });
	    }

}
