package com.example.ChessProject.model;

public class GameSettings {
    private int whiteTime;
    private int blackTime;
    private int whiteTimeIncrease;
    private int blackTimeIncrease;
    private int whitePlayerID;
    private int blackPlayerID;

    public GameSettings(int whiteTime, int blackTime, int whiteTimeIncrease, int blackTimeIncrease, int whitePlayerID, int blackPlayerID){
        this.whiteTime=whiteTime;
        this.blackTime=blackTime;
        this.whiteTimeIncrease=whiteTimeIncrease;
        this.blackTimeIncrease=blackTimeIncrease;
        this.whitePlayerID=whitePlayerID;
        this.blackPlayerID=blackPlayerID;
    }

    public int getWhiteTime(){
        return this.whiteTime;
    }
    public int getBlackTime(){
        return this.blackTime;
    }
    public int getWhiteTimeIncrease(){
        return this.whiteTimeIncrease;
    }
    public int getBlackTimeIncrease(){
        return this.blackTimeIncrease;
    }
    public int getWhitePlayerID(){
        return this.whitePlayerID;
    }
    public int getBlackPlayerID(){
        return this.blackPlayerID;
    }
}
