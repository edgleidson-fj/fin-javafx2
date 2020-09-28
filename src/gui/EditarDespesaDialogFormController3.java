package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;

public class EditarDespesaDialogFormController3 implements Initializable{
	
	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade; 
	private Despesa despesaEntidade;
	private DespesaService despesaService;
	//-------------------------------------------------------
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtPreco;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btVoltar;	
	//-------------------------------------------------------
	
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		Despesa obj = new Despesa();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		obj.setNome(txtNome.getText());
		obj.setPreco(Utils.stringParaDouble(txtPreco.getText()));
		despesaService.atualizar(obj);
		//Total
		lancamentoEntidade.getId();
		double subtrairDespesa = despesaEntidade.getPreco();
		double total;
		total = lancamentoEntidade.getTotal() + Utils.stringParaDouble(txtPreco.getText());
		total -= subtrairDespesa;
		lancamentoEntidade.setTotal(total);
		lancamentoService.atualizar(lancamentoEntidade);
		
		parentStage.close();
			
		carregarView("/gui/LanConfigView.fxml", (LanConfigController controller) -> {
			Lancamento lan = new Lancamento();
			lan.setId(lancamentoEntidade.getId());
			lan.setReferencia(lancamentoEntidade.getReferencia());
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(lan);
			controller.setDespesaService(new DespesaService());
			controller.setDespesa(new Despesa());
			controller.setItemService(new ItemService());
			controller.setItem(new Item());			
			controller.carregarTableView();
		//	controller.carregarCamposDeCadastro2();
			});	
		}
	
	@FXML
	public void onBtVoltar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);		
		parentStage.close();
	}
	//-------------------------------------------------------
	
	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}
	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}
	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}
	public void setDespesa(Despesa despesaEntidade) {
		this.despesaEntidade = despesaEntidade;
	}
	//------------------------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		inicializarNodes();
	}
	
	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(despesaEntidade.getId()));
		txtNome.setText(despesaEntidade.getNome());
		txtPreco.setText(String.valueOf(despesaEntidade.getPreco()));
	}	
	
	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 50);
		Restricoes.setTextFieldDouble(txtPreco);
		}
	// -----------------------------------------------------------------
	
	private synchronized <T> void carregarView(String caminhoDaView, Consumer<T> acaoDeInicializacao) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoDaView));
			VBox novoVBox = loader.load();

			Scene mainScene = Main.pegarMainScene();
			VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

			Node mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(novoVBox);

			// Pegando segundo parametro dos onMenuItem()
			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
		} catch (IOException ex) {
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar a tela.", ex.getMessage(), AlertType.ERROR);
		}
	}		

}
