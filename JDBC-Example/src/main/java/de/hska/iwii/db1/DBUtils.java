package de.hska.iwii.db1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class DBUtils {

	// Name des Servers, über den der SSH-Tunnel aufgebaut wird.
	private static final String SSH_SERVER = "login.hs-karlsruhe.de";
	
	private static Session sshSession;

	/**
	 * Erstellt eine Verbindung zur lokalen PostgreSQL-Datenbank.
	 * @param datebaseUser Datenbank-User.
	 * @param databasePassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getPostgreSQLConnection(String datebaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException {
		// PostgreSQL
		Class.forName("org.postgresql.Driver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", datebaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + databaseName, props);
	}

	/**
	 * Erstellt eine Verbindung zur lokalen MySQL-Datenbank.
	 * @param databaseUser Datenbank-User.
	 * @param databasePassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getMySQLConnection(String databaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException {
		// MySDQL
		Class.forName("com.mysql.cj.jdbc.Driver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", databaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/" + databaseName + "?serverTimezone=Europe/Berlin", props);
	}

	/**
	 * Erstellt eine Verbindung zur lokalen MSSQL-Datenbank.
	 * @param dbUser Datenbank-User.
	 * @param dbPassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getMSSQLConnection(String databaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException {
		// MS SQL
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", databaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=" + databaseName, props);
	}

	/**
	 * Erstellt eine Verbindung zur lokalen H2-Datenbank.
	 * @param dbUser Datenbank-User.
	 * @param dbPassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getH2Connection(String databaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException {
		// H2
		Class.forName("org.h2.Driver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", databaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:h2:~/" + databaseName);
	}
	
	/**
	 * Erstellt eine Verbindung zur H2-Datenbank mit In-Memory-Speicher her
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getH2InMemoryConnection(String databaseName) throws ClassNotFoundException, SQLException {
		// H2
		Class.forName("org.h2.Driver");
		
		// 2. Verbinden
		return DriverManager.getConnection("jdbc:h2:mem:" + databaseName);
	}
	
	/**
	 * Erstellt eine Verbindung zur Oracle-Datenbank der Hochschule.
	 * Diese Methode ist nur dann erfolgreich, wenn sich der Client
	 * im Netz der Hochschule befindet.
	 * @param dbUser Datenbank-User.
	 * @param dbPassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getOracleConnection(String databaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException {
		// Oracle
		Class.forName("oracle.jdbc.OracleDriver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", databaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:oracle:thin:@iwi-i-db-01:1521:" + databaseName, props);
	}
	
	/**
	 * Erstellt eine Verbindung zur Oracle-Datenbank an der Hochschule über
	 * einen SSH-Tunnel (login.hs-karlsruhe.de). Diese Methode kann verwendet
	 * werden, um auf die Oracle-Datenbank zuzugreifen, wenn sich der Client
	 * nicht im Netz der Hochschule befindet.
	 * @param adsName ADS-Account.
	 * @param adsPassword ADS-Passwort.
	 * @param databaseUser Datenbank-User.
	 * @param databasePassword Datenbank-Passwort.
	 * @param databaseName Name der Datenbank.
	 * @return Datenbankverbindung.
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws JSchException
	 */
	public static Connection getOracleSSHTunnelConnection(String adsName, String adsPassword,
			String databaseUser, String databasePassword, String databaseName) throws ClassNotFoundException, SQLException, JSchException {
		// SSH-Tunnel aufbauen
		JSch jsch = new JSch();
		sshSession = jsch.getSession(adsName, SSH_SERVER, 22);
		sshSession.setPassword(adsPassword);
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		sshSession.setConfig(config);
		sshSession.connect();
		sshSession.setPortForwardingL(22222, "iwi-i-db-01", 1521);
		
		// Oracle
		Class.forName("oracle.jdbc.OracleDriver");
		
		// 2. Verbinden mit Anmelde-Daten
		Properties props = new Properties();
		props.put("user", databaseUser);
		props.put("password", databasePassword);
		return DriverManager.getConnection("jdbc:oracle:thin:@//localhost:22222/" + databaseName, props);
	}
	
	/**
	 * Schließt des SSH-Tunnel wieder.
	 */
	public static void closeOracleSSHTunnel() {
		if (sshSession != null) {
			sshSession.disconnect();
		}
	}

	/**
	 * Gibt einen SQL-Fehler auf der Konsole aus.
	 * @param ex SQL-Exception.
	 */
	public static void dumpSQLException(SQLException ex) {
		System.out.println("SQLException: " + ex.getLocalizedMessage());
		SQLException nextException = ex.getNextException();
		while (nextException != null) {
			System.out.println("SQLException: " + nextException.getLocalizedMessage());
			nextException = nextException.getNextException();
		}
		ex.printStackTrace();
	}
}
