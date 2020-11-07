package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import bd.BDException;
import bd.BDIntegrityException;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entidade.TipoPag;
import model.servico.TipoPagService;

public class TipoPagController implements Initializable {

	private TipoPagService service;
	private TipoPag entidade;
	private ObservableList<TipoPag> obsLista;

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private Button btCancelar;

	@FXML
	private Button btSalvar;

	@FXML
	private TableView<TipoPag> tableViewTipoPagamento;

	@FXML
	private TableColumn<TipoPag, Integer> tableColumnID;

	@FXML
	private TableColumn<TipoPag, String> tableColumnNome;
	
	@FXML
	private TableColumn<TipoPag, TipoPag> tableColumnEditar;
	
	@FXML
	private TableColumn<TipoPag, TipoPag> tableColumnExcluir;

	// Injeção da dependência.
	public void setTipoPagService(TipoPagService service) {
		this.service = service;
	}

	public void setTipoPag(TipoPag entidade) {
		this.entidade = entidade;
	}
	

	public void onBtSalvar() {
		try {
			entidade = dadosDoCampoDeTexto();
			service.salvarOuAtualizar(entidade);
			atualizarPropriaView(entidade, "/gui/TipoPagView.fxml");
		} catch (BDException ex) {
			Alertas.mostrarAlerta("Erro ao salvar objeto", null, ex.getMessage(), AlertType.ERROR);
		}
	}

	public void onBtCancelar() {
		entidade = new TipoPag();
		atualizarPropriaView(entidade, "/gui/TipoPagView.fxml");
	}

	private void criarBotaoEditar() {
		tableColumnEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEditar.setCellFactory(param -> new TableCell<TipoPag, TipoPag>() {
			private final Button botao = new Button("Editar");

			@Override
			protected void updateItem(TipoPag obj, boolean vazio) {
				super.updateItem(obj, vazio);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				setStyle("-fx-color: #FFD700");	
				botao.setOnAction(evento -> atualizarPropriaView(obj, "/gui/TipoPagView.fxml"));
			}			
		});
	}

	private void criarBotaoExcluir() {
		tableColumnExcluir.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnExcluir.setCellFactory(param -> new TableCell<TipoPag, TipoPag>() {
			private final Button button = new Button("Remover");

			@Override
			protected void updateItem(TipoPag obj, boolean vazio) {
				super.updateItem(obj, vazio);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				setStyle("-fx-color: #FF6347");	
				button.setOnAction(evento -> excluirEntidade(obj));
			}
		});
	}
	
	private void excluirEntidade(TipoPag obj) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação", "Você tem certeza que deseja excluir?");

		if (result.get() == ButtonType.OK) {
			if (service == null) {
				throw new IllegalStateException("Service nulo!");
			}
			try {
				service.excluir(obj);
				carregarTableView();
			}
			catch (BDIntegrityException e) {
				Alertas.mostrarAlerta("Erro ao excluir objeto", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComportamento();
		}

	private void inicializarComportamento() {
		tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

		// Tamanho da tabela.
		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tableViewTipoPagamento.prefHeightProperty().bind(stage.heightProperty());

		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 20);
	}

	public void carregarTableView() {
		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		List<TipoPag> lista = service.buscarTodos();
		obsLista = FXCollections.observableArrayList(lista);
		tableViewTipoPagamento.setItems(obsLista);
		criarBotaoEditar();
		criarBotaoExcluir();
	}
	// ---------------------------------------------

	public TipoPag dadosDoCampoDeTexto() {
		TipoPag obj = new TipoPag();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setNome(txtNome.getText());
		return obj;
	}

	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(entidade.getId()));
		txtNome.setText(entidade.getNome());
	}
	
	private  void atualizarPropriaView(TipoPag obj, String caminhoDaView) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVBox = loader.load();			
			
			TipoPagController controller = loader.getController();
			controller.setTipoPag(obj);
			controller.carregarCamposDeCadastro();
			controller.setTipoPagService(new TipoPagService());
			controller.carregarTableView();
			        	
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
