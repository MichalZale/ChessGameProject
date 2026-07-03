package com.example.ChessProject.model;

import java.util.List;

import com.example.ChessProject.model.validators.KingValidator;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Game {
    private static final Logger log = LoggerFactory.getLogger(Game.class);

    public enum GameStatus {
        ACTIVE,
        FINISHED,
        ABANDONED,
        PENDING

    }

    public enum GameResult {
        NONE,
        CHECKMATE,
        STALEMATE,
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
    private int drawOfferedByUserID = -1;
    private GameResult gameResult = GameResult.NONE;

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
        this.isDrawOffered = false;
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

    public GameState getGameState() {
        return this.game;
    }

    public GameStatus getGameStatus() {
        return this.status;
    }

    public int getWhiteUserID() {
        return this.whiteUserID;
    }

    public int getBlackUserID() {
        return this.blackUserID;
    }

    public Timer getTimer() {
        return this.timer;
    }

    public List<Move> getGameHistory() {
        return this.gameHistory;
    }

    public GameResult getGameResult() {
        return this.gameResult;
    }

    public void makeMove(Move mv, int userID) {
        if (isOver()) {             throw new IllegalStateException("Game is over.");
        }

        Color sideThatMoved = game.getSideToMove();         if ((sideThatMoved == Color.WHITE && userID != this.whiteUserID) ||
                (sideThatMoved == Color.BLACK && userID != this.blackUserID)) {
            throw new IllegalStateException("It's not your turn!");
        }

                if (this.timer != null) {
            this.timer.applyIncrement(sideThatMoved);
        }

                if (this.isDrawOffered && this.drawOfferedByUserID != userID) {
            this.isDrawOffered = false;
            this.drawOfferedByUserID = -1;
        }

        log.debug("Move requested in game {} by user {}: {}", gameID, userID, mv);
        Piece piece = game.getBoard().getPiece(mv.getFrom());
        log.debug("Piece at move source {}: {}", mv.getFrom(), piece);

        if (piece == null) {
            throw new IllegalArgumentException("No piece at starting position: " + mv.getFrom());
        }

        MoveValidator validator = MoveValidatorFactory.getValidator(piece, game);

        if (!validator.isValidMove(mv)) {
            throw new IllegalArgumentException("Invalid move: " + mv);
        }

        GameState newGameState = validator.simulateMove(mv);
        this.game = newGameState; 
        log.debug("Move applied in game {}. Next side to move: {}", gameID, this.game.getSideToMove());
        gameHistory.add(mv);

        MoveValidator stateChecker = new KingValidator(this.game);
        boolean checkmate = stateChecker.isCheckmate();
        boolean stalemate = stateChecker.isStalemate();
        log.debug(
                "Post-move state for game {}: sideToMove={}, checkmate={}, stalemate={}",
                gameID,
                this.game.getSideToMove(),
                checkmate,
                stalemate);

        if (checkmate) {
            this.gameResult = GameResult.CHECKMATE;
            this.status = GameStatus.FINISHED;
            log.info("Game {} finished by checkmate.", gameID);
        } else if (stalemate) {
            this.gameResult = GameResult.STALEMATE;
            this.status = GameStatus.FINISHED;
            log.info("Game {} finished by stalemate.", gameID);
        }
    }

    public boolean isOver() {
                return status == GameStatus.FINISHED
                || status == GameStatus.ABANDONED
                || gameResult != GameResult.NONE;
    }

    public void proposeDraw(int userID) {
        if (isOver())
            throw new IllegalStateException("Game is over.");
                if (userID != this.whiteUserID && userID != this.blackUserID) {
            throw new IllegalStateException(
                    "User " + userID + " is not a player in this game and cannot offer a draw.");
        }
                if (this.isDrawOffered && this.drawOfferedByUserID == userID) {
                        System.out.println("User " + userID + " already offered a draw.");
            return;
        }
                if (this.isDrawOffered && this.drawOfferedByUserID != userID) {
            throw new IllegalStateException(
                    "Another player has already offered a draw. Respond to the existing offer.");
        }

        this.isDrawOffered = true;
        this.drawOfferedByUserID = userID;         System.out.println("Game " + this.gameID + ": User " + userID + " offered a draw. isDrawOffered="
                + this.isDrawOffered + ", offeredBy=" + this.drawOfferedByUserID);
    }

    public void acceptDraw(int userID) {
        if (isOver())
            throw new IllegalStateException("Game is over.");
        if (!isDrawOffered) {
            throw new IllegalStateException("Draw was not offered.");
        }
        if (this.drawOfferedByUserID == userID) {
            throw new IllegalStateException("Cannot accept your own draw offer.");
        }
                if (userID != this.whiteUserID && userID != this.blackUserID) {
            throw new IllegalStateException(
                    "User " + userID + " is not a player in this game and cannot accept a draw.");
        }
        if (this.drawOfferedByUserID == -1 || (userID != this.whiteUserID && userID != this.blackUserID)
                || this.drawOfferedByUserID == userID) {
            throw new IllegalStateException("Invalid conditions to accept a draw.");
        }

        this.gameResult = GameResult.DRAW_AGREEMENT;
        this.status = GameStatus.FINISHED;
        this.isDrawOffered = false;         this.drawOfferedByUserID = -1;         System.out.println("Game " + this.gameID + ": User " + userID + " accepted the draw offer from user "
                + this.drawOfferedByUserID + " (before reset).");
    }

    public void rejectDraw(int userID) {
        if (isOver())
            throw new IllegalStateException("Game is over.");
        if (!isDrawOffered) {
            throw new IllegalStateException("Draw was not offered.");
        }
                if (userID != this.whiteUserID && userID != this.blackUserID) {
            throw new IllegalStateException(
                    "User " + userID + " is not a player in this game and cannot reject a draw.");
        }
        if (this.drawOfferedByUserID == -1 || (userID != this.whiteUserID && userID != this.blackUserID)
                || this.drawOfferedByUserID == userID) {
            throw new IllegalStateException("Invalid conditions to reject a draw.");
        }

        this.isDrawOffered = false;         this.drawOfferedByUserID = -1;         System.out.println("Game " + this.gameID + ": User " + userID + " rejected the draw offer.");
    }

    public void resign(int resigningUserID) {
        if (isOver())
            throw new IllegalStateException("Game is over.");
        this.gameResult = GameResult.RESIGNATION;
        this.status = GameStatus.FINISHED;

    }

    public void setGameStatus(GameStatus status) {
        this.status = status;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setIsDrawOffered(boolean bool) {
        this.isDrawOffered = bool;
    }

    public int getDrawOfferedByUserID() {
        return drawOfferedByUserID;
    }

    public void setDrawOfferedByUserID(int drawOfferedByUserID) {
        this.drawOfferedByUserID = drawOfferedByUserID;
    }

    public boolean isDrawOffered() {
        return this.isDrawOffered;
    }

    public void setWhiteUserID(int ID) {
        this.whiteUserID = ID;
    }

    public void setBlackUserID(int ID) {
        this.blackUserID = ID;
    }
}
