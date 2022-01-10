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
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.StatusService;

public class LanConfigController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private ItemService itemService;
	private Item itemEntidade;
	private DespesaService despesaService;
	private Despesa despesaEntidade;
	private ItemPagamentoService itemPagamentoService;
	private StatusService statusService;

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
	private TextField txtDescontoIndividual;
	@FXML
	private TextArea txtAreaObs;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbBruto;
	@FXML
	private Label lbStatus;
	@FXML
	private Label lbPago;
	@FXML
	private Label lbAcrescimo;
	@FXML
	private Label lbDesconto;
	@FXML
	private Label lbRotuloItem;
	@FXML
	private Label lbRotuloQtde;
	@FXML
	private Label lbRotuloPreco;
	@FXML
	private Label lbRotuloDescInd;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private Button btItem;
	@FXML
	private Button btAtualizar;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btZerarDesconto;
	@FXML
	private TableView<Despesa> tbDespesa;
	@FXML
	private TableColumn<Despesa, Despesa> colunaRemover;
	@FXML
	private TableColumn<Despesa, Despesa> colunaReajustar;
	@FXML
	private TableColumn<Despesa, Despesa> colunaEditar;
	@FXML
	private TableColumn<Despesa, String> colunaDespNome;
	@FXML
	private TableColumn<Despesa, Double> colunaDespQuantidade;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorUnid;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorBruto;
	@FXML
	private TableColumn<Despesa, Double> colunaDespDesconto;
	@FXML
	private TableColumn<Despesa, Double> colunaDespAcrescimo;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorTotal;
	@FXML
	private TableView<ItemPagamento> tbTipoPag;
	@FXML
	private TableColumn<ItemPagamento, ItemPagamento> colunaRemoverTipoPag;
	@FXML
	private TableColumn<ItemPagamento, String> colunaTipoPagNome;
	@FXML
	private TableColumn<ItemPagamento, Double> colunaTipoPagValor;
	@FXML
	private ComboBox<Status> cmbStatus;

	private ObservableList<Despesa> obsListaDespesaTbView;
	private ObservableList<ItemPagamento> obsListaItemTipoPag;
	private ObservableList<Status> obsListaStatus;

	double total, descontoGlobal, acrescimo, descInd;
	int idLan, idDesp, idItem, x;
	TipoPag pag = new TipoPag();
	String ref;

	@FXML
	public void onBtItemAction(ActionEvent evento) {		
		Locale.setDefault(Locale.US);
		if(!lbStatus.getText().equals("PAGO")) {
			if (!txtItem.getText().equals("")) {
				int qtde = Utils.stringParaInteiro(0 + txtQuantidade.getText());
				if (!txtQuantidade.getText().equals("") && qtde > 0) {
					double preco = Utils.stringParaDouble(0 + txtPrecoUnid.getText());
					if (!txtPrecoUnid.getText().equals("") && preco > 0.0) {
						double desc = Utils.stringParaDouble(0 + txtDescontoIndividual.getText());
						if (desc < (preco * qtde)) {

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
		desp.setDescontoIndividual(Utils.stringParaDouble(0+ txtDescontoIndividual.getText()));
		double valorUnid, quantidade, descontoIndividual;
		valorUnid = Utils.stringParaDouble(txtPrecoUnid.getText());
		quantidade = Utils.stringParaInteiro(txtQuantidade.getText());
		descontoIndividual = Utils.stringParaDouble(0+ txtDescontoIndividual.getText());
		desp.setPrecoBruto(valorUnid * quantidade);
		desp.setPrecoTotal((valorUnid * quantidade) - descontoIndividual);
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
		txtDescontoIndividual.setText(String.valueOf(0.00));
		// Carregar TableView do Lançamento
		List<Despesa> listaDespesa = despesaService.listarPorId(obj.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		iniciarBotaoRemover();
		carregarValores();
		obj.setTotal(Utils.stringParaDouble(lbTotal.getText()));
		lancamentoService.atualizar(obj);
		carregarTableView();
		
						} else {
							Alertas.mostrarAlerta("Atenção!", "Desconto inválido.",
									"Valor do desconto igual ou superior ao valor do (Produto/Serviço).",
									AlertType.INFORMATION);
						}
					} else {
						Alertas.mostrarAlerta("Atenção!", "Preço inválido.", "Verificar se: \n-O valor informado é R$(0.00).",
								AlertType.INFORMATION);
					}
				} else {
					Alertas.mostrarAlerta("Atenção!", "Quantidade inválida.", "Verificar se: \n-A quantidade informada é 0.",
							AlertType.INFORMATION);
				}
			} else {
				Alertas.mostrarAlerta("Atenção!", "Descrição em branco.", "Favor inserir uma descrição para o (Produto/Serviço).",
						AlertType.INFORMATION);
			}
		}
		else {
			Alertas.mostrarAlerta("Atenção!", "Não é possível adicionar (Produto/Serviço), em lançamentos que já foram pagos.", "Para realizar esse tipo de ajuste é ncessário alterar o Status do lançamento.", AlertType.INFORMATION);
			txtItem.setText("");
		}
	}

	@FXML
	public void onBtAtualizar(ActionEvent evento) {		
		if(txtReferencia.getText() == null || txtReferencia.getText().equals("")) {
			Alertas.mostrarAlerta("Atenção!", "Referência em branco.", "Favor inserir uma referência para o lançamento ",
					AlertType.INFORMATION);
		}
		else {
			carregarValores();
			Lancamento obj = new Lancamento();
			obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setReferencia(txtReferencia.getText());		
		Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
		obj.setData(Date.from(instant));
		obj.setObs(txtAreaObs.getText());
		obj.setDesconto(Utils.stringParaDouble(lbDesconto.getText()));
		Status status = new Status();
		status = cmbStatus.getValue();
		if(lbStatus.getText().equals("EM ABERTO") && status == null){
			double t, b, d;
			b = Utils.stringParaDouble(lbBruto.getText());
			d = Utils.stringParaDouble(lbDesconto.getText());
			t = b-d; 
			Status status1 = new Status(1, null);
			obj.setStatus(status1);
			obj.setAcrescimo(0.00);
			obj.setTotal(t);			
		}else if(lbStatus.getText().equals("PAGO") && status == null) {
			Status status2 = new Status(2, null);
			obj.setStatus(status2);
			obj.setAcrescimo(Utils.stringParaDouble(lbAcrescimo.getText()));
			obj.setTotal(Utils.stringParaDouble(lbPago.getText()));
		} else {
			zerarAcrescimo();
			obj.setStatus(status);
			obj.setAcrescimo(0.00);
			obj.setTotal(total);
		}			
		lancamentoService.lanConfig(obj);
		if (status != null) {
			itemPagamentoService.limparItemPagamentoPorIdLan(obj);
		}
		carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.carregarTableView();
		});
		}
	}

	@FXML
	public void onBtVoltar(ActionEvent evento) {
		carregarPropriaView("/gui/TodasContasView.fxml", (TodasContasController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.carregarTableView();
		});
	}

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

	public void setItemPagamentoService(ItemPagamentoService itemPagamentoService) {
		this.itemPagamentoService = itemPagamentoService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComboBoxStatus();
		inicializarNodes();
		if (lancamentoEntidade != null) {
			carregarTableView();
			carregarTableViewItemPagamento();
		}
	}

	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		total = lancamentoEntidade.getTotal();
		lbStatus.setText(lancamentoEntidade.getStatus().getNome());
		txtAreaObs.setText(lancamentoEntidade.getObs());
		lbDesconto.setText(String.format("%.2f", lancamentoEntidade.getDesconto()));
		lbAcrescimo.setText(String.format("%.2f", lancamentoEntidade.getAcrescimo()));
	}
	
	public void carregarData() {
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
	}

	private void inicializarNodes() {
		txtId.setDisable(true);
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldInteger(txtQuantidade);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 70);		
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 60);
		Restricoes.setTextFieldTamanhoMaximo(txtQuantidade, 4);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldDouble(txtDescontoIndividual);
		Restricoes.setTextAreaTamanhoMaximo(txtAreaObs, 500);
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");

		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);// Formatar com(0,00)
		colunaDespValorBruto.setCellValueFactory(new PropertyValueFactory<>("precoBruto"));
		Utils.formatTableColumnValorDecimais(colunaDespValorBruto, 2);
		colunaDespDesconto.setCellValueFactory(new PropertyValueFactory<>("descontoIndividual"));
		Utils.formatTableColumnValorDecimais(colunaDespDesconto, 2);
		colunaDespAcrescimo.setCellValueFactory(new PropertyValueFactory<>("acrescimo"));
		Utils.formatTableColumnValorDecimais(colunaDespAcrescimo, 2);
		colunaDespValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(colunaDespValorTotal, 2);

		colunaTipoPagValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnValorDecimais(colunaTipoPagValor, 2);
		colunaTipoPagNome.setCellValueFactory(new PropertyValueFactory<>("nomePag"));

		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tbDespesa.prefHeightProperty().bind(stage.heightProperty());
	}

	public void carregarObjetosAssociados() {
		List<Status> listaStatus = statusService.buscarEmAbertoECancelado();
		obsListaStatus = FXCollections.observableArrayList(listaStatus);
		cmbStatus.setItems(obsListaStatus);
	}

	private void inicializarComboBoxStatus() {
		Callback<ListView<Status>, ListCell<Status>> factory = lv -> new ListCell<Status>() {
			@Override
			protected void updateItem(Status item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getNome());
			}
		};
		cmbStatus.setCellFactory(factory);
		cmbStatus.setButtonCell(factory.call(null));
	}

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

			if(x==1) {
			ReajustarFaturaDialogFormController controle = loader.getController();
			controle.setLancamentoService(new LancamentoService());
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(new Despesa());
			Lancamento lan = new Lancamento();
			lan.setId(Utils.stringParaInteiro(txtId.getText()));
			lan.setTotal(total);
			lan.setReferencia(txtReferencia.getText());
			lan.setObs(txtAreaObs.getText());
			Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
			lan.setData(Date.from(instant));
			controle.setLancamento(lan);
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(obj);
			controle.setItemService(new ItemService());
			controle.carregarCamposDeCadastro();
			}
			if(x==2) {
			ReajustarParcelaDialogFormController controle = loader.getController();
			controle.setLancamentoService(new LancamentoService());
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(new Despesa());
			Lancamento lan = new Lancamento();
			lan.setId(Utils.stringParaInteiro(txtId.getText()));
			lan.setTotal(total);
			lan.setReferencia(txtReferencia.getText());
			lan.setObs(txtAreaObs.getText());
			controle.setLancamento(lan);
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(obj);
			controle.setItemService(new ItemService());
			controle.carregarCamposDeCadastro();
			}
			if(x==3) {
				EditarDespesaDialogFormController3 controle = loader.getController();
				controle.setLancamentoService(new LancamentoService());
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));
				lan.setTotal(total);
				lan.setReferencia(txtReferencia.getText());
				lan.setObs(txtAreaObs.getText());
				controle.setLancamento(lan);
				controle.setDespesaService(new DespesaService());
				controle.setDespesa(obj);
				controle.carregarCamposDeCadastro();
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

	private void iniciarBotaoReajustar() {
		colunaReajustar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaReajustar.setCellFactory(param -> new TableCell<Despesa, Despesa>() {
			private final Button botao = new Button("Editar");
			
			@Override
			protected void updateItem(Despesa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				setStyle("-fx-color: #F0E68C");
				if(x==1) {
					x=1;
				botao.setOnAction(evento -> criarDialogForm(obj, "/gui/ReajustarFaturaDialogFormView.fxml",
						Utils.stageAtual(evento)));
				}
				if(x==2) {
					x=2;
					botao.setOnAction(evento -> criarDialogForm(obj, "/gui/ReajustarParcelaDialogFormView.fxml",
							Utils.stageAtual(evento)));
					}
				if(x==3) {
					x=3;
					botao.setOnAction(evento -> criarDialogForm(obj, "/gui/EditarDespesaDialogFormView3.fxml",
							Utils.stageAtual(evento)));
					}
				}
		});
	}
		
	private void iniciarBotaoRemover() {
		colunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemover.setCellFactory(param -> new TableCell<Despesa, Despesa>() {
			private final Button button = new Button("X");

			@Override
			protected void updateItem(Despesa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerDespesa(obj));
				setStyle("-fx-color: #FA8072");
				//setStyle("-fx-color: #FA8072; -fx-font-weight: bold");
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
				itemService.excluir(lan, desp);
				despesaService.excluir(desp);
				// Carregar TableView do Lançamento
				List<Despesa> listaDespesa = despesaService.listarPorId(Utils.stringParaInteiro(txtId.getText()));
				obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
				tbDespesa.setItems(obsListaDespesaTbView);
				iniciarBotaoRemover();
				carregarValores();
				lan.setTotal(Utils.stringParaDouble(lbTotal.getText()));
				lancamentoService.atualizar(lan);
				//total = soma;
				carregarTableView();
			} catch (BDIntegrityException ex) {
				Alertas.mostrarAlerta("Erro ao remover objeto", null, ex.getMessage(), AlertType.ERROR);
			}
		}
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);		
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		txtAreaObs.setText(lancamentoEntidade.getObs());
		if(!lbStatus.getText().equals("PAGO")) {
			if(lancamentoEntidade.getTipo().equals("F")) {
				x=1;
				desabilitarCampos();
			}
			else if(lancamentoEntidade.getTipo().equals("P")){
				x=2;
			}
			else {
				x=3;
			}
		iniciarBotaoReajustar();
		iniciarBotaoRemover();
		}
		else {		
		ocultarCampos();
		}
		carregarValores();
		desativarBtDesconto();
	}

	public void carregarTableViewItemPagamento() {
		List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lancamentoEntidade.getId());
		obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
		tbTipoPag.setItems(obsListaItemTipoPag);
		carregarValorPago();
	}

	public void carregarValorPago() {
		Double soma = 0.0;
		for (ItemPagamento tab : obsListaItemTipoPag) {
			soma += tab.getValor();
		}
		lbPago.setText(String.format("%.2f", soma));
	}
	
	public void carregarValores() {
		Double soma = 0.0;
		Double bruto = 0.0;
		Double acr = 0.0;
		descInd = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
			descInd += tab.getDescontoIndividual();
			bruto += tab.getPrecoBruto();	
			acr += tab.getAcrescimo();
		}
		if(!lbStatus.getText().equals("PAGO")) {
		lbDesconto.setText(String.format("%.2f", descInd));
		soma -= Utils.stringParaDouble(lbDesconto.getText());
		soma += Utils.stringParaDouble(lbAcrescimo.getText());
		lbTotal.setText(String.format("%.2f", soma));
		lbBruto.setText(String.format("%.2f", bruto));
		lbAcrescimo.setText(String.format("%.2f", acr));
		}
		else {
		lbDesconto.setText(String.format("%.2f", lancamentoEntidade.getDesconto()));
		}
		total = soma;
}
	public void ocultarCampos() {
		txtId.setDisable(true);
		txtItem.setVisible(false);
		txtPrecoUnid.setVisible(false);
		txtQuantidade.setVisible(false);
		txtDescontoIndividual.setVisible(false);
		btItem.setVisible(false);
		btZerarDesconto.setVisible(false);
		lbRotuloItem.setVisible(false);
		lbRotuloQtde.setVisible(false);
		lbRotuloPreco.setVisible(false);
		lbRotuloDescInd.setVisible(false);
	}
	
	public void desabilitarCampos() {
		txtQuantidade.setDisable(true);
		txtDescontoIndividual.setDisable(true);
		lbRotuloQtde.setDisable(true);
		lbRotuloDescInd.setDisable(true);
		}
	
	public void desativarBtDesconto() {
		if(lbDesconto.getText().equals("0.00")) {
		btZerarDesconto.setDisable(true);
		}
	}
	
	@FXML
	public void onBtZerarDesconto() {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação!", "Tem certeza que deseja remover os descontos dos itens?");
		if (result.get() == ButtonType.OK) {
		for (Despesa tab : obsListaDespesaTbView) {
			Despesa obj = new Despesa();
			obj.setId(tab.getId());
			obj.setPrecoTotal(tab.getPrecoBruto());
			obj.setDescontoIndividual(0.00);
			despesaService.rateioDesconto(obj);
			carregarTableView();
		}
		}
	}
	
	public void zerarAcrescimo() {
		for (Despesa tab : obsListaDespesaTbView) {
			Despesa obj = new Despesa();
			obj.setId(tab.getId());
			obj.setPrecoTotal(tab.getPrecoBruto());
			obj.setAcrescimo(0.00);
			despesaService.rateioAcrescimo(obj);
			carregarTableView();
		}
	}

}
