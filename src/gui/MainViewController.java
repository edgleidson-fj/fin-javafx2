package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alertas;
import gui.util.Restricoes;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.Mensagem;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemPagamentoService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.MensagemService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;
import seguranca.Criptografia;

public class MainViewController implements Initializable {

	private UsuarioService usuarioService;
	private Usuario usuarioEntidade; 
	private MensagemService mensagemService;
	
	@FXML
	private Menu menuLogin;
	@FXML
	private Menu menuLancamentos;
	@FXML
	private Menu menuRelatorios;
	@FXML
	private Menu menuCadastros;
	@FXML
	private MenuItem menuItemSair;
	@FXML
	private MenuItem menuItemLancamentoQuitado;
	@FXML
	private MenuItem menuItemLancamentoAPagar;
	@FXML
	private MenuItem menuItemLancamentoAPagarParcelado;
	@FXML
	private MenuItem menuItemLancamentoAPagarFatura;
	@FXML
	private MenuItem menuItemConsultaPorPagamento;
	@FXML
	private MenuItem menuItemConsultaPorRank;	
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
	private TextField txtCPF;
	@FXML
	private PasswordField txtSenha;
	@FXML
	private TextField txtID;
	@FXML
	private Button btEntrar;
	@FXML
	private Button btCriarUsuario;
	@FXML
	private Button btLimpar;
	@FXML
	private Button btEsqueciSenha;	

	int usuarioId;
	String userNome;
	int id;	
	
	@FXML
	public void onMenuItemSair(ActionEvent evento) {	
		usuarioEntidade.setLogado("N");
		Platform.exit();		
	}

	@FXML
	public void onMenuItemLancamentoQuitado() {
 List<Usuario> lista = usuarioService.buscarTodos();
 String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {		
		carregarView("/gui/LanQuitadoView.fxml", (LanQuitadoController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setTipoPagService(new TipoPagService());
			controller.setItemPagamentoService(new ItemPagamentoService());	
			controller.setItemPagamento(new ItemPagamento());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarObjetosAssociados();	
			controller.carregarUsuarioLogado();	
			controller.ocultarCampos();
		});
		logado = "S";
			 }
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}	
		}
	
	@FXML
	public void onMenuItemLancamentoAPagar() {
 List<Usuario> lista = usuarioService.buscarTodos();
 String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/LanAPagarView.fxml", (LanAPagarController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			controller.ocultarCampos();
			});
		logado = "S";
			 }			 
		 }
		 if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
		
	@FXML
	public void onMenuItemLancamentoAPagarParcelado() {
 List<Usuario> lista = usuarioService.buscarTodos();
 String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {		
		carregarView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			controller.ocultarCampos();
			});
		 logado = "S";
			 }			
		 }
		 if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemLancamentoAPagarFatura() {
 List<Usuario> lista = usuarioService.buscarTodos();
 String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {		
		carregarView("/gui/LanAPagarFaturaView.fxml", (LanAPagarFaturaController controller) -> {
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
			controller.ocultarCampos();
			});
		 logado = "S";
			 }			
		 }
		 if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemConsultaPorPagamento() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/ConsultaPorPagamentoView.fxml", (ConsultaPorPagamentoController controller) -> {
			controller.setItemPagamentoService(new ItemPagamentoService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}	
	
	@FXML
	public void onMenuItemConsultaPorRank() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/ConsultaPorRankView.fxml", (ConsultaPorRankController controller) -> {
			controller.setDespesaService(new DespesaService());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}	
	
	@FXML
	public void onMenuItemContasQuitadoMesAtual() {
 List<Usuario> lista = usuarioService.buscarTodos();
 String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
	carregarView("/gui/ContasQuitadasMesAtualView.fxml", (ContasQuitadasMesAtualController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
			controller.carregarUsuarioLogado();
		});
	logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemContasQuitadoPeriodo() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasQuitadasPeriodoView.fxml", (ContasQuitadasPeriodoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemContasQuitadoTodos() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasQuitadasView.fxml", (ContasQuitadasController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemContasEmAbertoMesAtual() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemContasEmAbertoPeriodo() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoPeriodoView.fxml", (ContasEmAbertoPeriodoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			Usuario user = new Usuario();
			user.setId(u.getId());
			user.setNome(u.getNome());
			controller.rotinasAutomaticas();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
		
	@FXML
	public void onMenuItemContasEmAbertoTodos() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/ContasEmAbertoView.fxml", (ContasEmAbertoController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}
	
	@FXML
	public void onMenuItemTodasContas() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {
		carregarView("/gui/TodasContasView.fxml", (TodasContasController controller) ->{
			controller.setLancamentoService(new LancamentoService());
			controller.setLancamento(new Lancamento());
			controller.rotinasAutomaticas();
			controller.carregarTableView();
			});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
			}
	}

	@FXML
	public void onMenuItemTipoPagamento() {
		List<Usuario> lista = usuarioService.buscarTodos();
		String logado = "N";
		 for(Usuario u : lista) {
			 u.getLogado();
			 
			 if(u.getLogado().equals("S")) {	
		carregarView("/gui/TipoPagView.fxml", (TipoPagController controller) -> {
			controller.setTipoPagService(new TipoPagService());
			controller.carregarTableView();
		});
		logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta("Acesso negado!",null , "Necessário efetuar Login.", AlertType.WARNING);
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

	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Criptografia c = new Criptografia();		
		String cpf = c.criptografia(txtCPF.getText());
		String senha ="";
		if(!txtSenha.getText().equals("")) {
			senha = c.criptografia(txtSenha.getText());	
		}
		usuarioEntidade.setNome(cpf);
		usuarioEntidade.setSenha(senha);
		Usuario user = usuarioService.login(cpf, senha);
		if(user != null) {				
				usuarioEntidade.setCpf(cpf);
				usuarioEntidade.setLogado("S");
				usuarioService.logado(usuarioEntidade);
		
				List<Usuario> lista = usuarioService.buscarTodos();
			for(Usuario u : lista) {
				 u.getLogado();		
				 if(u.getLogado().equals("S")) {
					 usuarioId = u.getId();			 
				 }				
			 }
			desocultarMenu();	
			carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.rotinasAutomaticas();
				controller.carregarTableView();
			});		
			
			Mensagem msg = new Mensagem();
			msg = mensagemService.buscarPorId(1);
			Calendar datahoje = Calendar.getInstance();
			int mesAtual = datahoje.get(Calendar.MONTH)+1;
			if(mesAtual == 12 && msg.getMostrar().equals("S")) {
			MsgProxAnoDialogForm();		
			}
			if(mesAtual != 12 && msg.getMostrar().equals("N")) {
				msg = new Mensagem();
				msg.setId(1);
				msg.setMostrar("S");
				mensagemService.atualizar(msg);
			}
		} else{
			Alertas.mostrarAlerta("Erro de autenticação.",null , "Usuário e/ou Senha incorreto!", AlertType.ERROR);
		}
		}
	
	@FXML
	public void onBtCriarUsuario() {
		carregarView("/gui/UsuarioView.fxml", (UsuarioController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
			desocultarMenu();
		});
	}
	
	@FXML
	public void onBtLimpar() {
		txtCPF.setText("");
		txtSenha.setText("");
	}
	
	@FXML
	public void onBtEsqueciASenha() {
		carregarView("/gui/EsqueciASenha.fxml", (EsqueciASenhaController controller) -> {
			controller.setUsuarioService(new UsuarioService());
			controller.setUsuario(new Usuario());
			controller.carregarCamposDeCadastro();
			desocultarMenu();
		});
	}
	
		public void setUsuarioService(UsuarioService usuarioService) {
			this.usuarioService = usuarioService;
		}
		public void setUsuario(Usuario usuarioEntidade) {
			this.usuarioEntidade = usuarioEntidade;
		}
		public void setMensagemService(MensagemService mensagemService) {
			this.mensagemService = mensagemService;
		}		
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setUsuario(new Usuario());
		setUsuarioService(new UsuarioService());
		setMensagemService(new MensagemService());
		Restricoes.setTextFieldInteger(txtCPF);
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 11);	
		Restricoes.setTextFieldTamanhoMaximo(txtSenha, 20);
		ocultarMenu();
		}
		
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

			T controller = loader.getController();
			acaoDeInicializacao.accept(controller);
		} catch (IOException ex) {
			Alertas.mostrarAlerta("IO Exception", "Erro ao carregar a tela.", ex.getMessage(), AlertType.ERROR);
		}
	}	
	
		public void MsgProxAnoDialogForm() {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MsgProxAnoView.fxml"));
				Pane painel = loader.load();					
				
				MsgProxAnoController controle = loader.getController();
				controle.setMensagem(new Mensagem());
				controle.setMensagemService(new MensagemService());
				controle.carregarMensagem();					

				Stage stageDialog = new Stage();
				stageDialog.setTitle("AVISO");
				stageDialog.setScene(new Scene(painel));
				stageDialog.setResizable(false); 
				stageDialog.initModality(Modality.WINDOW_MODAL); 
				stageDialog.showAndWait();
			} catch (IOException ex) {
				ex.printStackTrace();
				Alertas.mostrarAlerta("IO Exception", "Erro ao carregar View", ex.getMessage(), AlertType.ERROR);
			}
		}	
		
		public void ocultarMenu() {
			menuLogin.setVisible(false);
			menuLancamentos.setVisible(false);
			menuRelatorios.setVisible(false);
			menuCadastros.setVisible(false);
		}
		
		public void desocultarMenu() {
			menuLogin.setVisible(true);
			menuLancamentos.setVisible(true);
			menuRelatorios.setVisible(true);
			menuCadastros.setVisible(true);
		}
		
			}