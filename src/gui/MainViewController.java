package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.TipoPag;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.StatusService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;

public class MainViewController implements Initializable {

	private UsuarioService usuarioService;
	private Usuario usuarioEntidade; 
	//-------------------------------------------------------
	
	@FXML
	private MenuItem menuItemSair;
	@FXML
	private MenuItem menuItemLancamentoQuitado;
	@FXML
	private MenuItem menuItemLancamentoAPagar;
	@FXML
	private MenuItem menuItemLancamentoAPagarParcelado;
	@FXML
	private MenuItem menuItemContasQuitadoMesAtual;
	@FXML
	private MenuItem menuItemContasQuitadoPeriodo;
	@FXML
	private MenuItem menuItemContasQuitadoTodos;
	@FXML
	private MenuItem menuItemContasEmAbertoMesAtual;
	@FXML
	private MenuItem menuItemContasEmAbertoPeriodo;
	@FXML
	private MenuItem menuItemContasEmAbertoTodos;
	@FXML
	private MenuItem menuItemTodasContas;
	@FXML
	private MenuItem menuItemTipoPagamento;
	@FXML
	private MenuItem menuItemUsuario;
	@FXML
	private Label lbUsuario;
	@FXML
	private TextField txtNome;
	@FXML
	private TextField txtSenha;
	@FXML
	private TextField txtID;
	@FXML
	private Button btEntrar;
	@FXML
	private Button btCriarUsuario;
	//-----------------------------------------------------
	int usuarioId;
	String userNome;
	int id;	
	
	@FXML
	public void onMenuItemSair(ActionEvent evento) {	
		usuarioEntidade.setLogado("N");
		usuarioService.logadoN(usuarioEntidade);
		carregarView("/gui/LoginView.fxml", (LoginController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
		});		
	}

	@FXML
	public void onMenuItemLancamentoQuitado() {
 List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {		
		carregarView("/gui/LanQuitadoView.fxml", (LanQuitadoController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setDespesa(new Despesa());
			controller.setItemService(new ItemService());
			controller.setItem(new Item());			
			controller.setTipoPag(new TipoPag());
			controller.setTipoPagService(new TipoPagService());
			controller.setStatus(new Status());
			controller.setStatusService(new StatusService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarObjetosAssociados();	
			controller.carregarUsuarioLogado();
		});
			 }
		 }
		}
	
	@FXML
	public void onMenuItemLancamentoAPagar() {
 List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/LanAPagarView.fxml", (LanAPagarController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setDespesa(new Despesa());
			controller.setItemService(new ItemService());
			controller.setItem(new Item());	
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemLancamentoAPagarParcelado() {
 List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {		
		carregarView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setDespesa(new Despesa());
			controller.setItemService(new ItemService());
			controller.setItem(new Item());	
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemContasQuitadoMesAtual() {
 List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
	carregarView("/gui/ContasQuitadasMesAtualView.fxml", (ContasQuitadasMesAtualController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setUsuario(usuarioEntidade);
			controller.setUsuarioService(new UsuarioService());			
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemContasQuitadoPeriodo() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasQuitadasPeriodoView.fxml", (ContasQuitadasPeriodoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemContasQuitadoTodos() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasQuitadasView.fxml", (ContasQuitadasController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setUsuario(usuarioEntidade);
			controller.setUsuarioService(new UsuarioService());			
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemContasEmAbertoMesAtual() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setUsuario(usuarioEntidade);
			controller.setUsuarioService(new UsuarioService());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
			//controller.carregarUsuarioLogado();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemContasEmAbertoPeriodo() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoPeriodoView.fxml", (ContasEmAbertoPeriodoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			Usuario user = new Usuario();
			user.setId(u.getId());
			user.setNome(u.getNome());
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());			
			controller.rotinasAutomaticas();
			//controller.carregarUsuarioLogado();
		});
			 }
		 }
	}
		
	@FXML
	public void onMenuItemContasEmAbertoTodos() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoView.fxml", (ContasEmAbertoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setUsuario(usuarioEntidade);
			controller.setUsuarioService(new UsuarioService());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
			//controller.carregarUsuarioLogado();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemTodasContas() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/TodasContasView.fxml", (TodasContasController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
			Usuario user = new Usuario();
			});
			 }
		 }
	}

	@FXML
	public void onMenuItemTipoPagamento() {
		List<Usuario> lista = usuarioService.buscarTodos();
		 
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/TipoPagView.fxml", (TipoPagController controller) -> {
			controller.setTipoPagService(new TipoPagService());
			controller.carregarTableView();
		});
			 }
		 }
	}
	
	@FXML
	public void onMenuItemUsuario() {
	carregarView("/gui/UsuarioView.fxml", (UsuarioController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
		});			
	}
	
	//--------------------------------------------------------
	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Stage parentStage = Utils.stageAtual(evento);
		
	//	String nome = txtNome.getText();
		int id = Utils.stringParaInteiro(txtNome.getText());
		String senha = txtSenha.getText();		
		//usuarioEntidade.setNome(nome);
		usuarioEntidade.setId(id);
		usuarioEntidade.setSenha(senha);	
		Usuario user = usuarioService.login(id,senha);
		
		if(user != null) {				
				usuarioEntidade.setId(id);
				usuarioEntidade.setLogado("S");
				usuarioService.logado(usuarioEntidade);
				//usuarioId = usuarioEntidade.getId();
		
				List<Usuario> lista = usuarioService.buscarTodos();
			for(Usuario u : lista) {
				 u.getLogado();		
				 if(u.getLogado().equals("S")) {
					 usuarioId = u.getId();			 
				 }				
			 }
				
			carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.setUsuario(usuarioEntidade);
				controller.setUsuarioService(new UsuarioService());
				controller.rotinasAutomaticas();
				controller.carregarTableView();
				//controller.carregarUsuarioLogado();
			});			
		} else{
			System.out.println("Usuário Errado!");
		}			
		}
	
	@FXML
	public void onBtCriarUsuario() {
		carregarView("/gui/UsuarioView.fxml", (UsuarioController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
		});
	}
	
	//-------------------------------------------------------
	
		public void setUsuarioService(UsuarioService usuarioService) {
			this.usuarioService = usuarioService;
		}
		public void setUsuario(Usuario usuarioEntidade) {
			this.usuarioEntidade = usuarioEntidade;
		}
		//-------------------------------------------------------
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setUsuario(new Usuario());
		setUsuarioService(new UsuarioService());
		}
	// ------------------------------------------------------------------
		
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

