import React from "react";
import PatientDetails from "../components/PatientDetails";

interface Props {
  auth: string;
  role: string | null;
  userId: number | null;
  patientId: number;
  practitionerId: number | null;
}

const MyPatientPage: React.FC<Props> = ({ auth, role, userId, patientId, practitionerId }) => {
  return (
    <div>
      <h2>My Patient Details</h2>
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

export default MyPatientPage;