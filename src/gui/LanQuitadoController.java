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
import model.entidade.TipoPag;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;

public class LanQuitadoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private ItemService itemService;
	private DespesaService despesaService;
	private TipoPagService tipoPagService;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;
	private ItemPagamentoService itemPagamentoService;
	private ItemPagamento itemPagamentoEntidade;
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
	private TextField txtTipoPagValor;
	@FXML
	private TextField txtDescontoIndividual;
	@FXML
	private TextField txtDesconto;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbUsuario;
	@FXML
	private Label lbPago;
	@FXML
	private Label lbDiferenca;
	@FXML
	private Label lbDesconto;
	@FXML
	private Label lbDescontoIndividual;
	@FXML
	private Label lbRotuloItem;
	@FXML
	private Label lbRotuloQtde;
	@FXML
	private Label lbRotuloPreco;
	@FXML
	private Label lbRotuloDescInd;
	@FXML
	private Label lbRotuloDescGlobal;
	@FXML
	private Label lbRotuloPagamento;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private ComboBox<TipoPag> cmbTipoPag;
	@FXML
	private Button btCriarRegistroDeLancamento;
	@FXML
	private Button btItem;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btCancelar;
	@FXML
	private Button btDesconto;
	@FXML
	private Button btItemPagamento;
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
	private TableColumn<Despesa, Double> colunaDespValorBruto;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorTotal;
	@FXML
	private TableColumn<Despesa, Double> colunaDespDesconto;
	@FXML
	private TableView<ItemPagamento> tbTipoPag;
	@FXML
	private TableColumn<ItemPagamento, ItemPagamento> colunaRemoverTipoPag;
	@FXML
	private TableColumn<ItemPagamento, String> colunaTipoPagNome;
	@FXML
	private TableColumn<ItemPagamento, Double> colunaTipoPagValor;
	// --------------------------------------------------------
	private ObservableList<TipoPag> obsListaTipoPag;
	private ObservableList<Despesa> obsListaDespesaTbView;
	private ObservableList<ItemPagamento> obsListaItemTipoPag;
	// ---------------------------------------------------------

	double total, descontoGlobal;
	int idLan;
	int idDesp;
	int idItem;
	String ref;
	int usuarioId, x;
	String userNome;

	@FXML
	public void onBtCriarRegistroDeLancamento(ActionEvent evento) {
		if(txtId.getText().equals("")) {
		total += 0.0;
		Date hoje = new Date();
		Lancamento obj = new Lancamento();
		obj.setReferencia(txtReferencia.getText());
		obj.setTotal(total);
		if (txtReferencia.getText().equals("")) {
			Alertas.mostrarAlerta("Atenção!", "Referência em branco.", "Favor inserir uma referência para o lançamento ",
					AlertType.INFORMATION);
		} else {
			if (datePickerData.getValue() == null) {
				obj.setData(hoje);
			} else {
				Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
				obj.setData(Date.from(instant));
			}
			// Usuário logado.
			Usuario user = new Usuario();
			user.setId(usuarioId);
			obj.setUsuario(user);
			obj.setTipo("N");
			lancamentoService.salvar(obj);
			txtId.setText(String.valueOf(obj.getId()));
			datePickerData.setValue(LocalDate.ofInstant(obj.getData().toInstant(), ZoneId.systemDefault()));
			int id = obj.getId();
			idLan = id;
			ref = txtReferencia.getText();
			desocultarCampos();
		}
		}
		else {
			Alertas.mostrarAlerta("Lançamento registrado.", null, "Favor inserir o (Produto/Serviço) do lançamento. ", AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtItemAction(ActionEvent evento) {
		Locale.setDefault(Locale.US);
		if(!txtItem.getText().equals("")) {
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
		iniciarBotaoEditar();
		carregarValores();
		ativarCampos();
		obj.setTotal(Utils.stringParaDouble(lbTotal.getText()));
		lancamentoService.atualizar(obj);
		// Limpando Item de Pagamento
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));
				itemPagamentoService.limparItemPagamentoPorIdLan(lan);
				List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
				obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
				tbTipoPag.setItems(obsListaItemTipoPag);
				carregarValorPago();
				lbDesconto.setText("0.00");
				txtDesconto.setText("0.00");
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
	
	
	@FXML
	public void onBtItemPagamento(ActionEvent evento) {
		Locale.setDefault(Locale.US);

		double valorInformado, valorDiferenca;
		valorInformado = Utils.stringParaDouble(0+ txtTipoPagValor.getText());
		valorDiferenca = Utils.stringParaDouble(lbDiferenca.getText());

		if (valorInformado <= valorDiferenca && valorInformado != 0) {
			Lancamento obj = new Lancamento();
			obj.setId(Utils.stringParaInteiro(txtId.getText()));
			obj.setReferencia(txtReferencia.getText());
			obj.setTotal((total));
			lancamentoService.atualizar(obj);
			txtId.setText(String.valueOf(obj.getId()));

			TipoPag pag = new TipoPag();
			pag = cmbTipoPag.getValue();

			itemPagamentoEntidade.setLancamento(obj);
			itemPagamentoEntidade.setTipoPag(pag);
			itemPagamentoEntidade.setValor(Utils.stringParaDouble(txtTipoPagValor.getText()));
			itemPagamentoEntidade.setNomePag(pag.getNome());
			itemPagamentoService.salvar(itemPagamentoEntidade);

			List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(obj.getId());
			obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
			tbTipoPag.setItems(obsListaItemTipoPag);
			carregarValorPago();
			txtTipoPagValor.setText(lbDiferenca.getText());
			iniciarBotaoRemoverItemPagamento();		
			} else {
				Alertas.mostrarAlerta("Atenção!","Pagamento inválido.",  "Verificar se: \n- O valor informado superior ao valor restante. \n- O valor informado é R$(0.00).", AlertType.INFORMATION);
		}
	}

	public void onBtDesconto() {
		double t = Utils.stringParaDouble(lbTotal.getText());
		double desc = Utils.stringParaDouble(0+ txtDesconto.getText());
		if (desc < t) {
		Double soma = 0.0;
		Double desconto = 0.0;

		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
		}
		desconto = Utils.stringParaDouble(txtDesconto.getText());
		double total = soma - desconto;
		lbTotal.setText(String.format("%.2f", total));
		lbDiferenca.setText(String.format("%.2f", total));
		lbDesconto.setText(String.format("%.2f", desconto));
		txtDesconto.setText(String.format("%.2f", desconto));
		txtTipoPagValor.setText(String.format("%.2f", total));
		
		// Limpando Item de Pagamento
		Lancamento lan = new Lancamento();
		lan.setId(Utils.stringParaInteiro(txtId.getText()));
		itemPagamentoService.limparItemPagamentoPorIdLan(lan);
		List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
		obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
		tbTipoPag.setItems(obsListaItemTipoPag);
		carregarValorPago();
	}
		else {
			Alertas.mostrarAlerta("Atenção", "Desconto inválido.",
					"Valor do desconto igual ou superior ao valor do lançamento.",
					AlertType.INFORMATION);
		}
	}

	
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		try {
			obj.setId(Utils.stringParaInteiro(txtId.getText()));
			if (txtId.getText().equals("") || txtPrecoUnid.getText().equals("")) {
				Alertas.mostrarAlerta("Lançamento incompleto!", "Favor revisar todos campos", null, AlertType.INFORMATION);
			} else {
				double valorDiferenca;
				valorDiferenca = Utils.stringParaDouble(lbDiferenca.getText());

				if (valorDiferenca == 0) {
					double descInd, descGlobal;
					descInd = Utils.stringParaDouble(lbDescontoIndividual.getText());
					descGlobal = Utils.stringParaDouble(lbDesconto.getText());
					obj.setDesconto(descInd + descGlobal);
					total = Utils.stringParaDouble(lbTotal.getText());
					obj.setTotal(total);
					lancamentoService.confirmarLanQuitado(obj);
					carregarPropriaView("/gui/LanQuitadoView.fxml", (LanQuitadoController controller) -> {
						controller.setLancamento(new Lancamento());
						controller.setLancamentoService(new LancamentoService());
						controller.setDespesaService(new DespesaService());
						controller.setItemService(new ItemService());
						controller.setItemPagamentoService(new ItemPagamentoService());
						controller.setItemPagamento(new ItemPagamento());
						controller.setTipoPagService(new TipoPagService());
						controller.setUsuario(new Usuario());
						controller.setUsuarioService(new UsuarioService());
						controller.carregarObjetosAssociados();
						controller.carregarUsuarioLogado();
						controller.ocultarCampos();
					});
				} else {
					Alertas.mostrarAlerta("Atenção!", "Pagamento inválido!", "Favor revisar formas de pagamento.",
							AlertType.INFORMATION);
				}
			}
		} catch (RuntimeException ex) {
			Alertas.mostrarAlerta("Lançamento incompleto!", "Favor revisar todos campos", null, AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtCancelar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		if (!txtId.getText().equals("")) {
			lancamentoService.cancelar(obj);
			Alertas.mostrarAlerta(null, null, "Lançamento cancelado com sucesso", AlertType.INFORMATION);
		}
		carregarPropriaView("/gui/LanQuitadoView.fxml", (LanQuitadoController controller) -> {
			controller.setLancamento(new Lancamento());
			controller.setLancamentoService(new LancamentoService());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setTipoPagService(new TipoPagService());
			controller.setItemPagamentoService(new ItemPagamentoService());
			controller.setItemPagamento(new ItemPagamento());
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.carregarObjetosAssociados();
			controller.carregarUsuarioLogado();
			controller.ocultarCampos();
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

	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setTipoPagService(TipoPagService tipoPagService) {
		this.tipoPagService = tipoPagService;
	}

	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public void setUsuario(Usuario usuarioEntidade) {
		this.usuarioEntidade = usuarioEntidade;
	}

	public void setItemPagamentoService(ItemPagamentoService itemPagamentoService) {
		this.itemPagamentoService = itemPagamentoService;
	}

	public void setItemPagamento(ItemPagamento itemPagamentoEntidade) {
		this.itemPagamentoEntidade = itemPagamentoEntidade;
	}
	// -----------------------------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Date hoje = new Date();
		datePickerData.setValue(LocalDate.ofInstant(hoje.toInstant(), ZoneId.systemDefault()));
		inicializarComboBoxTipoPag();
		inicializarNodes();
		if (lancamentoEntidade != null) {
			carregarTableView();
		}
	}
	// ------------------------------------------------------------------

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldInteger(txtQuantidade);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 70);
		Restricoes.setTextFieldTamanhoMaximo(txtQuantidade, 4);
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 45);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldDouble(txtDescontoIndividual);
		Restricoes.setTextFieldDouble(txtDesconto);
		Restricoes.setTextFieldDouble(txtTipoPagValor);
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");

		//colunaDespId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);// Formatar com(0,00)
		colunaDespValorBruto.setCellValueFactory(new PropertyValueFactory<>("precoBruto"));
		Utils.formatTableColumnValorDecimais(colunaDespValorBruto, 2);
		colunaDespValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(colunaDespValorTotal, 2);
		colunaDespDesconto.setCellValueFactory(new PropertyValueFactory<>("descontoIndividual"));
		Utils.formatTableColumnValorDecimais(colunaDespDesconto, 2);
		
		colunaTipoPagValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnValorDecimais(colunaTipoPagValor, 2);// Formatar com(0,00)
		colunaTipoPagNome.setCellValueFactory(new PropertyValueFactory<>("nomePag"));

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

			EditarDespesaDialogFormController controle = loader.getController();
			controle.setLancamentoService(new LancamentoService());
			Lancamento lan = new Lancamento();
			lan.setId(Utils.stringParaInteiro(txtId.getText()));
			lan.setTotal(total);
			lan.setReferencia(txtReferencia.getText());
			Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
			lan.setData(Date.from(instant));
			controle.setLancamento(lan);
			controle.setDespesaService(new DespesaService());
			controle.setDespesa(obj);
			controle.carregarCamposDeCadastro();

			// Limpando Item de Pagamento
			itemPagamentoService.limparItemPagamentoPorIdLan(lan);
			List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
			obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
			tbTipoPag.setItems(obsListaItemTipoPag);
			carregarValorPago();
			ativarCampos();
			lbDiferenca.setText(lbTotal.getText());
			lbDesconto.setText("0.00");
			txtDesconto.setText("0.00");

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

	private void iniciarBotaoEditar() {
		colunaEditar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaEditar.setCellFactory(param -> new TableCell<Despesa, Despesa>() {
			private final Button button = new Button("Editar");

			@Override
			protected void updateItem(Despesa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				setStyle("-fx-color: #FFD700");
				button.setOnAction(evento -> criarDialogForm(obj, "/gui/EditarDespesaDialogFormView.fxml",
						Utils.stageAtual(evento)));
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
				setStyle("-fx-color: #FF6347");
			}
		});
	}

	private void removerDespesa(Despesa desp) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja remover?");
		if (result.get() == ButtonType.OK) {
			if (despesaService == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));
				// Limpando Item de Pagamento
				itemPagamentoService.limparItemPagamentoPorIdLan(lan);
				List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
				obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
				tbTipoPag.setItems(obsListaItemTipoPag);
				carregarValorPago();
				lbDiferenca.setText("0.00");
				lbDesconto.setText("0.00");
				txtDesconto.setText("0.00");

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
				desocultarCampos();
				ativarCampos();
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
		iniciarBotaoEditar();
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		carregarValores();
	}

	public void carregarTableViewItemPagamento() {
		List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lancamentoEntidade.getId());
		obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
		tbTipoPag.setItems(obsListaItemTipoPag);
		carregarValorPago();
		txtTipoPagValor.setText(lbDiferenca.getText());
	}

	public void carregarUsuarioLogado() {
		if (usuarioEntidade == null) {
			System.out.println("entidade nulo");
		}
		if (usuarioService == null) {
			System.out.println("service nulo");
		}
		List<Usuario> lista = usuarioService.buscarTodos();
		for (Usuario u : lista) {
			u.getLogado();

			if (u.getLogado().equals("S")) {
				usuarioId = u.getId();
				lbUsuario.setText(String.valueOf(u.getNome()));
			}
		}
	}

	private void iniciarBotaoRemoverItemPagamento() {
		colunaRemoverTipoPag.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemoverTipoPag.setCellFactory(param -> new TableCell<ItemPagamento, ItemPagamento>() {
			private final Button button = new Button("X");

			@Override
			protected void updateItem(ItemPagamento obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> removerItemPagamento(obj));
				setStyle("-fx-color: #FF6347");
			}
		});
	}

	private void removerItemPagamento(ItemPagamento desp) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja remover?");
		if (result.get() == ButtonType.OK) {
			if (itemPagamentoService == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));

				// Limpando Item de Pagamento
				itemPagamentoService.excluir(lan.getId(), desp.getTipoPag().getId());
				List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
				obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
				tbTipoPag.setItems(obsListaItemTipoPag);
				carregarValorPago();
				ativarCampos();
				lbDesconto.setText("0.00");
				txtDesconto.setText("0.00");
			} catch (BDIntegrityException ex) {
				Alertas.mostrarAlerta("Erro ao remover objeto", null, ex.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	public void carregarValores() {
				Double soma = 0.0;
				Double descInd = 0.0;
				for (Despesa tab : obsListaDespesaTbView) {
					soma += tab.getPrecoTotal();
					descInd += tab.getDescontoIndividual();
				}
				lbTotal.setText(String.format("%.2f", soma));
				lbDiferenca.setText(String.format("%.2f", soma));
				lbDescontoIndividual.setText(String.format("%.2f", descInd));
				txtTipoPagValor.setText(String.format("%.2f", soma));
	}
	
	public void carregarValorPago() {
		Double valorDiferenca = 0.0;
		Double valorTotal = 0.0;
		Double soma = 0.0;
		for (ItemPagamento tab : obsListaItemTipoPag) {
			soma += tab.getValor();
		}
		lbPago.setText(String.format("%.2f", soma));
		valorTotal += Utils.stringParaDouble(lbTotal.getText());
		valorDiferenca = valorTotal - soma;
		lbDiferenca.setText(String.format("%.2f", valorDiferenca));
		txtTipoPagValor.setText(String.format("%.2f", valorDiferenca));
		if(lbDiferenca.getText().equals("0.00")) {
			desativarCampos();
		}
	}

	
	public void carregarData() {
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
	}
	
	public void ocultarCampos() {
		txtId.setDisable(true);
		txtItem.setVisible(false);
		txtPrecoUnid.setVisible(false);
		txtQuantidade.setVisible(false);
		txtDescontoIndividual.setVisible(false);
		txtDesconto.setVisible(false);
		txtTipoPagValor.setVisible(false);
		btItem.setVisible(false);
		btDesconto.setVisible(false);
		btItemPagamento.setVisible(false);
		lbRotuloItem.setVisible(false);
		lbRotuloQtde.setVisible(false);
		lbRotuloPreco.setVisible(false);
		lbRotuloDescInd.setVisible(false);
		lbRotuloDescGlobal.setVisible(false);
		lbRotuloPagamento.setVisible(false);
		cmbTipoPag.setVisible(false);
	}

	public void desocultarCampos() {
		datePickerData.setDisable(true);
		txtReferencia.setDisable(true);
		txtItem.setVisible(true);
		txtPrecoUnid.setVisible(true);
		txtQuantidade.setVisible(true);
		txtDescontoIndividual.setVisible(true);
		txtDesconto.setVisible(true);
		txtTipoPagValor.setVisible(true);
		btItem.setVisible(true);
		btDesconto.setVisible(true);
		btItemPagamento.setVisible(true);
		lbRotuloItem.setVisible(true);
		lbRotuloQtde.setVisible(true);
		lbRotuloPreco.setVisible(true);
		lbRotuloDescInd.setVisible(true);
		lbRotuloDescGlobal.setVisible(true);
		lbRotuloPagamento.setVisible(true);
		cmbTipoPag.setVisible(true);
		btCriarRegistroDeLancamento.setVisible(false);
	}
	
	public void ativarCampos() {
		txtTipoPagValor.setDisable(false);
		cmbTipoPag.setDisable(false);
		btItemPagamento.setDisable(false);
		txtDesconto.setDisable(false);
		btDesconto.setDisable(false);
	}
	
	public void desativarCampos() {
		txtTipoPagValor.setDisable(true);
		cmbTipoPag.setDisable(true);
		btItemPagamento.setDisable(true);
		txtDesconto.setDisable(true);
		btDesconto.setDisable(true);
	}

}
