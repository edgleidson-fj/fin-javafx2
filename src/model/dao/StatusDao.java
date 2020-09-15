package model.dao;

import java.util.List;

import model.entidade.Status;

public interface StatusDao {

//	void inserir(Status obj);
//	void atualizar(Status obj);
//	void excluirPorId(Integer id);
	Status buscarPorId(Integer id);
	List<Status> buscarTudo();
}
