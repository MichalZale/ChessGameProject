package com.example.ChessProject.service;

import com.example.ChessProject.model.*;
import com.example.ChessProject.model.Game.GameStatus;
import com.example.ChessProject.service.GameHistoryService; 

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private int nextID = 1;
    public Map<Integer, Game> activeGames;
    public Map<String, Integer> gameCodes;
    private final GameHistoryService historyService;

    @Autowired
    public GameService(GameHistoryService historyService) {
        this.historyService = historyService;
        activeGames = new HashMap<>();
        gameCodes = new HashMap<>();  
    }

    public Game createGame(GameSettings settings) {
        String code = generateCode();
        int gameID = generateGameID();
        Game game = new Game(code, settings);
        game.setGameID(gameID);
        if (settings.getWhitePlayerID() != -1 && settings.getBlackPlayerID() != -1) {
            game.setGameStatus(GameStatus.ACTIVE);
        } else {
            game.setGameStatus(GameStatus.PENDING);
        }
        activeGames.put(gameID, game);
        gameCodes.put(code, gameID);
        return game;
    }

    /**
     * Allows a user to join an existing game using the game ID, if a slot is available.
     * @param gameID The ID of the game to join.
     * @param userID The ID of the user joining.
     * @return The game object after the user has joined.
     * @throws IllegalArgumentException if no game with the given ID exists or if the game is not pending.
     * @throws IllegalStateException if the user cannot join (e.g., game is full, or user is already a player).
     */
    public Game joinGame(int gameID, int userID) {
        Game game = activeGames.get(gameID);
        if (game == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        if (game.getGameStatus() != GameStatus.PENDING && !(game.getWhiteUserID() == userID || game.getBlackUserID() == userID)) {
            if (game.getWhiteUserID() != -1 && game.getBlackUserID() != -1) {
                 throw new IllegalStateException("Game is not awaiting players or is already full.");
            }
        }

        if (game.getWhiteUserID() == userID || game.getBlackUserID() == userID) {
            return game;
        }

        if (game.getWhiteUserID() == -1) {
            game.setWhiteUserID(userID);
        } else if (game.getBlackUserID() == -1) {
            game.setBlackUserID(userID);
        } else {
            throw new IllegalStateException("Game is full. Cannot join game with ID " + gameID);
        }

        if (game.getWhiteUserID() != -1 && game.getBlackUserID() != -1) {
            game.setGameStatus(GameStatus.ACTIVE);
        }
        return game;
    }

    public Game makeMove(int gameID, int userID, Move mv) {
        Game g = activeGames.get(gameID);
        if (g == null){
            System.out.println("GameService: No game with ID " + gameID + " found for makeMove.");
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        g.makeMove(mv, userID); 
        if (g.isOver()) {
            finishGame(g);
        }
        System.out.println("GameService: Move made in game " + gameID + " by user " + userID);
        return g;
    }

    public Game joinGameByCode(String inviteCode, int userID) {
        Integer gameID = gameCodes.get(inviteCode);
        if (gameID == null) {
            throw new IllegalArgumentException("Invalid game code: " + inviteCode);
        }
        Game game = activeGames.get(gameID);
        if (game == null) {
            throw new IllegalStateException("Game not found for ID: " + gameID + " associated with code: " + inviteCode);
        }
       
        if (game.getWhiteUserID() == userID || game.getBlackUserID() == userID) {
            return game;
        }

        if (game.getWhiteUserID() == -1) {
            game.setWhiteUserID(userID);
        } else if (game.getBlackUserID() == -1 && game.getWhiteUserID() != userID) { 
            game.setBlackUserID(userID);
        } else {
            throw new IllegalStateException("Game is full or user cannot join.");
        }

        if (game.getWhiteUserID() != -1 && game.getBlackUserID() != -1) {
            game.setGameStatus(GameStatus.ACTIVE);
        }
        return game;
    }

    public Game proposeDraw(int gameID, int userID) {
        Game g = activeGames.get(gameID);
        if (g == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        g.proposeDraw(userID); 
        return g;
    }

    public Game acceptDraw(int gameID, int userID) {
        Game g = activeGames.get(gameID);
        if (g == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        g.acceptDraw(userID); 
        if (g.isOver()) { 
            finishGame(g);
        }
        return g;
    }

    public Game rejectDraw(int gameID, int userID) {
        Game g = activeGames.get(gameID);
        if (g == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        g.rejectDraw(userID); 
        return g;
    }

    public Game resign(int gameID, int userID) {
        Game g = activeGames.get(gameID);
        if (g == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        g.resign(userID); 
        if (g.isOver()) { 
            finishGame(g);
        }
        return g;
    }

    public Game getGameState(int gameID) { 
        Game g = activeGames.get(gameID);
        if (g == null) {
            throw new IllegalArgumentException("No game with ID " + gameID);
        }
        return g;
    }

    private String generateCode() {
        Random random = new Random();
        while (true) {
            StringBuilder code = new StringBuilder();
            for (int i = 0; i < 4; i++) { 
                char c = (char) ('A' + random.nextInt(26));
                code.append(c);
            }
            String codeString = code.toString();
            if (!gameCodes.containsKey(codeString)) {
                return codeString;
            }
        }
    }

    public void finishGame(Game g) {
        if (g == null) return;
        if (g.getWhiteUserID() != -1) {
            historyService.addGame(g.getWhiteUserID(), g);
        }
        if (g.getBlackUserID() != -1 && g.getBlackUserID() != g.getWhiteUserID()) {
            historyService.addGame(g.getBlackUserID(), g);
        }

        activeGames.remove(g.getGameID());
        gameCodes.remove(g.getInviteCode());
        System.out.println("GameService: Game " + g.getGameID() + " finished and removed from active games.");
    }

    private int generateGameID() {
        return nextID++;
    }
}
