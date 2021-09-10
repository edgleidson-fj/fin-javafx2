package model.entidade;

import java.io.Serializable;

import model.entidade.pk.ItemPagamentoPK;

public class ItemPagamento implements Serializable{
	private static final long serialVersionUID = 1L;

	 // PrimaryKey composto - (Lancamento/TipoPag).
	private ItemPagamentoPK id = new ItemPagamentoPK();
	
	private Double valor;
	private String nomePag;
	
	public ItemPagamento() {
		}
	
	public ItemPagamento(Double valor, String nomePag) {
		this.valor = valor;
		this.nomePag = nomePag;
	}

	public ItemPagamento(Lancamento lancamento, TipoPag tipoPag) {
		super();
		id.setLancamento(lancamento);
		id.setTipoPag(tipoPag);
		}
	
	
	public Lancamento getLancamento() {
		return id.getLancamento();
	}
	
	public void setLancamento(Lancamento lancamento) {
		id.setLancamento(lancamento);
	}
	//------------------------	
	
	public TipoPag getTipoPag() {
		return id.getTipoPag();
	}
	
	public void setTipoPag(TipoPag tipoPag) {
		id.setTipoPag(tipoPag);
	}
	//-----------------------
	
	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}
	
	public String getNomePag() {
		return nomePag;
	}

	public void setNomePag(String nomePag) {
		this.nomePag = nomePag;
	}
	//-----------------------

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ItemPagamento other = (ItemPagamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
