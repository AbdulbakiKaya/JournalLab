const API_URL = "http://localhost:8080/api/auth";

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