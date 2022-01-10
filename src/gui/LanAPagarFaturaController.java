package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.UsuarioService;

public class LanAPagarFaturaController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private ItemService itemService;
	private DespesaService despesaService;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;

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
	private Label lbTotal;
	@FXML
	private Label lbUsuario;
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
	private Button btCriarRegistroDeLancamento;
	@FXML
	private Button btItem;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btCancelar;
	@FXML
	private TableView<Despesa> tbDespesa;
	@FXML
	private TableColumn<Despesa, Despesa> colunaRemover;
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
	private TextArea txtAreaObs;

	private ObservableList<Despesa> obsListaDespesaTbView;

	double total, descInd;
	int idLan;
	int idDesp;
	int idItem;
	int parcela;
	int aux;
	int despesaId;
	int lancamentoIds;
	int usuarioId;
	int anoFatura;

	@FXML
	public void onBtCriarRegistroDeLancamento(ActionEvent evento) {
		if (txtId.getText().equals("")) {
			total += 0.0;
			Lancamento obj = new Lancamento();
			obj.setTotal(total);
			if (txtReferencia.getText().equals("")) {
				Alertas.mostrarAlerta("Atenção!", "Referência em branco.", "Favor inserir uma referência para o lançamento.",
						AlertType.INFORMATION);
			} else {
				if (datePickerData.getValue() == null) {
					Alertas.mostrarAlerta("Atenção", "Data em branco.", "Necessário informar a data para o primeiro vencimento.",
							AlertType.INFORMATION);
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dataSelecionada = (Date.from(instant));
				Date hoje = new Date();

				if (dataSelecionada.before(hoje)) {
					Alertas.mostrarAlerta("Atenção", "Data inválida (" + sdf.format(dataSelecionada) + ").",
							"Data selecionada igual ou anterior a data atual.", AlertType.INFORMATION);
				} else {
					LocalDate dtPicker = datePickerData.getValue();
					anoFatura = dtPicker.getYear();
					int mesSelecionado = dtPicker.getMonth().getValue();
					int repeticao = 12 - mesSelecionado;
					
					if (mesSelecionado == 12) {
						repeticao = 1;
					}
					else {
						repeticao += 1;
					}

					parcela = repeticao;
					for (int x = 1; x <= parcela; x++) {
						int mais30Dias = (x - 1) * 30;
						GregorianCalendar data = new GregorianCalendar(dtPicker.getYear(), dtPicker.getMonthValue(),
								dtPicker.getDayOfMonth(), 0, 0, 0);
						int numero = 0;
						data.add(Calendar.DATE, numero + mais30Dias);
						data.add(Calendar.MONTH, numero - 1);
						Date dataParcelas = data.getTime();
						obj.setData(dataParcelas);
						// Usuário logado.
						Usuario user = new Usuario();
						user.setId(usuarioId);
						obj.setUsuario(user);
						obj.setReferencia("Fatura " + txtReferencia.getText());
						obj.setTipo("F");
						lancamentoService.salvar(obj);
						lancamentoIds = obj.getId();
					}
					txtId.setText(String.valueOf(obj.getId()));
					datePickerData.setValue(LocalDate.ofInstant(obj.getData().toInstant(), ZoneId.systemDefault()));
					int id = obj.getId();
					idLan = id;
					aux = id - parcela;
					desocultarCampos();
				}
			}
		} else {
			Alertas.mostrarAlerta("Lançamento registrado.", null, "Favor inserir o (Produto/Serviço) do lançamento. ",
					AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtADDItem(ActionEvent evento) {
		Locale.setDefault(Locale.US);
		if (!txtItem.getText().equals("")) {
			double preco = Utils.stringParaDouble(0 + txtPrecoUnid.getText());
			if (!txtPrecoUnid.getText().equals("") && preco > 0.0) {
				// Despesa
				Despesa desp = new Despesa();
				desp.setNome(txtItem.getText());
				desp.setQuantidade(Utils.stringParaInteiro(txtQuantidade.getText()));
				desp.setPrecoUnid(Utils.stringParaDouble(txtPrecoUnid.getText()));
				desp.setDescontoIndividual(Utils.stringParaDouble(txtDescontoIndividual.getText()));
				double valorUnid, quantidade, descontoIndividual;
				valorUnid = Utils.stringParaDouble(txtPrecoUnid.getText());
				quantidade = Utils.stringParaInteiro(txtQuantidade.getText());
				descontoIndividual = Utils.stringParaDouble(txtDescontoIndividual.getText());
				desp.setPrecoBruto(valorUnid * quantidade);
				desp.setPrecoTotal((valorUnid * quantidade) - descontoIndividual);
				despesaService.salvar(desp);
				despesaId = desp.getId();

				for (int x = 0; x < parcela; x++) {
					// Lancamento
					Lancamento obj = new Lancamento();
					lbTotal.setText(String.valueOf(obj.getTotal()));
					obj.setId(lancamentoIds);
					obj.setReferencia(txtReferencia.getText());
					obj.setTotal((total));
					lancamentoService.atualizar(obj);
					lancamentoIds--;
					// Item
					Item item = new Item();
					item.setLancamento(obj);
					item.setDespesa(desp);
					itemService.salvar(item);
					// Carregar campos
					txtId.setText(String.valueOf(obj.getId()));
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
				}
				lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
			} else {
				Alertas.mostrarAlerta("Atenção", "Preço inválido.", "Verificar se: \n-O valor informado é R$(0.00).",
						AlertType.INFORMATION);
			}
		} else {
			Alertas.mostrarAlerta("Atenção", "Descrição em branco.", "Favor inserir uma descrição para o (Produto/Serviço).",
					AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		if (txtId.getText().equals("") || txtPrecoUnid.getText().equals("")) {
			Alertas.mostrarAlerta("Lançamento incompleto!", "Favor revisar todos campos", null, AlertType.INFORMATION);
		} else {
			for (int x = 0; x < parcela; x++) {
				obj.setId(lancamentoIds);
				obj.setDesconto(0.00);
				obj.setObs(txtAreaObs.getText());
				obj.setTipo("F");
				lancamentoService.confirmarLanAPagar(obj);
				lancamentoIds--;
			}
			Alertas.mostrarAlerta(null, "Lançamento da fatura (" + txtReferencia.getText() + ") realizado com sucesso.",
					"Será registrado uma fatura mensalmente, até o final de " + anoFatura, AlertType.INFORMATION);
			carregarPropriaView("/gui/LanAPagarFaturaView.fxml", (LanAPagarFaturaController controller) -> {
				controller.setLancamento(new Lancamento());
				controller.setLancamentoService(new LancamentoService());
				controller.setDespesaService(new DespesaService());
				controller.setItemService(new ItemService());
				controller.setUsuario(new Usuario());
				controller.setUsuarioService(new UsuarioService());
				controller.carregarUsuarioLogado();
				controller.ocultarCampos();
			});
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
		carregarPropriaView("/gui/LanAPagarParceladoView.fxml", (LanAPagarFaturaController controller) -> {
			controller.setLancamento(new Lancamento());
			controller.setLancamentoService(new LancamentoService());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			controller.ocultarCampos();
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

	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public void setUsuario(Usuario usuarioEntidade) {
		this.usuarioEntidade = usuarioEntidade;
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		inicializarNodes();
	}

	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
	}

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 70);
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 45);
		Restricoes.setTextAreaTamanhoMaximo(txtAreaObs, 500);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldDouble(txtDescontoIndividual);		
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");

		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);// Formatar com(0,00)
		colunaDespValorBruto.setCellValueFactory(new PropertyValueFactory<>("precoBruto"));
		Utils.formatTableColumnValorDecimais(colunaDespValorBruto, 2);
		colunaDespDesconto.setCellValueFactory(new PropertyValueFactory<>("descontoIndividual"));
		Utils.formatTableColumnValorDecimais(colunaDespDesconto, 2);
		colunaDespValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(colunaDespValorTotal, 2);

		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tbDespesa.prefHeightProperty().bind(stage.heightProperty());
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
				setStyle("-fx-color: #FF6347");
				button.setOnAction(event -> removerDespesa(obj));
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
				for (int x = 0; x < parcela; x++) {
					lan.setId(lancamentoIds);
					itemService.excluir(lan, desp);
					despesaService.excluir(desp);
					// Carregar TableView do Lançamento
					List<Despesa> listaDespesa = despesaService.listarPorId(Utils.stringParaInteiro(txtId.getText()));
					obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
					tbDespesa.setItems(obsListaDespesaTbView);
					iniciarBotaoRemover();
					carregarValores();
					lan.setTotal(Utils.stringParaDouble(lbTotal.getText()));
					lancamentoIds--;
				}
				lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
				for (int x = 0; x < parcela; x++) {
					lan.setId(lancamentoIds);
					lan.setTotal(Utils.stringParaDouble(lbTotal.getText()));
					lancamentoService.atualizar(lan);
					lancamentoIds--;
				}
				lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
				desocultarCampos();
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
		carregarValores();
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

	public void carregarValores() {
		Double soma = 0.0;
		descInd = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
			descInd += tab.getDescontoIndividual();
		}
		lbTotal.setText(String.format("%.2f", soma));
	}

	public void ocultarCampos() {
		txtId.setDisable(true);
		txtItem.setVisible(false);
		txtPrecoUnid.setVisible(false);
		txtQuantidade.setVisible(false);
		txtDescontoIndividual.setVisible(false);
		btItem.setVisible(false);
		lbRotuloItem.setVisible(false);
		lbRotuloQtde.setVisible(false);
		lbRotuloPreco.setVisible(false);
		lbRotuloDescInd.setVisible(false);
	}

	public void desocultarCampos() {
		datePickerData.setDisable(true);
		txtReferencia.setDisable(true);
		txtItem.setVisible(true);
		txtPrecoUnid.setVisible(true);
		txtQuantidade.setVisible(true);
		txtDescontoIndividual.setVisible(true);
		btItem.setVisible(true);
		lbRotuloItem.setVisible(true);
		lbRotuloQtde.setVisible(true);
		lbRotuloPreco.setVisible(true);
		lbRotuloDescInd.setVisible(true);
		btCriarRegistroDeLancamento.setVisible(false);
	}

}
