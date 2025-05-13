package com.example.ChessProject.data;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GameHistoryRepository {
    static {
        try(Connection c = SQLiteConnector.connect();
            Statement s=c.createStatement()){
                s.executeUpdate("""
              CREATE TABLE IF NOT EXISTS game_history (
                id         INTEGER PRIMARY KEY AUTOINCREMENT,
                userID     INTEGER NOT NULL,
                gameData   TEXT    NOT NULL,
                playedAt   TEXT    DEFAULT CURRENT_TIMESTAMP
              )""");
        } catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

    public void addGame(int userID, String gameData) throws SQLException{
        String sql = "INSERT INTO game_history(userID,gameData) VALUES(?,?)";
        try (Connection c = SQLiteConnector.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userID);
            p.setString(2, gameData);
            p.executeUpdate();
        }
    }

    public List<String> getHistoryByUser(int userID) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT gameData FROM game_history WHERE userID=? ORDER BY playedAt DESC, id DESC";
        try (Connection c = SQLiteConnector.connect();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, userID);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) list.add(rs.getString("gameData"));
            }
        }
        return list;
    }
}
