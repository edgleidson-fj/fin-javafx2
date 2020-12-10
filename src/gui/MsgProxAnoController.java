package gui;

import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.entidade.Mensagem;
import model.servico.MensagemService;

public class MsgProxAnoController implements Initializable{
	
	private MensagemService mensagemService;
	private Mensagem mensagemEntidade;
	
	@FXML
	private Label lbMsg;
	@FXML
	private Label lbProxAno;
	@FXML
	private Button btNaoMostrar;
	
	@FXML
	public void onBtNaoMostrar(ActionEvent evento) {
		Mensagem msg = new Mensagem();
		msg.setId(1);
		msg.setMostrar("N");
		mensagemService.atualizar(msg);
		Utils.stageAtual(evento).close();
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub		
	}

	public void setMensagemService(MensagemService mensagemService) {
		this.mensagemService = mensagemService;
	}
	
	public void setMensagem(Mensagem mensagemEntidade) {
		this.mensagemEntidade = mensagemEntidade;
	}
	
	public void carregarMensagem() {
		Calendar datahoje = Calendar.getInstance();
		int proxAno = datahoje.get(Calendar.YEAR)+1;
		mensagemEntidade = mensagemService.buscarPorId(1);
		lbMsg.setText(mensagemEntidade.getDescricao());
		lbProxAno.setText(" "+proxAno+".");
	}	
}
