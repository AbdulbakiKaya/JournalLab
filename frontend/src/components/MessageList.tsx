import React from "react";

interface Message {
  id: number;
  senderId: number;
  receiverId: number;
  senderName: string;
  receiverName: string;
  text: string;
  timestamp: string;
}

interface Props {
  messages: Message[];
}

const MessageList: React.FC<Props> = ({ messages }) => {
  return (
    <div style={{ marginTop: 20 }}>
      <h3>Meddelanden</h3>

      {messages.length === 0 && <p>Inga meddelanden än.</p>}

      <ul>
        {messages.map((m) => (
          <li key={m.id}>
            <b>{new Date(m.timestamp).toLocaleString()}</b><br />
            <i>Från {m.senderName} → Till {m.receiverName}</i><br />
            {m.text}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default MessageList;
