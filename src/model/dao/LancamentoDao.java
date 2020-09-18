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
	List<Lancamento> buscarTudoEmAberto();
	List<Lancamento> buscarContasAPagarMesAtual();
	void cancelar(Lancamento obj);
	void confirmarLanQuitado(Lancamento obj);
	void confirmarLanAPagar(Lancamento obj);
	void confirmarPagamento(Lancamento obj);
	void cancelamentoAutomatico(Lancamento obj);
	void vencimentoAutomatico(Lancamento obj);
	void exclusaoAutomatico(Lancamento obj);
}
