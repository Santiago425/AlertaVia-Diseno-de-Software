export const STATUS_LABELS = {
  REPORTED: 'Reportado',
  IN_REVIEW: 'En revisión',
  IN_PROGRESS: 'En proceso',
  RESOLVED: 'Resuelto',
  REJECTED: 'Rechazado',
};

export default function StatusBadge({ status }) {
  return <span className={`badge badge-${status.toLowerCase()}`}>{STATUS_LABELS[status] ?? status}</span>;
}
