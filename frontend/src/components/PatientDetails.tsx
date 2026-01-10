import React, { useEffect, useState } from "react";
import { getPatientDetails } from "../api/patientApi";
import EncounterForm from "./EncounterForm";
import EncounterList from "./EncounterList";
import ConditionForm from "./ConditionForm";
import ConditionList from "./ConditionList";
import MessageForm from "./MessageForm";
import MessageList from "./MessageList";

interface Props {
  id: number;
  auth: string;
}

export default function PatientDetails({ id, auth }: Props) {
  const [details, setDetails] = useState<any>(null);

  const load = () => {
    getPatientDetails(id, auth)
      .then(setDetails)
      .catch((err) => console.error("Error loading details:", err));
  };

  useEffect(() => {
    load();
  }, [id, auth]);

  if (!details) return <p>Loading...</p>;

  return (
    <div style={{ padding: "20px" }}>
      <h2>
        {details.firstName} {details.lastName}
      </h2>

      {/* ENCOUNTERS */}
      <EncounterForm patientId={id} auth={auth} onCreated={load} />
      <EncounterList encounters={details.encounters} />

      {/* CONDITIONS */}
      <ConditionForm patientId={id} auth={auth} onCreated={load} />
      <ConditionList conditions={details.conditions} />

      {/* MESSAGES */}
     <MessageForm
       patientId={id}
       receiverId={details.userId}
       auth={auth}
       onSent={load}
     />


      <MessageList messages={details.messages} />
    </div>
  );
}