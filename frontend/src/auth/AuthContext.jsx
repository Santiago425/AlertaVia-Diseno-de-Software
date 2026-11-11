import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import * as authApi from '../services/authApi';
import { getToken, setToken } from '../services/apiClient';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(getToken()));

  useEffect(() => {
    if (!getToken()) {
      return;
    }
    authApi
      .getCurrentUser()
      .then(setUser)
      .catch(() => setToken(null))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    const handleLogout = () => setUser(null);
    window.addEventListener('alertavia:logout', handleLogout);
    return () => window.removeEventListener('alertavia:logout', handleLogout);
  }, []);

  const saveSession = useCallback((response) => {
    setToken(response.token);
    const sessionUser = { id: response.userId, name: response.name, email: response.email, role: response.role };
    setUser(sessionUser);
    return sessionUser;
  }, []);

  const login = useCallback(
    async (email, password) => saveSession(await authApi.login(email, password)),
    [saveSession],
  );

  const register = useCallback(async (data) => saveSession(await authApi.register(data)), [saveSession]);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
  }, []);

  const value = useMemo(
    () => ({ user, loading, login, register, logout, isAdmin: user?.role === 'ADMIN' }),
    [user, loading, login, register, logout],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
