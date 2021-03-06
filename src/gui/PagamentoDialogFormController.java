package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import model.entidade.Despesa;
import model.entidade.Lancamento;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;

public class PagamentoDialogFormController implements Initializable {

	private DespesaService despesaService;
	private Lancamento lancamentoEntidade;
	private LancamentoService lancamentoService;
	private TipoPagService tipoPagService;
	// ---------------------------------------------

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtRef;
	@FXML
	private TextField txtDesconto;
	@FXML
	private TextField txtAcrescimo;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbDespesa;
	@FXML
	private Label lbOutro;
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
	private ComboBox<TipoPag> cmbTipoPag;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btVoltar;
	@FXML
	private Button btAtualizarTotal;
	// ----------------------------------------------------------

	private ObservableList<Despesa> obsListaDespesaTbView;
	private ObservableList<TipoPag> obsListaTipoPag;
	// ---------------------------------------------------------
	double desconto, acrescimo, total;
	
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setTipoPagamento(cmbTipoPag.getValue());
		if(cmbTipoPag.getValue()== null) {
			Alertas.mostrarAlerta("Aten��o", null, "Favor informar o tipo de pagamento", AlertType.WARNING);
		}
		else {
		// Desconto-Acr�scimo
		Double soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
				soma += tab.getPrecoTotal();
		}
		total = soma;
		total -= Utils.stringParaDouble(txtDesconto.getText());
		total += Utils.stringParaDouble(txtAcrescimo.getText());
		if (Utils.stringParaDouble(txtDesconto.getText()) != 0) {
			obj.setDesconto(Utils.stringParaDouble(txtDesconto.getText()));
		}else {
			obj.setDesconto(desconto);
		}
		if (Utils.stringParaDouble(txtAcrescimo.getText()) != 0) {
			obj.setAcrescimo(Utils.stringParaDouble(txtAcrescimo.getText()));
		}
		else {
			obj.setAcrescimo(acrescimo);
		}
		obj.setTotal(total);
		Date hoje = new Date();
		obj.setData(hoje);
		lancamentoService.confirmarPagamento(obj);
		Utils.stageAtual(evento).close();
		carregarPropriaView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.carregarTableView();
		});
		 Alertas.mostrarAlerta(null, null, "Pagamento realizado com sucesso", AlertType.INFORMATION);
		}
	}

	@FXML
	public void onBtVoltar(ActionEvent evento) {
		Utils.stageAtual(evento).close();
	}

	public void onBtAtualizarTotal() {
		//lbDespesa.setText(String.format("R$ %.2f", lancamentoEntidade.getTotal()));
		Double soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
		}
		double total = soma;
		
		if (Utils.stringParaDouble(txtDesconto.getText()) > 0 ) {
			total -= Utils.stringParaDouble(txtDesconto.getText());
			lbOutro.setText(String.format("Desconto R$ %.2f", Utils.stringParaDouble(txtDesconto.getText())));
			 desconto = Utils.stringParaDouble(txtDesconto.getText());
			 txtAcrescimo.setText(String.valueOf(0.00));
			}
		
		if (Utils.stringParaDouble(txtAcrescimo.getText()) > 0) {
			total += Utils.stringParaDouble(txtAcrescimo.getText());
			lbOutro.setText(String.format("Acr�scimo R$ %.2f", Utils.stringParaDouble(txtAcrescimo.getText())));
			 acrescimo = Utils.stringParaDouble(txtAcrescimo.getText());
			 txtDesconto.setText(String.valueOf(0.00));
		}
		
		if (Utils.stringParaDouble(txtDesconto.getText()) < 1 && 
				Utils.stringParaDouble(txtAcrescimo.getText()) < 1) {
		soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPrecoTotal();
		}
		lbTotal.setText(String.format("%.2f", soma));
		lbOutro.setText("");
		}		
		else {
		lbTotal.setText(String.format("%.2f", total));
	}
	}
	// -----------------------------------------------

	
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

	// ----------------------------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComboBoxTipoPag();
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
		lbTotal.setText(String.format("%.2f", lancamentoEntidade.getTotal()));
		datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(), ZoneId.systemDefault()));
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");		
		txtDesconto.setText(String.valueOf(lancamentoEntidade.getDesconto()));
		txtAcrescimo.setText(String.valueOf(lancamentoEntidade.getAcrescimo()));
		if(lancamentoEntidade.getDesconto()> 0) {
		lbOutro.setText(String.format("Desconto R$ %.2f",lancamentoEntidade.getDesconto()));
		}
		if(lancamentoEntidade.getAcrescimo()> 0) {
			lbOutro.setText(String.format("Acr�scimo R$ %.2f",lancamentoEntidade.getAcrescimo()));
			}
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
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
}
