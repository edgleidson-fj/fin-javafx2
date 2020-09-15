package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.TipoPagDao;
import model.entidade.TipoPag;

public class TipoPagService {
	
	private TipoPagDao dao = DaoFactory.criarTipoPagDao();

	public List<TipoPag> buscarTodos(){
		return dao.buscarTudo();
	}
	
	public void salvarOuAtualizar(TipoPag obj) {
		if(obj.getId() == null) {
			dao.inserir(obj);
			}
		else {
			dao.atualizar(obj);
		}
	}
	
	public void excluir (TipoPag obj) {
		dao.excluirPorId(obj.getId());
	}
}
