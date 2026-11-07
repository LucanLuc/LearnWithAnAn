package Backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/vocab_app";

    private static final String USER = "postgres";

    private static final String PASSWORD = "12345678";

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "PostgreSQL JDBC Driver not found!", e
            );
        }

        return DriverManager.getConnection(
            URL,
            USER,
            PASSWORD
        );
    }

    public static void main(String[] args) {

        try (Connection conn = getConnection()) {

            System.out.println(
                "Connected to PostgreSQL successfully!"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}