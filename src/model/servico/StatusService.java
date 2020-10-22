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
	
	public void salvar(Status obj) {
			dao.inserir(obj);
			}
	
	
}
