package com.example.ChessProject.service;
import java.util.List;
import com.example.ChessProject.data.*;
import com.example.ChessProject.model.*;

public class GameHistoryService {

    private final GameHistoryRepository repo;

    /** Production constructor */
    public GameHistoryService() {
        this(new GameHistoryRepository());
    }

    /** Testing / DI constructor */
    public GameHistoryService(GameHistoryRepository repo) {
        this.repo = repo;
    }

    public List<String> getGamesByUser(int userID) {
        try {
            return repo.getHistoryByUser(userID);
        } catch (Exception e) {            // wraps SQLException etc.
            throw new RuntimeException(e);
        }
    }

    public void addGame(int userID, Game game) {
        try {
            String gameData = MoveListSerializer.serializeMoveList(game.getGameHistory());
            repo.addGame(userID, gameData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

