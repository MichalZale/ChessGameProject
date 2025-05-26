package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.GameCreationRequest;
import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.GameSettings;
import com.example.ChessProject.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController                 
@RequestMapping("/api/game")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame( @RequestBody GameCreationRequest req) 
    {
        GameSettings settings = new GameSettings(
            req.getWhitePlayerID(),
            req.getBlackPlayerID(), 
            req.getWhiteTime(),
            req.getBlackTime(),
            req.getWhiteTimeIncrease(),
            req.getBlackTimeIncrease()
        );

        Game game = gameService.createGame(settings);

        GameResponse resp = toGameResponse(game);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resp);
    }

    private GameResponse toGameResponse(Game game) {
        return GameResponse.builder()
                .gameId(         game.getGameID())
                .inviteCode(     game.getInviteCode())
                .status(         game.getGameStatus().name())
                .sideToMove(     game.getGameState().getSideToMove().name())
                .board(          game.getGameState().getBoard())
                .whiteUserId(    game.getWhiteUserID())
                .blackUserId(    game.getBlackUserID())
                .timer(          game.getTimer())
                .isDrawOffered(  game.isDrawOffered())
                .gameHistory(    game.getGameHistory())
                .gameResult(     game.getGameResult().name())
                .build();       
    }
}
