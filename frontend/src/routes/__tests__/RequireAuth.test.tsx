import { render, screen } from "@testing-library/react";
import { MemoryRouter, Route, Routes } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { AuthProvider } from "../../features/auth/AuthContext";
import { RequireAuth } from "../RequireAuth";

describe("RequireAuth", () => {
  it("redirects unauthenticated users away from protected content", () => {
    render(
      <MemoryRouter initialEntries={["/dashboard"]}>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<div>login</div>} />
            <Route
              path="/dashboard"
              element={(
                <RequireAuth>
                  <div>protected</div>
                </RequireAuth>
              )}
            />
          </Routes>
        </AuthProvider>
      </MemoryRouter>
    );

    expect(screen.queryByText("protected")).not.toBeInTheDocument();
    expect(screen.getByText("login")).toBeInTheDocument();
  });
});
