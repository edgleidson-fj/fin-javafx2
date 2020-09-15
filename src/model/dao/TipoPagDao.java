package model.dao;

import java.util.List;

import model.entidade.TipoPag;

public interface TipoPagDao {

	void inserir(TipoPag obj);
	void atualizar(TipoPag obj);
	void excluirPorId(Integer id);
	TipoPag buscarPorId(Integer id);
	List<TipoPag> buscarTudo();
}
