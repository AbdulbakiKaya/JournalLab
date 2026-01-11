const BASE_URL = "http://localhost:8080/api/messages";

export type ThreadType = "DOCTOR" | "STAFF";

export type MessageDto = {
  id: number;
  senderId: number;
  receiverId: number;
  text: string;
  timestamp: string;
  senderName?: string;
  receiverName?: string;
};

export async function getPatientThread(patientId: number, threadType: ThreadType, auth: string): Promise<MessageDto[]> {
  const res = await fetch(`${BASE_URL}/patient/${patientId}/thread/${threadType}`, {
    method: "GET",
    headers: { Authorization: "Basic " + auth },
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(`Failed to load thread (${res.status}): ${text}`);
  }

  return res.json();
}

export async function sendPatientMessage(patientId: number, threadType: ThreadType, text: string, auth: string) {
  const res = await fetch(`${BASE_URL}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: "Basic " + auth,
    },
    body: JSON.stringify({ patientId, threadType, text }),
  });

  if (!res.ok) {
    const body = await res.text().catch(() => "");
    throw new Error(`Failed to send message (${res.status}): ${body}`);
  }

  return res.json();
}