import React, { useState } from "react";

interface Props {
  onLogin: (token: string, user: string, role: string) => void;
}

export default function LoginPage({ onLogin }: Props) {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleLogin() {
    const token = btoa(username + ":" + password);

    const res = await fetch("http://localhost:8080/api/auth/me", {
      headers: { "Authorization": "Basic " + token }
    });

    if (!res.ok) {
      setError("Fel användarnamn eller lösenord");
      return;
    }

    const me = await res.text();
    const role = res.headers.get("X-Role");

    onLogin(token, me, role || "UNKNOWN");
  }

  return (
    <div style={{ marginBottom: 20 }}>
      <h2>Login</h2>

      <input placeholder="Användarnamn" onChange={(e) => setUsername(e.target.value)} /> <br/>
      <input type="password" placeholder="Lösenord" onChange={(e) => setPassword(e.target.value)} /> <br/>

      <button onClick={handleLogin}>Logga in</button>

      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}