package com.example.ChessProject.data;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.*;
import java.sql.*;
import com.example.ChessProject.data.*;

public class GameHistoryRepositoryTest {
    private GameHistoryRepository gameHistoryRepository;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        gameHistoryRepository = new GameHistoryRepository();
        userRepository = new UserRepository(); 
        SQLiteConnector.setDatabaseUrlForTesting("jdbc:sqlite:data1.db");

        try (Connection c = SQLiteConnector.connect(); Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE IF EXISTS users");
            s.executeUpdate("""
                    CREATE TABLE users (
                      userID       INTEGER PRIMARY KEY AUTOINCREMENT,
                      username     TEXT UNIQUE NOT NULL,
                      passwordHash   TEXT NOT NULL,
                      email        TEXT NOT NULL
                    )""");
            s.executeUpdate("DROP TABLE IF EXISTS game_history");
            s.executeUpdate("""
                    CREATE TABLE game_history (
                      id         INTEGER PRIMARY KEY AUTOINCREMENT,
                      userID     INTEGER NOT NULL,
                      gameData   TEXT    NOT NULL,
                      playedAt   TEXT    DEFAULT CURRENT_TIMESTAMP
                    )""");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        SQLiteConnector.setDatabaseUrlForTesting("jdbc:sqlite:data1.db");
        try (Connection c = SQLiteConnector.connect(); Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE IF EXISTS game_history");
            s.executeUpdate("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void addGameAndGetHistoryByUser_success() throws SQLException {
        int userId = 1;

        String gameData1 = "e4 e5 Nf3 Nc6";
        String gameData2 = "d4 d5 c4 c6";

        gameHistoryRepository.addGame(userId, gameData1);
        gameHistoryRepository.addGame(userId, gameData2);

        List<String> history = gameHistoryRepository.getHistoryByUser(userId);
        assertNotNull(history);
        assertEquals(2, history.size(), "Should retrieve two game history entries.");
        
        assertTrue(history.contains(gameData1));
        assertTrue(history.contains(gameData2));
    }

    @Test
    void getHistoryByUser_noHistory() throws SQLException {
        int userIdWithNoHistory = 99;
        List<String> history = gameHistoryRepository.getHistoryByUser(userIdWithNoHistory);
        assertNotNull(history);
        assertTrue(history.isEmpty(), "History list should be empty for a user with no games.");
    }

    @Test
    void addGame_multipleUsers() throws SQLException {
        int user1Id = 1;
        int user2Id = 2;

        String game1User1 = "e4";
        String game2User1 = "d4";
        String game1User2 = "c4";

        gameHistoryRepository.addGame(user1Id, game1User1);
        gameHistoryRepository.addGame(user2Id, game1User2);
        gameHistoryRepository.addGame(user1Id, game2User1);

        List<String> historyUser1 = gameHistoryRepository.getHistoryByUser(user1Id);
        assertEquals(2, historyUser1.size());
        assertTrue(historyUser1.contains(game1User1));
        assertTrue(historyUser1.contains(game2User1));

        List<String> historyUser2 = gameHistoryRepository.getHistoryByUser(user2Id);
        assertEquals(1, historyUser2.size());
        assertTrue(historyUser2.contains(game1User2));
    }
}
