package model.dao;

import java.util.List;

import model.entidade.Usuario;

public interface UsuarioDao {

	void inserir(Usuario obj);
	void atualizar(Usuario obj);
	void excluirPorId(Integer id);
	Usuario buscarPorId(Integer id);
	List<Usuario> buscarTudo();
	Usuario login (String nome, String senha);
	void logado(Usuario obj);
	void logadoN(Usuario obj);
	Usuario recuperarSenha (String nome, String cpf, String email, String novaSenha);
	Usuario verificarUsuario (String nome, String cpf, String email);
}
