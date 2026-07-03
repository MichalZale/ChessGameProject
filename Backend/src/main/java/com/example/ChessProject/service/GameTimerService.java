package com.example.ChessProject.service;

import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Timer;
import com.example.ChessProject.model.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameTimerService {

    private static final Logger logger = LoggerFactory.getLogger(GameTimerService.class);
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;
    private final Map<Integer, Boolean> activeGameTimers = new ConcurrentHashMap<>();


    @Autowired
    public GameTimerService(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 1000) 
    public void processActiveGameTimers() {
        if (gameService.activeGames.isEmpty()) {
            return; 
        }

        for (Game game : gameService.activeGames.values()) {
            if (game.getGameStatus() == Game.GameStatus.ACTIVE && !game.isOver()) {
                Timer timer = game.getTimer();
                if (timer == null) continue;

                Color sideToMove = game.getGameState().getSideToMove();
                boolean timeDecremented = timer.decrementTime(sideToMove);

                if (!timeDecremented) {
                    logger.info("Time ran out for {} in game {}", sideToMove, game.getGameID());
                    game.setGameStatus(Game.GameStatus.FINISHED);
                    
                    // Na razie ustawmy ogólny wynik, np. ABANDONED lub specyficzny TIMEOUT
                    // game.setGameResult(Game.GameResult.TIMEOUT); // Wymaga dodania TIMEOUT do enum GameResult
                    // gameService.finishGame(game); // Zakończ grę i usuń z aktywnych

                    // Poniżej uproszczona logika - ustawiamy wynik i status, GameService.finishGame zajmie się resztą
                    if (sideToMove == Color.WHITE) {
                        // Black wins by timeout
                        // game.setGameResult(Game.GameResult.BLACK_WINS_BY_TIMEOUT); // Wymaga dodania
                    } else {
                    }
                }
                
                GameResponse response = GameResponseMapper.toGameResponse(game);
                messagingTemplate.convertAndSend("/topic/game-" + game.getGameID(), response);

                if (!timeDecremented) {
                     gameService.finishGame(game); 
                }

            }
        }
    }
}