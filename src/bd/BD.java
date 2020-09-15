package bd;
// BANCO DE DADOS.

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BD {

	// CONEX�O
	private static Connection conector = null;

	// ABRIR CONEX�O
	public static Connection abrirConexao() {
		if (conector == null) {
			try {
				Properties prop = carregarPropriedade();
				String url = prop.getProperty("dburl"); // (dburl) - Dentro do arquivo (db.properties).
				conector = DriverManager.getConnection(url, prop); // Conectar no banco.
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
		return conector;
	}

	// FECHAR CONEX�O.
	public static void fecharConex�o() {
		if (conector != null) {
			try {
				conector.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}

	// CARREGAR PROPRIEDADE - Que est�o no arquivo (db.properties) dentro da pasta
	// raiz.
	private static Properties carregarPropriedade() {
		try (FileInputStream fileInputStream = new FileInputStream("db.properties")) {

			Properties propriedade = new Properties();
			propriedade.load(fileInputStream);
			return propriedade;
		} catch (IOException ex) {
			throw new BDException(ex.getMessage());
		}
	}

	// FECHAMENTO STATEMENT - Para evitar a necessidade de ficar realizando o Try-Catch toda vez.
	public static void fecharStatement(Statement statement) {
		if(statement != null) {
			try {
				statement.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}
	
	// FECHAMENTO ResultSet - Mesmo princ�pio do Statement.
	public static void fecharResultSet(ResultSet resultSet) {
		if(resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}
}
