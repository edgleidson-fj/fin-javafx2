package model.entidade;

import java.io.Serializable;

public class Despesa implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String nome;
	private Double precoUnid;
	private Double precoBruto;
	private Double precoTotal;
	private Integer quantidade;
	private Double descontoIndividual;
	private Double acrescimo;
	
	public Despesa() {
		}

	public Despesa(Integer id, String nome, Double precoUnid, Double precoBruto, Double precoTotal, Integer quantidade, Double descontoIndividual, Double acrescimo) {
		super();
		this.id = id;
		this.nome = nome;
		this.precoUnid = precoUnid;
		this.precoBruto = precoBruto;
		this.precoTotal = precoTotal;
		this.quantidade = quantidade;
		this.descontoIndividual = descontoIndividual;
		this.acrescimo = acrescimo;
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
	
	public Double getPrecoBruto() {
		return precoBruto;
	}

	public void setPrecoBruto(Double precoBruto) {
		this.precoBruto = precoBruto;
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
	
	public Double getDescontoIndividual() {
		return descontoIndividual;
	}

	public void setDescontoIndividual(Double descontoIndividual) {
		this.descontoIndividual = descontoIndividual;
	}
	
	public Double getAcrescimo() {
		return acrescimo;
	}

	public void setAcrescimo(Double acrescimo) {
		this.acrescimo = acrescimo;
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
