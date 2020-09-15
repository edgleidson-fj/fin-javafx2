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
import model.entidade.Despesa;
import model.entidade.Item;
import model.entidade.Lancamento;

public class ItemDaoJDBC implements ItemDao {

	private Connection connection;

	// Força injeção de dependencia (Connection) dentro da Classe.
	public ItemDaoJDBC(Connection connection) {
		this.connection = connection;
	}
//-------------------------------------------------------------------
	//ok
	@Override
	public void inserir(Item obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"INSERT INTO item "
						+ "(lancamento_id, despesa_id) "
							+ "VALUES  (?, ?) ",
							Statement.RETURN_GENERATED_KEYS);

			ps.setInt(1, obj.getLancamento().getId());
			ps.setInt(2, obj.getDespesa().getId());
	
			ps.executeUpdate();

		/*	if (linhasAfetadas > 0) {
				ResultSet rs = ps.getGeneratedKeys(); // ID gerado no Insert.
				if (rs.next()) {
					int id = rs.getInt(1); // ID do Insert.
					obj.setId(id);
				}
				BD.fecharResultSet(rs);
			} else {
				throw new BDException("Erro no INSERT, nenhuma linha foi afetada!");
			}*/
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
		//	ps = connection.prepareStatement("UPDATE item " + "SET lancamaneto_id = 1 " + "WHERE Id = ? ");
			ps = connection.prepareStatement(" update despesa set ativo = ?  where lancamento_id = ? and despesa_id = ?");

		//	ps.setString(1, obj.getNome());
	//		ps.setInt(2, obj.getId());
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
			ps = connection.prepareStatement("DELETE FROM item WHERE lancamento_id = ? AND despesa_id = ?");
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
	public Item buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
	//		ps = connection.prepareStatement("SELECT * FROM item " + "WHERE despesa_Id = ? ");
			
			ps = connection.prepareStatement(
			"SELECT * "
		+	"FROM lancamento "
		+	"inner JOIN item "
		+	"ON lancamento.id = item.Lancamento_id "
		+	"inner JOIN despesa "
		+	"ON despesa.id = item.despesa_id "
		+	"WHERE lancamento.id= 100");
			
			
	//		ps.setInt(1, id);
			rs = ps.executeQuery();

			if (rs.next()) {
				Despesa dep =  new Despesa();
				Lancamento lan = new Lancamento();
				Item obj = new Item();
			//	obj.setId(rs.getInt("Id"));
			//	obj.setNome(rs.getString("nome"));
			//	obj.setDespesa(rs.getInt(""));
			//	obj.getId().setDespesa(dep);;
				
				instantiateDespesa(rs);
				instantiateLancamento(rs);
				instantiateItem(rs, dep, lan);
				
				obj.getDespesa().getId();
				
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
	public List<Item> listarPorId(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
	/*		st = connection.prepareStatement(
					"SELECT Item.*,despesa.Nome as DNome "
					+ "FROM Item INNER JOIN despesa "
					+ "ON Item.Despesa_Id = despesa.Id "
					+ "WHERE Item.despesa_Id = ?");*/
			
			st = connection.prepareStatement(
					"SELECT * "
					+ "FROM Despesa inner JOIN item "
					+ "ON Item.Despesa_Id = despesa.Id "
					+ "WHERE Item.despesa_Id = ?");
			
			st.setInt(1, id);
	/*		rs = st.executeQuery();
			if (rs.next()) {*/
								
				rs = st.executeQuery();
				List<Item> lista = new ArrayList<>();

				while (rs.next()) {
					//Item obj = new Item();
			//		obj.setId(rs.getInt("Id"));
			//		obj.setNome(rs.getString("nome"));
								Despesa dep = instantiateDespesa(rs);
					//		Lancamento lan = instantiateLancamento(rs);
					//		Item obj = instantiateItem(rs, dep, lan);
//							Item obj = instantiateItem(rs, dep);
						//	return obj;
					
					
				//	lista.add(obj);
				}
				return lista;
							
			}
		catch (SQLException e) {
			throw new BDException(e.getMessage());
		}
		finally {
			BD.fecharStatement(st);
			BD.fecharResultSet(rs);
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
		//		obj.setId(rs.getInt("Id"));
		//		obj.setNome(rs.getString("nome"));
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
	
	//--------------------------------------------------------------
	private Item instantiateItem(ResultSet rs, Despesa dep, Lancamento lan) throws SQLException {
//	private Item instantiateItem(ResultSet rs, Despesa dep) throws SQLException {
		Item obj = new Item();
		obj.setLancamento(lan);
		obj.setDespesa(dep);
		return obj;
	}

	private Despesa instantiateDespesa(ResultSet rs) throws SQLException {
		Despesa dep = new Despesa();
		dep.setId(rs.getInt("Id"));
		//dep.setId(rs.getInt("despesa_id"));
		dep.setNome(rs.getString("Nome"));
		return dep;
	}
	
	private Lancamento instantiateLancamento(ResultSet rs) throws SQLException {
		Lancamento lan = new Lancamento();
		lan.setId(rs.getInt("Id"));
		return lan;
}
	
	//-----------------------------------------------------------------------------------------------------------------------
	
	}
