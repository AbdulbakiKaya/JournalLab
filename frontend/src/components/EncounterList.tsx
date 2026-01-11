import React from "react";

type Props = {
  encounters: any[];
};

export default function EncounterList({ encounters }: Props) {
  if (!encounters || encounters.length === 0) {
    return (
      <div style={{ marginTop: 16 }}>
        <h3>Journalanteckningar</h3>
        <p style={{ opacity: 0.7 }}>Inga journalanteckningar.</p>
      </div>
    );
  }

  return (
    <div style={{ marginTop: 16 }}>
      <h3>Journalanteckningar</h3>

      <ul style={{ paddingLeft: 18 }}>
        {encounters.map((e: any) => {
          const time = e.startTime ?? "";
          const note = e.note ?? "";
          const writerType = e.practitionerType ?? "UNKNOWN";
          const writerName = e.practitionerName ?? "Okänd";

          return (
            <li key={e.id} style={{ marginBottom: 8 }}>
              {time} – {note} – {writerType}: {writerName}
            </li>
          );
        })}
      </ul>
    </div>
  );
}