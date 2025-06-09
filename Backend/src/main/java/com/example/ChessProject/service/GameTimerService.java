package com.example.ChessProject.service;

import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Timer;
import com.example.ChessProject.model.Color; // Upewnij się, że masz klasę/enum Color
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
    private final GameService gameService; // Do dostępu do mapy aktywnych gier
    private final SimpMessagingTemplate messagingTemplate;
    // Mapa do śledzenia, czy dla danej gry timer jest aktywny (aby uniknąć wielokrotnego startu)
    // Można to zintegrować bezpośrednio z GameService.activeGames, jeśli GameService zarządza stanem aktywności timera
    private final Map<Integer, Boolean> activeGameTimers = new ConcurrentHashMap<>();


    @Autowired
    public GameTimerService(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    // Metoda wywoływana co sekundę do aktualizacji timerów
    @Scheduled(fixedRate = 1000) 
    public void processActiveGameTimers() {
        if (gameService.activeGames.isEmpty()) {
            return; // Brak aktywnych gier
        }

        // Iteruj po kopi mapy lub użyj iteratora, aby uniknąć ConcurrentModificationException
        // jeśli gameService.activeGames może być modyfikowane z innego wątku
        // Dla uproszczenia zakładamy, że iteracja jest bezpieczna lub gameService.activeGames jest ConcurrentHashMap
        for (Game game : gameService.activeGames.values()) {
            if (game.getGameStatus() == Game.GameStatus.ACTIVE && !game.isOver()) {
                Timer timer = game.getTimer();
                if (timer == null) continue;

                Color sideToMove = game.getGameState().getSideToMove();
                boolean timeDecremented = timer.decrementTime(sideToMove);

                if (!timeDecremented) { // Czas się skończył
                    logger.info("Time ran out for {} in game {}", sideToMove, game.getGameID());
                    // Ustaw wynik gry na timeout
                    game.setGameStatus(Game.GameStatus.FINISHED);
                    // Możesz dodać nowy GameResult np. TIMEOUT_WHITE_WINS, TIMEOUT_BLACK_WINS
                    // lub po prostu RESIGNATION i określić zwycięzcę na podstawie tego, czyj czas się skończył
                    // Na razie ustawmy ogólny wynik, np. ABANDONED lub specyficzny TIMEOUT
                    // game.setGameResult(Game.GameResult.TIMEOUT); // Wymaga dodania TIMEOUT do enum GameResult
                    // gameService.finishGame(game); // Zakończ grę i usuń z aktywnych

                    // Poniżej uproszczona logika - ustawiamy wynik i status, GameService.finishGame zajmie się resztą
                    if (sideToMove == Color.WHITE) {
                        // Black wins by timeout
                        // game.setGameResult(Game.GameResult.BLACK_WINS_BY_TIMEOUT); // Wymaga dodania
                    } else {
                        // White wins by timeout
                        // game.setGameResult(Game.GameResult.WHITE_WINS_BY_TIMEOUT); // Wymaga dodania
                    }
                    // Na razie, dla uproszczenia, niech GameService.finishGame obsłuży to po wysłaniu ostatniego stanu
                    // gameService.finishGame(game); // Przenieś to do miejsca, gdzie gra jest faktycznie kończona
                }
                
                // Zawsze wysyłaj aktualny stan gry (w tym zaktualizowany timer)
                // Można by stworzyć dedykowany typ wiadomości TIME_UPDATE, ale wysłanie pełnego GameResponse jest prostsze
                GameResponse response = GameResponseMapper.toGameResponse(game);
                messagingTemplate.convertAndSend("/topic/game-" + game.getGameID(), response);

                if (!timeDecremented) { // Jeśli czas się skończył, zakończ grę po wysłaniu odpowiedzi
                     gameService.finishGame(game); // Usuń grę z aktywnych po wysłaniu ostatniego stanu
                }

            }
        }
    }
}