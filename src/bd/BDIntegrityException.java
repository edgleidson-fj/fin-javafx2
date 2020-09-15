package bd;

public class BDIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BDIntegrityException(String mensagem) {
		super(mensagem);
	}
}
