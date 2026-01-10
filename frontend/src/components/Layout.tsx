import React from "react";

export default function Layout({ children }: { children: React.ReactNode }) {
  return (
    <div style={{ fontFamily: "Arial", padding: "20px" }}>
      <h1>Journal System</h1>
      <hr />
      {children}
    </div>
  );
}