package com.example.ChessProject.data;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnector {
    private static String currentDbURL = "jdbc:sqlite:data.db";

    public static void setDatabaseUrlForTesting(String testDbURL){
        currentDbURL=testDbURL;
    }

    public static Connection connect() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(currentDbURL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
    }
}

