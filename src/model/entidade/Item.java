package model.entidade;

import java.io.Serializable;

import model.entidade.pk.ItemPK;

public class Item implements Serializable{
	private static final long serialVersionUID = 1L;

	 // PrimaryKey composto - (Lancamento/Despesa).
	private ItemPK id = new ItemPK();
	
	public Item() {
		}

	public Item(Lancamento lancamento, Despesa despesa) {
		super();
		id.setLancamento(lancamento);
		id.setDespesa(despesa);
		}
	
	
	public Lancamento getLancamento() {
		return id.getLancamento();
	}
	
	public void setLancamento(Lancamento lancamento) {
		id.setLancamento(lancamento);
	}
	//------------------------	
	
	public Despesa getDespesa() {
		return id.getDespesa();
	}
	
	public void setDespesa(Despesa despesa) {
		id.setDespesa(despesa);
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
		Item other = (Item) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
}
