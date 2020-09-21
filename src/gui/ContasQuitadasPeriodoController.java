package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.LancamentoService;
import model.servico.StatusService;
import model.servico.TipoPagService;

public class ContasQuitadasPeriodoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private TipoPagService tipoPagService;
	private TipoPag tipoPagEntidade;
	private StatusService statusService;
	private Status statusEntidade;
	// -------------------------------------------

	@FXML
	private DatePicker datePickerDataInicial;
	@FXML
	private DatePicker datePickerDataFinal;
	@FXML
	private Label lbTotal;
	@FXML
	private Button btConsultar;
	@FXML
	private TableView<Lancamento> tbLancamento;
	@FXML
	private TableColumn<Lancamento, Integer> colunaLanId;
	@FXML
	private TableColumn<Lancamento, Date> colunaLanData;
	@FXML
	private TableColumn<Lancamento, String> colunaLanRef;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanValor;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanDesconto;
	@FXML
	private TableColumn<Lancamento, Double> colunaLanAcrescimo;
	@FXML
	private TableColumn<Lancamento, TipoPag> colunaTipoPag;
	@FXML
	private TableColumn<Lancamento, Lancamento> colunaDetalhe;
	// -----------------------------------------------------

	private ObservableList<Lancamento> obsListaLancamentoTbView;
	// -----------------------------------------------------

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	public void setTipoPagService(TipoPagService tipoPagService) {
		this.tipoPagService = tipoPagService;
	}

	public void setTipoPag(TipoPag tipoPagEntidade) {
		this.tipoPagEntidade = tipoPagEntidade;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}

	public void setStatus(Status statusEntidade) {
		this.statusEntidade = statusEntidade;
	}
	// ----------------------------------------------------------

	@FXML
	public void onConsulta(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		Instant instant1 = Instant.from(datePickerDataInicial.getValue().atStartOfDay(ZoneId.systemDefault()));		
		Date d1 = (Date.from(instant1));		
		Instant instant2 = Instant.from(datePickerDataFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
		Date d2 = (Date.from(instant2));
		Utils.formatDatePicker(datePickerDataInicial, "dd/MM/yyyy");
		Utils.formatDatePicker(datePickerDataFinal, "dd/MM/yyyy");

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);

		List<Lancamento> lista = lancamentoService.buscarContasQuitadoPeriodo(dataInicial, dataFinal);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		// Valor Total
		Double soma = 0.0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			soma += tab.getTotal();
		}
		lbTotal.setText(String.format("R$ %.2f", soma));
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		inicializarNodes();
	}

	private void inicializarNodes() {
		colunaLanData.setCellValueFactory(new PropertyValueFactory<>("data"));
		Utils.formatTableColumnData(colunaLanData, "dd/MM/yyyy");
		colunaLanId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaLanRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
		colunaLanValor.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnValorDecimais(colunaLanValor, 2); // Formatar com(0,00)
		colunaLanDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));
		Utils.formatTableColumnValorDecimais(colunaLanDesconto, 2);
		colunaLanAcrescimo.setCellValueFactory(new PropertyValueFactory<>("acrescimo"));
		Utils.formatTableColumnValorDecimais(colunaLanAcrescimo, 2);
		colunaTipoPag.setCellValueFactory(new PropertyValueFactory<>("tipoPagamento"));
	}

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			DetalheDialogFormController controle = loader.getController();
			controle.setLancamento(obj);
			controle.setLancamentoService(new LancamentoService());
			controle.setDespesaService(new DespesaService());
			controle.atualizarDialogForm();
			controle.carregarTableView();
			// Caixa de Dialogo.
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

	private void criarBotaoDetalhe() {
		colunaDetalhe.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaDetalhe.setCellFactory(param -> new TableCell<Lancamento, Lancamento>() {
			private final Button botao = new Button("+");

			@Override
			protected void updateItem(Lancamento obj, boolean vazio) {
				super.updateItem(obj, vazio);

				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				botao.setOnAction(
						evento -> criarDialogForm(obj, "/gui/DetalheDialogFormView.fxml", Utils.stageAtual(evento)));
			}
		});
	}

	public void rotinasAutomaticas() {
		lancamentoEntidade.setTotal(0.00);
		lancamentoService.exclusaoAutomatico(lancamentoEntidade);
		lancamentoService.cancelamentoAutomatico(lancamentoEntidade);
		lancamentoService.vencimentoAutomatico(lancamentoEntidade);
	}
}
