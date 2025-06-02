package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.GameCreationRequest;
import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.GameSettings;
import com.example.ChessProject.service.GameService;
import org.springframework.http.HttpStatus;
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
    public GameResponse createGame(@RequestBody GameCreationRequest req) {
        GameSettings settings = new GameSettings(
            req.getWhitePlayerID(),
            req.getBlackPlayerID(),
            req.getWhiteTime(),
            req.getBlackTime(),
            req.getWhiteTimeIncrease(),
            req.getBlackTimeIncrease()
        );

        Game game = gameService.createGame(settings);

        return toGameResponse(game);
    }

    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(game.getGameID())
                .inviteCode(game.getInviteCode())
                .status(game.getGameStatus().name())
                .sideToMove(game.getGameState().getSideToMove().name())
                .board(game.getGameState().getBoard().asArray())
                .whiteUserId(game.getWhiteUserID())
                .blackUserId(game.getBlackUserID())
                .timer(game.getTimer())
                .isDrawOffered(game.isDrawOffered())
                .gameHistory(game.getGameHistory())
                .gameResult(game.getGameResult().name())
                .build();
    }
}
