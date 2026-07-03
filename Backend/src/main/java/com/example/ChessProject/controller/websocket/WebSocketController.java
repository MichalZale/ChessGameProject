package com.example.ChessProject.controller.websocket;

import com.example.ChessProject.controller.dto.ApiError;
import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.controller.websocket.websocketMessage.WsMessage;
import com.example.ChessProject.controller.websocket.websocketMessage.WsType;
import com.example.ChessProject.model.Color;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Move;
import com.example.ChessProject.model.Piece;
import com.example.ChessProject.model.Position;
import com.example.ChessProject.model.pieces.Bishop;
import com.example.ChessProject.model.pieces.Knight;
import com.example.ChessProject.model.pieces.Pawn;
import com.example.ChessProject.model.pieces.Queen;
import com.example.ChessProject.model.pieces.Rook;
import com.example.ChessProject.service.GameService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/game/message")
    public void handleGameMessage(WsMessage msg) {
        int gameId = msg.getGameId();
        WsType type = msg.getType();
        int userId = msg.getUserId();

        try {
            Game updatedGame;
            switch (type) {
                case MOVE:
                    Move move = parseMove(msg.getPayload(), gameId);
                    updatedGame = gameService.makeMove(gameId, userId, move);
                    break;

                case RESIGN:
                    updatedGame = gameService.resign(gameId, userId);
                    break;

                case DRAW_PROPOSE:
                    updatedGame = gameService.proposeDraw(gameId, userId);
                    break;

                case DRAW_ACCEPT:
                    updatedGame = gameService.acceptDraw(gameId, userId);
                    break;

                case DRAW_REJECT:
                    updatedGame = gameService.rejectDraw(gameId, userId);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported WebSocket message type: " + type);
            }
            GameResponse response = GameResponseMapper.toGameResponse(updatedGame);
            messagingTemplate.convertAndSend("/topic/game-" + gameId, response);
            log.debug("Sent game {} update for message type {}", gameId, type);

        } catch (Exception e) {
            log.warn("Could not process WebSocket message for game {} and type {}: {}",
                    gameId, type, e.getMessage());
            messagingTemplate.convertAndSend("/topic/game-" + gameId,
                    new ApiError("WEBSOCKET_ERROR", e.getMessage(), 400, "/topic/game-" + gameId));
        }
    }

    private Move parseMove(Object payload, int gameId) {
        if (payload instanceof Map) {
            @SuppressWarnings("unchecked") // Bezpieczne, bo sprawdzamy instanceof
            Map<String, Object> map = (Map<String, Object>) payload;

            @SuppressWarnings("unchecked")
            Map<String, Object> fromMap = (Map<String, Object>) map.get("from");
            @SuppressWarnings("unchecked")
            Map<String, Object> toMap = (Map<String, Object>) map.get("to");

            if (fromMap == null || toMap == null) {
                throw new IllegalArgumentException("Invalid payload: 'from' or 'to' is missing.");
            }

            int fromRow = (int) fromMap.get("row");
            int fromCol = (int) fromMap.get("column");
            Position fromPos = new Position(fromRow, fromCol);

            int toRow = (int) toMap.get("row");
            int toCol = (int) toMap.get("column");
            Position toPos = new Position(toRow, toCol);

            String moveTypeStr = (String) map.get("moveType");
            if (moveTypeStr == null) {
                throw new IllegalArgumentException("Invalid payload: 'moveType' is missing.");
            }
            Move.MoveType moveType = Move.MoveType.valueOf(moveTypeStr.toUpperCase());

            Piece promotionToPiece = null;
            // Frontend wysyła string, np. "QUEEN"
            Object promoObj = map.get("promotionTo");

            if (moveType == Move.MoveType.PROMOTION && promoObj instanceof String) {
                String promotionToString = (String) promoObj;

                Game game = gameService.getGameState(gameId);
                if (game == null) {
                    throw new IllegalStateException("Game not found for promotion: " + gameId);
                }

                Piece pieceBeingPromoted = game.getGameState().getBoard().getPiece(fromPos);
                if (pieceBeingPromoted == null || !(pieceBeingPromoted instanceof Pawn)) {
                    throw new IllegalStateException("Pawn not found at promotion source square: " + fromPos);
                }

                Color pawnColor = pieceBeingPromoted.getColor();

                switch (promotionToString.toUpperCase()) {
                    case "QUEEN":
                        promotionToPiece = new Queen(pawnColor, toPos);
                        break;
                    case "ROOK":
                        promotionToPiece = new Rook(pawnColor, toPos);
                        break;
                    case "BISHOP":
                        promotionToPiece = new Bishop(pawnColor, toPos);
                        break;
                    case "KNIGHT":
                        promotionToPiece = new Knight(pawnColor, toPos);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid piece type for promotion: " + promotionToString);
                }

            } else if (moveType == Move.MoveType.PROMOTION && promoObj == null) {
                throw new IllegalArgumentException("Promotion move type specified but no promotionTo piece provided.");
            } else if (moveType == Move.MoveType.PROMOTION && !(promoObj instanceof String)) {
                throw new IllegalArgumentException("Invalid format for promotionTo field.");
            }

            return new Move(fromPos, toPos, promotionToPiece, moveType);
        }
        throw new IllegalArgumentException("Invalid payload for move: not a Map");
    }
}
