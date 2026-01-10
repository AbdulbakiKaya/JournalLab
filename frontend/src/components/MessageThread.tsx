import React, { useEffect, useState } from "react";
import { getPatientDetails } from "../api/patientApi";

export default function PatientDetails({ id, auth }: any) {
  const [details, setDetails] = useState<any>(null);

  useEffect(() => {
    getPatientDetails(id, auth).then(setDetails).catch(console.error);
  }, [id, auth]);

  return (
    <div>
      <h2>{details.firstName} {details.lastName}</h2>

      <h3>Encounters:</h3>
      <ul>
        {details.encounters.map((e: string, i: number) => (
          <li key={i}>{e}</li>
        ))}
      </ul>

      <h3>Conditions:</h3>
      <ul>
        {details.conditions.map((c: string, i: number) => (
          <li key={i}>{c}</li>
        ))}
      </ul>
    </div>
  );
}