package com.example.ChessProject.controller.websocket;

import com.example.ChessProject.controller.dto.GameResponse;
import com.example.ChessProject.controller.dto.GameResponseMapper;
import com.example.ChessProject.controller.websocket.websocketMessage.WsMessage;
import com.example.ChessProject.controller.websocket.websocketMessage.WsType;
import com.example.ChessProject.model.Game;
import com.example.ChessProject.model.Move;
import com.example.ChessProject.model.Piece;
import com.example.ChessProject.model.Position;
import com.example.ChessProject.service.GameService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

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

        try {
            switch (type) {
                case MOVE:
                    System.out.print("Otrzymano wiadomość MOVE!");
                    Move move = parseMove(msg.getPayload());
                    int userId=msg.getUserId();
                    System.out.print("Ruch" + move);
                    Game game = gameService.makeMove(gameId, userId, move);
                    System.out.println("Ruszono się.");
                    System.out.println("Stan planszy przed wysłaniem odpowiedzi:");
                    System.out.println(game.getGameState().getBoard().toString());

                    GameResponse response = GameResponseMapper.toGameResponse(game);
                    messagingTemplate.convertAndSend("/topic/game-" + gameId, response);
                    System.out.println("Wysłano.");
                    break;

                case RESIGN:
                    Game resignedGame = gameService.resign(gameId);
                    GameResponse responseToResign = GameResponseMapper.toGameResponse(resignedGame);
                    messagingTemplate.convertAndSend("/topic/game-" + gameId, responseToResign);
                    break;

                case DRAW_PROPOSE:
                    Game proposedDrawGame = gameService.proposeDraw(gameId);
                    messagingTemplate.convertAndSend("/topic/game-" + gameId, proposedDrawGame);
                    break;

                case DRAW_ACCEPT:
                    Game drawAcceptedGame = gameService.acceptDraw(gameId);
                    messagingTemplate.convertAndSend("/topic/game-" + gameId, drawAcceptedGame);
                    break;

                case RESULT:
                    break;
            }
        } catch (Exception e) {
            messagingTemplate.convertAndSend("/topic/game-" + gameId,
                    "Error: " + e.getMessage());
        }
    }

    private Move parseMove(Object payload) {
        if (payload instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) payload;

            Map<String, Object> fromMap = (Map<String, Object>) map.get("from");
            Map<String, Object> toMap = (Map<String, Object>) map.get("to");

            int fromRow = (int) fromMap.get("row");
            int fromCol = (int) fromMap.get("column");
            Position from = new Position(fromRow, fromCol);

            int toRow = (int) toMap.get("row");
            int toCol = (int) toMap.get("column");
            Position to = new Position(toRow, toCol);

            String moveTypeStr = (String) map.get("moveType");
            Move.MoveType moveType = Move.MoveType.valueOf(moveTypeStr);

            Piece promotionTo = null;
            Object promoObj = map.get("promotionTo");
            if (promoObj != null) {
                Map<String, Object> promoMap = (Map<String, Object>) promoObj;
                String pieceType = (String) promoMap.get("type");
                String color = (String) promoMap.get("color");
                promotionTo = Piece.of(pieceType, color, to);
            }

            return new Move(new Position(fromRow, fromCol), new Position(toRow, toCol), promotionTo, moveType);
        }
        throw new IllegalArgumentException("Invalid payload for move");
    }
}
