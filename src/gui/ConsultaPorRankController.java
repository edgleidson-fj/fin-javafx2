package gui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alertas;
import gui.util.Utils;
import impressora.Imprimir;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entidade.Despesa;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.UsuarioService;
import smtp.Email;

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
	@FXML
	private Button btGerarExcel;
	@FXML
	private Button btGerarExcelAno;
	@FXML
	private Button btGerarTxt;
	@FXML
	private Button btGerarTxtAno;
	@FXML
	private Button btImprimir;
	@FXML
	private Button btImprimirAno;
	@FXML
	private Button btIEnviaEmailMes;
	@FXML
	private Button btIEnviaEmailAno;

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
				usuarioEntidade = u;				
			}
		}
	}
	

	@FXML
	public void onBtGerarExcelMes() {
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbMesAtual.getText() + ").xls", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("RANKING DE DESPESAS DE " + lbMesAtual.getText() + " / " + lbAnoAtual.getText() + "\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Despesa tab : obsLista) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbMesAtual.getText() + ").xls", true); // True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome());
				printWrite.append("\t"); // Coluna
				printWrite.append(tab.getQuantidade().toString() + " .");
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getPrecoTotal()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Alertas.mostrarAlerta(null, "Excel gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	

	@FXML
	public void onBtGerarExcelAno() {
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbAnoAtual.getText() + ").xls", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("RANKING DE DESPESAS DE " + lbAnoAtual.getText() + "\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Despesa tab : obsListaAnoAtual) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbAnoAtual.getText() + ").xls", true); // True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome());
				printWrite.append("\t"); // Coluna
				printWrite.append(tab.getQuantidade().toString() + " .");
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getPrecoTotal()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Alertas.mostrarAlerta(null, "Excel gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	
	
	@FXML
	public void onBtGerarTxtMes() {
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbMesAtual.getText() + ").csv", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("RANKING DE DESPESAS DE " + lbMesAtual.getText() + " / " + lbAnoAtual.getText() + "\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Despesa tab : obsLista) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbMesAtual.getText() + ").csv", true); // True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome());
				printWrite.append("\t"); // Coluna
				printWrite.append(tab.getQuantidade().toString() + " .");
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getPrecoTotal()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Alertas.mostrarAlerta(null, "Arquivo CSV gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	

	@FXML
	public void onBtGerarTxtAno() {
		try (FileWriter fileWrite = new FileWriter(
				"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbAnoAtual.getText() + ").csv", false); // False->Cria
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("RANKING DE DESPESAS DE " + lbAnoAtual.getText() + "\r");
			printWrite.append("\rDespesa\t Qtde\t Valor ");
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Despesa tab : obsListaAnoAtual) {
			try (FileWriter fileWrite = new FileWriter(
					"C:\\Minhas Despesas App\\Relatorios\\Rank_Despesas(" + lbAnoAtual.getText() + ").csv", true); // True->Adiciona
					BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
					PrintWriter printWrite = new PrintWriter(bufferWrite);) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome());
				printWrite.append("\t"); // Coluna
				printWrite.append(tab.getQuantidade().toString() + " .");
				printWrite.append("\t");
				printWrite.append(String.format("%.2f",tab.getPrecoTotal()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Alertas.mostrarAlerta(null, "Arquivo CSV gerado com sucesso!",
				"Salvo no diretório: \nC:/Minhas Despesas App/Relatorios/*", AlertType.INFORMATION);
	}
	
	
	public void onBtImprimirMes() {
		String diretorio = "C:/Minhas Despesas App/Temp/Ranking_"+lbMesAtual.getText()+".txt";

		try (FileWriter fileWrite = new FileWriter(diretorio, true);
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\r\tRANKING DE DESPESAS DE "+lbMesAtual.getText()+" / "+ lbAnoAtual.getText() + "\r");
			for (Despesa tab : obsLista) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome()+"___");
				printWrite.append("R$ "+String.format("%.2f",tab.getPrecoTotal()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}				
		Imprimir.imprimirArquivo(diretorio);
	}	
	

	public void onBtImprimirAno() {
		String diretorio = "C:/Minhas Despesas App/Temp/Ranking_"+lbAnoAtual.getText()+".txt";

		try (FileWriter fileWrite = new FileWriter(diretorio, true);
				BufferedWriter bufferWrite = new BufferedWriter(fileWrite);
				PrintWriter printWrite = new PrintWriter(bufferWrite);) {
			printWrite.append("\r\tRANKING DE DESPESAS DE " + lbAnoAtual.getText() + "\r");
			for (Despesa tab : obsListaAnoAtual) {
				printWrite.append("\r"); // Linha
				printWrite.append(tab.getNome()+"___");
				printWrite.append("R$ "+String.format("%.2f",tab.getPrecoTotal()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}				
		Imprimir.imprimirArquivo(diretorio);
	}	
	
	
	public void onBtEnviarEmailMes() {					
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("********** RANKING DE DESPESAS REFERENTE AO MÊS "+lbMesAtual.getText()+" DE "+lbAnoAtual.getText()+" **********\n\r");		
		Double total = 0.0;
		for(Despesa tab : obsLista) {
			strBuilder.append("--> "+tab.getNome()+ "  R$ "+String.format("%.2f",tab.getPrecoTotal())+"\n");
			total += tab.getPrecoTotal();
		}	
		strBuilder.append("\r\n_________________\n");
		strBuilder.append("TOTAL  R$ "+String.format("%.2f",total)+"\n");
		String msg = strBuilder.toString();		
		Email.envio2(usuarioEntidade, msg);		
	}	
	
	
	public void onBtEnviarEmailAno() {					
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("********** RANKING DE DESPESAS REFERENTE AO ANO "+lbAnoAtual.getText()+" **********\n\r");		
		Double total = 0.0;
		for(Despesa tab : obsListaAnoAtual) {
			strBuilder.append("--> "+tab.getNome()+ "  R$ "+String.format("%.2f",tab.getPrecoTotal())+"\n");
			total += tab.getPrecoTotal();
		}	
		strBuilder.append("\r\n_________________\n");
		strBuilder.append("TOTAL  R$ "+String.format("%.2f",total)+"\n");
		String msg = strBuilder.toString();
		Email.envio2(usuarioEntidade, msg);		
	}	

}
