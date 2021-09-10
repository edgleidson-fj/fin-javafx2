package model.entidade.pk;

import java.io.Serializable;

import model.entidade.TipoPag;
import model.entidade.Lancamento;

public class ItemPagamentoPK implements Serializable{
	private static final long serialVersionUID = 1L;

	//(name = "lancamento_id")
	private Lancamento lancamento;
	
	//(name = "tipoPag_id")
	private TipoPag tipoPag;
	
	public Lancamento getLancamento() {
		return lancamento;
	}
	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}
	public TipoPag getTipoPag() {
		return tipoPag;
	}
	public void setTipoPag(TipoPag tipoPag) {
		this.tipoPag = tipoPag;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tipoPag == null) ? 0 : tipoPag.hashCode());
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
		ItemPagamentoPK other = (ItemPagamentoPK) obj;
		if (tipoPag == null) {
			if (other.tipoPag != null)
				return false;
		} else if (!tipoPag.equals(other.tipoPag))
			return false;
		if (lancamento == null) {
			if (other.lancamento != null)
				return false;
		} else if (!lancamento.equals(other.lancamento))
			return false;
		return true;
	}	
}
