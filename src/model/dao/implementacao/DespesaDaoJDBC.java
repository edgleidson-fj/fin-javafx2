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
							+ "(nome, quantidade, precoUnid, precoTotal, desconto, precoBruto) " 
							+ "VALUES  ( ?,?,?,?,?,?) ",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getNome());
			ps.setInt(2, obj.getQuantidade());
			ps.setDouble(3, obj.getPrecoUnid());
			ps.setDouble(4, obj.getPrecoTotal());
			ps.setDouble(5, obj.getDescontoIndividual());
			ps.setDouble(6, obj.getPrecoBruto());
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
			+ "precoBruto = ?, "
			+ "precoTotal = ?, "
			+ "desconto = ? " 
			+ "WHERE id = ? ");
			ps.setString(1, obj.getNome());
			ps.setDouble(2, obj.getPrecoUnid());
			ps.setInt(3, obj.getQuantidade());
			ps.setDouble(4, obj.getPrecoBruto());
			ps.setDouble(5, obj.getPrecoTotal());
			ps.setDouble(6, obj.getDescontoIndividual());
			ps.setInt(7, obj.getId());
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
				d.setPrecoBruto(rs.getDouble("precoBruto"));
				d.setPrecoTotal(rs.getDouble("precoTotal"));
				d.setDescontoIndividual(rs.getDouble("d.desconto"));
				d.setAcrescimo(rs.getDouble("d.acrescimo"));
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
	
	public List<Despesa> consultaPorRankDespesaMesAtual(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Calendar datahoje = Calendar.getInstance();
			int mesAtual = datahoje.get(Calendar.MONTH)+1;
			
			ps = connection.prepareStatement(					
					"select d.nome,  sum(d.precoTotal), count(d.nome) from item i "
					+"inner join lancamento l "
					+"on l.id = i.lancamento_id "
					+"inner join despesa d "
					+"on d.id = i.despesa_id "	
					+"where month(data) = "+mesAtual+" "
					+ "and year(data) = year(now()) "
					+ "and l.status_id = 2 "					
					+ "and l.usuario_id = ? "
					+"group by d.nome "
					+"order by sum(d.precoTotal) desc ");
					ps.setInt(1, id);
			rs = ps.executeQuery();
			List<Despesa> lista = new ArrayList<Despesa>();
			while (rs.next()) {
				Despesa d = new Despesa();
				d.setPrecoTotal(rs.getDouble("sum(d.precoTotal)"));
				d.setNome(rs.getString("nome"));
				d.setQuantidade(rs.getInt("count(d.nome)"));
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

	public List<Despesa> consultaPorRankDespesaAnoAtual(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Calendar datahoje = Calendar.getInstance();
			int anoAtual = datahoje.get(Calendar.YEAR);
			
			ps = connection.prepareStatement(					
					"select d.nome,  sum(d.precoTotal), count(d.nome) from item i "
							+"inner join lancamento l "
							+"on l.id = i.lancamento_id "
							+"inner join despesa d "
							+"on d.id = i.despesa_id "	
					+"where year(data) = "+anoAtual+" "
					+ "and l.status_id = 2 "					
					+ "and l.usuario_id = ? "
					+"group by d.nome "
					+"order by sum(d.precoTotal) desc ");
				ps.setInt(1, id);
			rs = ps.executeQuery();
			List<Despesa> lista = new ArrayList<Despesa>();
			while (rs.next()) {
				Despesa d = new Despesa();
				d.setPrecoTotal(rs.getDouble("sum(d.precoTotal)"));
				d.setNome(rs.getString("nome"));
				d.setQuantidade(rs.getInt("count(d.nome)"));
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
	public void rateioDesconto(Despesa obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE despesa " 
			+ "SET precoTotal = ?, "
			+ "desconto = ? " 
			+ "WHERE id = ? ");
			ps.setDouble(1, obj.getPrecoTotal());
			ps.setDouble(2, obj.getDescontoIndividual());
			ps.setInt(3, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
	
	@Override
	public void rateioAcrescimo(Despesa obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE despesa " 
			+ "SET precoTotal = ?, "
			+ "acrescimo = ? " 
			+ "WHERE id = ? ");
			ps.setDouble(1, obj.getPrecoTotal());
			ps.setDouble(2, obj.getAcrescimo());
			ps.setInt(3, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
}