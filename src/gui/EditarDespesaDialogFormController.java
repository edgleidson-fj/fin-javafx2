package gui;
//LanQuitado.
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
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;

public class EditarDespesaDialogFormController implements Initializable{
	
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
	private TextField txtPrecoUnid;
	@FXML
	private TextField txtQuantidade;
	@FXML
	private TextField txtDesconto;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btVoltar;	
	//----------------------------------------------------
	
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		Despesa desp =  new Despesa();
		desp.setId(Utils.stringParaInteiro(txtId.getText()));
		desp.setNome(txtNome.getText());
		desp.setPrecoUnid(Utils.stringParaDouble(txtPrecoUnid.getText()));
		desp.setQuantidade(Utils.stringParaInteiro(txtQuantidade.getText()));
		desp.setDescontoIndividual(Utils.stringParaDouble(txtDesconto.getText()));
		double valorUnid, quantidade, desconto;
		valorUnid = Utils.stringParaDouble(txtPrecoUnid.getText());
		quantidade = Utils.stringParaInteiro(txtQuantidade.getText());
		desconto = Utils.stringParaDouble(txtDesconto.getText());
		desp.setPrecoBruto(valorUnid * quantidade);
		desp.setPrecoTotal((valorUnid * quantidade)- desconto);
		despesaService.atualizar(desp);	
			
		parentStage.close();			
		carregarView("/gui/LanQuitadoView.fxml", (LanQuitadoController controller) -> {
			Lancamento lan = new Lancamento();
			lan.setId(lancamentoEntidade.getId());
			lan.setReferencia(lancamentoEntidade.getReferencia());
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(lan);
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setTipoPagService(new TipoPagService());
			controller.setItemPagamentoService(new ItemPagamentoService());	
			controller.setItemPagamento(new ItemPagamento());
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.carregarObjetosAssociados();
			controller.carregarUsuarioLogado();
			controller.carregarTableView();
			controller.desocultarCampos();
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
		txtPrecoUnid.setText(String.valueOf(despesaEntidade.getPrecoUnid()));
		txtQuantidade.setText(String.valueOf(despesaEntidade.getQuantidade()));
		txtDesconto.setText(String.valueOf(despesaEntidade.getDescontoIndividual()));
	}	
	
	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtNome, 60);
		Restricoes.setTextFieldDouble(txtPrecoUnid);
		Restricoes.setTextFieldInteger(txtQuantidade);
		Restricoes.setTextFieldDouble(txtDesconto);
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
