import React, { useState, useEffect } from 'react';
import Square from './Square.jsx';
import './Chessboard.css'
import king_w from '../assets/pieces/king-w.svg';
import king_b from '../assets/pieces/king-b.svg';
import pawn_w from '../assets/pieces/pawn-w.svg';
import pawn_b from '../assets/pieces/pawn-b.svg';
import knight_w from '../assets/pieces/knight-w.svg';
import knight_b from '../assets/pieces/knight-b.svg';
import bishop_w from '../assets/pieces/bishop-w.svg';
import bishop_b from '../assets/pieces/bishop-b.svg';
import queen_w from '../assets/pieces/queen-w.svg';
import queen_b from '../assets/pieces/queen-b.svg';
import rook_w from '../assets/pieces/rook-w.svg';
import rook_b from '../assets/pieces/rook-b.svg';

const pieceImages = {
    king_w, king_b, pawn_w, pawn_b, knight_w, knight_b,
    bishop_w, bishop_b, queen_w, queen_b, rook_w, rook_b,
};

const initialBoard = Array(8).fill(null).map(() => Array(8).fill(null));

export default function Chessboard({ backendBoard, playerColor, sendGameMessage, user, gameId }) {
    const [board, setBoard] = useState(backendBoard || initialBoard);
    const [draggedPiece, setDraggedPiece] = useState(null);

    useEffect(() => {
        if (backendBoard) {
            setBoard(backendBoard);
        }
    }, [backendBoard]);

    function handleDragStart(fromRow, fromCol) {
        setDraggedPiece({ fromRow, fromCol });
    }

    function handleDrop(toRow, toCol) {
        if (!draggedPiece) return;
        if (toRow === draggedPiece.fromRow && toCol === draggedPiece.fromCol) {
            setDraggedPiece(null);
            return;
        }

        const fromRow = draggedPiece.fromRow;
        const fromCol = draggedPiece.fromCol;

        const pieceName = board[fromRow]?.[fromCol];
        let moveType = "NORMAL";
        let promotionToPiece = null;

        if (pieceName) {
            const pieceType = pieceName.split('_')[0];             const pieceColor = pieceName.split('_')[1]; 
                        if (pieceType === "king") {
                if (Math.abs(toCol - fromCol) === 2 && toRow === fromRow) {
                    moveType = (toCol > fromCol) ? "CASTLE_KINGSIDE" : "CASTLE_QUEENSIDE";
                }
            }
                        else if (pieceType === "pawn") {
                const whitePromotionRank = 0;                 const blackPromotionRank = 7; 
                                if (Math.abs(toRow - fromRow) === 2 && fromCol === toCol) {
                    moveType = "PAWN_DOUBLE_PUSH";
                }
                                                                                                                                                                                                                                                                                                                                                                                                                                

                                if ((pieceColor === 'w' && toRow === whitePromotionRank) ||
                    (pieceColor === 'b' && toRow === blackPromotionRank)) {
                    moveType = "PROMOTION";
                                                                                const chosenPromotion = prompt("Promote to (Q, R, B, K)?", "Q")?.toUpperCase();
                    if (chosenPromotion && ["Q", "R", "B", "N"].includes(chosenPromotion)) {
                                                                                                                                                     promotionToPiece = chosenPromotion === "K" ? "KNIGHT" :                                            chosenPromotion === "Q" ? "QUEEN" :
                                           chosenPromotion === "R" ? "ROOK" :
                                           chosenPromotion === "B" ? "BISHOP" : null;
                        if (!promotionToPiece) {
                            alert("Invalid promotion choice!");
                            setDraggedPiece(null);
                            return; 
                        }
                    } else {
                        alert("Promotion cancelled or invalid choice.");
                        setDraggedPiece(null);
                        return; 
                    }
                }
            }
        }

        const move = {
            from: { row: fromRow, column: fromCol },
            to: { row: toRow, column: toCol },
            promotionTo: promotionToPiece, 
            moveType: moveType
        };

        console.log("SENT over WS (Chessboard.jsx):", {
            gameId: gameId, 
            userId: user?.userID ?? user?.id, 
            type: "MOVE",
            payload: move
        });

        
        if (!(user?.userID ?? user?.id)) {
            console.error("UserID is undefined. Move not sent.");
            alert("Error: User ID is missing. Cannot make a move.");
            setDraggedPiece(null);
            return;
        }
        if (!gameId) {
            console.error("GameID is undefined. Move not sent.");
            alert("Error: Game ID is missing. Cannot make a move.");
            setDraggedPiece(null);
            return;
        }

        sendGameMessage({
            gameId: gameId,
            userId: user?.userID ?? user?.id,
            type: "MOVE",
            payload: move
        });

        setDraggedPiece(null);
    }


    const squares = [];
    const effectivePlayerColor = playerColor || "white";

    const rowIterationOrder = effectivePlayerColor === "white"
        ? [...Array(8).keys()]
        : [...Array(8).keys()].reverse();

    const colIterationOrder = effectivePlayerColor === "black"
        ? [...Array(8).keys()]
        : [...Array(8).keys()].reverse();

    for (const row of rowIterationOrder) {
        for (const col of colIterationOrder) {
            const isBlack = (row + col) % 2 !== 1;
            const pieceName = board[row]?.[col] ?? null;
            const image = pieceName ? pieceImages[pieceName] : null;

            squares.push(
                <Square
                    key={`${row}-${col}`}
                    row={row}
                    col={col}
                    onDrop={handleDrop}
                    onDragStart={handleDragStart}
                    isBlack={isBlack}
                    piece={image}
                />
            );
        }
    }

    return <div className='board'>{squares}</div>;
}

export function mapBackendBoardToFrontend(backendBoard) {
    return backendBoard.map(row =>
        row.map(piece => {
            if (!piece) return null;
            if (!piece.type || !piece.color) {
                console.warn("Bad piece object:", piece);
                return null;
            }
            const type = piece.type.toLowerCase();
            const color = piece.color === "WHITE" ? "w" : "b";
            return `${type}_${color}`;
        })
    );
}