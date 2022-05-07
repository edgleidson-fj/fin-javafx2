package impressora;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class Imprimir {

	public static void imprimirArquivo(String diretorio) {
		try {
			Desktop desktop = Desktop.getDesktop();
			File arquivoTempImprimir = new File(diretorio);
			desktop.print(arquivoTempImprimir);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	// Referencia: https://blogprograminhas.blogspot.com/2017/09/tutorial-java-imprimindo-arquivos-na.html	
}
