package model.entidade.pk;

import java.io.Serializable;

import model.entidade.Despesa;
import model.entidade.Lancamento;

public class ItemPK implements Serializable{
	private static final long serialVersionUID = 1L;

	private Lancamento lancamento;
	
	private Despesa despesa;
	
	public Lancamento getLancamento() {
		return lancamento;
	}
	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	public Despesa getDespesa() {
		return despesa;
	}
	public void setDespesa(Despesa despesa) {
		this.despesa = despesa;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((despesa == null) ? 0 : despesa.hashCode());
		result = prime * result + ((lancamento == null) ? 0 : lancamento.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemPK other = (ItemPK) obj;
		if (despesa == null) {
			if (other.despesa != null)
				return false;
		} else if (!despesa.equals(other.despesa))
			return false;
		if (lancamento == null) {
			if (other.lancamento != null)
				return false;
		} else if (!lancamento.equals(other.lancamento))
			return false;
		return true;
	}	
}
