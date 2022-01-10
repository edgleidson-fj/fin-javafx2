package model.dao;

import java.util.List;

import model.entidade.Despesa;

public interface DespesaDao {

	void inserir(Despesa obj);
	void atualizar(Despesa obj);
	void excluirPorId(Integer id);
	List<Despesa> listarPorId(Integer id);
	List<Despesa> consultaPorRankDespesaMesAtual(Integer id);
	List<Despesa> consultaPorRankDespesaAnoAtual(Integer id);
	void rateioDesconto(Despesa obj);
	void rateioAcrescimo(Despesa obj);
}
