package gui;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Restricoes;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entidade.Despesa;
import model.entidade.Lancamento;
import model.servico.DespesaService;
import model.servico.LancamentoService;

public class DetalheDialogFormController implements Initializable {

	private Despesa despesaEntidade;
	private DespesaService despesaService;
	private Lancamento lancamentoEntidade;
	private LancamentoService lancamentoService;
	// ---------------------------------------------

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtRef;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbDescontoAcrescimo;
	@FXML
	private TableView<Despesa> tbDespesa;
	@FXML
	private TableColumn<Despesa, Integer> colunaDespId;
	@FXML
	private TableColumn<Despesa, String> colunaDespNome;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValor;
	@FXML
	private Button btVoltar;
	// -----------------------------------------------------------------

	private ObservableList<Despesa> obsListaDespesaTbView;
	// ---------------------------------------------------------

	public void setDespesa(Despesa despesaEntidade) {
		this.despesaEntidade = despesaEntidade;
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
	// ----------------------------------------------------------------

	@FXML
	public void onBtVoltar(ActionEvent evento) {
		Utils.stageAtual(evento).close();
	}
	// -----------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		colunaDespId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespValor.setCellValueFactory(new PropertyValueFactory<>("preco"));
		Utils.formatTableColumnValorDecimais(colunaDespValor, 2); // Formatar com(0,00)
	}

	public void atualizarDialogForm() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtRef.setText(lancamentoEntidade.getReferencia());
//		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));

		lbTotal.setText(String.format("%.2f", lancamentoEntidade.getTotal()));
		if (lancamentoEntidade.getDesconto() != 0) {
			lbDescontoAcrescimo.setText(String.format("Desconto %.2f",lancamentoEntidade.getDesconto()));
		}
		if (lancamentoEntidade.getAcrescimo() != 0) {
			lbDescontoAcrescimo.setText(String.format("Acréscimo %.2f",lancamentoEntidade.getAcrescimo()));
		}
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
	}
}
