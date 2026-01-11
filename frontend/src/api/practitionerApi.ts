export async function getDoctors(auth: string) {
  const res = await fetch("http://localhost:8080/api/practitioners/doctors", {
    headers: { Authorization: "Basic " + auth },
  });
  if (!res.ok) throw new Error("Failed to load doctors");
  return res.json();
}