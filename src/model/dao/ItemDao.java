package model.dao;

import java.util.List;

import model.entidade.Item;

public interface ItemDao {

	void inserir(Item obj);
	void atualizar(Item obj);
	void excluirPorId(Integer lanId, Integer despId);
	List<Item> buscarTudo();
	void limparItemPorIdLan(Integer lanId);
}
