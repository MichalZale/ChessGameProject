import { useState } from "react";
import Navbar from "./components/Navbar";
import StartingMenu from "./components/StartingMenu";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import Chessboard from "./components/Chessboard";
import GameCreationMenu from "./components/GameCreationMenu";
import "./components/GameCreationMenu.css";
import { createGame } from "./API/gameAPI";
import { mapBackendBoardToFrontend } from "./components/Chessboard";
import Timer from "./components/Timer";
import "./components/Timer.css";
import JoinGameForm from "./components/JoinGameComponent";
import "./components/JoinGameComponent.css";
import "./App.css"

function App() {
  const [screen, setScreen] = useState("menu");
  const [user, setUser] = useState(null);
  const [gameData, setGameData] = useState(null);

  return (
    <div className="app">
      <Navbar
        isLoggedIn={Boolean(user)}
        onProfileClick={() => alert("profile!")}
        onLoginClick={() => setScreen("login")}
        onRegisterClick={() => setScreen("register")}
      />

      <main className="main-content">
        {screen === "login" && (
          <LoginForm
            onSuccess={u => { setUser(u); setScreen("menu"); }}
            onCancel={() => setScreen("menu")}
          />
        )}

        {screen === "register" && (
          <RegisterForm
            onSuccess={u => { setUser(u); setScreen("menu"); }}
            onCancel={() => setScreen("menu")}
          />
        )}

        {screen === "menu" && (
          <StartingMenu
            onCreateGame={() => setScreen("create-game")}
            onJoinGame={() => setScreen("join-game")}
          />
        )}

        {screen === "create-game" && (
          <GameCreationMenu
            onReturn={() => setScreen("menu")}
            onCreate={({ whiteTime, blackTime, whiteInc, blackInc, playerColor }, onCodeReceived, onError) => {
              const playerOneID = user ? user.id : 1; // Przykładowe ID, dostosuj do swojej logiki użytkownika
              const playerTwoID = user ? (playerOneID === 1 ? 2 : 1) : 2; // Przykładowe ID dla drugiego gracza

              createGame({
                whiteTime: whiteTime,
                blackTime: blackTime,
                whiteTimeIncrease: whiteInc,
                blackTimeIncrease: blackInc,
                whitePlayerID: playerColor === "white" ? playerOneID : playerTwoID,
                blackPlayerID: playerColor === "black" ? playerOneID : playerTwoID,
              }).then(data => {
                if (data && data.gameId && data.inviteCode) {
                  console.log("Game created successfully:", data);
                  const mappedBoard = mapBackendBoardToFrontend(data.board);
                  setGameData({
                     ...data, 
                     board: mappedBoard,
                     playerColor: playerColor 
                    });

                  if (onCodeReceived) {
                    onCodeReceived(data.inviteCode); // Przekaż kod zaproszenia z powrotem do GameCreationMenu
                  }
                  
                  setTimeout(() => {
                    setScreen("game");
                  }, 3000);

                } else {
                  console.error("Failed to create game or missing data:", data);
                  if (onError) onError();
                }
              })
            }}

          />
        )}

        {screen === "join-game" && (
          <JoinGameForm
            onJoin={(gameCode) => {
              // Here you would typically make an API call to join the game
              console.log("Joining game with code:", gameCode);
              // For now, just switch to game screen
              alert(`Would join game with code: ${gameCode}`);
              // Later replace with actual API call and game data setting
              // setGameData(joinGameResponse); 
              // setScreen("game");
            }}
            onCancel={() => setScreen("menu")}
          />
        )}


        {screen === "game" && gameData && (
          <div className="game-area">
            <Chessboard backendBoard={gameData.board} playerColor={gameData.playerColor}/>
            <div className="timers-panel">
              <Timer
                label="Black"
                timeInSeconds={gameData.timer ? gameData.timer.blackTime : 0}
                isActive={gameData.sideToMove === 'BLACK'}
              />
              <Timer
                label="White"
                timeInSeconds={gameData.timer ? gameData.timer.whiteTime : 0}
                isActive={gameData.sideToMove === 'WHITE'}
              />
            </div>
          </div>
        )}
      </main>
    </div> 
  ); 
}

export default App;
