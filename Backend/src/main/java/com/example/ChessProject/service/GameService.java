package com.example.ChessProject.service;

import com.example.ChessProject.data.MoveListSerializer;
import com.example.ChessProject.model.*;
import com.example.ChessProject.data.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;


@Service
public class GameService {
    private int nextID = 1;
    public Map<Integer, Game> activeGames;
    public Map<String, Integer> gameCodes;
    private final GameHistoryService historyService = new GameHistoryService();

    public Game createGame(GameSettings settings) {
        activeGames = new HashMap<Integer, Game>();
        gameCodes = new HashMap<String, Integer>();
        String code = generateCode();
        int gameID = generateGameID();
        Game game = new Game(code, settings);
        game.setGameID(gameID);
        activeGames.put(gameID, game);
        gameCodes.put(code, gameID);
        return game;
    }

    public Game joinGame(int gameID, int userID){
        Game g = activeGames.get(gameID);
        if(g==null) throw new IllegalArgumentException("No game with ID " + gameID);
        g.joinGame(userID);
        return g;
    }

    public Game makeMove(int gameID, Move mv) {
        Game g = activeGames.get(gameID);
        if (g == null) throw new IllegalArgumentException("No game with ID " + gameID);
        g.makeMove(mv);
        if (g.isOver()) finishGame(g);
        return g;
    }

    public Game proposeDraw(int gameID) {
        Game g = activeGames.get(gameID);
        if (g == null) throw new IllegalArgumentException("No game with ID " + gameID);
        g.proposeDraw();
        return g;
    }

    public Game acceptDraw(int gameID){
        Game g = activeGames.get(gameID);
        if(g == null) throw new IllegalArgumentException("No game with ID " + gameID);
        g.acceptDraw();
        finishGame(g);
        return g;
    }

    public Game resign(int gameID) {
        Game g = activeGames.get(gameID);
        if (g == null) throw new IllegalArgumentException("No game with ID " + gameID);
        g.resign(); 
        finishGame(g);
        return g;
    }

    public Game getGameState(int gameID) {
        Game g = activeGames.get(gameID);
        if (g == null) throw new IllegalArgumentException("No game with ID " + gameID);
        return g;
    }

    private String generateCode() {
        while (true) {
            Random random = new Random();
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

    public void finishGame(Game g){
        if (g.getWhiteUserID() != -1)
            historyService.addGame(g.getWhiteUserID(), g);
        if (g.getBlackUserID() != -1 && g.getBlackUserID()!=g.getWhiteUserID())
            historyService.addGame(g.getBlackUserID(), g);

        activeGames.remove(g.getGameID());
        gameCodes.remove(g.getInviteCode());
    }

    private int generateGameID() {
        return nextID++;
    }
}
