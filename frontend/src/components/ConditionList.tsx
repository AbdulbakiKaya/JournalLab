import React from "react";

interface Props {
  conditions: string[];
}

const ConditionList: React.FC<Props> = ({ conditions }) => {
  return (
    <div style={{ marginTop: 20 }}>
      <h3>Diagnoser</h3>

      {conditions.length === 0 && <p>Inga diagnoser registrerade.</p>}

      <ul>
        {conditions.map((c, i) => (
          <li key={i}>
            <b>{c.text}</b> – {c.severity}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ConditionList;