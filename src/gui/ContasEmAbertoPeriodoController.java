package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alertas;
import gui.util.Utils;
import impressora.Imprimir;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;
import smtp.Email;

public class ContasEmAbertoPeriodoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;

	@FXML
	private DatePicker datePickerDataInicial;
	@FXML
	private DatePicker datePickerDataFinal;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbUsuario;
	@FXML
	private Button btConsultar;
	@FXML
	private Button btGerarExcel;
	@FXML
	private Button btGerarTxt;
	@FXML
	private Button btImprimir;
	@FXML
	private Button btEnviarEmail;
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
	private TextField txtConsultaReferenciaOuDespesa;

	private ObservableList<Lancamento> obsListaLancamentoTbView;

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
	
	Date d1, d2;

	@FXML
	public void onConsulta(ActionEvent evento) {
		if(datePickerDataInicial.getValue() != null && datePickerDataFinal.getValue() != null && !datePickerDataFinal.getValue().isBefore(datePickerDataInicial.getValue()))  {
		String refOuDespesa = txtConsultaReferenciaOuDespesa.getText();
		
		Instant instant1 = Instant.from(datePickerDataInicial.getValue().atStartOfDay(ZoneId.systemDefault()));
		d1 = (Date.from(instant1));
		Instant instant2 = Instant.from(datePickerDataFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
		d2 = (Date.from(instant2));
		Utils.formatDatePicker(datePickerDataInicial, "dd/MM/yyyy");
		Utils.formatDatePicker(datePickerDataFinal, "dd/MM/yyyy");

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);

		List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesaEmAbertoPeriodo(dataInicial, dataFinal, refOuDespesa);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		criarBotaoPagar();
		carregarSomaTotal();
		}
		else {
			Alertas.mostrarAlerta("Atenção!", "Data inválida.", "Verificar se: \n-Data INICIAL e/ou FINAL em branco. \n-Data INICIAL maior que a FINAL.", AlertType.INFORMATION);
		}
	}
	
	public void carregarSomaTotal() {
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
		Utils.formatDatePicker(datePickerDataInicial, "dd/MM/yyyy");
		Utils.formatDatePicker(datePickerDataFinal, "dd/MM/yyyy");		
		colunaLanData.setCellValueFactory(new PropertyValueFactory<>("data"));
		Utils.formatTableColumnData(colunaLanData, "dd/MM/yyyy");
		colunaLanId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaLanRef.setCellValueFactory(new PropertyValueFactory<>("referencia"));
		colunaLanValor.setCellValueFactory(new PropertyValueFactory<>("total"));
		Utils.formatTableColumnValorDecimais(colunaLanValor, 2); 
		colunaStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		renderizarColunas();
		
		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tbLancamento.prefHeightProperty().bind(stage.heightProperty());	
	}

	public void criarDialogForm(Lancamento obj, String nomeAbsoluto, Stage stagePai, String dialogForm) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(nomeAbsoluto));
			Pane painel = loader.load();

			if (dialogForm == "detalhe") {
				DetalheDialogFormController controle = loader.getController();
				obj.setObs(obj.getObs());
				controle.setLancamento(obj);
				controle.setDespesaService(new DespesaService());
				controle.setItemPagamentoService(new ItemPagamentoService());	
				controle.atualizarDialogForm();
				controle.carregarTableView();
			} else {
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
				botao.setOnAction(evento -> criarDialogForm(obj, "/gui/DetalheDialogFormView.fxml",
						Utils.stageAtual(evento), dialogForm));
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
				botao.setOnAction(evento -> criarDialogForm(obj, "/gui/PagamentoDialogFormView.fxml",
						Utils.stageAtual(evento), dialogForm));
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
				//usuarioId = u.getId();
				usuarioEntidade = u;
			}
		}
	}	
	
	
	@FXML
	public void onBtGerarExcel() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		
		try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Relatorios\\EmAberto_"+dataInicial+"_Ate_"+dataFinal+".xls", false);//False->Cria novo
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\tEM ABERTO DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			printWrite.append("\rVencimento\t Referência\t Total ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Lancamento tab : obsListaLancamentoTbView) {  
			try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Relatorios\\EmAberto_"+dataInicial+"_Ate_"+dataFinal+".xls", true);//True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
	     		
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	     		printWrite.append("\r"); //Linha
				printWrite.append(sdf.format(tab.getData())+" .");
				printWrite.append("\t"); //Coluna
				printWrite.append(tab.getReferencia());
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getTotal()));				
			} catch (IOException e) {
				e.printStackTrace();
			}
	     }	
		Alertas.mostrarAlerta(null, "Excel gerado com sucesso!", "Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	
	
	@FXML
	public void onBtGerarTxt() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Relatorios\\EmAberto_"+dataInicial+"_Ate_"+dataFinal+".csv", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("EM ABERTO DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Lancamento tab : obsListaLancamentoTbView) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Relatorios\\EmAberto_"+dataInicial+"_Ate_"+dataFinal+".csv", true); // True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
				
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	     		printWrite.append("\r"); //Linha
				printWrite.append(sdf.format(tab.getData())+" .");
				printWrite.append("\t"); //Coluna
				printWrite.append(tab.getReferencia());
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getTotal()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Alertas.mostrarAlerta(null, "Arquivo CSV gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	
	
	public void onBtImprimir() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		String diretorio = "C:/Minhas Despesas App/Temp/EmAberto_"+dataInicial+"_Ate_"+dataFinal+".txt";

		try (FileWriter fileWrite = new FileWriter(diretorio, true);
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\r\tEM ABERTO DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			for (Lancamento tab : obsListaLancamentoTbView) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	     		printWrite.append("\r"); //Linha
				printWrite.append(sdf.format(tab.getData())+" .");
				printWrite.append("\t"); //Coluna
				printWrite.append(tab.getReferencia());
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getTotal()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}				
		Imprimir.imprimirArquivo(diretorio);
	}	
	
	
	public void onBtEnviarEmail() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dataInicial = sdf.format(d1);
		String dataFinal = sdf.format(d2);
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("********** LANÇAMENTOS EM ABERTO DE "+dataInicial+" ATÉ "+dataFinal+" **********\n\r");		
		Double total = 0.0;
		for(Lancamento tab : obsListaLancamentoTbView) {
			strBuilder.append("--> "+sdf.format(tab.getData())+"  "+tab.getReferencia()+ "  (R$ "+String.format("%.2f",tab.getTotal())+")\n");
			total += tab.getTotal();
		}	
		strBuilder.append("\r\n_________________\n");
		strBuilder.append("TOTAL  R$ "+String.format("%.2f",total)+"\n");
		String msg = strBuilder.toString();
		Email.envio2(usuarioEntidade, msg);		
	}	
	
}
