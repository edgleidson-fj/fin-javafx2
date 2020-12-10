package model.dao;

import model.entidade.Mensagem;

public interface MensagemDao {

	Mensagem buscarPorId(Integer id);	
	void atualizar(Mensagem obj);
}
