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
import model.dao.DespesaDao;
import model.entidade.Despesa;

public class DespesaDaoJDBC implements DespesaDao {

	private Connection connection;

	public DespesaDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void inserir(Despesa obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"INSERT INTO despesa " 
							+ "(nome, quantidade, precoUnid, precoTotal) " 
							+ "VALUES  ( ?,?,?,?) ",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getNome());
			ps.setInt(2, obj.getQuantidade());
			ps.setDouble(3, obj.getPrecoUnid());
			ps.setDouble(4, obj.getPrecoTotal());
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
	public void atualizar(Despesa obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE despesa " 
			+ "SET nome = ?, " 
			+ "precoUnid = ?, "
			+ "quantidade = ?, "
			+ "precoTotal = ? " 
			+ "WHERE id = ? ");
			ps.setString(1, obj.getNome());
			ps.setDouble(2, obj.getPrecoUnid());
			ps.setInt(3, obj.getQuantidade());
			ps.setDouble(4, obj.getPrecoTotal());
			ps.setInt(5, obj.getId());
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
			ps = connection.prepareStatement("DELETE FROM despesa  " + "WHERE Id = ? ");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	// Sem uso.
	@Override
	public Despesa buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM despesa " + "WHERE Id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Despesa obj = new Despesa();
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

	// Sem uso.
	@Override
	public List<Despesa> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT *  " + "from lancamento as l, item as i, despesa as d "
					+ "where l.id = i.lancamento_id " + "and i.despesa_id = d.id " + "order by l.id desc");
			rs = ps.executeQuery();
			List<Despesa> lista = new ArrayList<>();
			while (rs.next()) {
				Despesa obj = new Despesa();
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

	public List<Despesa> listarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * " + "from lancamento as l, item as i, despesa as d "
					+ "where l.id = i.lancamento_id " 
					+ "and i.despesa_id = d.id " 
					+ "and l.id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			List<Despesa> lista = new ArrayList<Despesa>();
			while (rs.next()) {
				Despesa d = new Despesa();
				d.setNome(rs.getString("nome"));
				d.setId(rs.getInt("d.id"));
				d.setQuantidade(rs.getInt("d.quantidade"));
				d.setPrecoUnid(rs.getDouble("precoUnid"));
				d.setPrecoTotal(rs.getDouble("precoTotal"));
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