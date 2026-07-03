import { useState } from "react";
import "./AuthForm.css";

export default function LoginForm({ onSuccess, onCancel }) {
  const [login, setLogin]       = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr]           = useState(null);

  function handleSubmit(e) {
    e.preventDefault();
    // TODO 
    if (login && password) {
      onSuccess({ login });
    } else {
      setErr("Both fields are required.");
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <h2>Log in</h2>

      <input
        type="text"
        placeholder="Login"
        value={login}
        onChange={e => setLogin(e.target.value)}
        required
      />

      <input
        type="password"
        placeholder="Password"
        value={password}
        onChange={e => setPassword(e.target.value)}
        required
      />

      {err && <div className="auth-error">{err}</div>}

      <div className="auth-buttons">
        <button type="submit">Log in</button>
        <button type="button" onClick={onCancel}>Back</button>
      </div>
    </form>
  );
}
