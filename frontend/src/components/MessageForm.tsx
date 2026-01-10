import React, { useState } from "react";

interface Props {
  patientId: number;
  receiverId: number;
  auth: string;
  onSent: () => void;
}

const MessageForm: React.FC<Props> = ({ patientId, receiverId, auth, onSent }) => {
  const [text, setText] = useState("");

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();

    await fetch("http://localhost:8080/api/messages", {
      method: "POST",
      headers: {
        "Authorization": "Basic " + auth,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        patientId,
        receiverId,
        text,
      }),
    });

    setText("");
    onSent();
  };

  return (
    <form onSubmit={submit} style={{ marginTop: 20 }}>
      <h3>Skicka meddelande</h3>

      <textarea
        value={text}
        onChange={(e) => setText(e.target.value)}
        placeholder="Skriv meddelande..."
        style={{ width: "100%", height: "80px" }}
      />

      <button type="submit" style={{ marginTop: 10 }}>
        Skicka
      </button>
    </form>
  );
};

export default MessageForm;