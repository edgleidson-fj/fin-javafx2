package model.dao;

import java.util.List;

import model.entidade.Usuario;

public interface UsuarioDao {

	void inserir(Usuario obj);
	void atualizar(Usuario obj);
	void excluirPorId(Integer id);
	Usuario buscarPorId(Integer id);
	List<Usuario> buscarTudo();

	List<Usuario> listarPorId(Integer id);
}