import './Square.css';

export default function Square({row, col, isBlack, piece, onDragStart, onDrop}){
    const className = isBlack ? 'square black' : 'square white';
    return (
        <div className={className}
        onDragOver={(e) => e.preventDefault()}
        onDrop={()=>onDrop(row, col)}
        >
        {piece && 
            <img src={piece} 
            alt="piece" 
            draggable={true} 
            className="piece"
            onDragStart={() => onDragStart(row, col)} 
            />
        }
    </div>);
}
