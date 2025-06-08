package com.example.ChessProject.controller.websocket.websocketMessage;

public class WsMessage {
    private int gameId;
    private WsType type;
    private int userId;       
    private Object payload;   
    
    public int getGameId() { return gameId; }
    public void setGameId(int gameId) { this.gameId = gameId; }

    public WsType getType() { return type; }
    public void setType(WsType type) { this.type = type; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Object getPayload() { return payload; }
    public void setPayload(Object payload) { this.payload = payload; }
}

