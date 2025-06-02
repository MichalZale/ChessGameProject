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

function App() {
  const [screen, setScreen] = useState("menu");
  const [user, setUser] = useState(null);
  const [gameData, setGameData] = useState(null);

  function handleCreateGame() {
    setScreen("create-game");
  }

  function handleJoinGame() {
    setScreen("game");
  }

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
            onJoinGame={() => setScreen("game")}
          />
        )}

        {screen === "create-game" && (
          <GameCreationMenu
            onReturn={() => setScreen("menu")}
            onCreate={({ whiteTime, blackTime, whiteInc, blackInc }) => {
              createGame({
                whiteTime: whiteTime,
                blackTime: blackTime,
                whiteTimeIncrease: whiteInc,
                blackTimeIncrease: blackInc,
                whitePlayerID: 1,
                blackPlayerID: 2
              }).then(data => {
                console.log("BACKEND BOARD DATA:", data);
                const mappedBoard = mapBackendBoardToFrontend(data.board);
                setGameData({board: mappedBoard});
                setScreen("game");
              });
            }}

          />
        )}

        {screen === "game" && gameData && <Chessboard backendBoard={gameData.board} />}
      </main>
    </div>
  );
}

export default App;
