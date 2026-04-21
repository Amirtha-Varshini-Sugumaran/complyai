import { createContext, useContext, useEffect, useMemo, useState } from "react";
import type { PropsWithChildren } from "react";
import { authApi } from "../../api/services";
import type { AuthResponse, Role } from "../../types/auth";

interface AuthContextValue {
  session: AuthResponse | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signOut: () => void;
  hasRole: (roles: Role[]) => boolean;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: PropsWithChildren) {
  const [session, setSession] = useState<AuthResponse | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const raw = localStorage.getItem("complyai.session");
    if (raw) {
      setSession(JSON.parse(raw));
    }
    setLoading(false);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      session,
      loading,
      async signIn(email, password) {
        const response = await authApi.login(email, password);
        localStorage.setItem("complyai.token", response.accessToken);
        localStorage.setItem("complyai.session", JSON.stringify(response));
        setSession(response);
      },
      signOut() {
        localStorage.removeItem("complyai.token");
        localStorage.removeItem("complyai.session");
        setSession(null);
      },
      hasRole(roles) {
        return !!session?.roles.some((role) => roles.includes(role));
      }
    }),
    [loading, session]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }
  return context;
}
