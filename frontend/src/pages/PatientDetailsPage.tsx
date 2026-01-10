import React from "react";
import PatientDetails from "../components/PatientDetails";

interface Props {
  auth: string;
  patientId: number;
}

const PatientDetailsPage: React.FC<Props> = ({ auth, patientId }) => {
  return (
    <div>
      <h2>Patient Details</h2>
      <PatientDetails id={patientId} auth={auth} />
    </div>
  );
};

export default PatientDetailsPage;