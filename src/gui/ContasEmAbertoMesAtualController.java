package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
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
import model.entidade.Status;
import model.entidade.Usuario;
import model.entidade.ItemPagamento;
import model.servico.DespesaService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;
import model.servico.ItemPagamentoService;

public class ContasEmAbertoMesAtualController implements Initializable {

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
	private TableColumn<Lancamento, Status> colunaStatus;
	@FXML
	private TableColumn<Lancamento, Lancamento> colunaDetalhe;
	@FXML
	private TableColumn<Lancamento, Lancamento> colunaPagar;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbMes;
	@FXML
	private TextField txtConsultaReferenciaOuDespesa;
	@FXML 
	private Button btConsultaIDReferenciaOuDespesa;
	@FXML
	private Button btGerarExcel;

	private ObservableList<Lancamento> obsListaLancamentoTbView;
	
	@FXML
	public void onBtConsultaReferenciaOuDespesa(ActionEvent evento) {
		String refOuDespesa = txtConsultaReferenciaOuDespesa.getText();
		
		List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesaEmAbertoMesAtual(refOuDespesa);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);	
		carregarSomaTotal();
	}

	int usuarioId;

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}
	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}	

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();			
	}

	private void inicializarNodes() {
		colunaLanData.setCellValueFactory(new PropertyValueFactory<>("data"));
		Utils.formatTableColumnData(colunaLanData, "dd/MM/yyyy");
		colunaLanId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaLanRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
		colunaLanValor.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnValorDecimais(colunaLanValor, 2); 
		colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		renderizarColunas();
	}

	public void carregarTableView() {
		List<Lancamento> lista = lancamentoService.buscarContasAPagarMesAtual();
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		criarBotaoPagar();
		carregarSomaTotal();
		pegarMesAtual();
		}
	
	public void carregarSomaTotal() {
		Double soma = 0.0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			soma += tab.getTotal();
		}
		lbTotal.setText(String.format("R$ %.2f", soma));
	}

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai, String dialogForm) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();
			
			if(dialogForm == "detalhe") {
			DetalheDialogFormController controle = loader.getController();
			obj.setObs(obj.getObs());
			controle.setLancamento(obj);
			controle.setItemPagamentoService(new ItemPagamentoService());	
			controle.setDespesaService(new DespesaService());
			controle.atualizarDialogForm();
			controle.carregarTableView();
			}
			else {
				PagamentoDialogFormController controle = loader.getController();
				controle.setLancamento(obj);
				controle.setLancamentoService(new LancamentoService());
				controle.setDespesaService(new DespesaService());
				controle.setTipoPagService(new TipoPagService());
				controle.setItemPagamentoService(new ItemPagamentoService());	
				controle.setItemPagamento(new ItemPagamento());
				controle.atualizarDialogForm();
				controle.carregarTableView();
				controle.carregarObjetosAssociados();
			}

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
				String dialogForm = "detalhe";
				botao.setOnAction(
						evento -> criarDialogForm(obj, "/gui/DetalheDialogFormView.fxml", Utils.stageAtual(evento), dialogForm));
			}
		});
	}
	
	private void criarBotaoPagar() {
		colunaPagar.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaPagar.setCellFactory(param -> new TableCell<Lancamento, Lancamento>() {
			private final Button botao = new Button("R$");

			@Override
			protected void updateItem(Lancamento obj, boolean vazio) {
				super.updateItem(obj, vazio);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(botao);
				setStyle("-fx-color: #00FA9A");
				String dialogForm = "pagar";
				botao.setOnAction(
						evento -> criarDialogForm(obj, "/gui/PagamentoDialogFormView.fxml", Utils.stageAtual(evento), dialogForm));
			}
		});
	}
	
	public void rotinasAutomaticas() {
		lancamentoEntidade.setTotal(0.00);
		lancamentoService.exclusaoAutomatico(lancamentoEntidade);
		lancamentoService.cancelamentoAutomatico(lancamentoEntidade);
		lancamentoService.vencimentoAutomatico(lancamentoEntidade);
	}	
	
	private void renderizarColunas() {
		colunaStatus.setCellFactory(column -> {
			return new TableCell<Lancamento, Status>() {
				@Override
				protected void updateItem(Status item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText(null);
						setStyle("");
					} else {
						if (item.getNome().equals("VENCIDO")) {
							setText(item.getNome());
							setStyle("-fx-background-color:#FF6347");
						} else {
							setText("");
							setStyle("");
						}
					}
				}
			};
		});
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
	
	
	@FXML
	public void onBtGerarExcel() {	
		try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Arquivos Excel\\EmAberto"+lbMes.getText()+".xls", false);//False->Cria novo
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\tEM ABERTO "+lbMes.getText()+"\r");
			printWrite.append("\rVencimento\t Referência\t Total ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Lancamento tab : obsListaLancamentoTbView) {  
			try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Arquivos Excel\\EmAberto"+lbMes.getText()+".xls", true);//True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
	     		
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	     		printWrite.append("\r"); //Linha
				printWrite.append(sdf.format(tab.getData())+" .");
				printWrite.append("\t"); //Coluna
				printWrite.append(tab.getReferencia());
				printWrite.append("\t");
				printWrite.append(tab.getTotal().toString());				
			} catch (IOException e) {
				e.printStackTrace();
			}
	     }	
		Alertas.mostrarAlerta(null, "Excel gerado com sucesso!", "Salvo no diretório: \nC:/Minhas Despesas App/Arquivos Excel/*", AlertType.INFORMATION);
	}
	
}
