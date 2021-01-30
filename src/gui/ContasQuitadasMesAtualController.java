package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Alertas;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Lancamento;
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.LancamentoService;

public class ContasQuitadasMesAtualController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	// -------------------------------------------

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
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbTotalTipoPagamento1;
	@FXML
	private Label lbTotalTipoPagamento2;
	@FXML
	private Label lbTotalTipoPagamento3;
	@FXML
	private Label lbTotalTipoPagamento4;
	@FXML
	private Label lbTotalTipoPagamento5;
	// -----------------------------------------------------

	private ObservableList<Lancamento> obsListaLancamentoTbView;
	// -----------------------------------------------------

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}	
	// ----------------------------------------------------------
	
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

	public void carregarTableView() {
		if (lancamentoService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Lancamento> lista = lancamentoService.buscarContasQuitadoMesAtual();
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		
		// Valor Total
		Double soma = 0.0;
		Double somaTipoPag1 = 0.0;
		Double somaTipoPag2 = 0.0;
		Double somaTipoPag3 = 0.0;
		Double somaTipoPag4 = 0.0;
		Double somaTipoPag5 = 0.0;
		String nomeTipoPag1=null;
		String nomeTipoPag2=null;
		String nomeTipoPag3=null;
		String nomeTipoPag4=null;
		String nomeTipoPag5=null;
		for (Lancamento tab : obsListaLancamentoTbView) {
			soma += tab.getTotal();
			if(tab.getTipoPagamento().getId() == 2) {
				somaTipoPag1 += tab.getTotal();
				nomeTipoPag1 = tab.getTipoPagamento().getNome();
			}
			if(tab.getTipoPagamento().getId() == 3) {
				somaTipoPag2 += tab.getTotal();
				nomeTipoPag2 = tab.getTipoPagamento().getNome();
				}
			if(tab.getTipoPagamento().getId() == 4) {
				somaTipoPag3 += tab.getTotal();
				nomeTipoPag3 = tab.getTipoPagamento().getNome();
				}
			if(tab.getTipoPagamento().getId() == 5) {
				somaTipoPag4 += tab.getTotal();
				nomeTipoPag4 = tab.getTipoPagamento().getNome();
				}
			if(tab.getTipoPagamento().getId() == 6) {
				somaTipoPag5 += tab.getTotal();
				nomeTipoPag5 = tab.getTipoPagamento().getNome();
				}
		}
		lbTotal.setText(String.format("R$ %.2f", soma));
		if(nomeTipoPag1 != null) {
		lbTotalTipoPagamento1.setText(String.format(nomeTipoPag1+" "+"R$ %.2f", somaTipoPag1));
		}
		if(nomeTipoPag2 != null) {
			lbTotalTipoPagamento2.setText(String.format(nomeTipoPag2+" "+"R$ %.2f", somaTipoPag2));		
		}
		if(nomeTipoPag3 != null) {
			lbTotalTipoPagamento3.setText(String.format(nomeTipoPag3+" "+"R$ %.2f", somaTipoPag3));
			}
		if(nomeTipoPag4 != null) {
			lbTotalTipoPagamento4.setText(String.format(nomeTipoPag4+" "+"R$ %.2f", somaTipoPag4));
			}
		if(nomeTipoPag5 != null) {
			lbTotalTipoPagamento5.setText(String.format(nomeTipoPag5+" "+"R$ %.2f", somaTipoPag5));
			}
	}

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			DetalheDialogFormController controle = loader.getController();
			obj.setObs(obj.getObs());
			controle.setLancamento(obj);
			controle.setDespesaService(new DespesaService());
			controle.atualizarDialogForm();
			controle.carregarTableView();

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
			private final Button botao = new Button("?");

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
