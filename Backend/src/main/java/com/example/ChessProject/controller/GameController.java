package com.example.ChessProject.controller;

import com.example.ChessProject.controller.dto.GameCreationRequest;
import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.controller.dto.JoinGameRequest;
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

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<GameResponse> createGame(@RequestBody GameCreationRequest req) {
        GameSettings settings = new GameSettings(
                req.getWhiteTime(),
                req.getBlackTime(),
                req.getWhiteTimeIncrease(),
                req.getBlackTimeIncrease(),
                req.getWhitePlayerID(),
                req.getBlackPlayerID());

        Game game = gameService.createGame(settings);

        return ResponseEntity.status(HttpStatus.CREATED).body(GameResponseMapper.toGameResponse(game));
    }

    @PostMapping("/join")
    public ResponseEntity<GameResponse> joinGame(@RequestBody JoinGameRequest request) {
        Game game = gameService.joinGameByCode(request.getGameCode(), request.getUserID());
        return ResponseEntity.ok(GameResponseMapper.toGameResponse(game));
    }
}
