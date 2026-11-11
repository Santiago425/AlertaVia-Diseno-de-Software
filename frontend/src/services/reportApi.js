import { apiClient } from './apiClient';

export const REPORT_TYPES = {
  POTHOLE: 'Hueco en la vía',
  BROKEN_TRAFFIC_LIGHT: 'Semáforo dañado',
  DAMAGED_SIGNAGE: 'Señalización deteriorada',
  FLOODING: 'Inundación',
};

export function createReport(report, photos) {
  const formData = new FormData();
  formData.append('report', new Blob([JSON.stringify(report)], { type: 'application/json' }));
  photos.forEach((photo) => formData.append('photos', photo));
  return apiClient('/api/reports', { method: 'POST', formData });
}

export function getMyReports() {
  return apiClient('/api/reports/mine');
}

export function getReport(id) {
  return apiClient(`/api/reports/${id}`);
}

export function getNotifications() {
  return apiClient('/api/notifications');
}

export function markNotificationAsRead(id) {
  return apiClient(`/api/notifications/${id}/read`, { method: 'PATCH' });
}
