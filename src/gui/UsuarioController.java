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
import javafx.collections.ObservableList;
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
import model.entidade.Usuario;
import model.servico.UsuarioService;

public class UsuarioController implements Initializable/*, DataChangerListener*/ {

	private UsuarioService service;
	private Usuario entidade;
	private ObservableList<Usuario> obsLista;
	//private List<DataChangerListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;	
	@FXML
	private TextField txtSenha;
	@FXML
	private Button btCancelar;
	@FXML
	private Button btSalvar;
	@FXML
	
	// Injeção da dependência.
	public void setUsuarioService(UsuarioService service) {
		this.service = service;
	}

	public void setUsuario(Usuario entidade) {
		this.entidade = entidade;
	}
	
/*	public void sobrescrevaDataChangeListeners(DataChangerListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void notificacaoDataChangeListeners() {
		for(DataChangerListener x : dataChangeListeners) {
			x.onDataChanged();
		}
	}
	
	@Override
	public void onDataChanged() {
		carregarTableView();
	} */

	int x;
	public void onBtSalvar() {
		try {
			/*List<Usuario> lista = service.buscarTodos();
			for(Usuario u : lista) {
				 u.getLogado();*/
				
				 if(txtId.getText().equals("")) {
						x=1;
						entidade = dadosDoCampoDeTexto();
							service.salvar(entidade);
							atualizarPropriaView(entidade, "/gui/LoginView.fxml");						
				 }
				 else{
						x=2;
						 entidade = dadosDoCampoDeTexto();
							service.atualizar(entidade);
							atualizarPropriaView(entidade, "/gui/UsuarioView.fxml");
				 }
			} catch (BDException ex) {
			Alertas.mostrarAlerta("Erro ao salvar objeto", null, ex.getMessage(), AlertType.ERROR);
		}		
	}

	public void onBtCancelar() {
		x=1;
		atualizarPropriaView(null, "/gui/LoginView.fxml");
	}

		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComportamento();
		}

	private void inicializarComportamento() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 20);
		Restricoes.setTextFieldTamanhoMaximo(txtSenha, 40);
	}

	// ---------------------------------------------

	public Usuario dadosDoCampoDeTexto() {
		Usuario obj = new Usuario();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setNome(txtNome.getText());
		obj.setSenha(txtSenha.getText());
		return obj;
	}

	public void carregarCamposDeCadastro() {
		List<Usuario> lista = service.buscarTodos();
		for(Usuario u : lista) {
			 u.getLogado();
			
			 if(u.getLogado().equals("S")) {
				 txtId.setText(String.valueOf(u.getId()));
					txtNome.setText(u.getNome());
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
			UsuarioController controller = loader.getController();
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

}
