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
	public List<Lancamento> buscarContasEmAbertoPeriodo(String dataInicial, String dataFinal){
		return dao.buscarContasEmAbertoPeriodo(dataInicial, dataFinal);
	}
	public List<Lancamento> buscarContasQuitadoPeriodo(String dataInicial, String dataFinal){
		return dao.buscarContasQuitadoPeriodo(dataInicial, dataFinal);
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
	public void lanConfig(Lancamento obj) {
		dao.lanConfig(obj);
	}
	public void cancelamentoAutomatico(Lancamento obj) {
		dao.cancelamentoAutomatico(obj);
	}
	public void vencimentoAutomatico(Lancamento obj) {
		dao.vencimentoAutomatico(obj);
	}
	public void exclusaoAutomatico(Lancamento obj) {
		dao.exclusaoAutomatico(obj);
	}
	public List<Lancamento>buscarLanPorId (Integer id) {
		return dao.buscarLanPorId(id);
	}	
}
