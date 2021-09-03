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
import model.dao.LancamentoDao;
import model.entidade.Lancamento;
import model.entidade.Status;
import model.entidade.TipoPag;

public class LancamentoDaoJDBC implements LancamentoDao {

	private Connection connection;

	public LancamentoDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void inserir(Lancamento obj) {
		PreparedStatement ps = null;
			try {
			ps = connection.prepareStatement(
					"INSERT INTO lancamento "
						+ "(referencia, total, data, usuario_id) "
							+ "VALUES  (?, ?, ?, ?) ",
							Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, obj.getReferencia());
			ps.setDouble(2, obj.getTotal());
			ps.setDate(3, new java.sql.Date(obj.getData().getTime()));
			ps.setInt(4, obj.getUsuario().getId());			
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

	//Atualizar valor Total ao adicionar Itens no Lançamento. ok
	@Override
	public void atualizar(Lancamento obj) {
		PreparedStatement ps = null;
		try {
			ps = connection.prepareStatement(
					"UPDATE lancamento " 
					+ "SET total = ? "
					+ "WHERE Id = ? ");
			ps.setDouble(1, obj.getTotal());
			ps.setInt(2, obj.getId());
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
			ps = connection.prepareStatement("DELETE FROM lancamento  " + "WHERE Id = ? ");
			ps.setInt(1, id);
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDIntegrityException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}

	@Override
	public Lancamento buscarPorId(Integer id) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement("SELECT * FROM lancamento " + "WHERE Id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Lancamento obj = new Lancamento();
				obj.setId(rs.getInt("Id"));
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
	public List<Lancamento> buscarTudo() {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM Lancamento l "
					+ "INNER JOIN Status s "
					+ "ON l.status_id = s.id "
					+ "INNER JOIN TipoPag t "
					+ "ON l.tipoPag_id = t.id "
				+ "INNER JOIN Usuario u "
				+ "ON l.usuario_id = u.usuarioid "
				+ "WHERE u.logado = 'S' "
				+ "AND Year(data) >= Year(now())-2 "
					+ "ORDER BY l.id DESC"); 			
		rs = ps.executeQuery();
			List<Lancamento> lista = new ArrayList<>();			
			while (rs.next()) {
				TipoPag pag = new TipoPag();
				pag.setId(rs.getInt("t.id"));
				pag.setNome(rs.getString("t.nome"));	
				Status status = new Status();
				status.setNome(rs.getString("s.nome"));	
				Lancamento obj = new Lancamento();
				obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
				obj.setId(rs.getInt("id"));
				obj.setReferencia(rs.getString("referencia"));
				obj.setDesconto(rs.getDouble("desconto"));
				obj.setAcrescimo(rs.getDouble("acrescimo"));
				obj.setTotal(rs.getDouble("total"));
				obj.setTipoPagamento(pag);
				obj.setObs(rs.getString("obs"));
				obj.setStatus(status);
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
		public List<Lancamento> buscarTudoQuitado() {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT * FROM Lancamento l "								
						+ "INNER JOIN TipoPag t "
						+ "ON l.tipoPag_id = t.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = l.usuario_Id "
						+ "WHERE status_id = 2 "
						+ "AND u.logado = 'S' "
						+ "AND Year(data) >= Year(now())-2 "
						+ "ORDER BY data DESC");			
				rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();				
				while (rs.next()) {
				TipoPag pag = new TipoPag();
					pag.setId(rs.getInt("id"));
					pag.setNome(rs.getString("t.nome"));					
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setTotal(rs.getDouble("total"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setObs(rs.getString("obs"));
					obj.setTipoPagamento(pag);				
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
			
		//Listar todos Lancamento (Em Aberto) ok.
		@Override
		public List<Lancamento> buscarTudoEmAberto() {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT * FROM Lancamento "
								+ "INNER JOIN Status s "
								+ "ON lancamento.status_id = s.id "
								+ "INNER JOIN Usuario u "
								+ "ON u.usuarioId = lancamento.usuario_Id "
						+ "WHERE u.logado = 'S' "
						+ "AND (status_id = 1 OR status_id = 3) "
						+ "ORDER BY data ASC");					
				rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();				
				while (rs.next()) {
					Status status = new Status();
					status.setId(rs.getInt("s.id"));
					status.setNome(rs.getString("s.nome"));
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setTotal(rs.getDouble("total"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setObs(rs.getString("obs"));
					obj.setStatus(status);
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
	
	// Cancelar LAncamento ok
	@Override
	public void cancelar(Lancamento obj) {
		PreparedStatement ps = null;
		int status = 4; //Cancelado.
		try {
			ps = connection.prepareStatement("UPDATE lancamento " 
		+ "SET status_id = '"+status+"' "
				+ "WHERE Id = ? ");
			ps.setInt(1, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
	
	// Confirmar Lancamento (Quitado) ok
	@Override
	public void confirmarLanQuitado(Lancamento obj) {
		PreparedStatement ps = null;
		int status = 2; //Pago.
		try {
			ps = connection.prepareStatement("UPDATE lancamento " 
		+ "SET tipopag_id = ?, "
		+"status_id = '"+status+"', "
		+ "finalizado = 'S', "
		+ "desconto = ?,"
		+ "total = ? "			
		+ "WHERE Id = ? ");
			ps.setInt(1, obj.getTipoPagamento().getId());
			ps.setDouble(2, obj.getDesconto());
			ps.setDouble(3, obj.getTotal());
			ps.setInt(4, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}
	
	@Override
	public void confirmarPagamento(Lancamento obj) {
		PreparedStatement ps = null;
		int status = 2; //Pago.
		try {
			ps = connection.prepareStatement("UPDATE lancamento " 
		+ "SET tipopag_id = ?, "
		+"status_id = '"+status+"', "
		+"total = ?, "
		+"desconto = ?, "
		+"acrescimo = ?, "
		+ "data = ? "
		+ "WHERE Id = ? ");
			ps.setInt(1, obj.getTipoPagamento().getId());
			ps.setDouble(2, obj.getTotal());
			ps.setDouble(3, obj.getDesconto());
			ps.setDouble(4, obj.getAcrescimo());
			ps.setDate(5, new java.sql.Date(obj.getData().getTime()));
			ps.setInt(6, obj.getId());
			ps.executeUpdate();
		} catch (SQLException ex) {
			new BDException(ex.getMessage());
		} finally {
			BD.fecharStatement(ps);
		}
	}


	// Confirmar Lancamento (A Pagar) ok
		@Override
		public void confirmarLanAPagar(Lancamento obj) {
			PreparedStatement ps = null;
			int status = 1; //Em Aberto.
			try {
				ps = connection.prepareStatement(
			"UPDATE lancamento " 
			+ "SET status_id = '"+status+"', "
			+ "finalizado = 'S', "
			+ "obs = ? "
			+ "WHERE Id = ? ");
				ps.setString(1, obj.getObs());
				ps.setInt(2, obj.getId());
				ps.executeUpdate();
			} catch (SQLException ex) {
				new BDException(ex.getMessage());
			} finally {
				BD.fecharStatement(ps);
			}
		}
		
		// Confirmar Lancamento (A Pagar) ok
				@Override
				public void lanConfig(Lancamento obj) {
					PreparedStatement ps = null;
					try {
						ps = connection.prepareStatement(
					"UPDATE lancamento " 
					+ "SET referencia = ?, "
					+ "data = ?, "
					+ "tipopag_id = ?, "
					+ "total = ?, "
					+ "obs = ?,"
					+ "desconto = ?,"
					+ "acrescimo = ? "
					+ "WHERE Id = ? ");
						ps.setString(1, obj.getReferencia());
						ps.setDate(2, new java.sql.Date(obj.getData().getTime()));
						ps.setInt(3, obj.getTipoPagamento().getId());
						ps.setDouble(4, obj.getTotal());
						ps.setString(5, obj.getObs());
						ps.setDouble(6, obj.getDesconto());
						ps.setDouble(7, obj.getAcrescimo());
						ps.setInt(8, obj.getId());
						ps.executeUpdate();
					} catch (SQLException ex) {
						new BDException(ex.getMessage());
					} finally {
						BD.fecharStatement(ps);
					}
				}
	
		//Listar todos Lançamentos (Em Aberto do mês) ok
		public ArrayList<Lancamento> buscarContasAPagarMesAtual() {	
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
			Calendar datahoje = Calendar.getInstance();
			int mesAtual = datahoje.get(Calendar.MONTH);			
			switch (mesAtual) {
			case 0:
			ps = connection.prepareStatement("SELECT * FROM lancamento " 
					+ "INNER JOIN Status s "						
					+ "ON lancamento.status_id = s.id "
					+ "INNER JOIN Usuario u "
					+ "ON u.usuarioId = lancamento.usuario_Id "
			+"WHERE u.logado = 'S' AND (Month(data) = '01' and Status_id = 1 and Year(data) = Year(now()) "
			+ "OR status_id = 3) "
			+ "ORDER BY data ASC ");
			break;
			case 1:
				ps = connection.prepareStatement("SELECT * FROM lancamento "
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '02' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 2:
				ps = connection.prepareStatement("SELECT * FROM lancamento "
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '03' and Status_id = 1 and Year(data) = Year(now()) "
				+ " OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 3:
				ps = connection.prepareStatement("SELECT * FROM lancamento "
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '04' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 4:
				ps = connection.prepareStatement("SELECT * FROM lancamento "
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '05' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 5:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '06' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 6:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '07' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 7:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '08' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			case 8:
				ps = connection.prepareStatement("SELECT * FROM lancamento "
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '09' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3) "
				+ "ORDER BY data ASC ");
				break;
			case 9:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '10' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3) "
				+ "ORDER BY data ASC ");
				break;
			case 10:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '11' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			default:
				ps = connection.prepareStatement("SELECT * FROM lancamento " 
						+ "INNER JOIN Status s "						
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
				+"WHERE u.logado = 'S' AND (Month(data) =  '12' and Status_id = 1 and Year(data) = Year(now()) "
				+ "OR status_id = 3)  "
				+ "ORDER BY data ASC ");
				break;
			}
			rs = ps.executeQuery();
			ArrayList<Lancamento> itens = new ArrayList<Lancamento>();
			while (rs.next()) {
				Status status = new Status();	
				status.setId(rs.getInt("s.id"));
				status.setNome(rs.getString("s.nome"));
				Lancamento l = new Lancamento(); 
				l.setId(rs.getInt("lancamento.id"));
				l.setReferencia(rs.getString("lancamento.referencia"));
				l.setData(new java.util.Date(rs.getTimestamp("lancamento.data").getTime()));
				l.setTotal(rs.getDouble("lancamento.total"));
				l.setDesconto(rs.getDouble("lancamento.desconto"));
				l.setAcrescimo(rs.getDouble("lancamento.acrescimo"));
				l.setObs(rs.getString("lancamento.obs"));
				l.setStatus(status);
		        itens.add(l);
			}			
			return itens;
		}
			catch(SQLException ex) {
				return null;
				}
			finally {
				BD.fecharStatement(ps);
				BD.fecharResultSet(rs);
			}
			}
		
		public ArrayList<Lancamento> buscarContasQuitadoMesAtual() {	
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
			Calendar datahoje = Calendar.getInstance();
			int mesAtual = datahoje.get(Calendar.MONTH);			
			switch (mesAtual) {
			case 0:
			ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
			+"WHERE usuario.logado = 'S' AND  Month(data) =  '01' and Status_id = 2 and Year(data) = Year(now()) "
			+ "AND tipopag.id = tipopag_id "
			+ "AND usuario.usuarioId = usuario_id "
			+ "ORDER BY data DESC ");
			break;
			case 1:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '02' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 2:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '03' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 3:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario " 
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '04' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 4:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '05' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "		
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 5:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '06' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "		
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 6:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '07' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "		
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 7:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '08' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "		
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 8:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '09' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "		
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			case 9:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
					+"WHERE usuario.logado = 'S' AND Month(data) =  '10' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "		
				+ "ORDER BY data DESC ");
				break;
			case 10:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '11' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			default:
				ps = connection.prepareStatement("SELECT * FROM lancamento, tipopag, usuario "
				+"WHERE usuario.logado = 'S' AND  Month(data) =  '12' and Status_id = 2 and Year(data) = Year(now()) "
				+ "AND tipopag.id = tipopag_id "
				+ "AND usuario.usuarioId = usuario_id "
				+ "ORDER BY data DESC ");
				break;
			}	
			rs = ps.executeQuery();
			ArrayList<Lancamento> itens = new ArrayList<Lancamento>();
			while (rs.next()) {
				TipoPag tp = new TipoPag();
				tp.setId(rs.getInt("tipopag.id"));
				tp.setNome(rs.getString("tipopag.nome"));
				Lancamento l = new Lancamento(); 
				l.setId(rs.getInt("lancamento.id"));
				l.setReferencia(rs.getString("lancamento.referencia"));
				l.setData(new java.util.Date(rs.getTimestamp("lancamento.data").getTime()));				
				l.setTotal(rs.getDouble("lancamento.total"));
				l.setDesconto(rs.getDouble("lancamento.desconto"));
				l.setAcrescimo(rs.getDouble("lancamento.acrescimo"));
				l.setObs(rs.getString("lancamento.obs"));
		        l.setTipoPagamento(tp);
				itens.add(l);
			}			
			return itens;
		}
			catch(SQLException ex) {
				return null;
				}
			finally {
				BD.fecharStatement(ps);
				BD.fecharResultSet(rs);
			}
			}	
		
		@Override
		public List<Lancamento> buscarContasEmAbertoPeriodo(String dataInicial, String dataFinal) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT * FROM lancamento "
						+ "INNER JOIN status s "
						+ "ON lancamento.status_id = s.id "
						+ "INNER JOIN Usuario u "			
						+ "ON u.usuarioId = lancamento.usuario_Id "
						+ "WHERE u.logado = 'S' "
						+ "AND data >=  '"+dataInicial+"' "
						+ "AND data <= '"+dataFinal+"' "
						+ "AND (status_id = 1 "
						+ "OR status_id = 3) "
						/*+ "OR (status_id = 3 "
						+ "AND u.logado = 'S') "*/
						+ "ORDER BY data ASC");  
				rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();				
				while (rs.next()) {
					Status status = new Status();
					status.setId(rs.getInt("s.id"));
					status.setNome(rs.getString("s.nome"));
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setTotal(rs.getDouble("total"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setObs(rs.getString("obs"));
					obj.setStatus(status);
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
		public List<Lancamento> buscarContasQuitadoPeriodo(String dataInicial, String dataFinal) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT * FROM lancamento "
						+ "INNER JOIN tipopag "
						+ "ON lancamento.tipopag_id = tipopag.id "+ "INNER JOIN Usuario u "
						+ "ON u.usuarioId = lancamento.usuario_Id "
						+ "WHERE u.logado = 'S' "
						+ "AND data >=  '"+dataInicial+"' "
						+ "AND data <= '"+dataFinal+"' "
						+ "AND status_id = 2 "
						+ "ORDER BY data");  
				rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();				
				while (rs.next()) {
				TipoPag pag = new TipoPag();
					pag.setId(rs.getInt("tipopag.id"));
					pag.setNome(rs.getString("tipopag.nome"));
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setTotal(rs.getDouble("total"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setObs(rs.getString("obs"));
					obj.setTipoPagamento(pag);
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
				
		//Rotinas Automáticas
		@Override
		public void cancelamentoAutomatico(Lancamento obj) {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(
						"UPDATE lancamento " 
						+ "SET status_id = 4 "
						+ "WHERE finalizado = 'N' "
						+ "OR total = 0");
				ps.executeUpdate();
			} catch (SQLException ex) {
				new BDException(ex.getMessage());
			} finally {
				BD.fecharStatement(ps);
			}
		}
		
		@Override
		public void vencimentoAutomatico(Lancamento obj) {
			PreparedStatement ps = null;
			try {
				Calendar datahoje = Calendar.getInstance();
				int mesAtual = datahoje.get(Calendar.MONTH)+1;
				int diaAtual = datahoje.get(Calendar.DAY_OF_MONTH);
				ps = connection.prepareStatement(
						"UPDATE lancamento " 
						+ "SET status_id = 3 "
						+ "WHERE status_id = 1 "
						+ "AND Month(data) =  '"+mesAtual+"' "
						+ "AND Day(data) < '"+diaAtual+"' "
						+ "AND Year(data) = Year(now())"
						+ "OR (Month(data) < '"+mesAtual+"' "
						+ "AND Year(data) = Year(now()) "
						+ "AND status_id = 1)");
				ps.executeUpdate();
			} catch (SQLException ex) {
				new BDException(ex.getMessage());
			} finally {
				BD.fecharStatement(ps);
			}
		}
		
		//Não está excluindo por causa do vinculo do Usuário no Lançamento.
		@Override
		public void exclusaoAutomatico(Lancamento obj) {
			PreparedStatement ps = null;
			try {
				ps = connection.prepareStatement(
						"DELETE lancamento " 
						+ "FROM lancamento "
						+ "WHERE total <= ? ");
				ps.setDouble(1, obj.getTotal());
				ps.executeUpdate();
			} catch (SQLException ex) {
				new BDException(ex.getMessage());
			} finally {
				BD.fecharStatement(ps);
			}
		}
		//-------------------------------
		
		//Tela (Todas Contas)
		@Override
		public List<Lancamento> buscarLanPorId(Integer id) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT * FROM Lancamento l "
						+ "INNER JOIN Status s "
						+ "ON l.status_id = s.id "
						+ "INNER JOIN TipoPag t "
						+ "ON l.tipoPag_id = t.id "
					+ "INNER JOIN Usuario u "
					+ "ON l.usuario_id = u.usuarioid "
					+ "WHERE u.logado = 'S' "
					+ "AND l.id = ? "); 		
				ps.setDouble(1, id);
			rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();			
				while (rs.next()) {
					TipoPag pag = new TipoPag();
					pag.setId(rs.getInt("t.id"));
					pag.setNome(rs.getString("t.nome"));	
					Status status = new Status();
					status.setNome(rs.getString("s.nome"));	
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setTotal(rs.getDouble("total"));
					obj.setTipoPagamento(pag);
					obj.setObs(rs.getString("obs"));
					obj.setStatus(status);
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
		
		//Tela (Todas Contas)
		@Override
		public List<Lancamento> buscarPorReferenciaOuDespesa(String refOuDespesa) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = connection.prepareStatement(
						"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, t.nome, t.id, s.nome FROM Lancamento l "								
						+"INNER JOIN TipoPag t "
						+"ON l.tipoPag_id = t.id " 
						+"INNER JOIN Usuario u "
						+"ON u.usuarioId = l.usuario_Id " 
						+"INNER JOIN Item i "
						+"ON i.Lancamento_id = l.id "
						+"INNER JOIN Despesa d "
						+"on d.id = i.despesa_id "						
						+ "INNER JOIN Status s "
						+ "ON l.status_id = s.id "						
						+"WHERE u.logado = 'S' "
						+"AND Year(data) >= Year(now())-2 "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+"ORDER BY l.id DESC "
						+"LIMIT 2000");				
				rs = ps.executeQuery();
				List<Lancamento> lista = new ArrayList<>();			
				while (rs.next()) {
					TipoPag pag = new TipoPag();
					pag.setId(rs.getInt("t.id"));
					pag.setNome(rs.getString("t.nome"));	
					Status status = new Status();
					status.setNome(rs.getString("s.nome"));	
					Lancamento obj = new Lancamento();
					obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
					obj.setId(rs.getInt("id"));
					obj.setReferencia(rs.getString("referencia"));
					obj.setDesconto(rs.getDouble("desconto"));
					obj.setAcrescimo(rs.getDouble("acrescimo"));
					obj.setTotal(rs.getDouble("total"));
					obj.setTipoPagamento(pag);
					obj.setObs(rs.getString("obs"));
					obj.setStatus(status);
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
		
		//Tela (Todas Contas Quitadas)
				@Override
				public List<Lancamento> buscarPorReferenciaOuDespesaQuitado(String refOuDespesa) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, t.nome FROM Lancamento l "								
								+"INNER JOIN TipoPag t "
								+"ON l.tipoPag_id = t.id " 
								+"INNER JOIN Usuario u "
								+"ON u.usuarioId = l.usuario_Id " 
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "						
								+ "INNER JOIN Status s "
								+ "ON l.status_id = s.id "						
								+"WHERE u.logado = 'S' "
								+"AND s.id = 2 "
								+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
								+"ORDER BY data DESC "
								+"LIMIT 2000" );				
						rs = ps.executeQuery();
						List<Lancamento> lista = new ArrayList<>();				
						while (rs.next()) {
						TipoPag pag = new TipoPag();
							pag.setId(rs.getInt("id"));
							pag.setNome(rs.getString("t.nome"));					
							Lancamento obj = new Lancamento();
							obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
							obj.setId(rs.getInt("id"));
							obj.setReferencia(rs.getString("referencia"));
							obj.setTotal(rs.getDouble("total"));
							obj.setDesconto(rs.getDouble("desconto"));
							obj.setAcrescimo(rs.getDouble("acrescimo"));
							obj.setObs(rs.getString("obs"));
							obj.setTipoPagamento(pag);				
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
				
				//Tela (Contas Quitadas Mês Atual)
				@Override
				public List<Lancamento> buscarPorReferenciaOuDespesaQuitadoMesAtual(String refOuDespesa) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
					Calendar datahoje = Calendar.getInstance();
					int mesAtual = datahoje.get(Calendar.MONTH);			
					switch (mesAtual) {
					case 0:
					ps = connection.prepareStatement(
							"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
							+"INNER JOIN TipoPag "
							+"ON l.tipoPag_id = tipopag.id " 
							+"INNER JOIN Usuario "
							+"ON usuario.usuarioId = l.usuario_Id " 								
							+"INNER JOIN Item i "
							+"ON i.Lancamento_id = l.id "
							+"INNER JOIN Despesa d "
							+"on d.id = i.despesa_id "
					+"WHERE usuario.logado = 'S' AND  Month(data) =  '01' and Status_id = 2 and Year(data) = Year(now()) "
					+ "AND tipopag.id = tipopag_id "		
					+ "AND usuario.usuarioId = usuario_id "
					+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
					+ "ORDER BY data DESC ");
					break;
					case 1:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '02' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 2:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '03' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 3:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '04' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 4:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '05' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 5:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '06' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 6:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '07' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 7:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '08' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 8:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '09' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 9:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '10' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					case 10:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '11' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					default:
						ps = connection.prepareStatement(
								"SELECT distinct(l.id), l.referencia, l.data, l.total, l.acrescimo, l.desconto, l.obs, tipopag.nome FROM Lancamento l "
								+"INNER JOIN TipoPag "
								+"ON l.tipoPag_id = tipopag.id " 
								+"INNER JOIN Usuario "
								+"ON usuario.usuarioId = l.usuario_Id " 								
								+"INNER JOIN Item i "
								+"ON i.Lancamento_id = l.id "
								+"INNER JOIN Despesa d "
								+"on d.id = i.despesa_id "
						+"WHERE usuario.logado = 'S' AND  Month(data) =  '12' and Status_id = 2 and Year(data) = Year(now()) "
						+ "AND tipopag.id = tipopag_id "		
						+ "AND usuario.usuarioId = usuario_id "
						+"AND (d.nome like '%"+refOuDespesa+"%' OR l.referencia like '%"+refOuDespesa+"%') "
						+ "ORDER BY data DESC ");
						break;
					}
						rs = ps.executeQuery();
						List<Lancamento> lista = new ArrayList<>();				
						while (rs.next()) {
						TipoPag pag = new TipoPag();
							pag.setId(rs.getInt("id"));
							pag.setNome(rs.getString("tipopag.nome"));					
							Lancamento obj = new Lancamento();
							obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
							obj.setId(rs.getInt("id"));
							obj.setReferencia(rs.getString("referencia"));
							obj.setTotal(rs.getDouble("total"));
							obj.setDesconto(rs.getDouble("desconto"));
							obj.setAcrescimo(rs.getDouble("acrescimo"));
							obj.setObs(rs.getString("obs"));
							obj.setTipoPagamento(pag);				
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
				
				// Tela Contas Em Aberto
				@Override
				public List<Lancamento> buscarPorReferenciaOuDespesaEmAberto(String refOuDespesa) {
					PreparedStatement ps = null;
					ResultSet rs = null;
					try {
						ps = connection.prepareStatement(
								"SELECT * FROM Lancamento "
										+ "INNER JOIN Status s "
										+ "ON lancamento.status_id = s.id "
										+ "INNER JOIN Usuario u "
										+ "ON u.usuarioId = lancamento.usuario_Id "
										+"INNER JOIN Item i "
										+"ON i.Lancamento_id = lancamento.id "
										+"INNER JOIN Despesa d "
										+"on d.id = i.despesa_id "
								+ "WHERE u.logado = 'S' "
								+ "AND (status_id = 1 OR status_id = 3) "
								+"AND (d.nome like '%"+refOuDespesa+"%' OR lancamento.referencia like '%"+refOuDespesa+"%') "
								+ "ORDER BY data ASC");					
						rs = ps.executeQuery();
						List<Lancamento> lista = new ArrayList<>();				
						while (rs.next()) {
							Status status = new Status();
							status.setId(rs.getInt("s.id"));
							status.setNome(rs.getString("s.nome"));
							Lancamento obj = new Lancamento();
							obj.setData(new java.util.Date(rs.getTimestamp("data").getTime()));
							obj.setId(rs.getInt("id"));
							obj.setReferencia(rs.getString("referencia"));
							obj.setTotal(rs.getDouble("total"));
							obj.setDesconto(rs.getDouble("desconto"));
							obj.setAcrescimo(rs.getDouble("acrescimo"));
							obj.setObs(rs.getString("obs"));
							obj.setStatus(status);
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
}