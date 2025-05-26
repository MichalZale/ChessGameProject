// components/RegisterForm.jsx
import { useState } from "react";
import "./AuthForm.css";

export default function RegisterForm({ onSuccess, onCancel }) {
  const [step, setStep]             = useState(1);
  const [username, setUsername]     = useState("");
  const [password, setPassword]     = useState("");
  const [email, setEmail]           = useState("");
  const [err, setErr]               = useState(null);

  function next()  { setStep(step + 1); setErr(null); }
  function back()  { setStep(step - 1); setErr(null); }

  function handleSubmit(e) {
    e.preventDefault();
    if (step < 3) {
      next();
      return;
    }
    // TODO
    if (username && password && email) {
      onSuccess({ username });
    } else {
      setErr("All fields are required.");
    }
  }

  return (
    <form className="auth-form" onSubmit={handleSubmit}>
      <h2>Register</h2>

      {step === 1 && (
        <>
          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
          />
          <div className="auth-buttons">
            <button type="submit">Next</button>
            <button type="button" onClick={onCancel}>Back</button>
          </div>
        </>
      )}

      {step === 2 && (
        <>
          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
          <div className="auth-buttons">
            <button type="submit">Next</button>
            <button type="button" onClick={back}>Back</button>
          </div>
        </>
      )}

      {step === 3 && (
        <>
          <input
            type="email"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
            required
          />
          {err && <div className="auth-error">{err}</div>}
          <div className="auth-buttons">
            <button type="submit">Register</button>
            <button type="button" onClick={back}>Back</button>
          </div>
        </>
      )}
    </form>
  );
}
