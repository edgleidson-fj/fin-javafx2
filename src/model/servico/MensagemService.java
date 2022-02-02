package model.servico;

import model.dao.DaoFactory;
import model.dao.MensagemDao;
import model.entidade.Mensagem;

public class MensagemService {

	private MensagemDao dao = DaoFactory.criarMensagemDao();
			
	public Mensagem buscarPorId(Integer id){
		return dao.buscarPorId(id);
	}	
	
	public void atualizar(Mensagem obj) {
		dao.atualizar(obj);		
	}
	
	public void situacao(Mensagem obj) {
		dao.situacao(obj);		
	}
}
