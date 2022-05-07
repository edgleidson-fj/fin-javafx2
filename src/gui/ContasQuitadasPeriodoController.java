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
import model.entidade.Lancamento;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.LancamentoService;

public class ContasQuitadasPeriodoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;

	@FXML
	private DatePicker datePickerDataInicial;
	@FXML
	private DatePicker datePickerDataFinal;
	@FXML
	private Label lbTotal;
	@FXML
	private Button btConsultar;
	@FXML
	private Button btGerarExcel;
	@FXML
	private Button btGerarTxt;
	@FXML
	private Button btImprimir;
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
	private TextField txtConsultaReferenciaOuDespesa;

	private ObservableList<Lancamento> obsListaLancamentoTbView;

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}
	
	Date d1, d2;

	@FXML
	public void onConsulta(ActionEvent evento) {
		if(datePickerDataInicial.getValue() != null && datePickerDataFinal.getValue() != null && !datePickerDataFinal.getValue().isBefore(datePickerDataInicial.getValue())) {
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

		List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesaQuitadoPeriodo(dataInicial, dataFinal, refOuDespesa);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		carregarValorTotal();
	}
	else {
		Alertas.mostrarAlerta("Atenção!", "Data inválida.", "Verificar se: \n-Data INICIAL e/ou FINAL em branco. \n-Data INICIAL maior que a FINAL.", AlertType.INFORMATION);
	}
	}
		
	public void carregarValorTotal() {
		double total = 0.0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			total += tab.getTotal();
		lbTotal.setText(String.format("R$ %.2f", total));
		}
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
		colunaLanDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));
		Utils.formatTableColumnValorDecimais(colunaLanDesconto, 2);
		colunaLanAcrescimo.setCellValueFactory(new PropertyValueFactory<>("acrescimo"));
		Utils.formatTableColumnValorDecimais(colunaLanAcrescimo, 2);
		
		Stage stage = (Stage) Main.pegarMainScene().getWindow();
		tbLancamento.prefHeightProperty().bind(stage.heightProperty());	
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
	
	
	@FXML
	public void onBtGerarExcel() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		
		try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Arquivos Excel\\Quitados_"+dataInicial+"_Ate_"+dataFinal+".xls", false);//False->Cria novo
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\tQUITADOS DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			printWrite.append("\rData\t Referência\t Total ");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (Lancamento tab : obsListaLancamentoTbView) {  
			try (FileWriter fileWrite = new FileWriter("C:\\Minhas Despesas App\\Arquivos Excel\\Quitados_"+dataInicial+"_Ate_"+dataFinal+".xls", true);//True->Adiciona
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
	
	
	@FXML
	public void onBtGerarTxt() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Arquivos Excel\\Quitados_"+dataInicial+"_Ate_"+dataFinal+".csv", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("QUITADOS DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Lancamento tab : obsListaLancamentoTbView) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Arquivos Excel\\Quitados_"+dataInicial+"_Ate_"+dataFinal+".csv", true); // True->Adiciona
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
		Alertas.mostrarAlerta(null, "Arquivo de texto gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Arquivos Excel/*", AlertType.INFORMATION);
	}
	
	
	public void onBtImprimir() {
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);
		String diretorio = "C:/Minhas Despesas App/Temp/Quitados_"+dataInicial+"_Ate_"+dataFinal+".txt";

		try (FileWriter fileWrite = new FileWriter(diretorio, true);
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\r\tQUITADOS DE "+dataInicial+" ATÉ "+dataFinal+"\r");
			for (Lancamento tab : obsListaLancamentoTbView) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	     		printWrite.append("\r"); //Linha
				printWrite.append(sdf.format(tab.getData())+" .");
				printWrite.append("\t"); //Coluna
				printWrite.append(tab.getReferencia());
				printWrite.append("\t");
				printWrite.append(tab.getTotal().toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}				
		Imprimir.imprimirArquivo(diretorio);
	}	
	
}
