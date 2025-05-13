package com.example.ChessProject.data;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.*;
import java.sql.*;
import com.example.ChessProject.data.*;

public class GameHistoryRepositoryTest {
    private GameHistoryRepository gameHistoryRepository;
    private UserRepository userRepository; // To ensure user exists for foreign key, though not strictly enforced by
                                           // schema

    @BeforeEach
    void setUp() throws SQLException {
        gameHistoryRepository = new GameHistoryRepository(); // Triggers static block
        userRepository = new UserRepository(); // Triggers static block

        try (Connection c = SQLiteConnector.connect(); Statement s = c.createStatement()) {
            // Clear and recreate users table for FK reference (though not enforced by
            // schema)
            s.executeUpdate("DROP TABLE IF EXISTS users");
            s.executeUpdate("""
                    CREATE TABLE users (
                      userID       INTEGER PRIMARY KEY AUTOINCREMENT,
                      username     TEXT UNIQUE NOT NULL,
                      passwdHash   TEXT NOT NULL,
                      email        TEXT NOT NULL
                    )""");
            // Clear and recreate game_history table
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
        try (Connection c = SQLiteConnector.connect(); Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE IF EXISTS game_history");
            s.executeUpdate("DROP TABLE IF EXISTS users");
        }
    }

    @Test
    void addGameAndGetHistoryByUser_success() throws SQLException {
        // Create a dummy user for context, though userID in game_history is just an
        // INTEGER
        // com.example.ChessProject.model.User testUser = new
        // com.example.ChessProject.model.User("histUser", "h", "h@e.com");
        // userRepository.saveUser(testUser);
        // int userId = testUser.getUserID();
        int userId = 1; // Using a simple ID for the test

        String gameData1 = "e4 e5 Nf3 Nc6";
        String gameData2 = "d4 d5 c4 c6";

        gameHistoryRepository.addGame(userId, gameData1);
        gameHistoryRepository.addGame(userId, gameData2);

        List<String> history = gameHistoryRepository.getHistoryByUser(userId);
        assertNotNull(history);
        assertEquals(2, history.size(), "Should retrieve two game history entries.");
        // Order is DESC by playedAt (which defaults to CURRENT_TIMESTAMP)
        // So gameData2 should be first if inserted later.
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
