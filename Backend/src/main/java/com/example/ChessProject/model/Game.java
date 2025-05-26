package com.example.ChessProject.model;

import java.util.List;
import java.util.ArrayList;

public class Game {
    public enum GameStatus {
        ACTIVE,
        FINISHED,
        ABANDONED,
        PENDING

    }

    public enum GameResult {
        NONE,
        CHECKMATE, 
        STELMATE,
        RESIGNATION,
        DRAW_AGREEMENT
    }

    private int gameID = -1;
    private String inviteCode;
    private GameState game;
    private int whiteUserID = -1;
    private int blackUserID = -1;
    private GameStatus status;
    private Timer timer;
    private List<Move> gameHistory;
    private boolean isDrawOffered;
    private GameResult gameResult=GameResult.NONE;

    public Game(String inviteCode, GameSettings settings) {
        this.inviteCode = inviteCode;
        this.whiteUserID = settings.getWhitePlayerID();
        this.blackUserID = settings.getBlackPlayerID();
        this.status = GameStatus.PENDING;
        Board board = BoardFactory.createBoard();
        this.game = new GameState(board, Color.WHITE, true, true, true, true, null);
        this.timer = new Timer(settings.getWhiteTime(),
                settings.getBlackTime(),
                settings.getWhiteTimeIncrease(),
                settings.getBlackTimeIncrease());
        this.gameHistory = new ArrayList<>();
    }

    public void joinGame(int userID) {
        if (this.whiteUserID == -1) {
            this.whiteUserID = userID;
        } else if (this.blackUserID == -1) {
            this.blackUserID = userID;
        } else {
            throw new IllegalStateException("Game is full");
        }
    }

    public int getGameID() {
        return this.gameID;
    }

    public String getInviteCode() {
        return this.inviteCode;
    }

    public GameState getGameState(){
        return this.game;
    }

    public GameStatus getGameStatus(){
        return this.status;
    }

    public int getWhiteUserID() {
        return this.whiteUserID;
    }

    public int getBlackUserID() {
        return this.blackUserID;
    }

    public Timer getTimer(){
        return this.timer;
    }

    public List<Move> getGameHistory(){
        return this.gameHistory;
    }

    public GameResult getGameResult(){
        return this.gameResult;
    }

    public void makeMove(Move mv) {
        Piece piece = game.getBoard().getPiece(mv.getFrom());
        MoveValidator validator = MoveValidatorFactory.getValidator(piece, game);
        if (piece == null) {
            return;
        }

        if (!validator.isValidMove(mv))
            return;

        this.game = validator.simulateMove(mv);

        gameHistory.add(mv);

        if (MoveValidatorFactory.getValidator(piece, game).isCheckmate())
            this.gameResult=GameResult.CHECKMATE;
            this.status = GameStatus.FINISHED;
        if  (MoveValidatorFactory.getValidator(piece, game).isStelmate())
            this.gameResult=GameResult.STELMATE;
            this.status = GameStatus.FINISHED;
    }

    public boolean isOver() {
        return status == GameStatus.FINISHED
            || status == GameStatus.ABANDONED;
    }

    public void proposeDraw() {
        this.isDrawOffered=true;
    }

    public void acceptDraw(){
        if(isDrawOffered==false)
            throw new IllegalArgumentException("Draw was not being offered");
        this.gameResult=GameResult.DRAW_AGREEMENT;
        this.status=GameStatus.FINISHED;
    }

    public void rejectDraw(){
        if(isDrawOffered==false)
            throw new IllegalArgumentException("Draw was not being offered");
            setIsDrawOffered(false);
    }

    public void resign() {
        this.gameResult = GameResult.RESIGNATION;
        this.status = GameStatus.FINISHED;
    }


    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setIsDrawOffered(boolean bool){
        this.isDrawOffered=bool;
    }

    public boolean isDrawOffered(){
        return this.isDrawOffered;
    }
}
