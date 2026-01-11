import React, { useEffect, useState } from "react";

type DoctorOption = {
  id: number;
  firstName: string;
  lastName: string;
};

export default function RegisterPage() {
  const [role, setRole] = useState<"PATIENT" | "DOCTOR" | "STAFF">("PATIENT");

  const [form, setForm] = useState({
    username: "",
    password: "",

    firstName: "",
    lastName: "",
    personalNumber: "",

    practitionerFirstName: "",
    practitionerLastName: "",
    licenseNumber: "",
  });

  // Doctors dropdown
  const [doctors, setDoctors] = useState<DoctorOption[]>([]);
  const [assignedDoctorPractitionerId, setAssignedDoctorPractitionerId] = useState<number | "">("");

  const [error, setError] = useState<string>("");

  function update(field: string, value: string) {
    setForm({ ...form, [field]: value });
  }

  // Ladda doctors när man väljer PATIENT
  useEffect(() => {
    setError("");

    if (role !== "PATIENT") return;

    fetch("http://localhost:8080/api/practitioners/doctors")
      .then(async (res) => {
        if (!res.ok) {
          const text = await res.text().catch(() => "");
          throw new Error(`Kunde inte hämta doctors (${res.status}): ${text}`);
        }
        return res.json();
      })
      .then((list: DoctorOption[]) => {
        setDoctors(list);

        // välj första läkaren automatiskt om inget valt
        if (list.length > 0) {
          setAssignedDoctorPractitionerId((prev) => (prev === "" ? list[0].id : prev));
        } else {
          setAssignedDoctorPractitionerId("");
        }
      })
      .catch((e) => setError(e.message));
  }, [role]);

  async function register() {
    setError("");

    // PATIENT måste ha assigned doctor
    if (role === "PATIENT") {
      if (!assignedDoctorPractitionerId) {
        setError("Du måste välja en assigned doctor.");
        return;
      }
    }

    const payload: any = {
      ...form,
      role,
    };

    // Lägg bara till detta fält för PATIENT
    if (role === "PATIENT") {
      payload.assignedDoctorPractitionerId = Number(assignedDoctorPractitionerId);
    }

    const res = await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    if (!res.ok) {
      const text = await res.text().catch(() => "");
      setError(`Registrering misslyckades (${res.status}): ${text}`);
      return;
    }

    alert("Registrering klar!");
  }

  return (
    <div style={{ marginTop: 30 }}>
      <h2>Registrera ny användare</h2>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <label>Roll:</label>
      <select value={role} onChange={(e) => setRole(e.target.value as any)}>
        <option value="PATIENT">PATIENT</option>
        <option value="DOCTOR">DOCTOR</option>
        <option value="STAFF">STAFF</option>
      </select>

      <br />
      <br />

      <input placeholder="Username" onChange={(e) => update("username", e.target.value)} /> <br />
      <input type="password" placeholder="Password" onChange={(e) => update("password", e.target.value)} /> <br />

      {role === "PATIENT" && (
        <>
          <input placeholder="Förnamn" onChange={(e) => update("firstName", e.target.value)} /> <br />
          <input placeholder="Efternamn" onChange={(e) => update("lastName", e.target.value)} /> <br />
          <input placeholder="Personnummer" onChange={(e) => update("personalNumber", e.target.value)} /> <br />

          <br />
          <label>Assigned doctor:</label>
          <br />
          <select
            value={assignedDoctorPractitionerId}
            onChange={(e) => setAssignedDoctorPractitionerId(Number(e.target.value))}
            disabled={doctors.length === 0}
          >
            {doctors.length === 0 && <option value="">Inga läkare hittades</option>}
            {doctors.map((d) => (
              <option key={d.id} value={d.id}>
                {d.firstName} {d.lastName} (id: {d.id})
              </option>
            ))}
          </select>

          <br />
        </>
      )}

      {(role === "DOCTOR" || role === "STAFF") && (
        <>
          <input placeholder="Förnamn" onChange={(e) => update("practitionerFirstName", e.target.value)} /> <br />
          <input placeholder="Efternamn" onChange={(e) => update("practitionerLastName", e.target.value)} /> <br />
          <input placeholder="Licensnummer" onChange={(e) => update("licenseNumber", e.target.value)} /> <br />
        </>
      )}

      <button style={{ marginTop: 10 }} onClick={register}>
        Skapa konto
      </button>
    </div>
  );
}