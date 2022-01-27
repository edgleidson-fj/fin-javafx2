package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.LancamentoService;
import model.servico.UsuarioService;

public class ContasQuitadasMesAtualController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;

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
	private TableColumn<Lancamento, Lancamento> colunaDetalhe;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbMes;
	@FXML
	private Label lbMsgTetoDeGastos;
	@FXML
	private TextField txtConsultaReferenciaOuDespesa;
	@FXML
	private Button btConsultaIDReferenciaOuDespesa;

	private ObservableList<Lancamento> obsListaLancamentoTbView;
	private ObservableList<Lancamento> obsListaLancamentoTbViewAux;

	@FXML
	public void onBtConsultaReferenciaOuDespesa(ActionEvent evento) {
		String refOuDespesa = txtConsultaReferenciaOuDespesa.getText();
		if (!refOuDespesa.equals("")) {
			List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesaQuitadoMesAtual(refOuDespesa);
			obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
			tbLancamento.setItems(obsListaLancamentoTbView);
			carregarValorTotalFiltrado();
		} else {
			carregarTableView();
		}
	}

	public void carregarValorTotal() {
		List<Lancamento> listaAux = lancamentoService.AuxNaoContabilizarValorOutros();
		obsListaLancamentoTbViewAux = FXCollections.observableArrayList(listaAux);
		Double total = 0.0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			total += tab.getTotal();
		}
		// Subtrair valor do Tipo de Pagamento(Outros).
		String itemPagOutros = "";
		Double itemPagOutrosValor = 0.00;
		for (Lancamento tabAux : obsListaLancamentoTbViewAux) {
			itemPagOutros = tabAux.getItemPagamento().getNomePag();
			if (itemPagOutros.equals("Outros")) {
				itemPagOutrosValor += tabAux.getItemPagamento().getValor();
			}
		}
		lbTotal.setText(String.format("%.2f", total - itemPagOutrosValor));
	}

	public void carregarValorTotalFiltrado() {
		Double total = 0.0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			total += tab.getTotal();
		}
		lbTotal.setText(String.format("%.2f", total));
	}

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
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

	private void inicializarNodes() {
		colunaLanData.setCellValueFactory(new PropertyValueFactory<>("data"));
		Utils.formatTableColumnData(colunaLanData, "dd/MM/yyyy");
		colunaLanId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaLanRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
		colunaLanValor.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnValorDecimais(colunaLanValor, 2); 
		colunaLanDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));
		Utils.formatTableColumnValorDecimais(colunaLanDesconto, 2);
		colunaLanAcrescimo.setCellValueFactory(new PropertyValueFactory<>("acrescimo"));
		Utils.formatTableColumnValorDecimais(colunaLanAcrescimo, 2);
	}

	public void carregarTableView() {
		if (lancamentoService == null) {
			throw new IllegalStateException("Service was null");
		}
		List<Lancamento> lista = lancamentoService.buscarContasQuitadoMesAtual();
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		carregarValorTotal();
		pegarMesAtual();
	}

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			DetalheDialogFormController controle = loader.getController();
			obj.setObs(obj.getObs());
			controle.setLancamento(obj);
			controle.setDespesaService(new DespesaService());
			controle.setItemPagamentoService(new ItemPagamentoService());
			controle.atualizarDialogForm();
			controle.carregarTableView();

			Stage stageDialog = new Stage();
			stageDialog.setTitle("");
			stageDialog.setScene(new Scene(painel));
			stageDialog.setResizable(false); 
			stageDialog.initOwner(stagePai); 
			stageDialog.initModality(Modality.WINDOW_MODAL); 
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

	public void pegarMesAtual() {
		Calendar datahoje = Calendar.getInstance();
		int mesAtual = datahoje.get(Calendar.MONTH) + 1;

		switch (mesAtual) {
		case 1:
			lbMes.setText("(JANEIRO)");
			break;
		case 2:
			lbMes.setText("(FEVEREIRO)");
			break;
		case 3:
			lbMes.setText("(MARÇO)");
			break;
		case 4:
			lbMes.setText("(ABRIL)");
			break;
		case 5:
			lbMes.setText("(MAIO)");
			break;
		case 6:
			lbMes.setText("(JUNHO)");
			break;
		case 7:
			lbMes.setText("(JULHO)");
			break;
		case 8:
			lbMes.setText("(AGOSTO)");
			break;
		case 9:
			lbMes.setText("(SETEMBRO)");
			break;
		case 10:
			lbMes.setText("(OUTUBRO)");
			break;
		case 11:
			lbMes.setText("(NOVEMBRO)");
			break;
		case 12:
			lbMes.setText("(DEZEMBRO)");
			break;
		}
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
				Double tetoGasto = u.getTetoGasto();
				Double gasto = Utils.stringParaDouble(lbTotal.getText());
				Double valorComparativo = tetoGasto - gasto;
				double percentual = (tetoGasto * 30) / 100;

				if (gasto > tetoGasto && tetoGasto > 0) {
					valorComparativo = gasto - tetoGasto;
					lbMsgTetoDeGastos.setText("Atenção! \nVocê ultrapassou R$ "
							+ String.format("%.2f", valorComparativo) + " do seu teto de gasto mensal.");
				} else if (valorComparativo <= percentual && tetoGasto > 0) {
					valorComparativo = tetoGasto - gasto;
					lbMsgTetoDeGastos.setText("Atenção! \nFalta R$ " + String.format("%.2f", valorComparativo)
							+ " para atingir o seu teto de gasto mensal.");
				}

			}
		}
	}

}
