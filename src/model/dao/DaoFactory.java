package model.dao;

import bd.BD;
import model.dao.implementacao.DespesaDaoJDBC;
import model.dao.implementacao.ItemDaoJDBC;
import model.dao.implementacao.LancamentoDaoJDBC;
import model.dao.implementacao.StatusDaoJDBC;
import model.dao.implementacao.TipoPagDaoJDBC;

public class DaoFactory {

	
	public static TipoPagDao criarTipoPagDao() {
		return new TipoPagDaoJDBC(BD.abrirConexao());
	}
	
	public static DespesaDao criarDespesaDao() {
		return new DespesaDaoJDBC(BD.abrirConexao());
	}
	
	public static ItemDao criarItemDao() {
		return new ItemDaoJDBC(BD.abrirConexao());
	}
	
	public static LancamentoDao criarLancamentoDao() {
		return new LancamentoDaoJDBC(BD.abrirConexao());
	}
	
	public static StatusDao criarStatusDao() {
		return new StatusDaoJDBC(BD.abrirConexao());
	}
}
