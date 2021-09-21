package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ItemDao;
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;

public class ItemService {
	
	private ItemDao dao = DaoFactory.criarItemDao();

	public List<Item> buscarTodos(){
		return dao.buscarTudo();
	}
	
	public void salvar(Item obj) {
			dao.inserir(obj);
	}
	
	public void atualizar(Item obj) {
			dao.atualizar(obj);
		}
		
	public void excluir (Lancamento lanId, Despesa despId) {
	dao.excluirPorId(lanId.getId(), despId.getId());
	}
	
	
	public List<Item> listarPorId(Integer id){
		return dao.listarPorId(id);
	}
	
	
	public Item buscarPorId(Integer id){
		return dao.buscarPorId(id);
	}
	
	public void limparItemPorIdLan (Integer lanId) {
		dao.limparItemPorIdLan(lanId);
		}
}
