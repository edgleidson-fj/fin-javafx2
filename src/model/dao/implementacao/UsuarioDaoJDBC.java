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
import model.entidade.Lancamento;

public class UsuarioDaoJDBC implements UsuarioDao {

	private Connection connection;
	public UsuarioDaoJDBC(Connection connection) {
		this.connection = connection;
	}
//-------------------------------------------------------------------

	@Override //ok
	public void inserir(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"INSERT INTO despesa "
						+ "(nome, preco) "
							+ "VALUES  ( ?, ?) ",
							Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getNome());
			ps.setString(2, obj.getSenha());
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

	@Override //ok
	public void atualizar(Usuario obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					 "UPDATE despesa "
					+ "SET nome = ?,  "
					+ "preco = ? "
					+ "WHERE id = ? ");
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

	@Override //ok
	public void excluirPorId(Integer id) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
			"DELETE FROM despesa  " 
			+ "WHERE Id = ? ");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override //Falta testar
	public Usuario buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM despesa " + "WHERE Id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Usuario obj = new Usuario();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("nome"));
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

	@Override //Falta testar
	public List<Usuario> buscarTudo() {
		PreparedStatement ps = null;
		PreparedStatement ps1 = null;
		ResultSet rs = null;
		try {
	//		ps = connection.prepareStatement("SELECT * FROM despesa " + " ORDER BY nome ");
			int x = 100;
	
			ps = connection.prepareStatement(
					"SELECT *  "
				+ "from lancamento as l, item as i, despesa as d "	
+ "where l.id = i.lancamento_id "
+ "and i.despesa_id = d.id "
//+ "and l.id = 100 "
//+ "and l.id = 99 "
+ "order by l.id desc");
			
			rs = ps.executeQuery();
			List<Usuario> lista = new ArrayList<>();

			while (rs.next()) {
				Usuario obj = new Usuario();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("nome"));
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

	//Listar Usuarios da tela de Lancamentos ok.
    public List<Usuario> listarPorId(Integer id){
        PreparedStatement ps = null;
        ResultSet rs = null;
     try {
    	 ps = connection.prepareStatement(
        		"SELECT * "
        				+ "from lancamento as l, item as i, despesa as d "	
        				+ "where l.id = i.lancamento_id "
        				+ "and i.despesa_id = d.id "
        				+ "and l.id = ?");     
    	 ps.setInt(1, id);
        rs = ps.executeQuery();        
        List<Usuario> lista = new ArrayList<Usuario>();
        while(rs.next()){
            Usuario d = new Usuario();
            d.setNome(rs.getString("nome"));
            d.setId(rs.getInt("d.id"));
            d.setSenha(rs.getString("preco"));
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
}