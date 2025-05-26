import { useState } from 'react';
import Square from './Square.jsx';
import './Chessboard.css'
import king_w from '../assets/pieces/king-w.svg';
import pawn_w from '../assets/pieces/pawn-w.svg';

const pieceImages = {
  king_w,
  pawn_w
};

const initialBoard = Array(8).fill(null).map(() => Array(8).fill(null));
initialBoard[7][4] = 'king_w';

export default function Chessboard(){
    const [board, setBoard] = useState(initialBoard);
    const [draggedPiece, setDraggedPiece] = useState(null);

    function handleDragStart(fromRow, fromCol){
        setDraggedPiece({fromRow, fromCol});
    }

    function handleDrop(toRow, toCol){
        if(!draggedPiece) return;
        const newBoard = board.map(row => row.slice())
        newBoard[toRow][toCol]=board[draggedPiece.fromRow][draggedPiece.fromCol];
        if(toRow==draggedPiece.fromRow&&toCol==draggedPiece.fromCol)
            return;
        newBoard[draggedPiece.fromRow][draggedPiece.fromCol]=null;
        setBoard(newBoard);
        setDraggedPiece(null);
    }

    const squares=[]

    for(let row=0;row<8;row++){
        for(let col=0;col<8;col++){
            const isBlack = (row+col) % 2==0;
            const pieceName = board[row][col];
            const image = pieceName ? pieceImages[pieceName] : null;
            squares.push(
            <Square 
                key={'${row}-${col}'}
                row={row}
                col={col}
                onDrop={handleDrop}
                onDragStart={handleDragStart}
                isBlack={isBlack} 
                piece={image}
                />);
        }
    }
    return <div className='board'>{squares}</div>
}