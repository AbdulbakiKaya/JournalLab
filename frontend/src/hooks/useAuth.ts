import { useState } from "react";
import { login } from "../api/authApi";

export function useAuth() {
  const [authData, setAuthData] = useState<string | null>(null);
  const [username, setUsername] = useState("");
  const [role, setRole] = useState("");

  async function doLogin(user: string, pass: string) {
    const encoded = btoa(`${user}:${pass}`);

    const res = await login(user, pass);
    setUsername(user);
    setAuthData(encoded);

    if (res.includes("DOCTOR")) setRole("DOCTOR");
    else if (res.includes("STAFF")) setRole("STAFF");
    else setRole("PATIENT");
  }

  return {
    authData,
    username,
    role,
    login: doLogin,
  };
}
