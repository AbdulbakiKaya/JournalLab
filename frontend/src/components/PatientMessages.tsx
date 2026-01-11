import React, { useEffect, useMemo, useState } from "react";
import { getPatientThread, sendPatientMessage, ThreadType, MessageDto } from "../api/messageApi";

type Props = {
  patientId: number;
  auth: string;
  role: string | null;
  userId: number | null;
};

export default function PatientMessages({ patientId, auth, role }: Props) {
  const [thread, setThread] = useState<ThreadType>("DOCTOR");
  const [messages, setMessages] = useState<MessageDto[]>([]);
  const [text, setText] = useState("");

  const normalizedRole = useMemo(() => (role ?? "").toUpperCase(), [role]);

  // PATIENT: kan switcha (DOCTOR/STAFF). DOCTOR/STAFF: låst till sin tråd.
  const canSwitchThreads = useMemo(() => normalizedRole === "PATIENT", [normalizedRole]);

  // Sätt korrekt default-tråd när roll ändras
  useEffect(() => {
    if (normalizedRole === "STAFF") setThread("STAFF");
    else setThread("DOCTOR"); // PATIENT & DOCTOR defaultar på DOCTOR
  }, [normalizedRole]);

  const canWrite = useMemo(() => {
    if (normalizedRole === "PATIENT") return true;
    if (normalizedRole === "DOCTOR") return thread === "DOCTOR";
    if (normalizedRole === "STAFF") return thread === "STAFF";
    return false;
  }, [normalizedRole, thread]);

  async function load() {
    try {
      const data = await getPatientThread(patientId, thread, auth);
      setMessages(data);
    } catch (e) {
      console.error(e);
    }
  }

  useEffect(() => {
    load();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [thread, patientId]);

  async function onSend() {
    if (!text.trim()) return;

    try {
      await sendPatientMessage(patientId, thread, text, auth);
      setText("");
      load();
    } catch (e) {
      console.error(e);
      alert("Kunde inte skicka meddelande (behörighet eller policy).");
    }
  }

  return (
    <div style={{ marginTop: 16 }}>
      <h3>Meddelanden</h3>

      {canSwitchThreads && (
        <div style={{ display: "flex", gap: 8, marginBottom: 10 }}>
          <button onClick={() => setThread("DOCTOR")} disabled={thread === "DOCTOR"}>
            Doctor
          </button>
          <button onClick={() => setThread("STAFF")} disabled={thread === "STAFF"}>
            Övrig personal
          </button>
        </div>
      )}

      <div style={{ border: "1px solid #ddd", padding: 10, minHeight: 120, marginBottom: 10 }}>
        {messages.length === 0 ? (
          <p style={{ opacity: 0.7 }}>Inga meddelanden.</p>
        ) : (
          messages.map((m) => (
            <div key={m.id} style={{ marginBottom: 8 }}>
              <div style={{ fontSize: 12, opacity: 0.7 }}>{m.timestamp}</div>
              <div>
                <strong>{m.senderName ?? "Okänd"}:</strong> {m.text}
              </div>
            </div>
          ))
        )}
      </div>

      {canWrite ? (
        <>
          <textarea
            rows={3}
            style={{ width: "100%", marginBottom: 8 }}
            value={text}
            onChange={(e) => setText(e.target.value)}
            placeholder="Skriv…"
          />
          <button onClick={onSend}>Skicka</button>
        </>
      ) : (
        <p style={{ opacity: 0.75 }}>Du kan inte skriva i denna konversation.</p>
      )}
    </div>
  );
}