package model.dao;

import java.util.List;

import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;

public interface DespesaDao {

	void inserir(Despesa obj);
	void atualizar(Despesa obj);
	void excluirPorId(Integer id);
	Despesa buscarPorId(Integer id);
	List<Despesa> buscarTudo();

	List<Despesa> listarPorId(Integer id);
}
