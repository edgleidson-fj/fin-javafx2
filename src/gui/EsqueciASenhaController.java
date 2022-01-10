package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import bd.BDException;
import gui.util.Alertas;
import gui.util.Restricoes;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.LancamentoService;
import model.servico.UsuarioService;
import seguranca.Criptografia;

public class EsqueciASenhaController implements Initializable{

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

	// Injeção da dependência.
	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}

	public void setUsuario(Usuario entidade) {
		this.entidade = entidade;
	}

	int x;

	public void onBtRecuperarSenha() {
		if (!txtNome.getText().equals("") && !txtCPF.getText().equals("") && !txtEmail.getText().equals("")) {
			if (!txtNovaSenha.getText().isEmpty()) {
				try {
					Criptografia c = new Criptografia();
					String nome = txtNome.getText();
					String cpf = txtCPF.getText();
					String email = txtEmail.getText();
					String novaSenha = c.criptografia(txtNovaSenha.getText());
					entidade.setNome(nome);
					entidade.setCpf(cpf);
					entidade.setEmail(email);
					entidade.setSenha(novaSenha);
					Usuario user = service.recuperarSenha(nome, cpf, email, novaSenha);
					user = service.verificarUsuario(nome, cpf, email);
					if (user == null) {
						Alertas.mostrarAlerta("Atenção!", "Informação divergente entre: NOME, CPF e/ou EMAIL.", null,
								AlertType.INFORMATION);
					} else {
						entidade.setCpf(cpf);
						entidade.setLogado("S");
						service.logado(entidade);
						Alertas.mostrarAlerta(null, "Recuperação de senha realizada com sucesso.", null,
								AlertType.INFORMATION);
						carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
							controller.setLancamentoService(new LancamentoService());
							controller.setLancamento(new Lancamento());
							controller.rotinasAutomaticas();
							controller.carregarTableView();
						});		

					}
				} catch (BDException ex) {
					Alertas.mostrarAlerta("Erro!", "Erro ao tentar recuperar senha.", ex.getMessage(), AlertType.ERROR);
				}
			} else {
				Alertas.mostrarAlerta("Atenção!", "Senha em branco.", "Favor cadastrar nova senha.", AlertType.INFORMATION);
			}
		} else {
			Alertas.mostrarAlerta("Atenção!", "Campo(s) em branco", null, AlertType.INFORMATION);
		}
	}

	public void onBtVoltar() {
		carregarView("/gui/LoginView.fxml", (LoginController controller) ->{
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
		});
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

	public void carregarCamposDeCadastro() {
		List<Usuario> lista = service.buscarTodos();
		for (Usuario u : lista) {
			u.getLogado();

			if (u.getLogado().equals("S")) {
				txtNome.setText(u.getNome());
				txtEmail.setText(u.getEmail());
				txtCPF.setText(u.getCpf());
			}
		}
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

			// Pegando segundo parametro dos onMenuItem()
			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
		} catch (IOException ex) {
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar a tela.", ex.getMessage(), AlertType.ERROR);
		}
	}

	// Mascara 999.999.999-99
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
