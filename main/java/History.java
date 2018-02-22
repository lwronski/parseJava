import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class History {

    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_URL = "jdbc:sqlite:history.db";

    private Connection conn;
    private Statement stat;
    private Stack<String> session;
    private boolean checkIfLastInsert;

    public History() {
        session = new Stack<>();
        checkIfLastInsert = false;
        try {
            Class.forName(History.DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("Brak sterownika JDBC");
            e.printStackTrace();
        }

        try {
            conn = DriverManager.getConnection(DB_URL);
            stat = conn.createStatement();
        } catch (SQLException e) {
            System.err.println("Problem z otwarciem polaczenia");
            e.printStackTrace();
        }

        try {
            createTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createTables() throws Exception {
        String createTable = "CREATE TABLE IF NOT EXISTS links (id INTEGER PRIMARY KEY AUTOINCREMENT, link varchar(255))";
        try {
            stat.execute(createTable);
        } catch (SQLException e) {
            System.err.println("Blad przy tworzeniu tabeli");
            e.printStackTrace();
            throw new Exception(e);
        }
    }

    public void insertLinks(String url) {
        session.add(url);
        checkIfLastInsert = true;
        if (!findUrl(url)) {
            try {
                PreparedStatement prepStmt = conn.prepareStatement(
                        "insert into links values (NULL, ?);");
                prepStmt.setString(1, url);
                prepStmt.execute();
            } catch (SQLException e) {
                System.err.println("Blad przy wstawianiu czytelnika");
                e.printStackTrace();
            }
        }
    }

    private boolean findUrl(String url) {
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM links WHERE link = '" + url + "';");
            if (result.next()) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private List<String> selectLinks() {
        List<String> links = new LinkedList<String>();
        try {
            ResultSet result = stat.executeQuery("SELECT * FROM links");
            String link;
            while (result.next()) {
                link = result.getString("link");
                links.add(link);
                System.out.println(link);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return links;
    }

    public ObservableList<String> getLinks()  {
        ObservableList<String> link = FXCollections.observableArrayList();
        link.addAll(selectLinks());
        return link;
    }

    public String getLastUlr() {
        if (checkIfLastInsert) session.pop();
        checkIfLastInsert = false;
        return session.pop();
    }

}
