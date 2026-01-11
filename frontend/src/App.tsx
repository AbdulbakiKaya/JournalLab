import React, { useState } from "react";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import PatientListPage from "./pages/PatientListPage";
import PatientDetailsPage from "./pages/PatientDetailsPage";
import Navbar from "./components/Navbar";

function App() {
  const [auth, setAuth] = useState<string | null>(null);
  const [username, setUsername] = useState<string | null>(null);
  const [role, setRole] = useState<string | null>(null);
  const [userId, setUserId] = useState<number | null>(null);
  const [patientId, setPatientId] = useState<number | null>(null);
  const [practitionerId, setPractitionerId] = useState<number | null>(null);

  const [selectedPatient, setSelectedPatient] = useState<number | null>(null);

  function handleLogout() {
    setAuth(null);
    setUsername(null);
    setRole(null);
    setUserId(null);
    setPatientId(null);
    setPractitionerId(null);
    setSelectedPatient(null);
  }

  return (
    <div>
      <Navbar username={username} role={role} onLogout={handleLogout} />

      {!auth ? (
        <div style={{ padding: 16 }}>
          <LoginPage
            onLogin={(token, user, r, uId, pId, pracId) => {
              setAuth(token);
              setUsername(user);
              setRole(r);
              setUserId(uId);
              setPatientId(pId);
              setPractitionerId(pracId);

              // Om man loggar in som personal, nollställ ev. tidigare val
              setSelectedPatient(null);
            }}
          />
          <RegisterPage />
        </div>
      ) : role === "PATIENT" ? (
        <div style={{ padding: 16 }}>
          {patientId !== null ? (
            <PatientDetailsPage
              auth={auth}
              patientId={patientId}
              role={role}
              userId={userId}
              practitionerId={practitionerId}
            />
          ) : (
            <p>PatientId saknas för inloggad patient.</p>
          )}
        </div>
      ) : !selectedPatient ? (
        <PatientListPage auth={auth} onSelect={setSelectedPatient} />
      ) : (
        <>
          <button onClick={() => setSelectedPatient(null)} style={{ margin: 16 }}>
            ← Byt patient
          </button>

          <PatientDetailsPage
            auth={auth}
            patientId={selectedPatient}
            role={role}
            userId={userId}
            practitionerId={practitionerId}
          />
        </>
      )}
    </div>
  );
}

export default App;