package model.dao;

import java.util.List;

import model.entidade.ItemPagamento;

public interface ItemPagamentoDao {

	void inserir(ItemPagamento obj);
	void atualizar(ItemPagamento obj);
	void excluirPorId(Integer lanId, Integer despId);
	void limparItemPagamentoPorIdLan(Integer lanId);
	ItemPagamento buscarPorId(Integer id);
	List<ItemPagamento> buscarTudo();
	List<ItemPagamento> listarPorId(Integer id);
	List<ItemPagamento> consultaPorPagamentoMesAtual(Integer id);
	List<ItemPagamento> consultaPorPagamentoAnoAtual(Integer id);
}
