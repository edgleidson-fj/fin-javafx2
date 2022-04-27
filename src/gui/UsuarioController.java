package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import application.Main;
import bd.BDException;
import gui.util.Alertas;
import gui.util.BR;
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
import javafx.scene.layout.VBox;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.LancamentoService;
import model.servico.UsuarioService;
import seguranca.Criptografia;
import seguranca.Criptografia2;
import seguranca.Criptografia3;
import seguranca.Criptografia4;
import smtp.Email;

public class UsuarioController implements Initializable {

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
	private TextField txtTetoGastos;
	@FXML
	private TextField txtCPF;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btSalvar;
	@FXML
	private Button btLimparSenha;

	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}

	public void setUsuario(Usuario entidade) {
		this.entidade = entidade;
	}

	int x;
	String CPFexiste = "N";

	public void onBtSalvar() {
		Criptografia c = new Criptografia();
		Criptografia2 c2 = new Criptografia2();		
		try {
			if (!txtNome.getText().equals("") & !txtEmail.getText().equals("") & !txtCPF.getText().equals("")
					& !txtSenha.getText().equals("")) {
				entidade = dadosDoCampoDeTexto(); 
				if (txtId.getText().equals("") && x == 0) {
					if (CPFexiste.equals("N")) { 
						x = 1;
						entidade.setLogado("S");
						service.salvar(entidade);
						entidade.setCpf(c2.criptografia(txtCPF.getText()));
						service.logado(entidade);
						Email.envio(entidade, x);
						atualizarPropriaView(entidade, "/gui/ContasEmAbertoMesAtualView.fxml");
						Alertas.mostrarAlerta("Cadastro realizado com sucesso!", "Email enviado para "+c.descriptografar(entidade.getEmail()), "OBSERVAÇÃO: \nSe não estiver localizando o email, favor verificar: \n- Caixa de Spam;\n- O email informado no sistema; \n- Conexão de internet.", AlertType.INFORMATION);
					} else {
						Alertas.mostrarAlerta("Atenção!", "Cadastro inválido.", "CPF: "+txtCPF.getText()+" já existe no sistema.",
								AlertType.WARNING);
						x = 2;
						atualizarPropriaView(entidade, "/gui/UsuarioView.fxml");
					}
				} else if (!txtId.getText().equals("")) { 
					x = 3;
					entidade.setCpf(txtCPF.getText());
					entidade.setLogado("S");
					service.logado(entidade);
					service.atualizar(entidade);
					Email.envio(entidade, x);
					atualizarPropriaView(entidade, "/gui/UsuarioView.fxml");
					Alertas.mostrarAlerta("Atualização realizado com sucesso!", "Email enviado para "+c.descriptografar(entidade.getEmail()), "OBSERVAÇÃO: \nSe não estiver localizando o email, favor verificar: \n- Caixa de Spam;\n- O email informado no sistema; \n- Conexão de internet.", AlertType.INFORMATION);
				}
			} else {
				Alertas.mostrarAlerta("Atenção!", "Campo(s) em branco", null, AlertType.INFORMATION);
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
		Restricoes.setTextFieldInteger(txtCPF);
		Restricoes.setTextFieldDouble(txtTetoGastos);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 45);
		Restricoes.setTextFieldTamanhoMaximo(txtEmail, 45);
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 11);
		Restricoes.setTextFieldTamanhoMaximo(txtTetoGastos, 10);
		Restricoes.setTextFieldTamanhoMaximo(txtSenha, 20);
	}

	public Usuario dadosDoCampoDeTexto() {
		Criptografia c = new Criptografia();
		Criptografia2 c2 = new Criptografia2();
		Criptografia3 c3 = new Criptografia3();
		Criptografia4 c4 = new Criptografia4();
		service.logado(entidade);
		Boolean validarCPF = BR.validarCPF(txtCPF.getText());
		if (validarCPF.booleanValue() == true) { 
			Boolean validarEmail = Restricoes.validarEmail(txtEmail.getText());
			if (validarEmail.booleanValue() == true) { 
				int tamanhoDaSenha = txtSenha.getText().length();
				if (tamanhoDaSenha > 3) {
					x=0;					
					Usuario obj = new Usuario();
					obj.setId(Utils.stringParaInteiro(txtId.getText()));
					obj.setNome(c.criptografia(txtNome.getText()));
					obj.setSenha(c3.criptografia(txtSenha.getText()));
					obj.setEmail(c4.criptografia(txtEmail.getText()));
					obj.setCpf(c2.criptografia(txtCPF.getText()));
					double tetoGasto = Utils.stringParaDouble(0 + txtTetoGastos.getText());
					obj.setTetoGasto(tetoGasto);
					VerificarCPF();					
					return obj;
				} else {
					Alertas.mostrarAlerta("Atenção!", "Senha inválida.", "A senha deve ter no mínimo 4 caracteres.",
							AlertType.INFORMATION);
					x=5;
					Usuario user = new Usuario();
					return user;
				}
			} else {
				Alertas.mostrarAlerta("Atenção!", "Email inválido.", null, AlertType.INFORMATION);
				x=5;
				Usuario user = new Usuario();
				return user;
			}
		} else {
			Alertas.mostrarAlerta("Atenção!", "CPF inválido.", null, AlertType.INFORMATION);
			x=5;
			txtCPF.setText("");	
			Usuario user = new Usuario();
			return user;
		}		
	}

	public void carregarCamposDeCadastro() {
		Locale.setDefault(Locale.US);
		List<Usuario> lista = service.buscarTodos();
		for (Usuario u : lista) {
			u.getLogado();

			if (u.getLogado().equals("S")) { 
				Criptografia c = new Criptografia();
				Criptografia2 c2 = new Criptografia2();
				Criptografia3 c3 = new Criptografia3();
				Criptografia4 c4 = new Criptografia4();
				txtId.setText(String.valueOf(u.getId()));
				txtNome.setText(c.descriptografar(u.getNome()));
				txtEmail.setText(c4.descriptografar(u.getEmail()));
				txtCPF.setText(c2.descriptografar(u.getCpf()));
				txtSenha.setText(c3.descriptografar(u.getSenha()));
				if (u.getTetoGasto() > 0) {
					txtTetoGastos.setText(String.format("%.2f", +u.getTetoGasto()));
				} else {
					txtTetoGastos.setText("");
				}
				ocultarOuDesocultar();
				txtCPF.setEditable(false);
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

	public void ocultarOuDesocultar() {
		if (txtId.getText().equals("")) {
			btVoltar.setVisible(true);
		} else {
			btVoltar.setVisible(false);
		}
	}

	public void VerificarCPF() {
		Criptografia2 c2 = new Criptografia2();
		List<Usuario> lista = service.buscarTodos();
		for (Usuario u : lista) {
			u.getCpf();
			if (c2.criptografia(txtCPF.getText()).equals(u.getCpf())) {
				CPFexiste = "S";
			}
		}
	}

	public void OnBtLimparSenha() {
		txtSenha.setText("");	
	}
}
