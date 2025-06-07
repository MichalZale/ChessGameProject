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

export default function Chessboard({ backendBoard, playerColor }) {
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
        const newBoard = board.map(row => row.slice());
        newBoard[toRow][toCol] = board[draggedPiece.fromRow][draggedPiece.fromCol];
        newBoard[draggedPiece.fromRow][draggedPiece.fromCol] = null;
        setBoard(newBoard);
        setDraggedPiece(null);
    }

    const squares = [];
    const effectivePlayerColor = playerColor || "white";

    const rowIterationOrder = effectivePlayerColor === "black"
        ? [...Array(8).keys()]
        : [...Array(8).keys()].reverse();

    const colIterationOrder = effectivePlayerColor === "white"
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