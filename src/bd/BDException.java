package bd;
//Banco de Dados Exception.

public class BDException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public BDException(String mensagem) {
		super(mensagem);
	}
}
