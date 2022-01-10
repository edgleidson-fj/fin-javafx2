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
import model.dao.ItemDao;
import model.entidade.Item;

public class ItemDaoJDBC implements ItemDao {

	private Connection connection;
	
	public ItemDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void inserir(Item obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("INSERT INTO item " + "(lancamento_id, despesa_id) " + "VALUES  (?, ?) ",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, obj.getLancamento().getId());
			ps.setInt(2, obj.getDespesa().getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void atualizar(Item obj) {
		PreparedStatement ps = null;
		try {
			ps = connection
					.prepareStatement(" update despesa set ativo = ?  where lancamento_id = ? and despesa_id = ?");
			ps.setInt(1, obj.getLancamento().getId());
			ps.setInt(2, obj.getDespesa().getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void excluirPorId(Integer lanId, Integer despId) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("DELETE FROM item " + "WHERE lancamento_id = ? " + "AND despesa_id = ?");
			ps.setInt(1, lanId);
			ps.setInt(2, despId);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public List<Item> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM tipopag " + " ORDER BY nome ");
			rs = ps.executeQuery();
			List<Item> lista = new ArrayList<>();
			while (rs.next()) {
				Item obj = new Item();
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

	@Override
	public void limparItemPorIdLan(Integer lanId) {
		System.out.println("LanID: "+lanId);
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"DELETE FROM item "
			+ "WHERE lancamento_id = ? ");
			ps.setInt(1, lanId);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}	
	
	}
