import React from "react";

interface Props {
  encounters: any[];
}

const EncounterList: React.FC<Props> = ({ encounters }) => {
  return (
    <div style={{ marginTop: 20 }}>
      <h3>Encounters</h3>

      {encounters.length === 0 && <p>Inga journalanteckningar ännu.</p>}

      <ul>
        {encounters.map((e) => (
          <li key={e.id}>
            {new Date(e.startTime).toLocaleString()} – {e.note}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default EncounterList;