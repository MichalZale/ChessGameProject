import { useState, useEffect } from "react";
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
import "./App.css";
import { useChessWebSocket } from "./API/ChessWebSocket";
import GameActionsPanel from "./components/GameActionsPanel";
import "./components/GameActionsPanel.css";
import { joinGame } from "./API/gameAPI"
import { createGuestUser } from "./API/userAPI";

const WS_MSG_TYPES = {
  MOVE: "MOVE",
  RESIGN: "RESIGN",
  DRAW_PROPOSE: "DRAW_PROPOSE",
  DRAW_ACCEPT: "DRAW_ACCEPT",
  DRAW_REJECT: "DRAW_REJECT",
};

function App() {
  const [user, setUser] = useState(null);
  const [screen, setScreen] = useState("menu");
  const [gameData, setGameData] = useState(null);
  
  useEffect(() => {
    if (gameData) {
      console.log("<<<<< App.jsx - gameData updated >>>>>");
      console.log("Current gameData.isDrawOffered:", gameData.isDrawOffered);
      console.log("Current gameData.drawOfferedByUserID:", gameData.drawOfferedByUserID);
      console.log("Current gameData.whiteUserId:", gameData.whiteUserId);
      console.log("Current gameData.blackUserId:", gameData.blackUserId);
      console.log("Current gameData.status:", gameData.status);
      console.log("Current gameData.gameResult:", gameData.gameResult);
      console.log("Full gameData:", JSON.parse(JSON.stringify(gameData))); 
    }
  }, [gameData]); 
  useEffect(() => {
    if (!user) {
      createGuestUser()
        .then(res => setUser(res))
        .catch(() => alert("Could not create guest user!"));
    }
  }, [user]);

  

  function getUserId(user) {
    return user?.userID ?? user?.id;
  }

  const sendGameMessage = useChessWebSocket(
    gameData ? gameData.gameId : null,
    handleGameMessage   );

  function handleGameMessage(newGameData) {
    console.log("<<<<< WebSocket Received (handleGameMessage) >>>>>");
    console.log("Raw newGameData:", JSON.parse(JSON.stringify(newGameData))); 
    if (newGameData.error) {
      console.error("Error in game update:", newGameData.error);
      return;
    }

    const mappedBoard = newGameData.gameState?.board?.board 
        ? mapBackendBoardToFrontend(newGameData.gameState.board.board) 
        : gameData?.board; 

    setGameData(prevGameData => {
      console.log("<<<<< Inside setGameData (handleGameMessage) >>>>>");
      console.log("prevGameData.isDrawOffered:", prevGameData?.isDrawOffered);
      console.log("prevGameData.drawOfferedByUserID:", prevGameData?.drawOfferedByUserID);
      console.log("newGameData.isDrawOffered (from WebSocket):", newGameData.isDrawOffered);
      console.log("newGameData.drawOfferedByUserID (from WebSocket):", newGameData.drawOfferedByUserID);

      let determinedPlayerColor = prevGameData?.playerColor;
      if (!determinedPlayerColor && typeof newGameData.whiteUserId !== 'undefined' && typeof newGameData.blackUserId !== 'undefined' && user) {
        const currentUserId = getUserId(user);
        if (newGameData.whiteUserId === currentUserId) {
          determinedPlayerColor = "white";
        } else if (newGameData.blackUserId === currentUserId) {
          determinedPlayerColor = "black";
        }
      }

      const nextGameData = {
        ...prevGameData, 
        ...newGameData,
        board: mappedBoard || prevGameData?.board, 
        playerColor: determinedPlayerColor, 
        gameState: newGameData.gameState || prevGameData?.gameState,
        whiteTime: typeof newGameData.timer?.whiteTime === 'number' ? newGameData.timer.whiteTime : prevGameData?.timer?.whiteTime,
        blackTime: typeof newGameData.timer?.blackTime === 'number' ? newGameData.timer.blackTime : prevGameData?.timer?.blackTime,
      };
      console.log("nextGameData to be set:", JSON.parse(JSON.stringify(nextGameData)));
      console.log("<<<<< Exiting setGameData (handleGameMessage) >>>>>");
      return nextGameData;
    });
  }

  const handleOfferDraw = () => {
    if (!gameData || !user) return;
    console.log("Offering draw...");
    sendGameMessage({
      gameId: gameData.gameId,       userId: getUserId(user),       type: WS_MSG_TYPES.DRAW_PROPOSE, 
      payload: {} 
    });
  };

  const handleResign = () => {
    if (!gameData || !user) return;
    console.log("Resigning game...");
    sendGameMessage({
      gameId: gameData.gameId, 
      userId: getUserId(user), 
      type: WS_MSG_TYPES.RESIGN,
      payload: {}
    });
  };

  const handleAcceptDraw = () => {
    if (!gameData || !user || !gameData.isDrawOffered) return;
    console.log("Accepting draw...");
    sendGameMessage({
      gameId: gameData.gameId,
      userId: getUserId(user),
      type: WS_MSG_TYPES.DRAW_ACCEPT,
      payload: {}
    });
  };

  const handleRejectDraw = () => {
    if (!gameData || !user || !gameData.isDrawOffered) return;
    console.log("Rejecting draw...");
    sendGameMessage({
      gameId: gameData.gameId,
      userId: getUserId(user),
      type: WS_MSG_TYPES.DRAW_REJECT,
      payload: {}
    });
  };

  function isGuestUser(user) {
    return user?.username?.startsWith("Guest") || user?.isGuest === true;
  }

  return (
    <div className="app">
      <Navbar
        user={user}
        isLoggedIn={Boolean(user)}
        isGuest={Boolean(user) && isGuestUser(user)}
        onProfileClick={() => setScreen("profile")}
        onLoginClick={() => setScreen("login")}
        onRegisterClick={() => setScreen("register")}
        onLogout={() => {
          setUser(null);
          setScreen("menu");
          createGuestUser().then(setUser);
        }}
      />

      <main className="main-content">
        {screen === "profile" && user && !isGuestUser(user) && (
          <section className="profile-panel">
            <h1>Profile</h1>
            <div className="profile-details">
              <div>
                <span>Username</span>
                <strong>{user.username}</strong>
              </div>
              <div>
                <span>User ID</span>
                <strong>{getUserId(user)}</strong>
              </div>
              {user.email && (
                <div>
                  <span>Email</span>
                  <strong>{user.email}</strong>
                </div>
              )}
            </div>
            <button onClick={() => setScreen("menu")}>Back</button>
          </section>
        )}

        {screen === "login" && (
          <LoginForm
            onSuccess={(userData) => { 
              setUser(userData);
              setScreen("menu");
            }}
            onCancel={() => setScreen("menu")}
          />
        )}

        {screen === "register" && (
          <RegisterForm
            onSuccess={(userData) => {
              setUser(userData);
              setScreen("menu");
            }}
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
              const playerOneID = getUserId(user) ?? -1;
              const playerTwoID = -1;
              createGame({
                whiteTime: whiteTime,
                blackTime: blackTime,
                whiteTimeIncrease: whiteInc,
                blackTimeIncrease: blackInc,
                whitePlayerID: playerColor === "white" ? playerOneID : playerTwoID,
                blackPlayerID: playerColor === "black" ? playerOneID : playerTwoID,
              }).then(data => {
                if (data && data.gameId && data.inviteCode) {
                  const mappedBoard = mapBackendBoardToFrontend(data.gameState.board.board);
                  setGameData({
                    ...data,
                    board: mappedBoard,
                    playerColor: playerColor,
                    sideToMove: data.gameState.sideToMove,
                    gameState: data.gameState,
                    whiteTime: data.timer.whiteTime,
                    blackTime: data.timer.blackTime
                  });

                  if (onCodeReceived) {
                    onCodeReceived(data.inviteCode);
                  }
                  setTimeout(() => {
                    setScreen("game");
                  }, 3000);
                } else {
                  if (onError) onError();
                }
              })
            }}
          />
        )}

        {screen === "join-game" && (
          <JoinGameForm
            onJoin={async (gameCode) => {
              if (!user) {
                alert("You must be logged in to join a game.");
                return;
              }
              try {
                const currentUserId = getUserId(user);                 const data = await joinGame(gameCode, currentUserId);
                if (data && data.gameId) {
                  const mappedBoard = mapBackendBoardToFrontend(data.gameState.board.board);
                  let determinedPlayerColorOnJoin;
                  if (data.whiteUserId === currentUserId) {
                    determinedPlayerColorOnJoin = "white";
                  } else if (data.blackUserId === currentUserId) {
                    determinedPlayerColorOnJoin = "black";
                  } else {
                    determinedPlayerColorOnJoin = undefined; 
                    console.warn("User joined but is not assigned as white or black player.");
                  }

                  setGameData({
                    ...data,
                    board: mappedBoard,
                    playerColor: determinedPlayerColorOnJoin, 
                    sideToMove: data.gameState.sideToMove,
                    gameState: data.gameState,
                    whiteTime: data.timer.whiteTime,
                    blackTime: data.timer.blackTime
                  });
                  setScreen("game");
                } else {
                  alert("Could not join game. Invalid code or game is full.");
                }
              } catch {
                alert("Could not join game. Invalid code or game is full.");
              }
            }}
            onCancel={() => setScreen("menu")}
          />
        )}

        {screen === "game" && gameData && (
          <div className="game-area">
            <Chessboard
              backendBoard={gameData.board}
              playerColor={gameData.playerColor}
              sendGameMessage={sendGameMessage}
              user={user}
              gameId={gameData.gameId}
            />
            <div className="timers-panel">
              {gameData.playerColor === 'white' ? (
                <>
                  <Timer
                    label="Black" 
                    timeInSeconds={gameData.blackTime ?? (gameData.timer ? gameData.timer.blackTime : 0)}
                    isActive={gameData.gameState?.sideToMove === 'BLACK'}
                  />
                  <Timer
                    label="White" 
                    timeInSeconds={gameData.whiteTime ?? (gameData.timer ? gameData.timer.whiteTime : 0)}
                    isActive={gameData.gameState?.sideToMove === 'WHITE'}
                  />
                </>
              ) : ( 
                <>
                  <Timer
                    label="White" 
                    timeInSeconds={gameData.whiteTime ?? (gameData.timer ? gameData.timer.whiteTime : 0)}
                    isActive={gameData.gameState?.sideToMove === 'WHITE'}
                  />
                  <Timer
                    label="Black" 
                    timeInSeconds={gameData.blackTime ?? (gameData.timer ? gameData.timer.blackTime : 0)}
                    isActive={gameData.gameState?.sideToMove === 'BLACK'}
                  />
                </>
              )}
              <GameActionsPanel
                gameData={gameData}
                currentUser={user}   
                onOfferDraw={handleOfferDraw}
                onResign={handleResign}
                onAcceptDraw={handleAcceptDraw}
                onRejectDraw={handleRejectDraw}
              />
            </div>
          </div>
        )}
      </main>
    </div>
  );
}

export default App;
