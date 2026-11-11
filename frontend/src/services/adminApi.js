import { apiClient } from './apiClient';

export function getReports(status) {
  const query = status ? `?status=${status}` : '';
  return apiClient(`/api/admin/reports${query}`);
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
