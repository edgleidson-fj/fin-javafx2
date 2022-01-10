package model.dao;

import java.util.List;

import model.entidade.Status;

public interface StatusDao {

	void inserir(Status obj);
	Status buscarPorId(Integer id);
	List<Status> buscarTudo();
	List<Status> buscarEmAbertoECancelado();
}
