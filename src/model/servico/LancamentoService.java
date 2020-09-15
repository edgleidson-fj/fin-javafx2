package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.LancamentoDao;
import model.entidade.Lancamento;

public class LancamentoService {
	
	private LancamentoDao dao = DaoFactory.criarLancamentoDao();

	public List<Lancamento> buscarTodos(){
		return dao.buscarTudo();
	}
	
	public List<Lancamento> buscarTudoQuitado(){
		return dao.buscarTudoQuitado();
	}
	
	public List<Lancamento> buscarTudoEmAberto(){
		return dao.buscarTudoEmAberto();
	}
	
	public List<Lancamento> buscarContasAPagarMesAtual(){
		return dao.buscarContasAPagarMesAtual();
	}
	
	public List<Lancamento> buscarContasQuitadoMesAtual(){
		return dao.buscarContasQuitadoMesAtual();
	}
	
	
	
	public void salvar(Lancamento obj) {
			dao.inserir(obj);
		}
	
	public void atualizar(Lancamento obj) {
		dao.atualizar(obj);
		}
	
	public void excluir (Lancamento obj) {
		dao.excluirPorId(obj.getId());
	}
	
	public void cancelar(Lancamento obj) {
		dao.cancelar(obj);
		}
	
	public void confirmarLanQuitado(Lancamento obj) {
		dao.confirmarLanQuitado(obj);
		}
	
	public void confirmarLanAPagar(Lancamento obj) {
		dao.confirmarLanAPagar(obj);
		}
	
	public void confirmarPagamento(Lancamento obj) {
		dao.confirmarPagamento(obj);
		}
}
