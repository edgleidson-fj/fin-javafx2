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
import model.entidade.Despesa;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.UsuarioService;

public class ConsultaPorRankController implements Initializable {

	private DespesaService service;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;
	private ObservableList<Despesa> obsLista;
	private ObservableList<Despesa> obsListaAnoAtual;
	int usuarioId;

	@FXML
	private TableView<Despesa> tableViewDespesa;
	@FXML
	private TableColumn<Despesa, String> tableColumnNome;
	@FXML
	private TableColumn<Despesa, Double> tableColumnValorTotal;
	@FXML
	private TableColumn<Despesa, Double> tableColumnQuantidade;
	@FXML
	private TableView<Despesa> tableViewDespesaAnoAtual;
	@FXML
	private TableColumn<Despesa, String> tableColumnNomeAnoAtual;
	@FXML
	private TableColumn<Despesa, Double> tableColumnValorTotalAnoAtual;
	@FXML
	private TableColumn<Despesa, Double> tableColumnQuantidadeAnoAtual;
	@FXML
	private Label lbMesAtual;
	@FXML
	private Label lbAnoAtual;
		
	public void setDespesaService(DespesaService service) {
		this.service = service;
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
		tableColumnNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnValorTotal.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(tableColumnValorTotal, 2);
		tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		
		tableColumnNomeAnoAtual.setCellValueFactory(new PropertyValueFactory<>("nome"));
		tableColumnValorTotalAnoAtual.setCellValueFactory(new PropertyValueFactory<>("precoTotal"));
		Utils.formatTableColumnValorDecimais(tableColumnValorTotalAnoAtual, 2);
		tableColumnQuantidadeAnoAtual.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
		
		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tableViewDespesa.prefHeightProperty().bind(stage.heightProperty());
	}

	public void carregarTableView() {
		if (service == null) {
			throw new IllegalStateException("Service nulo");
		}
		carregarUsuarioLogado();
		List<Despesa> lista = service.consultaPorRankDespesaMesAtual(usuarioId);
		obsLista = FXCollections.observableArrayList(lista);
		tableViewDespesa.setItems(obsLista);

		List<Despesa> listaAnoAtual = service.consultaPorRankDespesaAnoAtual(usuarioId);
		obsListaAnoAtual = FXCollections.observableArrayList(listaAnoAtual);
		tableViewDespesaAnoAtual.setItems(obsListaAnoAtual);
		pegarMesEAnoAtual();
	}

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
			}
		}
	}

}
