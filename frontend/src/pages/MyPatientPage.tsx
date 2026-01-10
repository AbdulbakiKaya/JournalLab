import React, { useEffect, useState } from "react";
import PatientDetails from "../components/PatientDetails";
import { getPatientDetails } from "../api/patientApi";

interface Props {
  auth: string;
  patientId: number;
}

const MyPatientPage: React.FC<Props> = ({ auth, patientId }) => {
  const [details, setDetails] = useState<any>(null);

  useEffect(() => {
    getPatientDetails(patientId, auth).then(setDetails);
  }, [patientId, auth]);

  if (!details) return <div>Loading...</div>;

  return (
    <div>
      <h2>My Patient Details</h2>
      <PatientDetails id={patientId} auth={auth} />
    </div>
  );
};

export default MyPatientPage;