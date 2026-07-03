package com.example.ChessProject.service;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.ChessProject.data.*;
import com.example.ChessProject.model.*;

@Service
public class GameHistoryService {

    private final GameHistoryRepository repo;

    public GameHistoryService() {
        this(new GameHistoryRepository());
    }

    public GameHistoryService(GameHistoryRepository repo) {
        this.repo = repo;
    }

    public List<String> getGamesByUser(int userID) {
        try {
            return repo.getHistoryByUser(userID);
        } catch (Exception e) {            
            throw new RuntimeException("Error retrieving game history for user " + userID, e);
        }
    }

    public void addGame(int userID, Game game) {
        if (game == null) return;

        List<Move> history = game.getGameHistory();
        if (history == null) return;

        try {
            String gameData = MoveListSerializer.serializeMoveList(history);
            repo.addGame(userID, gameData);
        } catch (Exception e) {
            throw new RuntimeException("Error adding game to history for user " + userID, e);
        }
    }
}

