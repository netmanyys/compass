import axios from "axios";

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || "http://localhost:8080/api",
});

export const tokenStorage = {
    get access() {
      return localStorage.getItem("access_token");
    },
  get refresh() {
    return localStorage.getItem("refresh_token");
  },
  set(access: string, refresh: string) {
    localStorage.setItem("access_token", access);
    localStorage.setItem("refresh_token", refresh);
  },
  clear() {
    localStorage.removeItem("access_token");
    localStorage.removeItem("refresh_token");
  },
};

api.interceptors.request.use((config) => {
  const token = tokenStorage.access;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let pending: Array<(token: string | null) => void> = [];

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const original = error.config;
    if (error.response?.status === 401 && !original._retry) {
      original._retry = true;
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pending.push((token) => {
            if (!token) {
              reject(error);
              return;
            }
            original.headers.Authorization = `Bearer ${token}`;
            resolve(api(original));
          });
        });
      }
      isRefreshing = true;
      try {
        const refresh = tokenStorage.refresh;
        if (!refresh) throw error;
        const resp = await axios.post(
          `${import.meta.env.VITE_API_BASE || "http://localhost:8080/api"}/auth/refresh`,
          { refreshToken: refresh }
        );
        tokenStorage.set(resp.data.accessToken, refresh);
        pending.forEach((cb) => cb(resp.data.accessToken));
        pending = [];
        return api(original);
      } catch (err) {
        pending.forEach((cb) => cb(null));
        pending = [];
        tokenStorage.clear();
        window.location.href = "/login";
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

export default api;
