const BASE_URL = "http://localhost:8080";

export async function getAllPatients(auth: string) {
  const res = await fetch(`${BASE_URL}/api/patients`, {
    headers: { Authorization: "Basic " + auth },
  });

  if (!res.ok) throw new Error("Failed to load patients");
  return res.json();
}

export async function getPatientDetails(patientId: number, auth: string) {
  const res = await fetch(`${BASE_URL}/api/patients/${patientId}`, {
    headers: { Authorization: "Basic " + auth },
  });

  if (!res.ok) throw new Error("Failed to load patient details");
  return res.json();
}

export async function changeAssignedDoctor(patientId: number, doctorId: number, auth: string) {
  const res = await fetch(
    `${BASE_URL}/api/patients/${patientId}/assigned-doctor/${doctorId}`,
    {
      method: "PUT",
      headers: { Authorization: "Basic " + auth },
    }
  );

  if (!res.ok) {
    const txt = await res.text();
    throw new Error(txt || "Failed to change assigned doctor");
  }

  // Om din backend returnerar void: returnera bara true
  // Om din backend returnerar JSON: byt till `return res.json();`
  return true;
}