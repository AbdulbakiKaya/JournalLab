import React from "react";
import PatientList from "../components/PatientList";

interface Props {
  auth: string;
  onSelect: (id: number) => void;
}

const PatientListPage: React.FC<Props> = ({ auth, onSelect }) => {
  return (
    <div>
      <h2>Patient List</h2>
      <PatientList auth={auth} onSelect={onSelect} />
    </div>
  );
};

export default PatientListPage;