const API_URL = "http://localhost:8080/api/messages";

export async function sendMessage(receiverId: number, content: string, auth: string) {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Basic " + auth,
    },
    body: JSON.stringify({ receiverId, content }),
  });

  if (!res.ok) throw new Error("Message failed");
  return res.json();
}

export async function getMyMessages(auth: string) {
  const res = await fetch(`${API_URL}/my`, {
    headers: {
      Authorization: "Basic " + auth,
    },
  });

  if (!res.ok) throw new Error("Fetch messages failed");
  return res.json();
}