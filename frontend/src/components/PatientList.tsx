import React, { useEffect, useState } from "react";
import { getAllPatients } from "../api/patientApi";

export default function PatientList({ auth, onSelect }: any) {
  const [patients, setPatients] = useState([]);

  useEffect(() => {
    getAllPatients(auth).then(setPatients).catch(console.error);
  }, [auth]);

  return (
    <div>
      <h2>All Patients</h2>

      {patients.map((p: any) => (
        <div key={p.id}>
          <button onClick={() => onSelect(p.id)}>
            {p.firstName} {p.lastName}
          </button>
        </div>
      ))}
    </div>
  );
}