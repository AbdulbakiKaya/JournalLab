import React from "react";
import MessageThread from "../components/MessageThread";

interface Props {
  auth: string;
}

const MessagePage: React.FC<Props> = ({ auth }) => {
  return (
    <div>
      <h2>Messages</h2>
      <MessageThread auth={auth} />
    </div>
  );
};

export default MessagePage;