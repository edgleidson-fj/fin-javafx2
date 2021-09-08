package gui;

import java.io.IOException;
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
import model.entidade.TipoPag;
import model.servico.DespesaService;
import model.servico.LancamentoService;

public class ContasQuitadasPeriodoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
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
	@FXML
	private TextField txtConsultaReferenciaOuDespesa;
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

	@FXML
	public void onConsulta(ActionEvent evento) {
		String refOuDespesa = txtConsultaReferenciaOuDespesa.getText();
		
		Instant instant1 = Instant.from(datePickerDataInicial.getValue().atStartOfDay(ZoneId.systemDefault()));		
		Date d1 = (Date.from(instant1));		
		Instant instant2 = Instant.from(datePickerDataFinal.getValue().atStartOfDay(ZoneId.systemDefault()));
		Date d2 = (Date.from(instant2));
		Utils.formatDatePicker(datePickerDataInicial, "dd/MM/yyyy");
		Utils.formatDatePicker(datePickerDataFinal, "dd/MM/yyyy");

		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String dataInicial = fmt.format(d1);
		String dataFinal = fmt.format(d2);

		List<Lancamento> lista = lancamentoService.buscarPorReferenciaOuDespesaQuitadoPeriodo(dataInicial, dataFinal, refOuDespesa);
		obsListaLancamentoTbView = FXCollections.observableArrayList(lista);
		tbLancamento.setItems(obsListaLancamentoTbView);
		criarBotaoDetalhe();
		zerarValores();
		carregarSomaTotal();		
	}
	
	public void zerarValores() {
		System.out.println("Zerando valores");
		lbTotal.setText("");
		lbTotalTipoPagamento1.setText("");
		lbTotalTipoPagamento2.setText("");
		lbTotalTipoPagamento3.setText("");
		lbTotalTipoPagamento4.setText("");
		lbTotalTipoPagamento5.setText("");
	}
	
	public void carregarSomaTotal() {
		Double somaTotal = 0.0;
		Double somaTipoPag1 = 0.0;
		Double somaTipoPag2 = 0.0;
		Double somaTipoPag3 = 0.0;
		Double somaTipoPag4 = 0.0;
		Double somaTipoPag5 = 0.0;
		String nomeTipoPag1 = null;
		String nomeTipoPag2 = null;
		String nomeTipoPag3 = null;
		String nomeTipoPag4 = null;
		String nomeTipoPag5 = null;	
		int quantidade = 0;
		for (Lancamento tab : obsListaLancamentoTbView) {
			somaTotal += tab.getTotal();
			if(tab.getTipoPagamento().getId() == 2) {
				somaTipoPag1 += tab.getTotal();
				nomeTipoPag1 = tab.getTipoPagamento().getNome();
				quantidade+=1;
				}
			if(tab.getTipoPagamento().getId() == 3) {
				somaTipoPag2 += tab.getTotal();
				nomeTipoPag2 = tab.getTipoPagamento().getNome();
				quantidade+=1;
				}
			if(tab.getTipoPagamento().getId() == 4) {
				somaTipoPag3 += tab.getTotal();
				nomeTipoPag3 = tab.getTipoPagamento().getNome();
				quantidade+=1;
				}
			if(tab.getTipoPagamento().getId() == 5) {
				somaTipoPag4 += tab.getTotal();
				nomeTipoPag4 = tab.getTipoPagamento().getNome();
				quantidade+=1;
				}
			if(tab.getTipoPagamento().getId() == 6) {
				somaTipoPag5 += tab.getTotal();
				nomeTipoPag5 = tab.getTipoPagamento().getNome();
				quantidade+=1;
				}		
		}
		
		//Classificar os Tipo de Pagamento de acordo com o valor.
		Double valorPrimeiro= 0.0;
		Double valorSegundo= 0.0;
		Double valorTerceiro= 0.0;
		Double valorQuarto= 0.0;
		Double valorQuinto= 0.0;
		String A = null;
		String B = null;
		String C = null;
		String D = null;
		String E = null;					
		int contador = 1;					
			while(contador <= quantidade) {
				if(valorPrimeiro<=somaTipoPag1) {
					valorPrimeiro=somaTipoPag1;
					A=nomeTipoPag1;
				}						
				else
					if(valorSegundo<=somaTipoPag1) {
						valorSegundo=somaTipoPag1;
						B=nomeTipoPag1;
					}	
					else
						if(valorTerceiro<=somaTipoPag1) {
							valorTerceiro=somaTipoPag1;
							C=nomeTipoPag1;
						}	
						else
							if(valorQuarto<=somaTipoPag1) {
								valorQuarto=somaTipoPag1;
								D=nomeTipoPag1;
							}
							else
								if(valorQuinto<=somaTipoPag1) {
									valorQuinto=somaTipoPag1;
									E=nomeTipoPag1;
								}	
				
				if(valorPrimeiro<=somaTipoPag2) {
					valorPrimeiro=somaTipoPag2;
					A=nomeTipoPag2;
				}
				else
					if(valorSegundo<=somaTipoPag2) {
						valorSegundo=somaTipoPag2;
						B=nomeTipoPag2;
					}	
					else
						if(valorTerceiro<=somaTipoPag2) {
							valorTerceiro=somaTipoPag2;
							C=nomeTipoPag2;
						}	
						else
							if(valorQuarto<=somaTipoPag2) {
								valorQuarto=somaTipoPag2;
								D=nomeTipoPag2;
							}
							else
								if(valorQuinto<=somaTipoPag2) {
								valorQuinto=somaTipoPag2;
									E=nomeTipoPag2;
								}	
				
				if(valorPrimeiro<=somaTipoPag3) {
					valorPrimeiro=somaTipoPag3;
					A=nomeTipoPag3;
				}
				else
					if(valorSegundo<=somaTipoPag3) {
						valorSegundo=somaTipoPag3;
						B=nomeTipoPag3;
					}	
					else
						if(valorTerceiro<=somaTipoPag3) {
							valorTerceiro=somaTipoPag3;
							C=nomeTipoPag3;
						}	
						else
							if(valorQuarto<=somaTipoPag3) {
								valorQuarto=somaTipoPag3;
								D=nomeTipoPag3;
							}
							else
								if(valorQuinto<=somaTipoPag3) {
									valorQuinto=somaTipoPag3;
									E=nomeTipoPag3;
								}	
				
				if(valorPrimeiro<=somaTipoPag4) {
					valorPrimeiro=somaTipoPag4;
					A=nomeTipoPag4;
				}
				else
					if(valorSegundo<=somaTipoPag4) {
						valorSegundo=somaTipoPag4;
						B=nomeTipoPag4;
					}	
					else
						if(valorTerceiro<=somaTipoPag4) {
							valorTerceiro=somaTipoPag4;
							C=nomeTipoPag4;
						}	
						else
							if(valorQuarto<=somaTipoPag4) {
								valorQuarto=somaTipoPag4;
								D=nomeTipoPag4;
							}
							else
								if(valorQuinto<=somaTipoPag4) {
									valorQuinto=somaTipoPag4;
									E=nomeTipoPag4;
								}	
				
				if(valorPrimeiro<=somaTipoPag5) {
					valorPrimeiro=somaTipoPag5;
					A=nomeTipoPag5;
				}
				else
					if(valorSegundo<=somaTipoPag5) {
						valorSegundo=somaTipoPag5;
						B=nomeTipoPag5;
					}	
					else
						if(valorTerceiro<=somaTipoPag5) {
							valorTerceiro=somaTipoPag5;
							C=nomeTipoPag5;
						}	
						else
							if(valorQuarto<=somaTipoPag5) {
								valorQuarto=somaTipoPag5;
								D=nomeTipoPag5;
							}
							else
								if(valorQuinto<=somaTipoPag5) {
								valorQuinto=somaTipoPag5;
									E=nomeTipoPag5;
								}											
				contador ++;
				}
				
			//Exibir os valores na Tela.
		lbTotal.setText(String.format("R$ %.2f", somaTotal));
		if(A != null) {
		lbTotalTipoPagamento1.setText(String.format(A+" "+"R$ %.2f", valorPrimeiro));
		}
		
		if(B != null && B!=A) {
			lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", valorSegundo));	
		}
		else
			if(B==null && !nomeTipoPag1.equals(A)) {
				B=nomeTipoPag1;
				lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", somaTipoPag1));
			}	
			else
				if(B==null && !nomeTipoPag2.equals(A)) {
					B=nomeTipoPag2;
					lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", somaTipoPag2));
				}	
				else
					if(B==null && !nomeTipoPag3.equals(A)) {
						B=nomeTipoPag3;
						lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", somaTipoPag3));
					}	
					else
						if(B==null && !nomeTipoPag4.equals(A)) {
							B=nomeTipoPag4;
							lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", somaTipoPag4));
						}	
						else
							if(B==null && !nomeTipoPag5.equals(A)) {
								B=nomeTipoPag5;
								lbTotalTipoPagamento2.setText(String.format(B+" "+"R$ %.2f", somaTipoPag5));
							}	
		
		if(C != null && C!=A && C!=B) {
			lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", valorTerceiro));
			}
		else
			if(C==null && !nomeTipoPag1.equals(A) && !nomeTipoPag1.equals(B)) {
				C=nomeTipoPag1;
				lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", somaTipoPag1));
			}	
			else
				if(C==null && !nomeTipoPag2.equals(A) && !nomeTipoPag2.equals(B)) {
					C=nomeTipoPag2;
					lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", somaTipoPag2));
				}	
				else
					if(C==null && !nomeTipoPag3.equals(A) && !nomeTipoPag3.equals(B)) {
						C=nomeTipoPag3;
						lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", somaTipoPag3));
					}	
					else
						if(C==null && !nomeTipoPag4.equals(A) && !nomeTipoPag4.equals(B)) {
							C=nomeTipoPag4;
							lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", somaTipoPag4));
						}	
						else
							if(C==null && !nomeTipoPag5.equals(A) && !nomeTipoPag5.equals(B)) {
								C=nomeTipoPag5;
								lbTotalTipoPagamento3.setText(String.format(C+" "+"R$ %.2f", somaTipoPag5));
							}	
		
		if(D != null && D!=A && D!=B && D!=C) {					
			lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", valorQuarto));
		}else
			if(D==null && !nomeTipoPag1.equals(A) && !nomeTipoPag1.equals(B) && !nomeTipoPag1.equals(C)) {
				D=nomeTipoPag1;
				lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", somaTipoPag1));
			}	
			else
				if(D==null && !nomeTipoPag2.equals(A) && !nomeTipoPag2.equals(B) && !nomeTipoPag2.equals(C)) {
					D=nomeTipoPag2;
					lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", somaTipoPag2));
				}	
				else
					if(D==null && !nomeTipoPag3.equals(A) && !nomeTipoPag3.equals(B) && !nomeTipoPag3.equals(C)) {
						D=nomeTipoPag3;
						lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", somaTipoPag3));
					}	
					else
						if(D==null && !nomeTipoPag4.equals(A) && !nomeTipoPag4.equals(B) && !nomeTipoPag4.equals(C)) {
							D=nomeTipoPag4;
							lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", somaTipoPag4));
						}	
						else
							if(D==null && !nomeTipoPag5.equals(A) && !nomeTipoPag5.equals(B) && !nomeTipoPag5.equals(C)) {
								D=nomeTipoPag5;
								lbTotalTipoPagamento4.setText(String.format(D+" "+"R$ %.2f", somaTipoPag5));
							}	
		
		if(E != null && E!=A && E!=B && E!=C && E!=D) {
			lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", valorQuinto));
		}
		else
			if(E==null && !nomeTipoPag1.equals(A) && !nomeTipoPag1.equals(B) && !nomeTipoPag1.equals(C) && !nomeTipoPag1.equals(D)) {
				E=nomeTipoPag1;
				lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", somaTipoPag1));
			}	
			else
				if(E==null && !nomeTipoPag2.equals(A) && !nomeTipoPag2.equals(B) && !nomeTipoPag2.equals(C) && !nomeTipoPag2.equals(D)) {
					E=nomeTipoPag2;
					lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", somaTipoPag2));
				}	
				else
					if(E==null && !nomeTipoPag3.equals(A) && !nomeTipoPag3.equals(B) && !nomeTipoPag3.equals(C) && !nomeTipoPag3.equals(D)) {
						E=nomeTipoPag3;
						lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", somaTipoPag3));
					}	
					else
						if(E==null && !nomeTipoPag4.equals(A) && !nomeTipoPag4.equals(B) && !nomeTipoPag4.equals(C) && !nomeTipoPag4.equals(D)) {
							E=nomeTipoPag4;
							lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", somaTipoPag4));
						}	
						else
							if(E==null && !nomeTipoPag5.equals(A) && !nomeTipoPag5.equals(B) && !nomeTipoPag5.equals(C) && !nomeTipoPag5.equals(D)) {
								E=nomeTipoPag5;
								lbTotalTipoPagamento5.setText(String.format(E+" "+"R$ %.2f", somaTipoPag5));
							}	
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
