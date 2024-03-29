package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.entidade.Despesa;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;

public class PagamentoDialogFormController implements Initializable {

	private DespesaService despesaService;
	private Lancamento lancamentoEntidade;
	private LancamentoService lancamentoService;
	private TipoPagService tipoPagService;
	private ItemPagamentoService itemPagamentoService;
	private ItemPagamento itemPagamentoEntidade;

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtRef;
	@FXML
	private TextField txtDesconto;
	@FXML
	private TextField txtAcrescimo;
	@FXML
	private TextField txtTipoPagValor;
	@FXML
	private TextField txtDataHoje;
	@FXML
	private ComboBox<TipoPag> cmbTipoPag;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbDescontoGlobal;
	@FXML
	private Label lbDescontoIndividual;
	@FXML
	private Label lbPago;
	@FXML
	private Label lbDiferenca;
	@FXML
	private Label lbAcrescimo;
	@FXML
	private Label lbDescontoTxt;
	@FXML
	private Label lbAcrescimoTxt;
	@FXML
	private Label lbDesconto2;
	@FXML
	private Label lbAcrescimo2;
	@FXML
	private Label lbIndividual;
	@FXML
	private Label lbGlobal;
	@FXML
	private Label lbFaturaOuParcelado;
	@FXML
	private TableView<Despesa> tbDespesa;
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
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btDescontoOuAcrescimo;
	@FXML
	private Button btItemPagamento;

	private ObservableList<Despesa> obsListaDespesaTbView;
	private ObservableList<TipoPag> obsListaTipoPag;
	private ObservableList<ItemPagamento> obsListaItemTipoPag;

	double descGlobal, acrescimo, total, descInd;
	
	@FXML
	public void onBtItemPagamento(ActionEvent evento) {
		Locale.setDefault(Locale.US);

		double valorInformado, valorDiferenca;
		valorInformado = Utils.stringParaDouble(0 + txtTipoPagValor.getText());
		valorDiferenca = Utils.stringParaDouble(lbDiferenca.getText());

		if (valorInformado <= valorDiferenca && valorInformado != 0) {
			Lancamento obj = new Lancamento();
			obj.setId(Utils.stringParaInteiro(txtId.getText()));

			TipoPag pag = new TipoPag();
			pag = cmbTipoPag.getValue();

			itemPagamentoEntidade.setLancamento(obj);
			itemPagamentoEntidade.setTipoPag(pag);
			itemPagamentoEntidade.setValor(Utils.stringParaDouble(txtTipoPagValor.getText()));
			itemPagamentoEntidade.setNomePag(pag.getNome());
			itemPagamentoService.salvar(itemPagamentoEntidade);

			carregarTableViewItemPagamento();
			txtTipoPagValor.setText(lbDiferenca.getText());
			zerarDiferecaDePagamento();
			desativarBtDescontoAcrescimo();
		} else {
			Alertas.mostrarAlerta("Aten��o!", "Pagamento inv�lido.",
					"Verificar se: \n- O valor informado superior ao valor restante. \n- O valor informado � R$(0.00).",
					AlertType.INFORMATION);
		}
	}

	public void zerarDiferecaDePagamento() {
		if (lbPago.getText().equals(lbTotal.getText())) {
			txtTipoPagValor.setText("0.00");
			lbDiferenca.setText("0.00");
		}
	}

	public void limparItemPagamento() {
		itemPagamentoService.limparItemPagamentoPorIdLan(lancamentoEntidade);
		List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lancamentoEntidade.getId());
		obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
		tbTipoPag.setItems(obsListaItemTipoPag);
		carregarValorPago();
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
	}

	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		if (lbPago.getText().equals(lbTotal.getText())) {
			Lancamento obj = new Lancamento();
			obj.setId(Utils.stringParaInteiro(txtId.getText()));
			Date hoje = new Date();
			obj.setData(hoje);
			descInd = Utils.stringParaDouble(lbDescontoIndividual.getText());
			descGlobal = Utils.stringParaDouble(lbDescontoGlobal.getText());
			acrescimo = Utils.stringParaDouble(lbAcrescimo.getText());
			obj.setDesconto(descInd + descGlobal);
			obj.setAcrescimo(Utils.stringParaDouble(lbAcrescimo.getText()));
			obj.setTotal(Utils.stringParaDouble(lbTotal.getText()));
			lancamentoService.confirmarPagamento(obj);
			double ratDesc = Utils.stringParaDouble(lbDescontoGlobal.getText());
			if(ratDesc > 0) {
			rateioDesconto(descGlobal);
			}
			double ratAcr = Utils.stringParaDouble(lbAcrescimo.getText());
			if(ratAcr > 0) {
			rateioAcrescimo(acrescimo);
			}
			Utils.stageAtual(evento).close();
			carregarPropriaView("/gui/ContasEmAbertoMesAtualView.fxml",
					(ContasEmAbertoMesAtualController controller) -> {
						controller.setLancamentoService(new LancamentoService());
						controller.setLancamento(new Lancamento());
						controller.carregarTableView();
					});
			Alertas.mostrarAlerta(null, null, "Pagamento realizado com sucesso", AlertType.INFORMATION);
		} else {
			Alertas.mostrarAlerta("Erro ao finalizar pagamento", null, "Favor verificar forma de pagamento",
					AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtVoltar(ActionEvent evento) {
		limparItemPagamento();
		Utils.stageAtual(evento).close();
	}

	public void onBtDescontoOuAcrescimo() {
		double desc = Utils.stringParaDouble(0 + txtDesconto.getText());
		if (desc < Utils.stringParaDouble(lbDiferenca.getText())) {
			Double soma = 0.0;
			descInd = 0.0;
			for (Despesa tab : obsListaDespesaTbView) {
				soma += tab.getPrecoBruto();
				descInd += tab.getDescontoIndividual();
			}
			descGlobal = Utils.stringParaDouble(0 + txtDesconto.getText());
			lbDescontoGlobal.setText(String.format("%.2f", descGlobal));
			soma -= descGlobal + descInd;

			acrescimo = Utils.stringParaDouble(0 + txtAcrescimo.getText());
			lbAcrescimo.setText(String.format("%.2f", acrescimo));
			soma += acrescimo;

			lbTotal.setText(String.format("%.2f", soma));
			lbDiferenca.setText(String.format("%.2f", soma));

			txtDesconto.setText("0.00");
			txtAcrescimo.setText("0.00");
			txtTipoPagValor.setText(lbDiferenca.getText());
			limparItemPagamento();
		} else {
			Alertas.mostrarAlerta("Aten��o!", "Desconto inv�lido.",
					"Valor do desconto igual ou superior ao valor do lan�amento.", AlertType.INFORMATION);
		}
	}

	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setTipoPagService(TipoPagService tipoPagService) {
		this.tipoPagService = tipoPagService;
	}

	public void setItemPagamentoService(ItemPagamentoService itemPagamentoService) {
		this.itemPagamentoService = itemPagamentoService;
	}

	public void setItemPagamento(ItemPagamento itemPagamentoEntidade) {
		this.itemPagamentoEntidade = itemPagamentoEntidade;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComboBoxTipoPag();
		inicializarNodes();		
		lbFaturaOuParcelado.setVisible(false);
	}

	private void inicializarNodes() {
		txtId.setDisable(true);
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldDouble(txtTipoPagValor);
		Restricoes.setTextFieldDouble(txtDesconto);
		Restricoes.setTextFieldDouble(txtAcrescimo);

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

		colunaTipoPagValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnValorDecimais(colunaTipoPagValor, 2);
		colunaTipoPagNome.setCellValueFactory(new PropertyValueFactory<>("nomePag"));
	}

	public void atualizarDialogForm() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtRef.setText(lancamentoEntidade.getReferencia());
		lbTotal.setText(String.format("%.2f", lancamentoEntidade.getTotal()));
		Date hoje = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		txtDataHoje.setText(sdf.format(hoje));
		lbAcrescimo.setText(String.format("%.2f", lancamentoEntidade.getAcrescimo()));		
		faturaOuParcela();
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		carregarValores();
	}

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

	public void carregarTableViewItemPagamento() {
		List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lancamentoEntidade.getId());
		obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
		tbTipoPag.setItems(obsListaItemTipoPag);
		carregarValorPago();
		iniciarBotaoRemoverItemPagamento();
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
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirma��o", "Tem certeza que deseja remover?");
		if (result.get() == ButtonType.OK) {
			if (itemPagamentoService == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				Lancamento lan = new Lancamento();
				lan.setId(Utils.stringParaInteiro(txtId.getText()));
				itemPagamentoService.excluir(lan.getId(), desp.getTipoPag().getId());
				List<ItemPagamento> listaPagamento = itemPagamentoService.listarPorId(lan.getId());
				obsListaItemTipoPag = FXCollections.observableArrayList(listaPagamento);
				tbTipoPag.setItems(obsListaItemTipoPag);
				carregarValorPago();
				desativarBtDescontoAcrescimo();
			} catch (BDIntegrityException ex) {
				Alertas.mostrarAlerta("Erro ao remover objeto", null, ex.getMessage(), AlertType.ERROR);
			}
		}
	}

	public void carregarValores() {
		Double soma = 0.0;
		descInd = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoBruto();
			descInd += tab.getDescontoIndividual();
		}
		lbTotal.setText(String.format("%.2f", soma - descInd));
		lbDiferenca.setText(String.format("%.2f", soma - descInd));
		lbDescontoIndividual.setText(String.format("%.2f", descInd));
		txtTipoPagValor.setText(lbDiferenca.getText());
	}

	public void rateioDesconto(Double desc) {
		Double t = 0.0;
		for (Despesa tab1 : obsListaDespesaTbView) {
			t += tab1.getPrecoTotal();
		}
		for (Despesa tab : obsListaDespesaTbView) {
			double percentual = (tab.getPrecoTotal() / t) * 100;
			double descAux = (percentual / 100) * desc;
			double descTotal = tab.getDescontoIndividual() + descAux;
			Despesa obj = new Despesa();
			obj.setId(tab.getId());
			obj.setPrecoTotal(tab.getPrecoTotal() - descTotal);
			obj.setDescontoIndividual(descTotal);
			despesaService.rateioDesconto(obj);
			descAux = desc;
		}
	}
		
	public void rateioAcrescimo(Double acr) {
		Double t = 0.0;
		for (Despesa tab1 : obsListaDespesaTbView) {
			t += tab1.getPrecoTotal();
		}
		for (Despesa tab : obsListaDespesaTbView) {
			double percentual = (tab.getPrecoTotal() / t) * 100;
			double acrAux = (percentual / 100) * acr;
			Despesa obj = new Despesa();
			obj.setId(tab.getId());
			obj.setPrecoTotal(tab.getPrecoTotal() + acrAux);
			obj.setAcrescimo(acrAux);
			despesaService.rateioAcrescimo(obj);
			acrAux = acr;
		}
	}

	public void desativarBtDescontoAcrescimo() {
		if (!lbPago.getText().equals("0.00")) {
			lbDescontoTxt.setDisable(true);
			lbAcrescimoTxt.setDisable(true);
			txtDesconto.setDisable(true);
			txtAcrescimo.setDisable(true);
			btDescontoOuAcrescimo.setDisable(true);
		} else {
			lbDescontoTxt.setDisable(false);
			lbAcrescimoTxt.setDisable(false);
			txtDesconto.setDisable(false);
			txtAcrescimo.setDisable(false);
			btDescontoOuAcrescimo.setDisable(false);
		}
		if(lbDiferenca.getText().equals("0.00")) {
			txtTipoPagValor.setDisable(true);
			btItemPagamento.setDisable(true);
			cmbTipoPag.setDisable(true);
		}
		else {
			txtTipoPagValor.setDisable(false);
			btItemPagamento.setDisable(false);
			cmbTipoPag.setDisable(false);
		}
	}	
	
	public void faturaOuParcela() {
		lbFaturaOuParcelado.setText("Obs: \nSe precisar ajustar o valor da Parcela ou Fatura \npor favor acesse CONFIG.");
		String tipo = lancamentoEntidade.getTipo();
		if(tipo.equals("F") || tipo.equals("P")) {
			lbAcrescimo.setVisible(false);
			lbAcrescimoTxt.setVisible(false);
			lbDescontoTxt.setVisible(false);
			lbDescontoIndividual.setVisible(false);
			lbDescontoGlobal.setVisible(false);
			lbDesconto2.setVisible(false);
			lbAcrescimo2.setVisible(false);
			lbIndividual.setVisible(false);
			lbGlobal.setVisible(false);
			lbFaturaOuParcelado.setVisible(true);
			txtDesconto.setVisible(false);
			txtAcrescimo.setVisible(false);
			btDescontoOuAcrescimo.setVisible(false);			
		}
	}
	
}
