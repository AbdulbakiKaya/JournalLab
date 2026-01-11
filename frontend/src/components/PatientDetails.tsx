import React, { useCallback, useEffect, useState } from "react";
import { getPatientDetails, changeAssignedDoctor } from "../api/patientApi";
import { getDoctors } from "../api/practitionerApi";

import EncounterForm from "./EncounterForm";
import EncounterList from "./EncounterList";
import ConditionForm from "./ConditionForm";
import ConditionList from "./ConditionList";
import PatientMessages from "./PatientMessages";

interface Props {
  id: number;
  auth: string;
  role: string | null;
  userId: number | null;
  practitionerId: number | null;
}

export default function PatientDetails({ id, auth, role, userId, practitionerId }: Props) {
  const [details, setDetails] = useState<any>(null);

  const [doctors, setDoctors] = useState<any[]>([]);
  const [selectedDoctorId, setSelectedDoctorId] = useState<number | null>(null);
  const [savingDoctor, setSavingDoctor] = useState(false);

  const load = useCallback(async () => {
    try {
      const d = await getPatientDetails(id, auth);
      setDetails(d);
    } catch (e) {
      console.error(e);
    }
  }, [id, auth]);

  // Ladda patient details + doctors-lista
  useEffect(() => {
    load();

    getDoctors(auth)
      .then((list) => setDoctors(list))
      .catch(console.error);
  }, [load, auth]);

  // När details väl är laddad: defaulta selectedDoctorId till current assigned
  useEffect(() => {
    if (details?.assignedDoctorId != null) {
      setSelectedDoctorId(details.assignedDoctorId);
    } else {
      setSelectedDoctorId(null);
    }
  }, [details?.assignedDoctorId]);

  if (!details) return <p>Loading...</p>;

  const isStaff = role === "STAFF";
  const isDoctor = role === "DOCTOR";
  const isAssignedDoctor =
    isDoctor && practitionerId != null && details.assignedDoctorId === practitionerId;

  const canWrite = isStaff || isAssignedDoctor;

  // ✅ Behörighet för att byta assigned doctor:
  // STAFF: alltid
  // DOCTOR: endast om doctor redan är assigned (annars kan random doctor "ta över")
  const canReassign = isStaff || isAssignedDoctor;

  async function onSaveAssignedDoctor() {
    if (!selectedDoctorId) return;

    try {
      setSavingDoctor(true);
      await changeAssignedDoctor(id, selectedDoctorId, auth);
      await load(); // refresh patient details efter uppdatering
    } catch (e: any) {
      console.error(e);
      alert("Kunde inte byta assigned doctor: " + (e?.message ?? ""));
    } finally {
      setSavingDoctor(false);
    }
  }

  return (
    <div style={{ padding: "20px" }}>
      <h2>
        {details.firstName} {details.lastName}
      </h2>

      <div style={{ marginBottom: "12px" }}>
        <strong>Assigned doctor:</strong> {details.assignedDoctorName}
      </div>

      {/* ✅ Byt assigned doctor (endast STAFF eller assigned DOCTOR) */}
      {canReassign && (
        <div style={{ marginBottom: 16, padding: 10, border: "1px solid #ddd" }}>
          <strong>Byt assigned doctor</strong>

          <div style={{ marginTop: 8, display: "flex", gap: 8, alignItems: "center" }}>
            <select
              value={selectedDoctorId ?? ""}
              onChange={(e) => setSelectedDoctorId(Number(e.target.value))}
            >
              <option value="" disabled>
                Välj doctor
              </option>

              {doctors.map((d) => (
                <option key={d.id} value={d.id}>
                  {d.firstName} {d.lastName}
                </option>
              ))}
            </select>

            <button onClick={onSaveAssignedDoctor} disabled={savingDoctor || !selectedDoctorId}>
              {savingDoctor ? "Sparar..." : "Spara"}
            </button>
          </div>
        </div>
      )}

      {!canWrite && isDoctor && (
        <div style={{ marginBottom: 12, padding: 10, border: "1px solid #ccc" }}>
          Du kan läsa denna patient, men du är inte assigned doctor och kan därför inte skriva.
        </div>
      )}

      {/* ENCOUNTERS (journalanteckningar) */}
      {canWrite && <EncounterForm patientId={id} auth={auth} onCreated={load} />}
      <EncounterList encounters={details.encounters} />

      {/* CONDITIONS (diagnoser) */}
      {canWrite && <ConditionForm patientId={id} auth={auth} onCreated={load} />}
      <ConditionList conditions={details.conditions} />

      {/* MESSAGES */}
      <PatientMessages patientId={id} auth={auth} role={role} userId={userId} />
    </div>
  );
}