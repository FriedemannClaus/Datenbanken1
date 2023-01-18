package de.hska.iwii.db1.jdbc;
import javax.xml.transform.Result;
import java.sql.*;
import java.util.Properties;

public class Task4 {
    private static JDBCBikeShop jdbcBikeShop;

    public static void main(String args[]) {
        Connection conn = connect();
        jdbcBikeShop = new JDBCBikeShop();
        jdbcBikeShop.reInitializeDB(conn);
        getInfo(conn);


        task42(conn);
        task43(conn);
        task44(conn);
        close(conn);

    }

    private static void prettyPrint(ResultSet rs) {
        try {
            int cols = rs.getMetaData().getColumnCount();
            int colWidth[] = new int[cols];

            for (int i = 0; i < cols; i++) {
                colWidth[i] = rs.getMetaData().getColumnDisplaySize(i + 1);
            }
            String result;
            int i;

            //Kopfzeile1:
            for (i = 0; i < cols; i++) {
                if(i == 0) {
                    result = String.format("%-" + (colWidth[i] + 2) + "s", rs.getMetaData().getColumnName(i + 1));
                } else {
                    result = "|  " + String.format("%-" + (colWidth[i] + 2) + "s", rs.getMetaData().getColumnName(i + 1));
                }
                if (i != cols - 1) {
                    System.out.print(result);
                } else {
                    System.out.println(result);
                }
            }

            //Kopfzeile2:
            for (i = 0; i < cols; i++) {
                if (i == 0) {
                    result = String.format("%-" + (colWidth[i] + 2) + "s", rs.getMetaData().getColumnTypeName(i + 1));
                } else {
                    result = "|  " + String.format("%-" + (colWidth[i] + 2) + "s", rs.getMetaData().getColumnTypeName(i + 1));
                }
                if (i != cols - 1) {
                    System.out.print(result);
                } else {
                    System.out.println(result);
                }
            }

            //Linie:
            for (i = 0; i < cols; i++) {
                if (i == 0) {
                    result = String.format("%-" + (colWidth[i] + 2) + "s", "-").replace(' ', '-');
                } else {
                    result = String.format("%-" + (colWidth[i] + 4) + "s", "-").replace(' ', '-');
                }
                if (i != cols - 1) {
                    System.out.print(result + "+");
                } else {
                    System.out.println(result);
                }
            }

            while (rs.next()) {
                //String result = String.format("%." + colWidth + "s", "-").replace(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
                //String result = String.format("%-" + colWidth + "s", rs.getString(1)
                for (i = 0; i < cols; i++) {
                    if (i == 0) {
                        result = String.format("%" + colWidth[i] + "s", rs.getString(i + 1)) + "  ";
                    } else {
                        result = "|  " + String.format("%" + colWidth[i] + "s", rs.getString(i + 1)) + "  ";
                    }
                    if (i != cols - 1) {
                        System.out.print(result);
                    } else {
                        System.out.println(result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void task42(Connection conn) {
        try {
            Statement s = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rs = s.executeQuery("SELECT persnr, name, ort, aufgabe FROM personal");

            prettyPrint(rs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void task43(Connection conn) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT vorerg4.name, kundnr, lt.name, lt.nr FROM lieferant lt RIGHT OUTER JOIN (SELECT name, kundnr, l.liefnr FROM lieferung l RIGHT OUTER JOIN (SELECT  name, kundnr, t.teilnr FROM teilestamm t JOIN (SELECT  name, kundnr, ap.teilnr FROM auftragsposten ap JOIN (SELECT name, kundnr, auftrnr FROM kunde k JOIN auftrag a ON a.kundnr = k.nr) vorerg1 ON ap.auftrnr = vorerg1.auftrnr) vorerg2 ON vorerg2.teilnr = t.teilnr) vorerg3 ON l.teilnr = vorerg3.teilnr) vorerg4 ON vorerg4.liefnr = lt.nr"); // ? ?
//            ps.setString(1, "WHERE vorerg4.name LIKE ");
//            ps.setString(2,"Rafa%"); //Um das hier tun zu können, braucht man ein preparedStatement (dynamisch nach Namen suchen)
//            ps.setString(1, "");
//            ps.setString(2, "");
            //Ingrid hat ein Damenrad bestellt, welches im Lager war, es gibt aber keine Lieferung dafür. Die dann trotzdem includen
            //Die anderen vier aufgeführten Kunden haben auch Teile bestellt, für die es keine Lieferung gibt. Die müssen für diese Information trotzdem drin bleiben, es steht da ja auch '...'
            //Sonst wären es nur genau die sechs und nicht noch mehr ('...')
            ResultSet rs = ps.executeQuery();

            System.out.println("\nAufgabe 4.3:");
            prettyPrint(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void task44(Connection conn) {
        try {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO kunde VALUES ( 7, 'Friedemann', 'Gaussstr. 42', 76131, 'Karlsruhe', '0');");
            Boolean success = ps.execute();
            PreparedStatement ps2 = conn.prepareStatement("INSERT INTO auftrag VALUES ( 6, DATE '2022-01-18', 7, 1);"); //auftrn, kundnr, persnr
            Boolean success2 = ps2.execute();
            PreparedStatement ps3 = conn.prepareStatement("INSERT INTO auftragsposten VALUES ( 61, 6, 100001, 1,  700.00);"); //posnr (6 ist auftrnr, 1. pos), auftrnr, teilnr, anzahl, gesamtpreis
            Boolean success3 = ps3.execute();


            System.out.println("\nAufgabe 4.4 Kunde nach einfügen:");
            PreparedStatement psAusgabe1 = conn.prepareStatement("SELECT * from kunde");
            ResultSet rs1 = psAusgabe1.executeQuery();
            prettyPrint(rs1);
            System.out.println("\nAufgabe 4.4 Auftrag nach einfügen:");
            PreparedStatement psAusgabe2 = conn.prepareStatement("SELECT * from auftrag");
            ResultSet rs2 = psAusgabe2.executeQuery();
            prettyPrint(rs2);
            System.out.println("\nAufgabe 4.4 Auftragsposten nach einfügen:");
            PreparedStatement psAusgabe3 = conn.prepareStatement("SELECT * from auftragsposten");
            ResultSet rs3 = psAusgabe3.executeQuery();
            prettyPrint(rs3);

            PreparedStatement ps4 = conn.prepareStatement("UPDATE kunde SET sperre = 1 WHERE nr = 7;");
            Boolean success4 = ps4.execute();

            System.out.println("\nAufgabe 4.4 kunde nach Sperrung:");
            PreparedStatement psAusgabe4 = conn.prepareStatement("SELECT * from kunde");
            ResultSet rs4 = psAusgabe4.executeQuery();
            prettyPrint(rs4);


            //Wenn man direkt den neuen Kunden löschen will, kommt die Fehlermeldung: ERROR: update or delete on table "kunde" violates foreign key constraint "fk_auftrag_auftrag" on table "auftragsposten"
            //Detail: Key (nr)=(7) is still referenced from table "auftragsposten".

            //Jetzt richtiges löschen (in korrekter Reihenfolge von Childtable nach Parenttable):
            PreparedStatement ps6 = conn.prepareStatement("DELETE FROM auftragsposten WHERE posnr = 61;");
            Boolean success6 = ps6.execute();
            PreparedStatement ps7 = conn.prepareStatement("DELETE FROM auftrag WHERE auftrnr = 6;");
            Boolean success7 = ps7.execute();
            PreparedStatement ps8 = conn.prepareStatement("DELETE FROM kunde WHERE nr = 7;"); //keine Probleme zu löschen, aber Auftrag noch verhanden und posten
            Boolean success8 = ps8.execute();

            System.out.println("\nAufgabe 4.4 Kunde nach korrektem löschen:");
            PreparedStatement psAusgabe8 = conn.prepareStatement("SELECT * from kunde");
            ResultSet rs8 = psAusgabe8.executeQuery();
            prettyPrint(rs8);
            System.out.println("\nAufgabe 4.4 Auftrag nach korrektem löschen:");
            PreparedStatement psAusgabe9 = conn.prepareStatement("SELECT * from auftrag");
            ResultSet rs9 = psAusgabe9.executeQuery();
            prettyPrint(rs9);
            System.out.println("\nAufgabe 4.4 Auftragsposten nach korrektem löschen:");
            PreparedStatement psAusgabe10 = conn.prepareStatement("SELECT * from auftragsposten");
            ResultSet rs10 = psAusgabe10.executeQuery();
            prettyPrint(rs10);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection connect() {
        Connection conn;
        try {
            conn = getPostgreSQLConnection("g16", "UBzzMUEqfY", "g16");
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void getInfo(Connection conn) {
        try {
            System.out.println(conn.getMetaData().getDatabaseProductName());
            System.out.println(conn.getMetaData().getDriverName());
            System.out.println(conn.getMetaData().getDriverVersion());
            System.out.println(conn.getMetaData().getURL());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void close(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        return DriverManager.getConnection("jdbc:postgresql://datenbanken1.ddns.net:3690/" + databaseName, props);
    }
}