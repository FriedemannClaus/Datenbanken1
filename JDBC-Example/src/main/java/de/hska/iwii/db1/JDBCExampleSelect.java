package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jcraft.jsch.JSchException;

public class JDBCExampleSelect {

	public static void main(String[] args) throws JSchException {
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			// 3. Absetzen einer SELECT-Anweisung
			try (Statement stmt = connection.createStatement()) {
				ResultSet resultSet = stmt.executeQuery("SELECT matr_number, first_name, last_name FROM students");
				
				// 4. Auslesen aller Zeilen mit einem ResultSet
				// Die Zählung der Spalten fängt mit 1 an!
				while (resultSet.next()) {
					int matrNumber = resultSet.getInt(1); // Spaltennummer
					// Auslesen mit Spaltennummer
					String firstName = resultSet.getString(2);
					// Auslesen mit Spaltennamen
					String lastName = resultSet.getString("last_name");
					
					// Ausgabe der Ergebnisse
					System.out.println(matrNumber + " (" + firstName + " " + lastName + ")");
				}
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
		finally {
			DBUtils.closeOracleSSHTunnel();
		}
	}

}
