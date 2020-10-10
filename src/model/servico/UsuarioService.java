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
	
/*	public Usuario login(Usuario obj){
			return dao.login(obj);
	}*/
	
	public Usuario login(String nome, String senha){
		return dao.login(nome, senha);
	}
	
	public void logado(Usuario obj) {
		dao.logado(obj);		
	}
}
