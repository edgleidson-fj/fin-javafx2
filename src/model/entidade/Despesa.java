package model.entidade;

import java.io.Serializable;

public class Despesa implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nome;
	private Double precoUnid;
	private Double precoTotal;
	private Integer quantidade;
	
	public Despesa() {
		}

	public Despesa(Integer id, String nome, Double precoUnid, Double precoTotal, Integer quantidade) {
		super();
		this.id = id;
		this.nome = nome;
		this.precoUnid = precoUnid;
		this.precoTotal = precoTotal;
		this.quantidade = quantidade;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Double getPrecoUnid() {
		return precoUnid;
	}

	public void setPrecoUnid(Double precoUnid) {
		this.precoUnid = precoUnid;
	}
	
	public Double getPrecoTotal() {
		return precoTotal;
	}

	public void setPrecoTotal(Double precoTotal) {
		this.precoTotal = precoTotal;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

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
		Despesa other = (Despesa) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
