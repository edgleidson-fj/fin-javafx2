package model.dao;

import java.util.List;

import model.entidade.Lancamento;

public interface LancamentoDao {

	void inserir(Lancamento obj);
	void atualizar(Lancamento obj);
	void excluirPorId(Integer id);
	Lancamento buscarPorId(Integer id);
	List<Lancamento> buscarTudo();
	List<Lancamento> buscarTudoQuitado();
	List<Lancamento> buscarContasQuitadoMesAtual();
	List<Lancamento> buscarContasQuitadoPeriodo(String dataInicial, String dataFinal);
	List<Lancamento> buscarTudoEmAberto();
	List<Lancamento> buscarContasAPagarMesAtual();
	List<Lancamento> buscarContasEmAbertoPeriodo(String dataInicial, String dataFinal);
	void cancelar(Lancamento obj);
	void confirmarLanQuitado(Lancamento obj);
	void confirmarLanAPagar(Lancamento obj);
	void confirmarPagamento(Lancamento obj);
	void lanConfig(Lancamento obj);
	void cancelamentoAutomatico(Lancamento obj);
	void vencimentoAutomatico(Lancamento obj);
	void exclusaoAutomatico(Lancamento obj);
	List<Lancamento> buscarLanPorId(Integer id);
	List<Lancamento> buscarPorReferenciaOuDespesa(String refOuDespesa);
	List<Lancamento> buscarPorReferenciaOuDespesaQuitado(String refOuDespesa);
	List<Lancamento> buscarPorReferenciaOuDespesaQuitadoMesAtual(String refOuDespesa);
	List<Lancamento> buscarPorReferenciaOuDespesaEmAberto(String refOuDespesa);
	List<Lancamento> buscarPorReferenciaOuDespesaEmAbertoMesAtual(String refOuDespesa);
}
