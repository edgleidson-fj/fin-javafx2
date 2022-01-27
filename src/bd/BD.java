package bd;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BD {

	private static Connection conector = null;

	public static Connection abrirConexao() {
		if (conector == null) {
			try {
				Properties prop = carregarPropriedade();
				String url = prop.getProperty("dburl");
				conector = DriverManager.getConnection(url, prop);
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
		return conector;
	}

	public static void fecharConexão() {
		if (conector != null) {
			try {
				conector.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}

	private static Properties carregarPropriedade() {
		try (FileInputStream fileInputStream = new FileInputStream("db.properties")) {

			Properties propriedade = new Properties();
			propriedade.load(fileInputStream);
			return propriedade;
		} catch (IOException ex) {
			throw new BDException(ex.getMessage());
		}
	}

	public static void fecharStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}

	public static void fecharResultSet(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException ex) {
				throw new BDException(ex.getMessage());
			}
		}
	}
}
