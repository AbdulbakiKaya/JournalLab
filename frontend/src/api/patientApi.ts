const API_URL = "http://localhost:8080/api/patients";

export async function getAllPatients(auth: string) {
  const res = await fetch(API_URL, {
    headers: {
      Authorization: "Basic " + auth,
    },
  });

  if (!res.ok) throw new Error("Not allowed");
  return res.json();
}

export async function getPatientDetails(id: number, auth: string) {
  const res = await fetch(`${API_URL}/${id}`, {
    headers: {
      Authorization: "Basic " + auth,
    },
  });

  if (!res.ok) throw new Error("Not allowed");
  return res.json();
}