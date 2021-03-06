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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.entidade.Despesa;
import model.entidade.Lancamento;
import model.servico.DespesaService;

public class DetalheDialogFormController implements Initializable {

	private DespesaService despesaService;
	private Lancamento lancamentoEntidade;
	//private LancamentoService lancamentoService;
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
	private TableColumn<Despesa, Double> colunaDespQuantidade;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorUnid;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValorTotal;
	@FXML
	private Button btVoltar;
	@FXML
	private TextArea txtObs;
	// -----------------------------------------------------------------

	private ObservableList<Despesa> obsListaDespesaTbView;
	// ---------------------------------------------------------


	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	/*public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}*/
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
		colunaDespQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		colunaDespValorUnid.setCellValueFactory(new PropertyValueFactory<>("precoUnid"));
		Utils.formatTableColumnValorDecimais(colunaDespValorUnid, 2);// Formatar com(0,00)
		colunaDespValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(colunaDespValorTotal, 2); 
	}

	public void atualizarDialogForm() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtRef.setText(lancamentoEntidade.getReferencia());
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		lbTotal.setText(String.format("R$ %.2f", lancamentoEntidade.getTotal()));
		if (lancamentoEntidade.getDesconto() != 0) {
			lbDescontoAcrescimo.setText(String.format("Desconto R$ %.2f",lancamentoEntidade.getDesconto()));
		}
		if (lancamentoEntidade.getAcrescimo() != 0) {
			lbDescontoAcrescimo.setText(String.format("Acr�scimo R$ %.2f",lancamentoEntidade.getAcrescimo()));
		}
		txtObs.setText(lancamentoEntidade.getObs());
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
	}
}
