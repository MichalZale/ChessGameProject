package com.example.ChessProject.controller.dto;

import lombok.Data;

@Data
public class GameCreationRequest {
    private int whiteTime;
    private int blackTime;
    private int whiteTimeIncrease;
    private int blackTimeIncrease;
    private int whitePlayerID=-1;
    private int blackPlayerID=-1;
}
