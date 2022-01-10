package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Restricoes;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entidade.Despesa;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;

public class DetalheDialogFormController implements Initializable {

	private DespesaService despesaService;
	private Lancamento lancamentoEntidade;
	private ItemPagamentoService itemPagamentoService;

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtRef;
	@FXML
	private TextField txtData;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbPago;
	@FXML
	private Label lbAcrescimo;
	@FXML
	private Label lbDesconto;
	@FXML
	private Label lbDescontoAcrescimo;
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
	private TableColumn<Despesa, Double> colunaDespAcrescimo;
	@FXML
	private TableView<ItemPagamento> tbTipoPag;
	@FXML
	private TableColumn<ItemPagamento, String> colunaTipoPagNome;
	@FXML
	private TableColumn<ItemPagamento, Double> colunaTipoPagValor;
	@FXML
	private TextArea txtObs;

	private ObservableList<Despesa> obsListaDespesaTbView;
	private ObservableList<ItemPagamento> obsListaItemTipoPag;

	double descInd;

	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}
	public void setItemPagamentoService(ItemPagamentoService itemPagamentoService) {
		this.itemPagamentoService = itemPagamentoService;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}

	private void inicializarNodes() {
		txtId.setDisable(true);
		Restricoes.setTextFieldInteger(txtId);
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
	}

	public void atualizarDialogForm() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtRef.setText(lancamentoEntidade.getReferencia());
		txtObs.setText(lancamentoEntidade.getObs());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		txtData.setText(sdf.format(lancamentoEntidade.getData()));
		lbAcrescimo.setText(String.format("%.2f", lancamentoEntidade.getAcrescimo()));
		}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		carregarTableViewItemPagamento();
		carregarValores();
		atualizarDialogForm();
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
		descInd = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoBruto();
			descInd += tab.getDescontoIndividual();
		}
		lbTotal.setText(String.format("%.2f", soma));
		if(lbPago.getText().equals("0.00")) {
			lbDesconto.setText(String.format("%.2f", descInd));
			}
		else {
			lbDesconto.setText(String.format("%.2f", lancamentoEntidade.getDesconto()));
		}
		}

}
