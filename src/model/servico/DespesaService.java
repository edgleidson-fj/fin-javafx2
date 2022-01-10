package model.servico;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DespesaDao;
import model.entidade.Despesa;

public class DespesaService {

	private DespesaDao dao = DaoFactory.criarDespesaDao();

	public void salvar(Despesa obj) {
		dao.inserir(obj);
	}

	public void atualizar(Despesa obj) {
		dao.atualizar(obj);
	}

	public void excluir(Despesa obj) {
		dao.excluirPorId(obj.getId());
	}

	public List<Despesa> listarPorId(Integer id) {
		return dao.listarPorId(id);
	}

	public List<Despesa> consultaPorRankDespesaMesAtual(Integer id) {
		return dao.consultaPorRankDespesaMesAtual(id);
	}

	public List<Despesa> consultaPorRankDespesaAnoAtual(Integer id) {
		return dao.consultaPorRankDespesaAnoAtual(id);
	}

	public void rateioDesconto(Despesa obj) {
		dao.rateioDesconto(obj);
	}

	public void rateioAcrescimo(Despesa obj) {
		dao.rateioAcrescimo(obj);
	}
}
