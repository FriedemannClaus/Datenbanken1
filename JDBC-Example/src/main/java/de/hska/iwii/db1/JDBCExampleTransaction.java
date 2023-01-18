package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExampleTransaction {

	public static void main(String[] args) {
		// 1. Laden des PostgreSQL-Treibers
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			// 3. Isolationsebene eintragen
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			// 4. Auto-Commit aus Geschwindigkeitgründen absachalten (hier überflüssig)
			connection.setAutoCommit(false);
			
			// 5. Absetzen einer UPDATE-Anweisung
			try (Statement stmt = connection.createStatement()) {
				int updatedRows = stmt.executeUpdate("UPDATE applications SET grade = 200 WHERE id IN (2, 3)");
	
				System.out.println("Aktualisierte Zeilen: " + updatedRows);
				
				// 6. Änderung übernehmen, rollback zur Rücknahme der Änderung
				connection.commit();
			}
			catch (SQLException ex) {
				DBUtils.dumpSQLException(ex);
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
