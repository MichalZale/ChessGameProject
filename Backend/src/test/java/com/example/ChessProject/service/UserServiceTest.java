package com.example.ChessProject.service;

import com.example.ChessProject.data.SQLiteConnector;
import com.example.ChessProject.data.UserRepository;
import com.example.ChessProject.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;
    private static String TEST_DB_URL;

    @TempDir
    static Path tempDir;

    @BeforeEach
    void setUp() throws SQLException {
        File dbFile = new File(tempDir.toFile(), "test_user_service.db");
        TEST_DB_URL = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        SQLiteConnector.setDatabaseUrlForTesting(TEST_DB_URL);

        userService = new UserService();
        userRepository = new UserRepository();

        try (Connection conn = SQLiteConnector.connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                      userID       INTEGER PRIMARY KEY AUTOINCREMENT,
                      username     TEXT UNIQUE NOT NULL,
                      passwordHash TEXT NOT NULL,
                      email        TEXT NOT NULL
                    )""");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        File dbFile = new File(tempDir.toFile(), "test_user_service.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        SQLiteConnector.resetDatabaseUrlToDefault();
    }

    @Test
    void registerUser_success() throws Exception {
        userService.registerUser("testuser", "password123", "test@example.com");

        Optional<User> foundUserOpt = userRepository.findByUsername("testuser");
        assertTrue(foundUserOpt.isPresent(), "User should be found in the database after registration.");
        User foundUser = foundUserOpt.get();
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("test@example.com", foundUser.getEmail());
        assertNotNull(foundUser.getPasswordHash(), "Password hash should not be null.");
        assertNotEquals("password123", foundUser.getPasswordHash(), "Password should be hashed.");
    }

    @Test
    void registerUser_usernameAlreadyExists_throwsRuntimeException() throws Exception {
        userService.registerUser("existinguser", "password123", "existing@example.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("existinguser", "anotherPassword", "another@example.com");
        });
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void login_success() throws Exception {
        userService.registerUser("loginuser", "correctPassword", "login@example.com");
        User loggedInUser = userService.login("loginuser", "correctPassword");

        assertNotNull(loggedInUser, "Login should be successful and return a User object.");
        assertEquals("loginuser", loggedInUser.getUsername());
    }

    @Test
    void login_userNotFound() {
        User loggedInUser = userService.login("nonexistentuser", "password123");
        assertNull(loggedInUser, "Login should fail for a non-existent user.");
    }

    @Test
    void login_incorrectPassword() throws Exception {
        userService.registerUser("authuser", "actualPassword", "auth@example.com");
        User loggedInUser = userService.login("authuser", "wrongPassword");
        assertNull(loggedInUser, "Login should fail with an incorrect password.");
    }
}
