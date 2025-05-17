package com.example.ChessProject.service;

import com.example.ChessProject.data.GameHistoryRepository;
import com.example.ChessProject.data.MoveListSerializer;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Move;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameHistoryServiceTest {

    @Mock
    private GameHistoryRepository gameHistoryRepositoryMock;

    @Mock
    private Game gameMock; 

    @InjectMocks
    private GameHistoryService gameHistoryService;

    private List<Move> sampleMoveList;

    @BeforeEach
    void setUp() {
        sampleMoveList = new ArrayList<>();
    }

    @Test
    void getGamesByUser_shouldReturnHistory() throws Exception {
        int userId = 1;
        List<String> expectedHistory = Arrays.asList("game1_data", "game2_data");
        when(gameHistoryRepositoryMock.getHistoryByUser(userId)).thenReturn(expectedHistory);

        List<String> actualHistory = gameHistoryService.getGamesByUser(userId);

        assertEquals(expectedHistory, actualHistory);
        verify(gameHistoryRepositoryMock).getHistoryByUser(userId);
    }

    @Test
    void getGamesByUser_shouldThrowRuntimeException() throws Exception {
        int userId = 1;
        SQLException dbException = new SQLException("Database connection error");
        when(gameHistoryRepositoryMock.getHistoryByUser(userId)).thenThrow(dbException);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            gameHistoryService.getGamesByUser(userId);
        });

        assertEquals("Error retrieving game history for user " + userId, thrown.getMessage());
        assertSame(dbException, thrown.getCause());
        verify(gameHistoryRepositoryMock).getHistoryByUser(userId);
    }

    @Test
    void addGame_shouldSerializeAndStoreGame_whenGameIsValid() throws Exception {
        int userId = 1;
        String expectedSerializedData = "{\"moves\":[]}"; // Example serialized data
        when(gameMock.getGameHistory()).thenReturn(sampleMoveList);

        // Mocking static method MoveListSerializer.serializeMoveList
        // This requires the 'mockito-inline' dependency.
        try (MockedStatic<MoveListSerializer> mockedSerializer = Mockito.mockStatic(MoveListSerializer.class)) {
            mockedSerializer.when(() -> MoveListSerializer.serializeMoveList(sampleMoveList))
                            .thenReturn(expectedSerializedData);

            gameHistoryService.addGame(userId, gameMock);

            verify(gameHistoryRepositoryMock).addGame(userId, expectedSerializedData);
            mockedSerializer.verify(() -> MoveListSerializer.serializeMoveList(sampleMoveList));
        }
    }

    @Test
    void addGame_shouldReturnEarlyAndNotCallRepo_whenGameIsNull() throws SQLException {
        int userId = 1;
        // No need to mock MoveListSerializer here as it shouldn't be called.
        // We are testing the early exit condition.
        assertDoesNotThrow(() -> gameHistoryService.addGame(userId, null));

        verify(gameHistoryRepositoryMock, never()).addGame(anyInt(), anyString());
        // If MoveListSerializer had other static methods that could be called,
        // you might want to ensure no interactions with it at all:
        // try (MockedStatic<MoveListSerializer> mockedSerializer = Mockito.mockStatic(MoveListSerializer.class)) {
        //     assertDoesNotThrow(() -> gameHistoryService.addGame(userId, null));
        //     mockedSerializer.verifyNoInteractions();
        // }
    }

    @Test
    void addGame_shouldReturnEarlyAndNotCallRepo_whenGameHistoryIsNull() throws SQLException{
        int userId = 1;
        when(gameMock.getGameHistory()).thenReturn(null);

        assertDoesNotThrow(() -> gameHistoryService.addGame(userId, gameMock));

        verify(gameHistoryRepositoryMock, never()).addGame(anyInt(), anyString());
    }


    @Test
    void addGame_shouldThrowRuntimeException_whenSerializerFails() throws SQLException{
        int userId = 1;
        when(gameMock.getGameHistory()).thenReturn(sampleMoveList);
        RuntimeException serializationEx = new RuntimeException("JSON processing error");

        try (MockedStatic<MoveListSerializer> mockedSerializer = Mockito.mockStatic(MoveListSerializer.class)) {
            mockedSerializer.when(() -> MoveListSerializer.serializeMoveList(sampleMoveList))
                            .thenThrow(serializationEx);

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                gameHistoryService.addGame(userId, gameMock);
            });

            assertEquals("Error adding game to history for user " + userId, thrown.getMessage());
            assertSame(serializationEx, thrown.getCause());
            verify(gameHistoryRepositoryMock, never()).addGame(anyInt(), anyString());
        }
    }

    @Test
    void addGame_shouldThrowRuntimeException_whenRepositoryFails() throws Exception {
        int userId = 1;
        String fakeSerializedData = "{\"moves\":[]}";
        when(gameMock.getGameHistory()).thenReturn(sampleMoveList);
        SQLException dbException = new SQLException("Failed to insert game history");

        try (MockedStatic<MoveListSerializer> mockedSerializer = Mockito.mockStatic(MoveListSerializer.class)) {
            mockedSerializer.when(() -> MoveListSerializer.serializeMoveList(sampleMoveList))
                            .thenReturn(fakeSerializedData);
            doThrow(dbException).when(gameHistoryRepositoryMock).addGame(userId, fakeSerializedData);

            RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
                gameHistoryService.addGame(userId, gameMock);
            });

            assertEquals("Error adding game to history for user " + userId, thrown.getMessage());
            assertSame(dbException, thrown.getCause());
            verify(gameHistoryRepositoryMock).addGame(userId, fakeSerializedData);
        }
    }
}
