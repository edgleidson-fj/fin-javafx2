package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
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
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.ItemService;
import model.servico.LancamentoService;

public class ReajustarParcelaDialogFormController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private Despesa despesaEntidade;
	private DespesaService despesaService;
	private ItemService itemService;
	private Item itemEntidade;
	// -------------------------------------------------------

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtRef;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtPrecoUnid;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btVoltar;
	// -------------------------------------------------------
	Date data = new Date();
	double itemValorAnterior;
	double novoTotal;

	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		
		if(!txtPrecoUnid.getText().equals("")) {
		excluirItemAntesDoReajuste();
		inserirNovoItemDoReajuste();
		parentStage.close();

		carregarView("/gui/LanConfigView.fxml", (LanConfigController controller) -> {
			Lancamento lan = new Lancamento();
			lan.setId(lancamentoEntidade.getId());
			lan.setReferencia(lancamentoEntidade.getReferencia());
			lan.setData(data);
			lan.setTipo("P");
			lan.setObs(lancamentoEntidade.getObs());
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(lan);
			controller.setDespesaService(new DespesaService());
			controller.setDespesa(new Despesa());
			controller.setItemService(new ItemService());
			controller.setItem(new Item());
			controller.setItemPagamentoService(new ItemPagamentoService());
			controller.setItemPagamento(new ItemPagamento());
			controller.carregarData();
			controller.carregarTableView();
		});				
		}
		else {
			Alertas.mostrarAlerta("Valor Inválido!", "Favor verificar o valor informado.",null, AlertType.WARNING);
		}
	}

	@FXML
	public void onBtVoltar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		parentStage.close();
	}
	// -------------------------------------------------------

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

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setItem(Item itemEntidade) {
		this.itemEntidade = itemEntidade;
	}

	// ------------------------------------------------------------

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		Restricoes.setTextFieldDouble(txtPrecoUnid);
	}

	public void carregarCamposDeCadastro() {
		txtRef.setText(lancamentoEntidade.getReferencia());
		txtNome.setText(despesaEntidade.getNome());
		txtPrecoUnid.setText(String.valueOf(despesaEntidade.getPrecoUnid()));
		itemValorAnterior = despesaEntidade.getPrecoUnid();
		txtPrecoUnid.setText("");
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

	public void excluirItemAntesDoReajuste() {
		List<Lancamento> listaExcluir = lancamentoService.auxReajuste(txtRef.getText());
		for (Lancamento x : listaExcluir) {		
			x.getId();
			x.getReferencia();			
			novoTotal =  x.getTotal()- itemValorAnterior;
			x.setTotal(novoTotal);
			data = x.getData();
			itemService.excluir(x, despesaEntidade);
			lancamentoService.reajustarValorTotal(x);
		}
	}

	public void inserirNovoItemDoReajuste() {
		Despesa desp = new Despesa();
		desp.setNome(txtNome.getText());
		desp.setQuantidade(1);
		desp.setPrecoUnid(Utils.stringParaDouble(txtPrecoUnid.getText()));
		desp.setPrecoBruto(Utils.stringParaDouble(txtPrecoUnid.getText()));
		desp.setDescontoIndividual(0.00);
		desp.setPrecoTotal(Utils.stringParaDouble(txtPrecoUnid.getText()));
		despesaService.salvar(desp);

		List<Lancamento> lista = lancamentoService.auxReajuste(txtRef.getText());
		for (Lancamento x : lista) {
			double novoTotal = 0.00;
			x.getId();
			x.getReferencia();
			novoTotal =  x.getTotal()+ Utils.stringParaDouble(txtPrecoUnid.getText()) ;
			x.setTotal(novoTotal);
			Item item = new Item();
			item.setLancamento(x);
			item.setDespesa(desp);
			itemService.salvar(item);
			lancamentoService.reajustarValorTotal(x);
		}
	}
	
}
