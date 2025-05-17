package com.example.ChessProject.data;

import java.sql.*;
import java.util.Optional;

import com.example.ChessProject.model.*;

public class UserRepository {
    static {
        try (Connection c = SQLiteConnector.connect();
                Statement s = c.createStatement()) {
            s.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                      userID       INTEGER PRIMARY KEY AUTOINCREMENT,
                      username     TEXT UNIQUE NOT NULL,
                      passwdHash   TEXT NOT NULL,
                      email        TEXT NOT NULL
                    )""");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User saveUser(User user) throws SQLException {
        String sql = "INSERT INTO users(username,passwdHash,email) VALUES(?,?,?)";
        try (Connection c = SQLiteConnector.connect();
                PreparedStatement p = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, user.getUsername());
            p.setString(2, user.getPasswordHash());
            p.setString(3, user.getEmail());
            p.executeUpdate();
            try (ResultSet rs = p.getGeneratedKeys()) {
                if (rs.next())
                    user.setUserID(rs.getInt(1));
            }
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<User> findByUsername(String name) throws SQLException {
        String sql = "SELECT userID,username,passwdHash,email FROM users WHERE username=?";
        try (Connection c = SQLiteConnector.connect();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, name);
            try (ResultSet rs = p.executeQuery()) {
                if (!rs.next())
                    return Optional.empty();
                User u = new User(
                        rs.getString("username"),
                        rs.getString("passwdHash"),
                        rs.getString("email"));
                u.setUserID(rs.getInt("userID"));
                return Optional.of(u);
            }
        }
    }

    public void updateUser(User u) throws SQLException {
        String sql = "UPDATE users SET username=?,passwdHash=?,email=? WHERE userID=?";
        try (Connection c = SQLiteConnector.connect();
                PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, u.getUsername());
            p.setString(2, u.getPasswordHash());
            p.setString(3, u.getEmail());
            p.setInt(4, u.getUserID());
            p.executeUpdate();
        }
    }

    public void deleteUser(int id) throws SQLException {
        try (Connection c = SQLiteConnector.connect();
                PreparedStatement p = c.prepareStatement("DELETE FROM users WHERE userID=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }
}
