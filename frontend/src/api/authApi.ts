const API_URL = "http://localhost:8080/api/auth";

// ===== LOGIN (oförändrad) =====
export async function login(username: string, password: string): Promise<string> {
  const res = await fetch(`${API_URL}/me`, {
    method: "GET",
    headers: {
      Authorization: "Basic " + btoa(`${username}:${password}`),
    },
  });

  if (!res.ok) {
    throw new Error("Invalid credentials");
  }

  return res.text();
}

// ===== REGISTER (NY) =====
export type RegisterPayload = {
  username: string;
  password: string;
  role: "PATIENT" | "DOCTOR" | "STAFF";

  // patient
  firstName?: string;
  lastName?: string;
  personalNumber?: string;
  assignedDoctorPractitionerId?: number;

  // doctor/staff (om du använder dessa)
  practitionerFirstName?: string;
  practitionerLastName?: string;
  licenseNumber?: string;
};

export async function registerUser(payload: RegisterPayload) {
  const res = await fetch(`${API_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Register failed (${res.status}): ${text}`);
  }

  return res.json();
}
