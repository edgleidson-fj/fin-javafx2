package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.ItemPagamentoDao;
import model.entidade.Despesa;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.TipoPag;

public class ItemPagamentoService {
	
	private ItemPagamentoDao dao = DaoFactory.criarItemPagamentoDao();

	public List<ItemPagamento> buscarTodos(){
		return dao.buscarTudo();
	}
	
	public void salvar(ItemPagamento obj) {
			dao.inserir(obj);
	}
	
	public void atualizar(ItemPagamento obj) {
			dao.atualizar(obj);
		}
		
	public void excluir (Integer lanId, Integer pagId) {
	dao.excluirPorId(lanId, pagId);
	}
	
	public void limparItemPagamentoPorIdLan (Lancamento lanId) {
		dao.limparItemPagamentoPorIdLan(lanId.getId());
		}
	
	
	public List<ItemPagamento> listarPorId(Integer id){
		return dao.listarPorId(id);
	}
	
	
	public ItemPagamento buscarPorId(Integer id){
		return dao.buscarPorId(id);
	}
}
