import React, { useState } from "react";

interface Props {
  patientId: number;
  auth: string;
  onCreated: () => void;
}

const EncounterForm: React.FC<Props> = ({ patientId, auth, onCreated }) => {
  const [note, setNote] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    await fetch(`http://localhost:8080/api/encounters/patient/${patientId}`, {
      method: "POST",
      headers: {
        "Authorization": "Basic " + auth,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        note,
        locationId: null,
      }),
    });

    setNote("");
    onCreated();
  };

  return (
    <form onSubmit={submit} style={{ marginTop: 20 }}>
      <h3>Ny journalnotering</h3>

      <textarea
        value={note}
        onChange={(e) => setNote(e.target.value)}
        placeholder="Skriv journalanteckning..."
        style={{ width: "100%", height: "80px" }}
      />

      <button type="submit" style={{ marginTop: 10 }}>
        Spara encounter
      </button>
    </form>
  );
};

export default EncounterForm;