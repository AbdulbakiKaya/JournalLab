import React from "react";
import PatientDetails from "../components/PatientDetails";

interface Props {
  auth: string;
  patientId: number;
  role: string | null;
  userId: number | null;
  practitionerId: number | null;
}

const PatientDetailsPage: React.FC<Props> = ({ auth, patientId, role, userId, practitionerId }) => {
  return (
    <div>
      <h2>Patient Details</h2>
      <PatientDetails
        id={patientId}
        auth={auth}
        role={role}
        userId={userId}
        practitionerId={practitionerId}
      />
    </div>
  );
};

export default PatientDetailsPage;