import React from "react";
import "./StartingMenu.css";

export default function StartingMenu({ onCreateGame, onJoinGame }) {
  return (
    <div className="starting-menu">
      <h1>Welcome to ReactChess!</h1>
      <div className="menu-buttons">
        <button onClick={onCreateGame}>Create Game</button>
        <button onClick={onJoinGame}>Join Game</button>
      </div>
    </div>
  );
}
