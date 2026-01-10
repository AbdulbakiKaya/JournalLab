import React, { useState } from "react";

export default function RegisterPage() {
  const [role, setRole] = useState("PATIENT");

  const [form, setForm] = useState({
    username: "",
    password: "",

    firstName: "",
    lastName: "",
    personalNumber: "",

    practitionerFirstName: "",
    practitionerLastName: "",
    licenseNumber: "",
  });

  function update(field: string, value: string) {
    setForm({ ...form, [field]: value });
  }

  async function register() {
    await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ ...form, role }),
    });

    alert("Registrering klar!");
  }

  return (
    <div style={{ marginTop: 30 }}>
      <h2>Registrera ny användare</h2>

      <label>Roll:</label>
      <select value={role} onChange={(e) => setRole(e.target.value)}>
        <option>PATIENT</option>
        <option>DOCTOR</option>
        <option>STAFF</option>
      </select>

      <br/><br/>

      <input placeholder="Username" onChange={(e) => update("username", e.target.value)} /> <br/>
      <input type="password" placeholder="Password" onChange={(e) => update("password", e.target.value)} /> <br/>

      {role === "PATIENT" && (
        <>
          <input placeholder="Förnamn" onChange={(e) => update("firstName", e.target.value)} /> <br/>
          <input placeholder="Efternamn" onChange={(e) => update("lastName", e.target.value)} /> <br/>
          <input placeholder="Personnummer" onChange={(e) => update("personalNumber", e.target.value)} /> <br/>
        </>
      )}

      {(role === "DOCTOR" || role === "STAFF") && (
        <>
          <input placeholder="Förnamn" onChange={(e) => update("practitionerFirstName", e.target.value)} /> <br/>
          <input placeholder="Efternamn" onChange={(e) => update("practitionerLastName", e.target.value)} /> <br/>
          <input placeholder="Licensnummer" onChange={(e) => update("licenseNumber", e.target.value)} /> <br/>
        </>
      )}

      <button style={{ marginTop: 10 }} onClick={register}>
        Skapa konto
      </button>
    </div>
  );
}