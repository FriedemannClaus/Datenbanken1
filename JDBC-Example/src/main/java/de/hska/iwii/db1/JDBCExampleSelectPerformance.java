package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCExampleSelectPerformance {

	public static void main(String[] args) {
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			// 3. Absetzen einer SELECT-Anweisung
			try (Statement stmt = connection.createStatement()) {
	
				long avg = 0;
				long cnt = 0;
				long startTime = System.nanoTime();
				for (int id = 1; id < 4687; id++) {
					ResultSet resultSet = stmt.executeQuery("SELECT note FROM intranet.applications WHERE id = " + id);
					
					// Die Zählung der Spalten fängt mit 1 an!
					while (resultSet.next()) {
						long note = resultSet.getLong(1);
						if (note != -1) {
							avg += note;
						}
						cnt++;
					}
					resultSet.close();
				}
				System.out.println("Zeit [usec]: " + (System.nanoTime() - startTime) / 1_000 + ", Durchschnitt: " + (avg / cnt));
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
