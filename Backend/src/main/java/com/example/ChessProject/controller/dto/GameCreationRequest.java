package com.example.ChessProject.controller.dto;

public class GameCreationRequest {
    private int whiteTime;
    private int blackTime;
    private int whiteTimeIncrease;
    private int blackTimeIncrease;
    private int whitePlayerID;
    private int blackPlayerID;

    public int getWhiteTime() {
        return whiteTime;
    }

    public void setWhiteTime(int whiteTime) {
        this.whiteTime = whiteTime;
    }

    public int getBlackTime() {
        return blackTime;
    }

    public void setBlackTime(int blackTime) {
        this.blackTime = blackTime;
    }

    public int getWhiteTimeIncrease() {
        return whiteTimeIncrease;
    }

    public void setWhiteTimeIncrease(int whiteTimeIncrease) {
        this.whiteTimeIncrease = whiteTimeIncrease;
    }

    public int getBlackTimeIncrease() {
        return blackTimeIncrease;
    }

    public void setBlackTimeIncrease(int blackTimeIncrease) {
        this.blackTimeIncrease = blackTimeIncrease;
    }

    public int getWhitePlayerID() {
        return whitePlayerID;
    }

    public void setWhitePlayerID(int whitePlayerID) {
        this.whitePlayerID = whitePlayerID;
    }

    public int getBlackPlayerID() {
        return blackPlayerID;
    }

    public void setBlackPlayerID(int blackPlayerID) {
        this.blackPlayerID = blackPlayerID;
    }
}
