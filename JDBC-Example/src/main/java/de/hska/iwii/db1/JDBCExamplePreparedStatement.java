package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCExamplePreparedStatement {

	public static void main(String[] args) {
		// 1. Laden des PostgreSQL-Treibers
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			// 3. Isolationsebene eintragen
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			
			// 4. Auto-Commit aus Geschwindigkeitgründen abschalten (hier überflüssig)
			connection.setAutoCommit(false);
			
			// 5. Absetzen einer UPDATE-Anweisung auf einem PreparedStatement
			try (PreparedStatement stmt = connection.prepareStatement("UPDATE applications SET grade = ? WHERE id = ?")) {
	
				stmt.setInt(1, 200); // Erster Wert = Note
				stmt.setInt(2, 1);  // Zweiter Wert = id
	
				int updatedRows = stmt.executeUpdate();
	
				System.out.println("Aktualisierte Zeilen: " + updatedRows);
	
				stmt.setInt(1, 130); // Erster Wert = Note
				stmt.setInt(2, 2);  // Zweiter Wert = id
	
				updatedRows = stmt.executeUpdate();
	
				System.out.println("Aktualisierte Zeilen: " + updatedRows);
				
				// 6. Änderung übernehmen, rollback zur Rücknahme der Änderung
				connection.rollback();
			}
			catch (SQLException ex) {
				DBUtils.dumpSQLException(ex);
			}			
		}
		// 6. Fehlerbehandlung
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (SQLException ex) {
			DBUtils.dumpSQLException(ex);
		}			
	}
}
