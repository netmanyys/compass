import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { vi } from "vitest";
import { AuthProvider, useAuth } from "../context/AuthContext";

vi.mock("../api/client", () => ({
  default: {
    get: vi.fn(() => Promise.resolve({ data: { id: 1, username: "admin", email: "", role: "admin" } })),
    post: vi.fn(() => Promise.resolve({ data: { access: "token", refresh: "refresh" } })),
  },
  tokenStorage: {
    get access() {
      return "token";
    },
    get refresh() {
      return "refresh";
    },
    set: vi.fn(),
    clear: vi.fn(),
  },
}));

const Consumer = () => {
  const { user, loading } = useAuth();
  if (loading) return <div>loading</div>;
  return <div>{user?.role}</div>;
};

test("AuthProvider loads user", async () => {
  render(
    <AuthProvider>
      <Consumer />
    </AuthProvider>
  );

  await waitFor(() => expect(screen.getByText("admin")).toBeInTheDocument());
});
