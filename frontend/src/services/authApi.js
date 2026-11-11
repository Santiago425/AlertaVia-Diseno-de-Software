import { apiClient } from './apiClient';

export function login(email, password) {
  return apiClient('/api/auth/login', { method: 'POST', body: { email, password } });
}

export function register(data) {
  return apiClient('/api/auth/register', { method: 'POST', body: data });
}

export function getCurrentUser() {
  return apiClient('/api/auth/me');
}
