package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

//import javafx.scene.paint.Color;
import application.Main;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;

public class TodasContasController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	// -------------------------------------------

	@FXML
	private TableView<Lancamento> tbLancamento;
	@FXML
	private TableColumn<Lancamento, Integer> colunaLanId;
	@FXML
	private TableColumn<Lancamento, Date> colunaLanData;
	@FXML
	private TableColumn<Lancamento, String> colunaLanRef;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanValor;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanDesconto;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanAcrescimo;
	@FXML
	private TableColumn<Lancamento, TipoPag> colunaTipoPag;
	@FXML
	private TableColumn<Lancamento, Status> colunaStatus;
	@FXML
	private TableColumn<Lancamento, Lancamento> colunaDetalhe;
	@FXML
	private TableColumn<Lancamento, Lancamento> colunaConfig;
	@FXML 
	private TextField txtConsultaID;	
	@FXML 
	private Button btConsultaID;
	@FXML
	private TextField txtConsultaReferenciaOuDespesa;
	@FXML 
	private Button btConsultaIDReferenciaOuDespesa;
	// -----------------------------------------------------

	private ObservableList<Lancamento> obsListaLancamentoTbView;
	// -----------------------------------------------------
	
	public void onBtConsultaID(ActionEvent evento) {
		Integer id = Utils.stringParaInteiro(txtConsultaID.getText());
		carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.carregarTableViewID(id);
			});		
	}
	
	@FXML
	public void onBtConsultaReferenciaOuDespesa(ActionEvent evento) {
		String refOuDespesa = txtConsultaReferenciaOuDespesa.getText();
		
		List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesa(refOuDespesa);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);				
	}

	//------------------------------------------------------

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	// ----------------------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();				
	}

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtConsultaID);
		colunaLanData.setCellValueFactory(new PropertyValueFactory<>("data"));
		Utils.formatTableColumnData(colunaLanData, "dd/MM/yyyy");
		colunaLanId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaLanRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
		colunaLanValor.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnValorDecimais(colunaLanValor, 2); // Formatar com(0,00)
		colunaLanDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));
		Utils.formatTableColumnValorDecimais(colunaLanDesconto, 2);
		colunaLanAcrescimo.setCellValueFactory(new PropertyValueFactory<>("acrescimo"));
		Utils.formatTableColumnValorDecimais(colunaLanAcrescimo, 2);
		colunaTipoPag.setCellValueFactory(new PropertyValueFactory<>("tipoPagamento"));
		colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		renderizarColunas();
	}
	
	public void carregarTableView() {		
		List<Lancamento> lista = lancamentoService.buscarTodos();
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoConfig();	
	}

	//Buscando Lançamento por ID.
	public void carregarTableViewID(Integer id) {			
		if(id != null) {
		List<Lancamento> unicoResultado = lancamentoService.buscarLanPorId(id);		
		obsListaLancamentoTbView = FXCollections.observableArrayList(unicoResultado);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoConfig();
		}else {
			carregarTableView();
		}
	}
	
//---------------------------------------------------------------------------
	private synchronized <T> void carregarPropriaView(String caminhoDaView, Consumer<T> acaoDeInicializacao) {
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

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai, String dialogForm) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			if (dialogForm == "detalhe") {
				DetalheDialogFormController controle = loader.getController();
				controle.setLancamento(obj);
				controle.setDespesaService(new DespesaService());
				controle.atualizarDialogForm();
				controle.carregarTableView();
			} else {
				LanConfigController controle = loader.getController();
				obj.setObs(obj.getObs());
				controle.setLancamento(obj);
				controle.setLancamentoService(new LancamentoService());
				controle.setDespesaService(new DespesaService());
				controle.setDespesa(new Despesa());
				controle.setItemService(new ItemService());
				controle.setItem(new Item());
				controle.setTipoPagService(new TipoPagService());
				controle.carregarCamposDeCadastro();
				controle.carregarObjetosAssociados();
				controle.carregarTableView();
			}

			Stage stageDialog = new Stage();
			stageDialog.setTitle("");
			stageDialog.setScene(new Scene(painel));
			stageDialog.setResizable(false); // Redimencionavel.
			stageDialog.initOwner(stagePai); // Stage pai da janela.
			stageDialog.initModality(Modality.WINDOW_MODAL); // Impedir o acesso de outras janela.
			stageDialog.showAndWait();
		} catch (IOException ex) {
			ex.printStackTrace();
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar View", ex.getMessage(), AlertType.ERROR);
		}
	}

	private void criarBotaoConfig() {
		colunaConfig.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaConfig.setCellFactory(param -> new TableCell<Lancamento, Lancamento>() {
			private final Button botao = new Button("@");

			@Override
			protected void updateItem(Lancamento obj, boolean vazio) {
				super.updateItem(obj, vazio);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				if (obj.getStatus().getNome().equals("CANCELADO")) {
					setGraphic(null);
				} else {
					setGraphic(botao);
					setStyle("-fx-color: #008080");
				}

				botao.setOnAction(
						evento -> carregarPropriaView("/gui/LanConfigView.fxml", (LanConfigController controle) -> {
							controle.setLancamento(obj);
							controle.setLancamentoService(new LancamentoService());
							controle.setDespesaService(new DespesaService());
							controle.setDespesa(new Despesa());
							controle.setItemService(new ItemService());
							controle.setItem(new Item());
							controle.setTipoPagService(new TipoPagService());
							controle.carregarCamposDeCadastro();
							controle.carregarObjetosAssociados();
							controle.carregarTableView();
						}));
			}
		});
	}

	public void rotinasAutomaticas() {
		lancamentoEntidade.setTotal(0.00);
		lancamentoService.exclusaoAutomatico(lancamentoEntidade);
		lancamentoService.cancelamentoAutomatico(lancamentoEntidade);
		lancamentoService.vencimentoAutomatico(lancamentoEntidade);
	}

	private void renderizarColunas() {
		// Status.
		colunaStatus.setCellFactory(column -> {
			return new TableCell<Lancamento, Status>() {
				@Override
				protected void updateItem(Status item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						if (item.getNome().equals("CANCELADO")) {
							setText(item.getNome());
							setStyle("-fx-background-color:#DCDCDC");
						}
						else {
						if(item.getNome().equals("VENCIDO")) {
							setText(item.getNome());
							setStyle("-fx-background-color:#FF6347");
						}
						else {
							setText(item.getNome());
							setStyle("");
						}
						}
					}
				}
			};
		});
	}

}
