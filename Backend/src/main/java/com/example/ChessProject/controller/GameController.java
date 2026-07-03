package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.GameCreationRequest;
import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.JoinGameRequest;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.GameSettings;
import com.example.ChessProject.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://127.0.0.1:5174")

public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GameResponse> createGame(@RequestBody GameCreationRequest req) {
        GameSettings settings = new GameSettings(
                req.getWhiteTime(),
                req.getBlackTime(),
                req.getWhiteTimeIncrease(),
                req.getBlackTimeIncrease(),
                req.getWhitePlayerID(),
                req.getBlackPlayerID());

        Game game = gameService.createGame(settings);

        return ResponseEntity.status(HttpStatus.CREATED).body(toGameResponse(game));
    }

    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getGameID())
                .inviteCode(game.getInviteCode())
                .status(game.getGameStatus().name())
                .gameState(game.getGameState())
                .whiteUserId(game.getWhiteUserID())
                .blackUserId(game.getBlackUserID())
                .timer(game.getTimer())
                .isDrawOffered(game.isDrawOffered())
                .drawOfferedByUserID(game.getDrawOfferedByUserID())
                .gameHistory(game.getGameHistory())
                .gameResult(game.getGameResult().name())
                .build();
    }

    @PostMapping("/join")
    public ResponseEntity<GameResponse> joinGame(@RequestBody JoinGameRequest request) {
        try {
            Game game = gameService.joinGameByCode(request.getGameCode(), request.getUserID());
            return ResponseEntity.ok(toGameResponse(game));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
