import { useState } from 'react';
import StatusBadge, { STATUS_LABELS } from './StatusBadge';
import { REPORT_TYPES } from '../services/reportApi';

export function formatDate(value) {
  return new Date(value).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
}

export default function ReportCard({ report, children }) {
  const [showHistory, setShowHistory] = useState(false);

  return (
    <article className="report-card">
      <div className="report-photo">
        {report.photos[0] ? <img src={report.photos[0]} alt="Foto del reporte" /> : <div className="no-photo">Sin foto</div>}
      </div>
      <div className="report-body">
        <header className="report-header">
          <div>
            <span className="report-id">Reporte #{report.id}</span>
            <h3>{REPORT_TYPES[report.type] ?? report.type}</h3>
          </div>
          <StatusBadge status={report.status} />
        </header>
        <p className="report-description">{report.description}</p>
        <ul className="report-meta">
          <li>
            <strong>Ubicación:</strong>{' '}
            <a href={report.mapUrl} target="_blank" rel="noreferrer">
              {report.address}
            </a>
          </li>
          <li>
            <strong>Fecha:</strong> {formatDate(report.createdAt)}
          </li>
          <li>
            <strong>Reportado por:</strong> {report.citizenName}
          </li>
          <li>
            <strong>Entidad asignada:</strong> {report.mobilityEntity ?? 'Sin asignar'}
          </li>
        </ul>
        {report.photos.length > 1 && (
          <div className="report-thumbs">
            {report.photos.map((url) => (
              <a key={url} href={url} target="_blank" rel="noreferrer">
                <img src={url} alt="Foto adicional" />
              </a>
            ))}
          </div>
        )}
        <button type="button" className="link-button" onClick={() => setShowHistory(!showHistory)}>
          {showHistory ? 'Ocultar historial' : `Ver historial (${report.history.length})`}
        </button>
        {showHistory && (
          <ol className="timeline">
            {report.history.map((entry, index) => (
              <li key={index}>
                <span className="timeline-status">{STATUS_LABELS[entry.newStatus]}</span>
                <span className="timeline-date">{formatDate(entry.changedAt)}</span>
                {entry.comment && <p>{entry.comment}</p>}
              </li>
            ))}
          </ol>
        )}
        {children}
      </div>
    </article>
  );
}
