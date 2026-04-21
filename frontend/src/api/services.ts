import api from "./client";
import type { AuthResponse } from "../types/auth";
import type { DashboardSummary } from "../types/dashboard";
import type { PagedResponse } from "../types/common";

export const authApi = {
  login: async (email: string, password: string) => {
    const { data } = await api.post<AuthResponse>("/auth/login", { email, password });
    return data;
  },
  me: async () => {
    const { data } = await api.get("/users/me");
    return data;
  }
};

export const dashboardApi = {
  summary: async () => {
    const { data } = await api.get<DashboardSummary>("/dashboard/summary");
    return data;
  }
};

export const resourceApi = {
  list: async <T>(path: string) => {
    const { data } = await api.get<PagedResponse<T>>(path);
    return data;
  },
  get: async <T>(path: string) => {
    const { data } = await api.get<T>(path);
    return data;
  },
  create: async <T>(path: string, payload: unknown) => {
    const { data } = await api.post<T>(path, payload);
    return data;
  },
  update: async <T>(path: string, payload: unknown) => {
    const { data } = await api.put<T>(path, payload);
    return data;
  },
  post: async <T>(path: string, payload: unknown) => {
    const { data } = await api.post<T>(path, payload);
    return data;
  }
};
