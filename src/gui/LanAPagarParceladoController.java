package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import bd.BDIntegrityException;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;
import model.entidade.Usuario;
import model.servico.DespesaService;
import model.servico.ItemService;
import model.servico.LancamentoService;
import model.servico.UsuarioService;

public class LanAPagarParceladoController implements Initializable {

	private LancamentoService lancamentoService;
	private Lancamento lancamentoEntidade;
	private ItemService itemService;
	private DespesaService despesaService;
	private UsuarioService usuarioService;
	private Usuario usuarioEntidade;		
	// ------------------------------------------------------

	@FXML
	private TextField txtId;
	@FXML
	private TextField txtReferencia;
	@FXML
	private TextField txtItem;
	@FXML
	private TextField txtPreco;
	@FXML
	private TextField txtParcela;
	@FXML
	private Label lbTotal;
	@FXML
	private Label lbUsuario;
	@FXML
	private DatePicker datePickerData;
	@FXML
	private Button btCriarRegistroDeLancamento;
	@FXML
	private Button btItem;
	@FXML
	private Button btConfirmar;
	@FXML
	private Button btCancelar;
	@FXML
	private TableView<Despesa> tbDespesa;
	@FXML
	private TableColumn<Despesa, Despesa> colunaRemover;
	@FXML
	private TableColumn<Despesa, Despesa> colunaEditar;
	@FXML
	private TableColumn<Despesa, Integer> colunaDespId;
	@FXML
	private TableColumn<Despesa, String> colunaDespNome;
	@FXML
	private TableColumn<Despesa, Double> colunaDespValor;
	@FXML
	private CheckBox cbDetalheParcela;
	@FXML
	private TextArea txtAreaObs;
//--------------------------------------------------------

	private ObservableList<Despesa> obsListaDespesaTbView;
// ---------------------------------------------------------

	double total;
	int idLan;
	int idDesp;
	int idItem;
	int parcela;
	int aux;
	int despesaId;
	int lancamentoIds;
	int usuarioId;
	// ----------------------------------------------------------------------------------------------------------

	@FXML
	public void onBtCriarRegistroDeLancamento(ActionEvent evento) {
		total += 0.0;
		Lancamento obj = new Lancamento();
		obj.setTotal(total);
		if(txtReferencia.getText().equals("")) {
			Alertas.mostrarAlerta("Atenção", null, "Favor inserir registro do lançamento ", AlertType.WARNING);
		}
		else {
		if (datePickerData.getValue() == null) {
			Alertas.mostrarAlerta("", null, "Necessário informar a data para pagamento", AlertType.INFORMATION);
		} else {
			parcela = Utils.stringParaInteiro(txtParcela.getText());
			LocalDate dtPicker = datePickerData.getValue();
			for (int x = 1; x <= parcela; x++) {
				int mais30Dias = (x-1) * 30;
				GregorianCalendar data = new GregorianCalendar(dtPicker.getYear(), dtPicker.getMonthValue(),
						dtPicker.getDayOfMonth(), 0, 0, 0);
				int numero = 0;
				data.add(Calendar.DATE, numero + mais30Dias);
				data.add(Calendar.MONTH, numero - 1);
				Date dataParcelas = data.getTime();
				obj.setData(dataParcelas);
				
				//Usuário logado.
				Usuario user = new Usuario();
				user.setId(usuarioId);
				obj.setUsuario(user);							
				
				//Detalhe do parcelamento no Registro.				
				if(cbDetalheParcela.isSelected()) {
					obj.setReferencia(txtReferencia.getText()+" ["+x+"/"+parcela+"]");
				}else {
					obj.setReferencia(txtReferencia.getText());
				}
								
				lancamentoService.salvar(obj);
				lancamentoIds = obj.getId();
			}
			txtId.setText(String.valueOf(obj.getId()));
			datePickerData.setValue(LocalDate.ofInstant(obj.getData().toInstant(), ZoneId.systemDefault()));
			int id = obj.getId();
			idLan = id;
			aux = id - parcela;
		}
		}
	}

	@FXML
	public void onBtADDItem(ActionEvent evento) {
		Locale.setDefault(Locale.US);
		// Despesa
		Despesa desp = new Despesa();
		desp.setNome(txtItem.getText());
		desp.setPreco(Utils.stringParaDouble(txtPreco.getText()));
		if(txtItem.getText().equals("") || txtPreco.getText().equals("")) {
			Alertas.mostrarAlerta("Atenção", null, "Favor inserir Item e Valor", AlertType.WARNING);
		}else {
		despesaService.salvar(desp);
		despesaId = desp.getId();

		for (int x = 0; x < parcela; x++) {
			// Lancamento
			Lancamento obj = new Lancamento();
			lbTotal.setText(String.valueOf(obj.getTotal()));
			obj.setId(lancamentoIds);
			obj.setReferencia(txtReferencia.getText());
			obj.setTotal((total));
			lancamentoService.atualizar(obj);
			lancamentoIds--;
			// Item
			Item item = new Item();
			item.setLancamento(obj);
			item.setDespesa(desp);
			itemService.salvar(item);
			// Carregar campos
			txtId.setText(String.valueOf(obj.getId()));
			txtItem.setText("");
			txtPreco.setText(String.valueOf(0));
			// Carregar TableView do Lançamento
			List<Despesa> listaDespesa = despesaService.listarPorId(obj.getId());
			obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
			tbDespesa.setItems(obsListaDespesaTbView);
			iniciarBotaoRemover();
			// Valor Total
			Double soma = 0.0;
			for (Despesa tab : obsListaDespesaTbView) {
				soma += tab.getPreco();
				total = soma;
			}
			lbTotal.setText(String.format("R$ %.2f", soma));
			obj.setTotal(soma);
			lancamentoService.atualizar(obj);
		}
		lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
		}
	}

	@FXML
	public void onBtConfirmar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		if(txtId.getText().equals("") || txtPreco.getText().equals("")) {
			 Alertas.mostrarAlerta("Incompleto!", null, "Favor revisar todos campos", AlertType.WARNING);
		}
		else {
		for (int x = 0; x < parcela; x++) {
			obj.setId(lancamentoIds);
			obj.setObs(txtAreaObs.getText());
			lancamentoService.confirmarLanAPagar(obj);
			lancamentoIds--;
		}
		carregarPropriaView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
			controller.setLancamento(new Lancamento());
			controller.setLancamentoService(new LancamentoService());
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
		});
	}
			}

	@FXML
	public void onBtCancelar(ActionEvent evento) {
		Lancamento obj = new Lancamento();
		obj.setId(Utils.stringParaInteiro(txtId.getText()));
		if(!txtId.getText().equals("")) {
			lancamentoService.cancelar(obj);
			Alertas.mostrarAlerta(null, null, "Lançamento cancelado com sucesso", AlertType.INFORMATION);
		}
		carregarPropriaView("/gui/LanAPagarParceladoView.fxml", (LanAPagarParceladoController controller) -> {
			controller.setLancamento(new Lancamento());
			controller.setLancamentoService(new LancamentoService());			
			controller.setDespesaService(new DespesaService());
			controller.setItemService(new ItemService());
			controller.setUsuario(new Usuario());
			controller.setUsuarioService(new UsuarioService());
			controller.carregarUsuarioLogado();
		});
	}
	// ------------------------------------------------------------------

	public void setLancamentoService(LancamentoService lancamentoService) {
		this.lancamentoService = lancamentoService;
	}

	public void setLancamento(Lancamento lancamentoEntidade) {
		this.lancamentoEntidade = lancamentoEntidade;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	
	public void setDespesaService(DespesaService despesaService) {
		this.despesaService = despesaService;
	}

	
	public void setUsuarioService(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public void setUsuario(Usuario usuarioEntidade) {
		this.usuarioEntidade = usuarioEntidade;
	}
	// ------------------------------------------------------------

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		inicializarNodes();
	}

	public void carregarCamposDeCadastro() {
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
	}

	private void inicializarNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldTamanhoMaximo(txtReferencia, 50);
		Restricoes.setTextFieldDouble(txtPreco);
		Restricoes.setTextFieldTamanhoMaximo(txtItem, 30);
		Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		colunaDespId.setCellValueFactory(new PropertyValueFactory<>("id"));
		colunaDespNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
		colunaDespValor.setCellValueFactory(new PropertyValueFactory<>("preco"));
		Utils.formatTableColumnValorDecimais(colunaDespValor, 2); // Formatar com(0,00)
	}
	// -----------------------------------------------------------------

	private synchronized <T> void carregarPropriaView(String caminhoDaView, Consumer<T> acaoDeInicializacao) {
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

	
	private void iniciarBotaoRemover() {
		colunaRemover.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		colunaRemover.setCellFactory(param -> new TableCell<Despesa, Despesa>() {
			private final Button button = new Button("remover");

			@Override
			protected void updateItem(Despesa obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				setStyle("-fx-color: #FF6347");	
				button.setOnAction(event -> removerDespesa(obj));
			}
		});
	}

	private void removerDespesa(Despesa desp) {
		Optional<ButtonType> result = Alertas.mostrarConfirmacao("Confirmação", "Tem certeza que deseja remover?");
		if (result.get() == ButtonType.OK) {
			if (despesaService == null) {
				throw new IllegalStateException("Service nulo");
			}
			try {
				Lancamento lan = new Lancamento();
				for (int x = 0; x < parcela; x++) {
					lan.setId(lancamentoIds);
					itemService.excluir(lan, desp);
					despesaService.excluir(desp);
					// Carregar TableView do Lançamento
					List<Despesa> listaDespesa = despesaService.listarPorId(Utils.stringParaInteiro(txtId.getText()));
					obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
					tbDespesa.setItems(obsListaDespesaTbView);
					iniciarBotaoRemover();
					// Valor Total
					Double soma = 0.0;
					for (Despesa tab : obsListaDespesaTbView) {
						soma += tab.getPreco();
					}
					total = soma;
					lbTotal.setText(String.format("R$ %.2f", soma));
					lancamentoIds--;
				}
				lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
				for (int x = 0; x < parcela; x++) {
					lan.setId(lancamentoIds);
					lan.setTotal(total);
					lancamentoService.atualizar(lan);
					lancamentoIds--;
				}
				lancamentoIds += parcela; // Voltar o valor do primeiro ID Lançamentos do loop.
			} catch (BDIntegrityException ex) {
				Alertas.mostrarAlerta("Erro ao remover objeto", null, ex.getMessage(), AlertType.ERROR);
			}
		}
	}

	public void carregarTableView() {
		List<Despesa> listaDespesa = despesaService.listarPorId(lancamentoEntidade.getId());
		obsListaDespesaTbView = FXCollections.observableArrayList(listaDespesa);
		tbDespesa.setItems(obsListaDespesaTbView);
		iniciarBotaoRemover();
		txtId.setText(String.valueOf(lancamentoEntidade.getId()));
		txtReferencia.setText(lancamentoEntidade.getReferencia());
		// datePickerData.setValue(LocalDate.ofInstant(lancamentoEntidade.getData().toInstant(),
		// ZoneId.systemDefault()));
		// Utils.formatDatePicker(datePickerData, "dd/MM/yyyy");
		// Valor Total
		Double soma = 0.0;
		for (Despesa tab : obsListaDespesaTbView) {
			soma += tab.getPreco();
		}
		lbTotal.setText(String.format("R$ %.2f", soma));
	}
	
	public void carregarUsuarioLogado() {
		if(usuarioEntidade == null) {
			System.out.println("entidade nulo");
		}
		if(usuarioService == null) {
			System.out.println("service nulo");
		}
		List<Usuario> lista = usuarioService.buscarTodos();
		for(Usuario u : lista) {
			 u.getLogado();
			
			 if(u.getLogado().equals("S")) {
				 usuarioId = u.getId();	
				 lbUsuario.setText(String.valueOf(u.getNome()));
			 }
		 }
	}
}
