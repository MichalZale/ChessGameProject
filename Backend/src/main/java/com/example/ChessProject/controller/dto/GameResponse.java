package com.example.ChessProject.controller.dto;

import com.example.ChessProject.model.*;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class GameResponse {
    private int      gameId;
    private String   inviteCode;
    private String   status;
    private GameState gameState;
    private int      whiteUserId;
    private int      blackUserId;
    private Timer    timer;
    private boolean  isDrawOffered;
    private List<Move> gameHistory;
    private String   gameResult;
}


