package gui;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import bd.BDIntegrityException;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;

public class LanConfigController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private ItemService itemService;
	private Item itemEntidade;
	private DespesaService despesaService;
	private Despesa despesaEntidade;
	private TipoPagService tipoPagService;
	// ----------------------------------------------------------------

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtReferencia;
	@FXML
	private TextField txtItem;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private TextField txtPrecoUnid;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbStatus;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private ComboBox<TipoPag> cmbTipoPag;
	@FXML
	private Button btItem;
	@FXML
	private Button btAtualizar;
	@FXML
	private Button btCancelar;
	@FXML
	private Button btVoltar;
	@FXML
	private TableView<Despesa> tbDespesa;
	@FXML
	private TableColumn<Despesa, Despesa> colunaRemover;
	@FXML
	private TableColumn<Despesa, Despesa> colunaEditar;
	@FXML
	private TableColumn<Despesa, Integer> colunaDespId;
	@FXML
	private TableColumn<Despesa, String> colunaDespNome;
	@FXML
	private TableColumn<Despesa, Double> colunaDespQuantidade;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorUnid;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorTotal;
	@FXML
	private TextArea txtAreaObs;
	@FXML
	private TextField txtDesconto;
	@FXML
	private TextField txtAcrescimo;
	@FXML
	private Button btZerar;
//--------------------------------------------------------
	private ObservableList<TipoPag> obsListaTipoPag;
	private ObservableList<Despesa> obsListaDespesaTbView;
	// ---------------------------------------------------------

	double total, desconto, acrescimo;
	int idLan, idDesp, idItem;
	TipoPag pag = new TipoPag();
	String ref;

	@FXML
	public void onBtItemAction(ActionEvent evento) {
		Locale.setDefault(Locale.US);
		// Lancamento
		Lancamento obj = new Lancamento();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setReferencia(txtReferencia.getText());
		obj.setTotal((total));
		lancamentoService.atualizar(obj);
		txtId.setText(String.valueOf(obj.getId()));
		// Despesa
		Despesa desp = new Despesa();
		desp.setNome(txtItem.getText());
		desp.setQuantidade(Utils.stringParaInteiro(txtQuantidade.getText()));
		desp.setPrecoUnid(Utils.stringParaDouble(txtPrecoUnid.getText()));
		double valorUnid, quantidade;
		valorUnid = Utils.stringParaDouble(txtPrecoUnid.getText());
		quantidade = Utils.stringParaInteiro(txtQuantidade.getText());
		desp.setPrecoTotal(valorUnid * quantidade);
		despesaService.salvar(desp);
		// Item
		Item item = new Item();
		item.setLancamento(obj);
		item.setDespesa(desp);
		itemService.salvar(item);
		// Total
		total += desp.getPrecoTotal();
		lbTotal.setText(String.format("%.2f", total));
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		// Limpando os campos
		txtItem.setText("");
		txtQuantidade.setText(String.valueOf(1));
		txtPrecoUnid.setText(String.valueOf(0.00));
		// Carregar TableView do Lançamento
		List<Despesa> listaDespesa = despesaService.listarPorId(obj.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		iniciarBotaoRemover();
		// Valor Total
		Double soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
		}
		desconto = Utils.stringParaDouble(txtDesconto.getText());
		acrescimo = Utils.stringParaDouble(txtAcrescimo.getText());
		soma -= desconto;
		soma += acrescimo;
		lbTotal.setText(String.format("%.2f", soma));
		obj.setTotal(soma);
		lancamentoService.atualizar(obj);
		total = soma;
	}

	@FXML
	public void onBtAtualizar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
			obj.setId(Utils.stringParaInteiro(txtId.getText()));
			obj.setReferencia(txtReferencia.getText());
			Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setData(Date.from(instant));
			obj.setObs(txtAreaObs.getText());
			obj.setTipoPagamento(cmbTipoPag.getValue());
			obj.setDesconto(Utils.stringParaDouble(txtDesconto.getText()));
			obj.setAcrescimo(Utils.stringParaDouble(txtAcrescimo.getText()));
			desconto = Utils.stringParaDouble(txtDesconto.getText());
			acrescimo = Utils.stringParaDouble(txtAcrescimo.getText());			
			carregarTableView();			
			obj.setTotal(total);
		lancamentoService.lanConfig(obj);
			carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) -> {
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.carregarTableView();
			});
			 Alertas.mostrarAlerta(null, null, "Lançamento editado com sucesso", AlertType.INFORMATION);
	}

	@FXML
	public void onBtCancelar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		lancamentoService.cancelar(obj);
		carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.carregarTableView();
		});
		 Alertas.mostrarAlerta(null, null, "Lançamento cancelado com sucesso", AlertType.INFORMATION);
	}
	
		@FXML
		public void onBtVoltar(ActionEvent evento) {
			carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) -> {
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.carregarTableView();
			});
	}
	// ------------------------------------------------------------------

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setItem(Item itemEntidade) {
		this.itemEntidade = itemEntidade;
	}

	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setDespesa(Despesa despesaEntidade) {
		this.despesaEntidade = despesaEntidade;
	}

	public void setTipoPagService(TipoPagService tipoPagService) {
		this.tipoPagService = tipoPagService;
	}	
	// -----------------------------------------------------------------
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComboBoxTipoPag();
		inicializarNodes();
		if (lancamentoEntidade != null) {
			carregarTableView();
		}
	}

	// ------------------------------------------------------------------
	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		cmbTipoPag.setValue(lancamentoEntidade.getTipoPagamento());
		lbTotal.setText(String.format("%.2f", lancamentoEntidade.getTotal()));
		total = lancamentoEntidade.getTotal();
		lbStatus.setText(lancamentoEntidade.getStatus().getNome());		
		pag = lancamentoEntidade.getTipoPagamento();		
		txtAreaObs.setText(lancamentoEntidade.getObs());
		txtDesconto.setText(String.valueOf(lancamentoEntidade.getDesconto()));
		txtAcrescimo.setText(String.valueOf(lancamentoEntidade.getAcrescimo()));
	}	
	// -----------------------------------------------------------------------------------------------------

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 70);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 45);
		Restricoes.setTextAreaTamanhoMaximo(txtAreaObs, 500);
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");

		colunaDespId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);// Formatar com(0,00)
		colunaDespValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(colunaDespValorTotal, 2);

		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tbDespesa.prefHeightProperty().bind(stage.heightProperty());		
	}
	// -----------------------------------------------------------------

	public void carregarObjetosAssociados() {
		List<TipoPag> listaTipoPag = tipoPagService.buscarTodos();
		obsListaTipoPag = FXCollections.observableArrayList(listaTipoPag);
		cmbTipoPag.setItems(obsListaTipoPag);
		}

	private void inicializarComboBoxTipoPag() {
		Callback<ListView<TipoPag>, ListCell<TipoPag>> factory = lv -> new ListCell<TipoPag>() {
			@Override
			protected void updateItem(TipoPag item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		cmbTipoPag.setCellFactory(factory);
		cmbTipoPag.setButtonCell(factory.call(null));
	}
	
	// --------------------------------------------------

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

	public void criarDialogForm(Despesa obj, String nomeAbsoluto, Stage stagePai) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			EditarDespesaDialogFormController3 controle = loader.getController();
			controle.setLancamentoService(new LancamentoService());
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(new Despesa());
			Lancamento lan = new Lancamento();
			lan.setId(Utils.stringParaInteiro(txtId.getText()));
			lan.setTotal(total);
			lan.setReferencia(txtReferencia.getText());
			controle.setLancamento(lan);
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(obj);
			controle.carregarCamposDeCadastro();

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
	// -----------------------------------------------------------------------------------------------------------

	private void iniciarBotaoRemover() {
		colunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemover.setCellFactory(param -> new TableCell<Despesa, Despesa>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Despesa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerDespesa(obj));
			}
		});
	}

	private void removerDespesa(Despesa desp) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja remover?");
		if (result.get() == ButtonType.OK) {
			if (despesaService == null) {
				throw new IllegalStateException("Service desp nulo");
			}
			if (despesaEntidade == null) {
				throw new IllegalStateException("Entidade desp nulo");
			}
			if (itemService == null) {
				throw new IllegalStateException("Service item nulo");
			}
			if (itemEntidade == null) {
				throw new IllegalStateException("Entidade item nulo");
			}
			try {
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));
				itemService.excluir(lan,desp);
				despesaService.excluir(desp);
				//Carregar TableView do Lançamento 				
				List<Despesa> listaDespesa = despesaService.listarPorId(Utils.stringParaInteiro(txtId.getText())); 
				obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
				  tbDespesa.setItems(obsListaDespesaTbView);			
				  iniciarBotaoRemover();				  
				// Valor Total
					Double soma = 0.0;
					for (Despesa tab : obsListaDespesaTbView) {
						soma += tab.getPrecoTotal();
					}
					desconto = Utils.stringParaDouble(txtDesconto.getText());
					acrescimo = Utils.stringParaDouble(txtAcrescimo.getText());
					soma -= desconto;
					soma += acrescimo;
					lbTotal.setText(String.format("%.2f", soma));
					lan.setTotal(soma);
					lancamentoService.atualizar(lan);
					total = soma;
				  } catch (BDIntegrityException ex) {
				Alertas.mostrarAlerta("Erro ao remover objeto", null, ex.getMessage(), AlertType.ERROR);
			}
		}
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		iniciarBotaoRemover();
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		// Valor Total
		Double soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
		}
		desconto = Utils.stringParaDouble(txtDesconto.getText());
		acrescimo = Utils.stringParaDouble(txtAcrescimo.getText());
		soma -= desconto;
		soma += acrescimo;
		lbTotal.setText(String.format("%.2f", soma));
		total = soma;
	}
	
	public void zerarDescontoAcrescimo() {
		txtDesconto.setText("0.00");
		txtAcrescimo.setText("0.00");
		carregarTableView();
	}
}
