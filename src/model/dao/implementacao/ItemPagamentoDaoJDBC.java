package model.dao.implementacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import bd.BD;
import bd.BDException;
import bd.BDIntegrityException;
import model.dao.ItemPagamentoDao;
import model.entidade.ItemPagamento;
import model.entidade.Lancamento;
import model.entidade.TipoPag;

public class ItemPagamentoDaoJDBC implements ItemPagamentoDao {

	private Connection connection;

	// Força injeção de pagendencia (Connection) dentro da Classe.
	public ItemPagamentoDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void inserir(ItemPagamento obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("INSERT INTO item_Pagamento "
					+ "(lancamento_id, tipopag_id, valor, nomePag) " + "VALUES  (?, ?, ?, ?) ",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, obj.getLancamento().getId());
			ps.setInt(2, obj.getTipoPag().getId());
			ps.setDouble(3, obj.getValor());
			ps.setString(4, obj.getNomePag());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void atualizar(ItemPagamento obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"" + "update item_pagamento set valor = ?  " + "where lancamento_id = ? " + "and tipopag_id = ?");
			ps.setDouble(1, obj.getValor());
			ps.setInt(2, obj.getLancamento().getId());
			ps.setInt(3, obj.getTipoPag().getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void excluirPorId(Integer lanId, Integer pagId) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"DELETE FROM item_pagamento " + "WHERE lancamento_id = ? " + "AND tipopag_id = ?");
			ps.setInt(1, lanId);
			ps.setInt(2, pagId);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public void limparItemPagamentoPorIdLan(Integer lanId) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement("DELETE FROM item_pagamento " + "WHERE lancamento_id = ? ");
			ps.setInt(1, lanId);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public ItemPagamento buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * " + "FROM lancamento " + "inner JOIN itemPagamento "
					+ "ON lancamento.id = itemPagamento.Lancamento_id " + "inner JOIN tipopag "
					+ "ON tipopag.id = itemPagamento.tipopag_id " + "WHERE lancamento.id= 100");
			rs = ps.executeQuery();
			if (rs.next()) {
				TipoPag pag = new TipoPag();
				Lancamento lan = new Lancamento();
				ItemPagamento obj = new ItemPagamento();
				instantiateTipoPag(rs);
				instantiateLancamento(rs);
				instantiateItemPagamento(rs, pag, lan);
				obj.getTipoPag().getId();
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

	public List<ItemPagamento> listarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT *  FROM item_pagamento " + "INNER JOIN tipopag "
							+ "ON item_pagamento.tipopag_id = tipopag.id " + "INNER JOIN lancamento "
							+ "ON lancamento.id = item_pagamento.lancamento_id "
							+ "where lancamento.id = item_pagamento.lancamento_id "
							+ "and item_pagamento.tipopag_id = tipopag.id " + "and lancamento.id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			List<ItemPagamento> lista = new ArrayList<ItemPagamento>();
			while (rs.next()) {
				TipoPag tp = new TipoPag();
				tp.setNome(rs.getString("tipopag.nome"));
				tp.setId(rs.getInt("tipopag.id"));

				ItemPagamento ip = new ItemPagamento();
				ip.setTipoPag(tp);
				ip.setValor(rs.getDouble("valor"));
				ip.setNomePag(rs.getString("nomePag"));
				lista.add(ip);
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
	public List<ItemPagamento> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM tipopag " + " ORDER BY nome ");
			rs = ps.executeQuery();
			List<ItemPagamento> lista = new ArrayList<>();
			while (rs.next()) {
				ItemPagamento obj = new ItemPagamento();
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
	
	public List<ItemPagamento> consultaPorPagamentoMesAtual(Integer id) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				Calendar datahoje = Calendar.getInstance();
				int mesAtual = datahoje.get(Calendar.MONTH)+1;
				
				ps = connection.prepareStatement(					
						"select i.nomePag, sum(i.valor) from item_pagamento i "
						+"inner join lancamento l "
						+"on l.id = i.lancamento_id "						
						+"where month(data) = "+mesAtual+" "
						+ "and year(data) = year(now()) "
						+ "and l.usuario_id = ? "
						+"group by i.tipopag_id "
						+"order by sum(i.valor) desc");						
						ps.setInt(1, id);
				rs = ps.executeQuery();
				List<ItemPagamento> lista = new ArrayList<ItemPagamento>();
				while (rs.next()) {
					ItemPagamento ip = new ItemPagamento();
					ip.setValor(rs.getDouble("sum(i.valor)"));
					ip.setNomePag(rs.getString("nomePag"));
					lista.add(ip);
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
	
		public List<ItemPagamento> consultaPorPagamentoAnoAtual(Integer id) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				Calendar datahoje = Calendar.getInstance();
				int anoAtual = datahoje.get(Calendar.YEAR);
				
				ps = connection.prepareStatement(					
						"select i.nomePag, sum(i.valor) from item_pagamento i "
						+"inner join lancamento l "
						+"on l.id = i.lancamento_id "
						+"where year(data) = "+anoAtual+" "
						+ "and l.usuario_id = ? "
						+"group by i.tipopag_id "
						+"order by sum(i.valor) desc");						
						ps.setInt(1, id);
				rs = ps.executeQuery();
				List<ItemPagamento> lista = new ArrayList<ItemPagamento>();
				while (rs.next()) {
					ItemPagamento ip = new ItemPagamento();
					ip.setValor(rs.getDouble("sum(i.valor)"));
					ip.setNomePag(rs.getString("nomePag"));
					lista.add(ip);
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

	private ItemPagamento instantiateItemPagamento(ResultSet rs, TipoPag pag, Lancamento lan) throws SQLException {
		ItemPagamento obj = new ItemPagamento();
		obj.setLancamento(lan);
		obj.setTipoPag(pag);
		return obj;
	}

	private TipoPag instantiateTipoPag(ResultSet rs) throws SQLException {
		TipoPag pag = new TipoPag();
		pag.setId(rs.getInt("Id"));
		pag.setNome(rs.getString("Nome"));
		return pag;
	}

	private Lancamento instantiateLancamento(ResultSet rs) throws SQLException {
		Lancamento lan = new Lancamento();
		lan.setId(rs.getInt("Id"));
		return lan;
	}
}
