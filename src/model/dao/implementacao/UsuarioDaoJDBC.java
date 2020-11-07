package model.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import bd.BD;
import bd.BDException;
import bd.BDIntegrityException;
import model.dao.UsuarioDao;
import model.entidade.Usuario;

public class UsuarioDaoJDBC implements UsuarioDao {

	private Connection connection;
	public UsuarioDaoJDBC(Connection connection) {
		this.connection = connection;
	}
//-------------------------------------------------------------------

	@Override 
	public void inserir(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"INSERT INTO usuario "
						+ "(usuarioNome, usuarioSenha, email, cpf) "
							+ "VALUES  ( ?, ?, ?, ?) ",
							Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getSenha());
			ps.setString(3, obj.getEmail());
			ps.setString(4, obj.getCpf());
			int linhasAfetadas = ps.executeUpdate();
			if (linhasAfetadas > 0) {
				ResultSet rs = ps.getGeneratedKeys(); // ID gerado no Insert.
				if (rs.next()) {
					int id = rs.getInt(1); // ID do Insert.
					obj.setId(id);
				}
				BD.fecharResultSet(rs);
			} else {
				throw new BDException("Erro no INSERT, nenhuma linha foi afetada!");
			}
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override 
	public void atualizar(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					 "UPDATE usuario "
					+ "SET usuarioNome = ?,  "
					+ "usuarioSenha = ? "
					+ "WHERE usuarioId = ? ");
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getSenha());
			ps.setInt(3, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override 
	public void excluirPorId(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
			"DELETE FROM usuario  " 
			+ "WHERE usuarioId = ? ");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override 
	public Usuario buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM usuario " 
					+ "WHERE usuarioId = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Usuario obj = new Usuario();
				obj.setId(rs.getInt("usuarioId"));
				obj.setNome(rs.getString("usuarioNome"));
				return obj;
			}
			return null;
		} catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	}

	@Override 
	public List<Usuario> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM usuario ");
			rs = ps.executeQuery();
			List<Usuario> lista = new ArrayList<>();

			while (rs.next()) {
				Usuario obj = new Usuario();
				obj.setId(rs.getInt("usuarioId"));
				obj.setNome(rs.getString("usuarioNome"));
				obj.setSenha(rs.getString("usuarioSenha"));
				obj.setLogado(rs.getString("logado"));
				obj.setEmail(rs.getString("email"));
				obj.setCpf(rs.getString("cpf"));
				lista.add(obj);
			}
			return lista;
		} catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	}

	//Sem Uso.
   public List<Usuario> listarPorId(Integer id){
        PreparedStatement ps = null;
        ResultSet rs = null;
     try {
    	 ps = connection.prepareStatement(
        		"");     
    	 ps.setInt(1, id);
        rs = ps.executeQuery();        
        List<Usuario> lista = new ArrayList<Usuario>();
        while(rs.next()){
            Usuario d = new Usuario();
            d.setNome(rs.getString("usuarioNome"));
            d.setId(rs.getInt("usuarioId"));
            d.setSenha(rs.getString("usuarioSenha"));
            lista.add(d);
        }
        rs.close();
        ps.close();
        return lista;
     } catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	     }
    
	
    
    @Override 
	public Usuario login(String cpf, String senha) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM usuario " 
					//+ "WHERE usuarioNome = ? "
					+ "WHERE cpf = ? "
					+ "AND usuarioSenha = ? ");
			//ps.setString(1, nome);
			ps.setString(1, cpf);
			ps.setString(2, senha);
			rs = ps.executeQuery();
			if (rs.next()) {
				Usuario obj = new Usuario();
				obj.setId(rs.getInt("usuarioId"));
				obj.setNome(rs.getString("usuarioNome"));
				obj.setSenha(rs.getString("usuarioSenha"));
				obj.setCpf(rs.getString("cpf"));
				return obj;
			}
			return null;
		} catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	}
    
    
  
    
    @Override
	public void logado(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE usuario " 
					+ "SET logado = ? "
					//+ "WHERE usuarioNome = ?");
					+ "WHERE cpf = ?");
			ps.setString(1, obj.getLogado());
			//ps.setString(2, obj.getNome());
			ps.setString(2, obj.getCpf());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
    
    @Override
	public void logadoN(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE usuario " 
					+ "SET logado = ? ");
			ps.setString(1, obj.getLogado());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}


}