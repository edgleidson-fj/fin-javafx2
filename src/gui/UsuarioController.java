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

public class UsuarioController implements Initializable/* , DataChangerListener */ {

	private UsuarioService service;
	private Usuario entidade;

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtSenha;
	@FXML
	private TextField txtEmail;
	@FXML
	private TextField txtCPF;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btSalvar;

	// Injeção da dependência.
	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}

	public void setUsuario(Usuario entidade) {
		this.entidade = entidade;
	}

	int x;
	String CPFexiste = "N";

	public void onBtSalvar() {
		try {
			if (!txtNome.getText().equals("") & !txtEmail.getText().equals("") & !txtCPF.getText().equals("")
					& !txtSenha.getText().equals("")) {
				entidade = dadosDoCampoDeTexto();
				if (txtId.getText().equals("")) {
					if (CPFexiste.equals("N")) {
						x = 1;
						service.salvar(entidade);
						entidade.setCpf(txtCPF.getText());
						entidade.setLogado("S");
						service.logado(entidade);
						atualizarPropriaView(entidade, "/gui/ContasEmAbertoMesAtualView.fxml");
						Alertas.mostrarAlerta(null, "Cadastro realizado com sucesso!", null, AlertType.INFORMATION);
					} else {
						Alertas.mostrarAlerta("Atenção!", "Cadastro inválido.", "CPF informado já existe no sistema.",
								AlertType.WARNING);
						x = 2;
						atualizarPropriaView(entidade, "/gui/UsuarioView.fxml");
					}
				} else {
					x = 3;
					service.atualizar(entidade);
					atualizarPropriaView(entidade, "/gui/UsuarioView.fxml");
					Alertas.mostrarAlerta(null, "Atualização realizado com sucesso!", null, AlertType.INFORMATION);
				}
			} else {
				Alertas.mostrarAlerta("Atenção!", "Nome, CPF, Email e/ou Senha em branco.", null,
						AlertType.INFORMATION);
			}
		} catch (BDException ex) {
			Alertas.mostrarAlerta("Erro ao salvar", null, ex.getMessage(), AlertType.ERROR);
		}
	}

	public void onBtVoltar() {
		x = 4;
		atualizarPropriaView(null, "/gui/LoginView.fxml");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComportamento();
	}

	private void inicializarComportamento() {
		txtId.setDisable(true);
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 20);
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 14);
		Restricoes.setTextFieldTamanhoMaximo(txtSenha, 40);
	}

	// ---------------------------------------------

	public Usuario dadosDoCampoDeTexto() {
		service.logado(entidade);
		Criptografia c = new Criptografia();
		Usuario obj = new Usuario();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setNome(txtNome.getText());
		obj.setSenha(c.criptografia(txtSenha.getText()));
		obj.setEmail(txtEmail.getText());
		obj.setCpf(txtCPF.getText());
		VerificarCPF();
		return obj;
	}

	public void carregarCamposDeCadastro() {
		List<Usuario> lista = service.buscarTodos();
		for (Usuario u : lista) {
			u.getLogado();

			if (u.getLogado().equals("S")) {
				txtId.setText(String.valueOf(u.getId()));
				txtNome.setText(u.getNome());
				txtEmail.setText(u.getEmail());
				txtCPF.setText(u.getCpf());
				Criptografia c = new Criptografia();
				txtSenha.setText(c.descriptografar(u.getSenha()));
				ocultarOuDesocultar();
			}
		}
	}

	private void atualizarPropriaView(Usuario obj, String caminhoDaView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVBox = loader.load();

			if (x == 1) {
				ContasEmAbertoMesAtualController controller = loader.getController();
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.rotinasAutomaticas();
				controller.carregarTableView();
			}
			if (x == 2) {
				UsuarioController controller = loader.getController();
				controller.setUsuarioService(new UsuarioService());
				controller.setUsuario(new Usuario());
				controller.carregarCamposDeCadastro();
			}
			if (x == 3) {
				UsuarioController controller = loader.getController();
				controller.setUsuario(obj);
				controller.setUsuarioService(new UsuarioService());
				controller.carregarCamposDeCadastro();
			}
			if (x == 4) {
				LoginController controller = loader.getController();
				controller.setUsuario(new Usuario());
				controller.setUsuarioService(new UsuarioService());
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

	public void ocultarOuDesocultar() {
		if (txtId.getText().equals("")) {
			btVoltar.setVisible(true);
		} else {
			btVoltar.setVisible(false);
		}
	}

	public void VerificarCPF() {
		List<Usuario> lista = service.buscarTodos();
		for (Usuario u : lista) {
			u.getCpf();
			if (txtCPF.getText().equals(u.getCpf())) {
				CPFexiste = "S";
			}
		}
	}
}
