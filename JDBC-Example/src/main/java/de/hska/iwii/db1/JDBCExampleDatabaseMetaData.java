package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCExampleDatabaseMetaData {

	public static void main(String[] args) {
		try (Connection connection = DBUtils.getPostgreSQLConnection("db1", "db1", "db1")) {
			DatabaseMetaData metaData = connection.getMetaData();
			
			// Informationen zur Datenbank sowie zum Treiber und dem angemeldeten user
			System.out.println("Database: " + metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion());
			System.out.println("Database Driver: " + metaData.getDriverName() + " " + metaData.getDriverVersion());
			System.out.println("Database User: " + metaData.getUserName());
			
			// Alle Schema-Informationen
			ResultSet resultSet = metaData.getSchemas();
			dumpResultSet(resultSet);

			// Auslesen aller Tabellen
			resultSet = metaData.getTables(null, "public", "", new String[] {"TABLE"});
			
			// Alle Tabellen im Schema "public"
			dumpResultSet(resultSet);
			
			// Zugriffsrechte auf Tabelle "students"
			resultSet = metaData.getTablePrivileges(null, "public", "students");
			dumpResultSet(resultSet);
			
			// Alle Spalten der Tabelle "students"
			resultSet = metaData.getColumns(null, "public", "students", "");
			dumpResultSet(resultSet);
			
			// 5. Aufräumen
			resultSet.close();
		}
		// 6. Fehlerbehandlung
		catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		catch (SQLException ex) {
			DBUtils.dumpSQLException(ex);
		}
	}

	private static void dumpResultSet(ResultSet resultSet) throws SQLException {
		while (resultSet.next()) {
			for (int i = 1; i < resultSet.getMetaData().getColumnCount(); i++) {
				System.out.print((i != 1 ? ", " : "" ) + resultSet.getMetaData().getColumnName(i) + ": " + resultSet.getString(i));
			}
			System.out.println();
		}
	}
}
