package gui;

import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.ItemPagamentoService;
import model.servico.LancamentoService;
import model.servico.UsuarioService;

public class ConsultaPorPagamentoController implements Initializable {

	private ItemPagamentoService service;
	private ItemPagamento entidade;
	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;
	private ObservableList<ItemPagamento> obsLista;
	private ObservableList<ItemPagamento> obsListaAnoAtual;
	int usuarioId;

	@FXML
	private TableView<ItemPagamento> tableViewItemPagamento;
	@FXML
	private TableColumn<ItemPagamento, String> tableColumnNome;
	@FXML
	private TableColumn<ItemPagamento, Double> tableColumnValorTotal;
	@FXML
	private TableView<ItemPagamento> tableViewItemPagamentoAnoAtual;
	@FXML
	private TableColumn<ItemPagamento, String> tableColumnNomeAnoAtual;
	@FXML
	private TableColumn<ItemPagamento, Double> tableColumnValorTotalAnoAtual;
	@FXML
	private Label lbMesAtual;
	@FXML
	private Label lbAnoAtual;

	// Injeção da dependência.
	public void setItemPagamentoService(ItemPagamentoService service) {
		this.service = service;
	}
	public void setItemPagamento(ItemPagamento entidade) {
		this.entidade = entidade;
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
	public void initialize(URL url, ResourceBundle rb) {
		inicializarComportamento();
	}

	private void inicializarComportamento() {
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nomePag"));
		tableColumnValorTotal.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnValorDecimais(tableColumnValorTotal, 2);// Formatar com(0,00)

		tableColumnNomeAnoAtual.setCellValueFactory(new PropertyValueFactory<>("nomePag"));
		tableColumnValorTotalAnoAtual.setCellValueFactory(new PropertyValueFactory<>("valor"));
		Utils.formatTableColumnValorDecimais(tableColumnValorTotalAnoAtual, 2);// Formatar com(0,00)
		// Tamanho da tabela.
		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tableViewItemPagamento.prefHeightProperty().bind(stage.heightProperty());
	}

	public void carregarTableView() {
		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		carregarUsuarioLogado();
		List<ItemPagamento> lista = service.consultaPorPagamentoMesAtual(usuarioId);
		obsLista = FXCollections.observableArrayList(lista);
		tableViewItemPagamento.setItems(obsLista);

		List<ItemPagamento> listaAnoAtual = service.consultaPorPagamentoAnoAtual(usuarioId);
		obsListaAnoAtual = FXCollections.observableArrayList(listaAnoAtual);
		tableViewItemPagamentoAnoAtual.setItems(obsListaAnoAtual);
		pegarMesEAnoAtual();
	}
	// ---------------------------------------------

	public void pegarMesEAnoAtual() {
		Calendar datahoje = Calendar.getInstance();
		int mesAtual = datahoje.get(Calendar.MONTH) + 1;
		int anoAtual = datahoje.get(Calendar.YEAR);
		lbAnoAtual.setText("" + anoAtual);

		switch (mesAtual) {
		case 1:
			lbMesAtual.setText("JANEIRO");
			break;
		case 2:
			lbMesAtual.setText("FEVEREIRO");
			break;
		case 3:
			lbMesAtual.setText("MARÇO");
			break;
		case 4:
			lbMesAtual.setText("ABRIL");
			break;
		case 5:
			lbMesAtual.setText("MAIO");
			break;
		case 6:
			lbMesAtual.setText("JUNHO");
			break;
		case 7:
			lbMesAtual.setText("JULHO");
			break;
		case 8:
			lbMesAtual.setText("AGOSTO");
			break;
		case 9:
			lbMesAtual.setText("SETEMBRO");
			break;
		case 10:
			lbMesAtual.setText("OUTUBRO");
			break;
		case 11:
			lbMesAtual.setText("NOVEMBRO");
			break;
		case 12:
			lbMesAtual.setText("DEZEMBRO");
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
				usuarioId = u.getId();
				// lbUsuario.setText(String.valueOf(u.getNome()));
			}
		}
	}

}
