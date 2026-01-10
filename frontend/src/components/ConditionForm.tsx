import React, { useState } from "react";

interface Props {
  patientId: number;
  auth: string;
  onCreated: () => void;
}

const ConditionForm: React.FC<Props> = ({ patientId, auth, onCreated }) => {
  const [text, setText] = useState("");
  const [severity, setSeverity] = useState("Mild");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    await fetch(`http://localhost:8080/api/conditions/patient/${patientId}`, {
      method: "POST",
      headers: {
        "Authorization": "Basic " + auth,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        text,
        severity,
        practitionerId: 1,     // <-- Doctor ID
      }),
    });

    setText("");
    onCreated();
  };

  return (
    <form onSubmit={submit} style={{ marginTop: 20 }}>
      <h3>Ny diagnos</h3>

      <input
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Diagnos text"
        style={{ width: "100%", marginBottom: 8 }}
      />

      <select
        value={severity}
        onChange={(e) => setSeverity(e.target.value)}
        style={{ marginBottom: 8 }}
      >
        <option value="Mild">Mild</option>
        <option value="Moderate">Moderate</option>
        <option value="Severe">Severe</option>
      </select>

      <button>Spara diagnos</button>
    </form>
  );
};

export default ConditionForm;
