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
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entidade.Lancamento;
import model.entidade.Mensagem;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.MensagemService;
import model.servico.TipoPagService;
import model.servico.UsuarioService;

public class MainViewController implements Initializable {

	private UsuarioService usuarioService;
	private Usuario usuarioEntidade; 
	private MensagemService mensagemService;
	private Mensagem mensagemEntidade;
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
			Usuario user = new Usuario();
			user.setId(usuarioId);
			user.setNome(userNome);
			controller.setUsuario(user);
			controller.setUsuarioService(new UsuarioService());
			controller.carregarObjetosAssociados();	
			controller.carregarUsuarioLogado();			
		});
		logado = "S";
			 }
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
			});
		logado = "S";
			 }			 
		 }
		 if(logado.equals("N")) {
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
			});
		 logado = "S";
			 }			
		 }
		 if(logado.equals("N")) {
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
			controller.rotinasAutomaticas();
			controller.carregarTableView();
		});
	logado = "S";
			 }			 
		 }if(logado.equals("N")) {
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
				Alertas.mostrarAlerta(null,null , "Necessário efetuar Login!", AlertType.WARNING);
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
		String cpf = txtCPF.getText();
		String senha = txtSenha.getText();		
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
				
			carregarView("/gui/ContasEmAbertoMesAtualView.fxml", (ContasEmAbertoMesAtualController controller) ->{
				controller.setLancamentoService(new LancamentoService());
				controller.setLancamento(new Lancamento());
				controller.rotinasAutomaticas();
				controller.carregarTableView();
			});		
			
			//Mensagem de aviso para próximo ano.
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
			Alertas.mostrarAlerta(null,null , "Usuário ou Senha incorreto!", AlertType.ERROR);
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
		});
	}
	//-------------------------------------------------------
	
		public void setUsuarioService(UsuarioService usuarioService) {
			this.usuarioService = usuarioService;
		}
		public void setUsuario(Usuario usuarioEntidade) {
			this.usuarioEntidade = usuarioEntidade;
		}
		public void setMensagemService(MensagemService mensagemService) {
			this.mensagemService = mensagemService;
		}		
		public void setMensagem(Mensagem mensagemEntidade) {
			this.mensagemEntidade = mensagemEntidade;
		}
		//-------------------------------------------------------
		
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		setUsuario(new Usuario());
		setUsuarioService(new UsuarioService());
		setMensagemService(new MensagemService());
		setMensagem(new Mensagem());
		Restricoes.setTextFieldTamanhoMaximo(txtCPF, 14);
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
	
	
	//Mascara 999.999.999-99
		@FXML
		private void mascaraCPF() {
		   txtCPF.setOnKeyTyped((KeyEvent evento) -> {
	            if (!"01234567891234".contains(evento.getCharacter())) {
	                evento.consume();
	            }
	            if (evento.getCharacter().trim().length() == 0) {

	            } else if (txtCPF.getText().length() == 16) {
	                evento.consume();
	            }
	            switch (txtCPF.getText().length()) {
	                case 3:
	                	txtCPF.setText(txtCPF.getText() + ".");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 7:
	                	txtCPF.setText(txtCPF.getText() + ".");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 11:
	                	txtCPF.setText(txtCPF.getText() + "-");
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	                case 14:
	                	txtCPF.positionCaret(txtCPF.getText().length());
	                    break;
	            }
	        });
	    }
	
		//Mensagem de aviso.
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
				stageDialog.setResizable(false); // Redimencionavel.
				//stageDialog.initOwner(stagePai); // Stage pai da janela.
				stageDialog.initModality(Modality.WINDOW_MODAL); // Impedir o acesso de outras janela.
				stageDialog.showAndWait();
			} catch (IOException ex) {
				ex.printStackTrace();
				Alertas.mostrarAlerta("IO Exception", "Erro ao carregar View", ex.getMessage(), AlertType.ERROR);
			}
		}	
	
		}

