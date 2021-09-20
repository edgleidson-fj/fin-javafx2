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
import gui.util.Alertas;
import javafx.scene.control.Alert.AlertType;
import model.dao.TipoPagDao;
import model.entidade.ItemPagamento;
import model.entidade.TipoPag;
import model.entidade.TipoPag;

public class TipoPagDaoJDBC implements TipoPagDao {

	private Connection connection;

	// Força injeção de dependencia (Connection) dentro da Classe.
	public TipoPagDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override //ok
	public void inserir(TipoPag obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"INSERT INTO tipopag "
							+ "(nome) " 
							+ "VALUES  (?) ",
					Statement.RETURN_GENERATED_KEYS);

			ps.setString(1, obj.getNome());
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
	public void atualizar(TipoPag obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE tipopag " 
							+ "SET nome = ? " 
							+ "WHERE Id = ? ");
			ps.setString(1, obj.getNome());
			ps.setInt(2, obj.getId());
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
					"DELETE FROM tipopag  " 
				 			+ "WHERE Id = ? ");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
			Alertas.mostrarAlerta("Erro ao deletar", null, "Não é possível remover TIPO DE PAGAMENTO, que já foi vinculado à lançamento.", AlertType.ERROR);
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override //ok
	public TipoPag buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM tipopag " 
							+ "WHERE Id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				TipoPag obj = new TipoPag();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("nome"));
				return obj;
			}
			return null;
		} 
		catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} 
		finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	}

	@Override //ok
	public List<TipoPag> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM tipopag "
					+ "WHERE id > 0 " 
							+ " ORDER BY id ");
			rs = ps.executeQuery();
			List<TipoPag> lista = new ArrayList<>();

			while (rs.next()) {
				TipoPag obj = new TipoPag();
				obj.setId(rs.getInt("Id"));
				obj.setNome(rs.getString("nome"));
				lista.add(obj);
			}
			return lista;
		} 
		catch (SQLException ex) {
			throw new BDException(ex.getMessage());
		} 
		finally {
			BD.fecharStatement(ps);
			BD.fecharResultSet(rs);
		}
	}
	
	//Teste
	public List<TipoPag> listarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * from item_pagamento as ip, lancamento as l , tipopag as tp "
					+ "where l.id = ip.lancamento_id " 
					+ "and ip.tipopag_id = tp.id " 
					+ "and l.id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			List<TipoPag> lista = new ArrayList<TipoPag>();
			while (rs.next()) {
				TipoPag tp = new TipoPag();
				tp.setNome(rs.getString("tp.nome"));
				tp.setId(rs.getInt("tp.id"));				
				lista.add(tp);
				
				ItemPagamento ip = new ItemPagamento();
				ip.setTipoPag(tp);
				ip.setValor(rs.getDouble("ip.valor"));
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
