import { apiClient } from './apiClient';

export function getReports(status, type) {
  const params = new URLSearchParams();
  if (status) {
    params.set('status', status);
  }
  if (type) {
    params.set('type', type);
  }
  const query = params.toString();
  return apiClient(`/api/admin/reports${query ? `?${query}` : ''}`);
}

export function changeStatus(reportId, status, comment) {
  return apiClient(`/api/admin/reports/${reportId}/status`, { method: 'PUT', body: { status, comment } });
}

export function assignEntity(reportId, entityId) {
  return apiClient(`/api/admin/reports/${reportId}/entity/${entityId}`, { method: 'PUT' });
}

export function getEntities() {
  return apiClient('/api/admin/entities');
}

export function getStatistics() {
  return apiClient('/api/admin/statistics');
}
