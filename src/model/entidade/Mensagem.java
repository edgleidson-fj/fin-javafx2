package model.entidade;

import java.io.Serializable;

public class Mensagem implements Serializable{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String descricao;
	private String mostrar;
	
	public Mensagem() {
	}
	
	public Mensagem(Integer id, String descricao, String mostrar) {
		this.id = id;
		this.descricao = descricao;
		this.mostrar = mostrar;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMostrar() {
		return mostrar;
	}

	public void setMostrar(String mostrar) {
		this.mostrar = mostrar;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Mensagem other = (Mensagem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mensagem [id=" + id + ", descricao=" + descricao + ", mostrar=" + mostrar + "]";
	}
	
	
}
