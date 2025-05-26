package com.example.ChessProject.data;

import com.example.ChessProject.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.File; 
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserRepositoryTest {

    private UserRepository userRepository;
    private static final String TEST_DB_FILE_PATH = "test_user_repo.db";
    private static final String TEST_DB_URL = "jdbc:sqlite:" + TEST_DB_FILE_PATH;

    @BeforeEach
    void setUp() throws SQLException {
        File dbFile = new File(TEST_DB_FILE_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }

        SQLiteConnector.setDatabaseUrlForTesting(TEST_DB_URL);
        userRepository = new UserRepository();

        try (Connection c = SQLiteConnector.connect();
             Statement s = c.createStatement()) {
            s.executeUpdate("""
                    CREATE TABLE users (
                      userID       INTEGER PRIMARY KEY AUTOINCREMENT,
                      username     TEXT UNIQUE NOT NULL,
                      passwordHash   TEXT NOT NULL,
                      email        TEXT NOT NULL
                    )""");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        File dbFile = new File(TEST_DB_FILE_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
        SQLiteConnector.resetDatabaseUrlToDefault();
    }

    @Test
    void saveUserAndFindByUsername_success() throws SQLException {
        User user = new User("testuser", "validPasswordHash123", "test@example.com");
        User savedUser = userRepository.saveUser(user);

        assertNotNull(savedUser, "Saved user should not be null.");
        assertTrue(savedUser.getUserID() > 0, "User ID should be positive after saving.");
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("validPasswordHash123", savedUser.getPasswordHash());
        assertEquals("test@example.com", savedUser.getEmail());

        Optional<User> foundUserOpt = userRepository.findByUsername("testuser");
        assertTrue(foundUserOpt.isPresent(), "User should be found by username.");
        User foundUser = foundUserOpt.get();

        assertEquals(savedUser.getUserID(), foundUser.getUserID());
        assertEquals("testuser", foundUser.getUsername());
        assertEquals("validPasswordHash123", foundUser.getPasswordHash());
        assertEquals("test@example.com", foundUser.getEmail());
    }

    @Test
    void findByUsername_notFound() throws SQLException {
        Optional<User> foundUserOpt = userRepository.findByUsername("nonexistentuser");
        assertFalse(foundUserOpt.isPresent(), "User should not be found.");
    }

    @Test
    void saveUser_duplicateUsername_throwsException() throws SQLException {
        User user1 = new User("duplicateuser", "hash1", "email1@example.com");
        userRepository.saveUser(user1);

        User user2 = new User("duplicateuser", "hash2", "email2@example.com");
      
        assertThrows(RuntimeException.class, () -> {
            userRepository.saveUser(user2);
        }, "Saving user with duplicate username should throw RuntimeException.");
    }

    @Test
    void updateUser_success() throws SQLException {
        User originalUser = new User("originaluser", "originalHash", "original@example.com");
        userRepository.saveUser(originalUser); 

        assertTrue(originalUser.getUserID() > 0, "Original user ID should be set.");

        User userToUpdate = new User(originalUser.getUsername(), "updatedHash", "updated@example.com");
        userToUpdate.setUserID(originalUser.getUserID());
        userToUpdate.setUsername("updatedusername"); 
        userRepository.updateUser(userToUpdate);

        Optional<User> updatedUserOpt = userRepository.findByUsername("updatedusername");
        assertTrue(updatedUserOpt.isPresent(), "Updated user should be found by new username.");
        User updatedUser = updatedUserOpt.get();

        assertEquals(originalUser.getUserID(), updatedUser.getUserID());
        assertEquals("updatedusername", updatedUser.getUsername());
        assertEquals("updatedHash", updatedUser.getPasswordHash());
        assertEquals("updated@example.com", updatedUser.getEmail());

        Optional<User> oldUserOpt = userRepository.findByUsername("originaluser");
        assertFalse(oldUserOpt.isPresent(), "Original username should no longer be found.");
    }

    @Test
    void updateUser_userNotFound_noEffect() throws SQLException {
        User nonExistentUser = new User("ghostuser", "ghosthash", "ghost@example.com");
        nonExistentUser.setUserID(9999); 

        assertDoesNotThrow(() -> userRepository.updateUser(nonExistentUser));

        Optional<User> foundUserOpt = userRepository.findByUsername("ghostuser");
        assertFalse(foundUserOpt.isPresent(), "Non-existent user should not be found after update attempt.");
    }

    @Test
    void deleteUser_success() throws SQLException {
        User user = new User("usertodelete", "deleteHash", "delete@example.com");
        userRepository.saveUser(user);
        int userId = user.getUserID();
        assertTrue(userId > 0, "User ID should be set before deletion.");

        Optional<User> foundBeforeDelete = userRepository.findByUsername("usertodelete");
        assertTrue(foundBeforeDelete.isPresent(), "User should be found before deletion.");

        userRepository.deleteUser(userId);

        Optional<User> foundAfterDelete = userRepository.findByUsername("usertodelete");
        assertFalse(foundAfterDelete.isPresent(), "User should be deleted and not found.");
    }

    @Test
    void deleteUser_userNotFound_noEffect() throws SQLException {
        assertDoesNotThrow(() -> userRepository.deleteUser(8888),
                "Deleting a non-existent user should not throw an exception.");
    }
}