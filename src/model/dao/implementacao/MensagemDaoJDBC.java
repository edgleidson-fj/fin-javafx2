package model.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import bd.BD;
import bd.BDException;
import model.dao.MensagemDao;
import model.entidade.Mensagem;

public class MensagemDaoJDBC implements MensagemDao {

	private Connection connection;

	public MensagemDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public Mensagem buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM mensagem " + "WHERE Id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Mensagem obj = new Mensagem();
				obj.setId(rs.getInt("Id"));
				obj.setDescricao(rs.getString("descricao"));
				obj.setMostrar(rs.getString("mostrar"));
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
	public void atualizar(Mensagem obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
			"UPDATE mensagem " 
			+ "SET mostrar = ? " 
			+ "WHERE id = ? ");
			ps.setString(1, obj.getMostrar());
			ps.setInt(2, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void situacao(Mensagem obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
			"UPDATE mensagem "
			+ "SET descricao = ?, "
			+ "mostrar ='' " 
			+ "WHERE id = ? ");
			ps.setString(1, obj.getDescricao());
			ps.setInt(2, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
}
