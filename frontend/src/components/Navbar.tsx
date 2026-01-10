import React from "react";

interface Props {
  username: string | null;
  role: string | null;
  onLogout: () => void;
}

export default function Navbar({ username, role, onLogout }: Props) {
  return (
    <div style={{
      padding: "10px",
      background: "#f0f0f0",
      marginBottom: "20px",
      display: "flex",
      justifyContent: "space-between"
    }}>
      <div>
        <b>Journal System</b>
      </div>

      {username && (
        <div>
          Inloggad som: <b>{username}</b> ({role})
          <button style={{ marginLeft: 20 }} onClick={onLogout}>Logga ut</button>
        </div>
      )}
    </div>
  );
}
