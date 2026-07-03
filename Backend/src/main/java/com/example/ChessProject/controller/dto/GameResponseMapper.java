package com.example.ChessProject.controller.dto;

import org.springframework.stereotype.Component;

import com.example.ChessProject.model.Game;

@Component
public class GameResponseMapper {
    public static GameResponse toGameResponse(Game game) {
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
}