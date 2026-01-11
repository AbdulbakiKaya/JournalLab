import React from "react";

type Props = {
  conditions: any[];
};

export default function ConditionList({ conditions }: Props) {
  if (!conditions || conditions.length === 0) {
    return (
      <div style={{ marginTop: 16 }}>
        <h3>Diagnoser</h3>
        <p style={{ opacity: 0.7 }}>Inga diagnoser.</p>
      </div>
    );
  }

  return (
    <div style={{ marginTop: 16 }}>
      <h3>Diagnoser</h3>

      <ul style={{ paddingLeft: 18 }}>
        {conditions.map((c: any) => {
          const text = c.text ?? "";
          const severity = c.severity ?? "";
          const writerName = c.practitionerName ?? "Okänd";

          return (
            <li key={c.id} style={{ marginBottom: 8 }}>
              {text} – {severity} – Doctor: {writerName}
            </li>
          );
        })}
      </ul>
    </div>
  );
}