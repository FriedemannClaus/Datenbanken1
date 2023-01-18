package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExampleSelectFunction {

	public static void main(String[] args) {
		// 1. Laden des PostgreSQL-Treibers
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			// 3. Absetzen einer SELECT-Anweisung
			try (Statement stmt = connection.createStatement()) {
				ResultSet resultSet = stmt.executeQuery("SELECT AVG(grade) AS avg FROM applications WHERE grade > 0");
				
				// 4. Auslesen aller Zeilen mit einem ResultSet
				// Die Zählung der Spalten fängt mit 1 an!
				while (resultSet.next()) {
					// Auslesen mit Spaltennummer
					int avg1 = resultSet.getInt(1);
					// Auslesen derselben Spalte über deren Namen
					int avg2 = resultSet.getInt("avg");
					
					// Ausgabe der Ergebnisse
					System.out.println("Avg = " + avg1 + ", " + avg2);
				}
			}
			// Fehlerbehandlung
			catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		// Fehlerbehandlung
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (SQLException ex) {
			DBUtils.dumpSQLException(ex);
		}
	}
}
