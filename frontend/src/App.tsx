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
  const [selectedPatient, setSelectedPatient] = useState<number | null>(null);

  const logout = () => {
    setAuth(null);
    setUsername(null);
    setRole(null);
    setSelectedPatient(null);
  };

  if (!auth) {
    return (
      <div style={{ padding: 20 }}>
        <h1>Journal System</h1>
        <LoginPage
          onLogin={(token, user, role) => {
            setAuth(token);
            setUsername(user);
            setRole(role);
          }}
        />
        <RegisterPage />
      </div>
    );
  }

  return (
    <div>
      <Navbar username={username} role={role} onLogout={logout} />

      {!selectedPatient ? (
        <PatientListPage auth={auth} onSelect={setSelectedPatient} />
      ) : (
        <PatientDetailsPage
          auth={auth}
          patientId={selectedPatient}
        />
      )}
    </div>
  );
}

export default App;