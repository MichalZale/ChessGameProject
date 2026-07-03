package com.example.ChessProject.controller.dto;
import com.example.ChessProject.model.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("isDrawOffered")
    private boolean  isDrawOffered;
    private int      drawOfferedByUserID;
    private List<Move> gameHistory;
    private String   gameResult;

    @JsonIgnore
    public boolean isDrawOffered() {
        return isDrawOffered;
    }

    @JsonProperty("isDrawOffered")
    public boolean getIsDrawOffered() {
        return isDrawOffered;
    }
}
