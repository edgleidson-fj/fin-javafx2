package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.UsuarioDao;
import model.entidade.Usuario;

public class UsuarioService {

	private UsuarioDao dao = DaoFactory.criarUsuarioDao();

	public List<Usuario> buscarTodos(){
		return dao.buscarTudo();
	}
		
	public void salvar(Usuario obj) {
			dao.inserir(obj);	
	}
	
	public void atualizar(Usuario obj) {
		dao.atualizar(obj);		
	}
	
	public void excluir (Usuario obj) {
		dao.excluirPorId(obj.getId());
	}
		
	public List<Usuario> listarPorId(Integer id){
		return dao.listarPorId(id);
	}
		
	public Usuario buscarPorId(Integer id){
		return dao.buscarPorId(id);
	}	
	
	public Usuario login(String nome, String senha){
		return dao.login(nome, senha);
	}
	
	/*public Usuario login(int id, String senha){
		return dao.login(id, senha);
	}*/
	
	public void logado(Usuario obj) {
		dao.logado(obj);		
	}
	
	public void logadoN(Usuario obj) {
		dao.logadoN(obj);		
	}
	
	public Usuario recuperarSenha(String nome, String cpf, String email, String novaSenha){
		return dao.recuperarSenha(nome, cpf, email, novaSenha);
	}
	
	public Usuario verificarUsuario(String nome, String cpf, String email){
		return dao.verificarUsuario(nome, cpf, email);
	}
	
}
