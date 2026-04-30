package dao;

import java.sql.*;

public class DAO {
	protected Connection conexao;

	public DAO() {
		conexao = null;
	}

	public boolean conectar() {
		String driverName = "org.postgresql.Driver";

		// DEFAULT: Azure
		String defaultUrl = "jdbc:postgresql://stockit.postgres.database.azure.com:5432/postgres?sslmode=require";
		String defaultUsername = "postgres";
		String defaultPassword = "StockIt2004";

		// Lê das variáveis de ambiente, se existirem
		String url = System.getenv().getOrDefault("DB_URL", defaultUrl);
		String username = System.getenv().getOrDefault("DB_USER", defaultUsername);
		String password = System.getenv().getOrDefault("DB_PASS", defaultPassword);

		boolean status = false;

		try {
			Class.forName(driverName);
			conexao = DriverManager.getConnection(url, username, password);
			status = (conexao != null);
			System.out.println("Conexão efetuada com o Postgres! URL usada: " + url);
		} catch (ClassNotFoundException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- Driver não encontrado -- " + e.getMessage());
		} catch (SQLException e) {
			System.err.println("Conexão NÃO efetuada com o postgres -- " + e.getMessage());
		}

		return status;
	}

	public boolean close() {
		boolean status = false;
		try {
			if (conexao != null && !conexao.isClosed()) {
				conexao.close();
			}
			status = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		}
		return status;
	}
}
