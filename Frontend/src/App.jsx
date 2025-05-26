import { useState } from "react";
import Navbar from "./components/Navbar";
import StartingMenu from "./components/StartingMenu";
import LoginForm from "./components/LoginForm";
import RegisterForm from "./components/RegisterForm";
import Chessboard from "./components/Chessboard";

function App() {
  const [screen, setScreen] = useState("menu");
  const [user, setUser]     = useState(null);

  function handleCreateGame() {
    setScreen("game"); 
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
            onCreateGame={() => setScreen("game")}
            onJoinGame={() => setScreen("game")}
          />
        )}

        {screen === "game" && <Chessboard />}
      </main>
    </div>
  );
}

export default App;
