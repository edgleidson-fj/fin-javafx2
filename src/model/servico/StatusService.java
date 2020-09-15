package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.StatusDao;
import model.entidade.Status;

public class StatusService {
	
	private StatusDao dao = DaoFactory.criarStatusDao();

	public List<Status> buscarTodos(){
		return dao.buscarTudo();
	}
	
/*	public void salvarOuAtualizar(Status obj) {
		if(obj.getId() == null) {
			dao.inserir(obj);
			}
		else {
			dao.atualizar(obj);
		}
	}
	
	public void excluir (Status obj) {
		dao.excluirPorId(obj.getId());
	}*/
}
