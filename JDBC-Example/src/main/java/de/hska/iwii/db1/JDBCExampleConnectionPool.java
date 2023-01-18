package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariDataSource;

public class JDBCExampleConnectionPool {

	public static void main(String[] args) {
		// PostgreSQL
		HikariDataSource pool = initConnectionPool("org.postgresql.Driver", "jdbc:postgresql://localhost:5432/db1", "db1", "db1");

		// MySQL
//		BasicDataSource pool = initConnectionPool("com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/db1?serverTimezone=Europe/Berlin", "db1", "db1");
		
		// SQL Server
//		BasicDataSource pool = initConnectionPool("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://localhost:1433;databaseName=db1", "SA", "db1");
		// Absetzen einer SELECT-Anweisung
		long startTime = System.nanoTime();
		int avg = 0;
		for (int i = 0; i < 10_000; i++) {
			try (Connection connection = pool.getConnection(); Statement stmt = connection.createStatement()) {
				ResultSet resultSet = stmt.executeQuery("SELECT AVG(grade) AS avg FROM applications WHERE grade > 0");
				
				// 4. Auslesen aller Zeilen mit einem ResultSet
				// Die Zählung der Spalten fängt mit 1 an!
				while (resultSet.next()) {
					// Auslesen mit Spaltennummer
					avg = resultSet.getInt(1);
					
					// Ausgabe der Ergebnisse
					//System.out.println("Avg = " + avg);
				}
				
				// 5. Aufräumen
				resultSet.close();
				stmt.close();
				connection.close();
			}
			// 6. Fehlerbehandlung
			catch (SQLException ex) {
				DBUtils.dumpSQLException(ex);
			}
			System.out.println("Zeit [usec]: " + (System.nanoTime() - startTime) / 1_000 + ", AVG: " + avg);
		}
	}

	/**
	 * Initialisiert den Connection-Pool für eine Datenbank.
	 * @param driverClass Name der Klasse des JDBC-Treibers.
	 * @param databaseURL URL der Datenbank.
	 * @param userName Name des Datenbank-Anwenders.
	 * @param password Passwort des Datenbank-Anwenders.
	 * @return Referenz auf den Pool.
	 */
	private static HikariDataSource initConnectionPool(String driverClass, String databaseURL, String userName, String password) {
		HikariDataSource pool = new HikariDataSource();
		pool.setDriverClassName(driverClass);
		pool.setJdbcUrl(databaseURL);
		pool.setUsername(userName);
		pool.setPassword(password);
		pool.setMaximumPoolSize(10); // Max. 10 gleichzeitige Datenbankverbindung
		return pool;
	}
}
