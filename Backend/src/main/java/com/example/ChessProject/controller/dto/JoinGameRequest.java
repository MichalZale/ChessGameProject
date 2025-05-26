package com.example.ChessProject.controller.dto;
import lombok.Data;

@Data
public class JoinGameRequest {
    private String gameCode;
    private int userID;
}
