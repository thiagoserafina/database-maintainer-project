package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    public static Connection connect() {
        Connection conn = null;

        try {
            String url = "jdbc:postgresql://localhost:5432/banco2";
            String user = "postgres";
            String password = "admin";
            conn = DriverManager.getConnection(url, user, password);

            System.out.println("Conectado ao PostgreSQL com sucesso!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
}
