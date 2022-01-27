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
import javafx.scene.control.CheckBox;
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
import seguranca.Criptografia;

public class LanAPagarParceladoController implements Initializable {

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
	private TextField txtParcela;
	@FXML
	private TextField txtItem;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private TextField txtPrecoUnid;
	@FXML
	private TextField txtDescontoIndividual;
	@FXML
	private TextField txtTotalCompra;
	@FXML
	private TextField txtDescontoCompra;
	@FXML
	private TextField txtChaveNFE;
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
	private Label lbRotuloDataCompra;
	@FXML
	private Label lbRotuloDescCompra;
	@FXML
	private Label lbRotuloTotalCompra;
	@FXML
	private Label lbRotuloChaveNFE;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private DatePicker datePickerDataCompra;
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
	private CheckBox cbDetalheParcela;
	@FXML
	private TextArea txtAreaObs;

	private ObservableList<Despesa> obsListaDespesaTbView;

	double total, descInd, valorTotalCompra;
	int idLan;
	int idDesp;
	int idItem;
	int parcela;
	int aux;
	int despesaId;
	int lancamentoIds;
	int usuarioId;
	String dataCompra, parcelado, totalCompra, descontoCompra, chaveNFE;

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
					Alertas.mostrarAlerta("Atenção!", "Data em branco", "Necessário informar a data para primeira parcela.",
							AlertType.INFORMATION);
				}

				int parc = Utils.stringParaInteiro(0 + txtParcela.getText());
				if (txtParcela.getText().equals(null) || parc <= 0) {
					Alertas.mostrarAlerta("Atenção!", "Parcela inválida.", "Verificar se: \n-A parcela informada é 0.",
							AlertType.INFORMATION);
				}

				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Instant instant = Instant.from(datePickerData.getValue().atStartOfDay(ZoneId.systemDefault()));
				Date dataSelecionada = (Date.from(instant));
				Date hoje = new Date();

				if (dataSelecionada.before(hoje)) {
					Alertas.mostrarAlerta("Atenção!", "Data inválida (" + sdf.format(dataSelecionada) + ").",
							"Data selecionada igual ou anterior a data atual.", AlertType.INFORMATION);
				} else {
					parcela = Utils.stringParaInteiro(txtParcela.getText());
					LocalDate dtPicker = datePickerData.getValue();
					for (int x = 1; x <= parcela; x++) {
						int mais30Dias = (x - 1) * 30;
						GregorianCalendar data = new GregorianCalendar(dtPicker.getYear(), dtPicker.getMonthValue(),
								dtPicker.getDayOfMonth(), 0, 0, 0);
						int numero = 0;
						data.add(Calendar.DATE, numero + mais30Dias);
						data.add(Calendar.MONTH, numero - 1);
						Date dataParcelas = data.getTime();
						obj.setData(dataParcelas);

						Usuario user = new Usuario();
						user.setId(usuarioId);
						obj.setUsuario(user);

						if (cbDetalheParcela.isSelected()) {
							obj.setReferencia(txtReferencia.getText() + " " + x + "/" + parcela + " ");
						} else {
							obj.setReferencia(txtReferencia.getText());
						}
						obj.setTipo("P");
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
			int qtde = Utils.stringParaInteiro(0 + txtQuantidade.getText());
			if (!txtQuantidade.getText().equals("") && qtde > 0) {
				double preco = Utils.stringParaDouble(0 + txtPrecoUnid.getText());
				if (!txtPrecoUnid.getText().equals("") && preco > 0.0) {
					double desc = Utils.stringParaDouble(0 + txtDescontoIndividual.getText());
					if (desc < (preco * qtde)) {
						Despesa desp = new Despesa();
						desp.setNome(txtItem.getText());
						desp.setQuantidade(Utils.stringParaInteiro(txtQuantidade.getText()));
						desp.setPrecoUnid(Utils.stringParaDouble(txtPrecoUnid.getText()) / parcela);
						double valorUnid, quantidade, descontoIndividual;
						valorUnid = Utils.stringParaDouble(txtPrecoUnid.getText());
						quantidade = Utils.stringParaInteiro(txtQuantidade.getText());
						descontoIndividual = Utils.stringParaDouble(0 + txtDescontoIndividual.getText()) / parcela;
						desp.setPrecoBruto((valorUnid * quantidade) / parcela);
						desp.setPrecoTotal(((valorUnid * quantidade) / parcela) - descontoIndividual);
						desp.setDescontoIndividual(descontoIndividual);
						despesaService.salvar(desp);
						despesaId = desp.getId();

						for (int x = 0; x < parcela; x++) {
							Lancamento obj = new Lancamento();
							lbTotal.setText(String.valueOf(obj.getTotal()));
							obj.setId(lancamentoIds);
							obj.setReferencia(txtReferencia.getText());
							obj.setTotal((total));
							lancamentoService.atualizar(obj);
							lancamentoIds--;
							
							Item item = new Item();
							item.setLancamento(obj);
							item.setDespesa(desp);
							itemService.salvar(item);
							
							txtId.setText(String.valueOf(obj.getId()));
							txtItem.setText("");
							txtQuantidade.setText(String.valueOf(1));
							txtPrecoUnid.setText(String.valueOf(0.00));
							txtDescontoIndividual.setText(String.valueOf(0.00));
							
							List<Despesa> listaDespesa = despesaService.listarPorId(obj.getId());
							obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
							tbDespesa.setItems(obsListaDespesaTbView);
							iniciarBotaoRemover();
							carregarValores();
							obj.setTotal(Utils.stringParaDouble(lbTotal.getText()));
							lancamentoService.atualizar(obj);
						}
						lancamentoIds += parcela; 
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
	public void onBtConfirmar(ActionEvent evento) {
		infoExtra();
		Lancamento obj = new Lancamento();
		if (txtId.getText().equals("") || txtPrecoUnid.getText().equals("")) {
			Alertas.mostrarAlerta("Lançamento incompleto!", "Favor revisar todos campos", null, AlertType.INFORMATION);
		} else {
			for (int x = 0; x < parcela; x++) {
				obj.setId(lancamentoIds);
				obj.setDesconto(0.00);
				obj.setObs(dataCompra + parcelado + descontoCompra + totalCompra + chaveNFE + txtAreaObs.getText());
				obj.setTipo("P");
				lancamentoService.confirmarLanAPagar(obj);
				lancamentoIds--;
			}
			carregarPropriaView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
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
		carregarPropriaView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
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
		Restricoes.setTextFieldInteger(txtParcela);
		Restricoes.setTextFieldInteger(txtQuantidade);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 70);
		Restricoes.setTextFieldTamanhoMaximo(txtQuantidade, 4);
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 45);
		Restricoes.setTextFieldTamanhoMaximo(txtChaveNFE, 44);
		Restricoes.setTextFieldTamanhoMaximo(txtParcela, 2);
		Restricoes.setTextAreaTamanhoMaximo(txtAreaObs, 500);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldDouble(txtDescontoIndividual);
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		Utils.formatDatePicker(datePickerDataCompra, "dd/MM/YYYY");
		cbDetalheParcela.setSelected(true); 

		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);
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
					
					List<Despesa> listaDespesa = despesaService.listarPorId(Utils.stringParaInteiro(txtId.getText()));
					obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
					tbDespesa.setItems(obsListaDespesaTbView);
					iniciarBotaoRemover();
					carregarValores();
					lan.setTotal(Utils.stringParaDouble(lbTotal.getText()));
					lancamentoIds--;
				}
				lancamentoIds += parcela; 
				for (int x = 0; x < parcela; x++) {
					lan.setId(lancamentoIds);
					lan.setTotal(Utils.stringParaDouble(lbTotal.getText()));
					lancamentoService.atualizar(lan);
					lancamentoIds--;
				}
				lancamentoIds += parcela; 
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
		Criptografia c = new Criptografia();
		List<Usuario> lista = usuarioService.buscarTodos();
		for (Usuario u : lista) {
			u.getLogado();

			if (u.getLogado().equals("S")) {
				usuarioId = u.getId();
				lbUsuario.setText(c.descriptografar(u.getNome()));
			}
		}
	}

	public void carregarValores() {
		Double soma = 0.0;
		descInd = 0.0;
		valorTotalCompra = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
			descInd += tab.getDescontoIndividual();
			valorTotalCompra += (tab.getPrecoBruto() * parcela) - (descInd * parcela);
		}
		lbTotal.setText(String.format("%.2f", soma));
		txtTotalCompra.setText(String.format("%.2f", valorTotalCompra));
		txtDescontoCompra.setText(String.format("%.2f", descInd * parcela));
	}

	public void infoExtra() {
		if (datePickerDataCompra.getValue() != null) {
			LocalDate dtPicker = datePickerDataCompra.getValue();
			GregorianCalendar data = new GregorianCalendar(dtPicker.getYear(), dtPicker.getMonthValue(),
					dtPicker.getDayOfMonth(), 0, 0, 0);
			data.add(Calendar.MONTH, -1);
			Date dtCompra = data.getTime();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			dataCompra = "Feito no dia " + sdf.format(dtCompra) + " - ";
			parcelado = "Parcelado em " + txtParcela.getText() + "x - ";
			totalCompra = "Total (" + txtTotalCompra.getText() + "). \n";
		} else {
			dataCompra = "";
			parcelado = "";
			totalCompra = "";

		}
		if (!txtDescontoCompra.getText().equals("0.00")) {
			descontoCompra = "Desconto (" + txtDescontoCompra.getText() + ") - ";
		} else {
			descontoCompra = "";
		}
		if (!txtChaveNFE.getText().equals("")) {
			chaveNFE = "Chave NFe: (" + txtChaveNFE.getText() + "). \n";
		} else {
			chaveNFE = "";
		}
	}

	public void ocultarCampos() {
		txtId.setDisable(true);
		txtItem.setVisible(false);
		txtPrecoUnid.setVisible(false);
		txtQuantidade.setVisible(false);
		txtDescontoIndividual.setVisible(false);
		txtDescontoCompra.setVisible(false);
		txtTotalCompra.setVisible(false);
		txtChaveNFE.setVisible(false);
		btItem.setVisible(false);
		datePickerDataCompra.setVisible(false);
		lbRotuloItem.setVisible(false);
		lbRotuloQtde.setVisible(false);
		lbRotuloPreco.setVisible(false);
		lbRotuloDescInd.setVisible(false);
		lbRotuloDataCompra.setVisible(false);
		lbRotuloDescCompra.setVisible(false);
		lbRotuloTotalCompra.setVisible(false);
		lbRotuloChaveNFE.setVisible(false);
	}

	public void desocultarCampos() {
		datePickerData.setDisable(true);
		txtReferencia.setDisable(true);
		txtParcela.setDisable(true);
		cbDetalheParcela.setDisable(true);
		txtItem.setVisible(true);
		txtPrecoUnid.setVisible(true);
		txtQuantidade.setVisible(true);
		txtDescontoIndividual.setVisible(true);
		txtDescontoCompra.setVisible(true);
		txtTotalCompra.setVisible(true);
		txtChaveNFE.setVisible(true);
		btItem.setVisible(true);
		datePickerDataCompra.setVisible(true);
		lbRotuloItem.setVisible(true);
		lbRotuloQtde.setVisible(true);
		lbRotuloPreco.setVisible(true);
		lbRotuloDescInd.setVisible(true);
		lbRotuloDataCompra.setVisible(true);
		lbRotuloDescCompra.setVisible(true);
		lbRotuloTotalCompra.setVisible(true);
		lbRotuloChaveNFE.setVisible(true);
		btCriarRegistroDeLancamento.setVisible(false);
	}
}
