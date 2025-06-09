package com.example.ChessProject.service;

import com.example.ChessProject.model.*;
import com.example.ChessProject.service.GameHistoryService; // Ensure this import is correct
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

public class GameServiceTest {

    private GameHistoryService mockHistoryService;
    private GameService gameService;

    private final int USER_ID_1 = 1;
    private final int USER_ID_2 = 2;
    private final int USER_ID_3 = 3;


    @BeforeEach
    void setUp() {
        mockHistoryService = Mockito.mock(GameHistoryService.class);
        gameService = new GameService(mockHistoryService);
    }

    @Test
    void testCreateGame_WithPredefinedPlayers() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings);

        assertNotNull(game);
        assertTrue(gameService.activeGames.containsKey(game.getGameID()));
        assertEquals(USER_ID_1, game.getWhiteUserID());
        assertEquals(USER_ID_2, game.getBlackUserID());
        assertEquals(Game.GameStatus.ACTIVE, game.getGameStatus());
    }

    @Test
    void testCreateGame_AwaitingPlayers() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, -1, -1); // No players assigned
        Game game = gameService.createGame(settings);

        assertNotNull(game);
        assertEquals(-1, game.getWhiteUserID());
        assertEquals(-1, game.getBlackUserID());
        assertEquals(Game.GameStatus.PENDING, game.getGameStatus());
    }


    @Test
    void testJoinGame_Success() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, -1, -1); // White player slot open
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        assertEquals(-1, game.getWhiteUserID());
        assertEquals(-1, game.getBlackUserID());
        assertEquals(Game.GameStatus.PENDING, game.getGameStatus());

        // User 3 joins as white
        Game joinedGameWhite = gameService.joinGame(gameId, USER_ID_3);
        assertNotNull(joinedGameWhite);
        assertEquals(USER_ID_3, joinedGameWhite.getWhiteUserID());
        assertEquals(-1, joinedGameWhite.getBlackUserID()); // Black still open
        assertEquals(Game.GameStatus.PENDING, joinedGameWhite.getGameStatus()); // Still pending for black

        // User 2 joins as black
        Game joinedGameBlack = gameService.joinGame(gameId, USER_ID_2);
        assertNotNull(joinedGameBlack);
        assertEquals(USER_ID_3, joinedGameBlack.getWhiteUserID());
        assertEquals(USER_ID_2, joinedGameBlack.getBlackUserID());
        assertEquals(Game.GameStatus.ACTIVE, joinedGameBlack.getGameStatus()); // Now active
    }

    @Test
    void testJoinGame_GameFull_Throws() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings); // Game is already full
        int gameId = game.getGameID();

        Exception ex = assertThrows(IllegalStateException.class,
                () -> gameService.joinGame(gameId, USER_ID_3)); // User 3 tries to join
        assertTrue(ex.getMessage().contains("Game is not awaiting players or is already full."));
    }

    @Test
    void testJoinGame_UserAlreadyInGame() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, -1);
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        // User 1 tries to "re-join"
        Game rejoinedGame = gameService.joinGame(gameId, USER_ID_1);
        assertNotNull(rejoinedGame);
        assertEquals(USER_ID_1, rejoinedGame.getWhiteUserID()); // No change
    }


    /*@Test // This test was commented out in your provided code
    void testMakeMove_NoSuchGame_Throws() {
        // To make this test work, you'd need a mock Move object or a real one.
        // The signature of makeMove is (int gameID, int userID, Move mv)
        Move mockMove = Mockito.mock(Move.class);
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> gameService.makeMove(999, USER_ID_1, mockMove));
        assertTrue(ex.getMessage().contains("No game with ID"));
    }*/

    @Test
    void testProposeDrawAndAcceptDraw() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        // User 1 proposes draw
        gameService.proposeDraw(gameId, USER_ID_1);
        assertTrue(game.isDrawOffered());
        assertEquals(USER_ID_1, game.getDrawOfferedByUserID());

        // User 2 accepts draw
        gameService.acceptDraw(gameId, USER_ID_2);
        assertEquals(Game.GameResult.DRAW_AGREEMENT, game.getGameResult());
        assertTrue(game.isOver());
        assertFalse(gameService.activeGames.containsKey(gameId)); // Game should be finished and removed
        Mockito.verify(mockHistoryService, Mockito.times(1)).addGame(USER_ID_1, game);
        Mockito.verify(mockHistoryService, Mockito.times(1)).addGame(USER_ID_2, game);
    }

    @Test
    void testProposeDrawAndRejectDraw() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        // User 1 proposes draw
        gameService.proposeDraw(gameId, USER_ID_1);
        assertTrue(game.isDrawOffered());
        assertEquals(USER_ID_1, game.getDrawOfferedByUserID());

        // User 2 rejects draw
        gameService.rejectDraw(gameId, USER_ID_2);
        assertFalse(game.isDrawOffered());
        assertEquals(-1, game.getDrawOfferedByUserID()); // Assuming -1 means no offer
        assertFalse(game.isOver()); // Game continues
        assertTrue(gameService.activeGames.containsKey(gameId)); // Game should still be active
    }


    @Test
    void testResign() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        // User 1 resigns
        gameService.resign(gameId, USER_ID_1);
        assertEquals(Game.GameResult.RESIGNATION, game.getGameResult());
        assertTrue(game.isOver());
        assertFalse(gameService.activeGames.containsKey(gameId)); // Game should be finished and removed
        Mockito.verify(mockHistoryService, Mockito.times(1)).addGame(USER_ID_1, game);
        Mockito.verify(mockHistoryService, Mockito.times(1)).addGame(USER_ID_2, game);
    }

    @Test
    void testJoinGameByCode_Success() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, -1, -1); // No players initially
        Game game = gameService.createGame(settings);
        String code = game.getInviteCode();

        // User 10 joins
        Game joinedGame1 = gameService.joinGameByCode(code, 10);
        assertNotNull(joinedGame1);
        assertEquals(10, joinedGame1.getWhiteUserID()); // First joiner becomes white
        assertEquals(-1, joinedGame1.getBlackUserID());
        assertEquals(Game.GameStatus.PENDING, joinedGame1.getGameStatus());

        // User 11 joins
        Game joinedGame2 = gameService.joinGameByCode(code, 11);
        assertNotNull(joinedGame2);
        assertEquals(10, joinedGame2.getWhiteUserID());
        assertEquals(11, joinedGame2.getBlackUserID());
        assertEquals(Game.GameStatus.ACTIVE, joinedGame2.getGameStatus());
    }

    @Test
    void testJoinGameByCode_InvalidCode_Throws() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> gameService.joinGameByCode("XXXX", 42));
        assertTrue(ex.getMessage().contains("Invalid game code"));
    }

    @Test
    void testGetGameState_Success() {
        GameSettings settings = new GameSettings(300, 300, 5, 5, USER_ID_1, USER_ID_2);
        Game game = gameService.createGame(settings);
        int gameId = game.getGameID();

        Game fetched = gameService.getGameState(gameId);
        assertEquals(game, fetched);
    }

    @Test
    void testGetGameState_NoSuchGame_Throws() {
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> gameService.getGameState(12345)); // Assuming 12345 is not a valid game ID
        assertTrue(ex.getMessage().contains("No game with ID"));
    }
}
