import { useCallback, useEffect, useState } from 'react';
import ReportCard from '../components/ReportCard';
import { STATUS_LABELS } from '../components/StatusBadge';
import { assignEntity, changeStatus, getEntities, getReports, getStatistics } from '../services/adminApi';
import { REPORT_TYPES } from '../services/reportApi';

const NEXT_STATUSES = {
  REPORTED: ['IN_REVIEW', 'REJECTED'],
  IN_REVIEW: ['IN_PROGRESS', 'REJECTED'],
  IN_PROGRESS: ['RESOLVED'],
  RESOLVED: [],
  REJECTED: [],
};

function AdminActions({ report, entities, onUpdated }) {
  const nextOptions = NEXT_STATUSES[report.status];
  const [status, setStatus] = useState(nextOptions[0] ?? '');
  const [comment, setComment] = useState('');
  const [entityId, setEntityId] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const run = async (action) => {
    setError('');
    setSaving(true);
    try {
      const updated = await action();
      setComment('');
      onUpdated(updated);
    } catch (err) {
      setError(err.message);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="admin-actions">
      {nextOptions.length > 0 && (
        <div className="action-row">
          <select value={status} onChange={(e) => setStatus(e.target.value)}>
            {nextOptions.map((value) => (
              <option key={value} value={value}>
                {STATUS_LABELS[value]}
              </option>
            ))}
          </select>
          <input placeholder="Comentario para el ciudadano" value={comment} onChange={(e) => setComment(e.target.value)} />
          <button
            type="button"
            className="btn btn-primary"
            disabled={saving || !status}
            onClick={() => run(() => changeStatus(report.id, status, comment))}
          >
            Cambiar estado
          </button>
        </div>
      )}
      {report.status !== 'REJECTED' && report.status !== 'RESOLVED' && (
        <div className="action-row">
          <select value={entityId} onChange={(e) => setEntityId(e.target.value)}>
            <option value="">Selecciona una entidad de movilidad</option>
            {entities.map((entity) => (
              <option key={entity.id} value={entity.id}>
                {entity.name}
              </option>
            ))}
          </select>
          <button
            type="button"
            className="btn btn-secondary"
            disabled={saving || !entityId}
            onClick={() => run(() => assignEntity(report.id, entityId))}
          >
            Asignar entidad
          </button>
        </div>
      )}
      {error && <p className="alert alert-error">{error}</p>}
    </div>
  );
}

export default function AdminDashboardPage() {
  const [reports, setReports] = useState([]);
  const [entities, setEntities] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [filter, setFilter] = useState('');
  const [typeFilter, setTypeFilter] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const loadStatistics = useCallback(() => getStatistics().then(setStatistics), []);

  useEffect(() => {
    Promise.all([getEntities(), loadStatistics()])
      .then(([entityList]) => setEntities(entityList))
      .catch((err) => setError(err.message));
  }, [loadStatistics]);

  useEffect(() => {
    setLoading(true);
    getReports(filter, typeFilter)
      .then(setReports)
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, [filter, typeFilter]);

  const handleUpdated = (updated) => {
    setReports(
      reports
        .map((report) => (report.id === updated.id ? updated : report))
        .filter((report) => (!filter || report.status === filter) && (!typeFilter || report.type === typeFilter)),
    );
    loadStatistics();
  };

  const maxByType = statistics ? Math.max(1, ...Object.values(statistics.byType)) : 1;

  return (
    <div className="page">
      <h1>Panel administrativo</h1>
      <p className="subtitle">Valida los reportes, actualiza su estado y asígnalos a la entidad encargada.</p>
      {error && <p className="alert alert-error">{error}</p>}

      {statistics && (
        <section className="stats">
          <div className="stat-card">
            <span className="stat-value">{statistics.total}</span>
            <span className="stat-label">Reportes totales</span>
          </div>
          {Object.entries(statistics.byStatus).map(([status, count]) => (
            <div key={status} className={`stat-card stat-${status.toLowerCase()}`}>
              <span className="stat-value">{count}</span>
              <span className="stat-label">{STATUS_LABELS[status]}</span>
            </div>
          ))}
        </section>
      )}

      {statistics && (
        <section className="card">
          <h2>Reportes por tipo de problema</h2>
          <div className="bars">
            {Object.entries(statistics.byType).map(([type, count]) => (
              <div key={type} className="bar-row">
                <span className="bar-label">{REPORT_TYPES[type]}</span>
                <div className="bar-track">
                  <div className="bar-fill" style={{ width: `${(count / maxByType) * 100}%` }} />
                </div>
                <span className="bar-value">{count}</span>
              </div>
            ))}
          </div>
        </section>
      )}

      <div className="filter-row">
        <label>
          Filtrar por estado
          <select value={filter} onChange={(e) => setFilter(e.target.value)}>
            <option value="">Todos</option>
            {Object.entries(STATUS_LABELS).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </label>
        <label>
          Filtrar por tipo
          <select value={typeFilter} onChange={(e) => setTypeFilter(e.target.value)}>
            <option value="">Todos</option>
            {Object.entries(REPORT_TYPES).map(([value, label]) => (
              <option key={value} value={value}>
                {label}
              </option>
            ))}
          </select>
        </label>
      </div>

      {loading ? (
        <p className="loading">Cargando reportes...</p>
      ) : reports.length === 0 ? (
        <div className="card empty">
          <p>No hay reportes para mostrar.</p>
        </div>
      ) : (
        <div className="report-list">
          {reports.map((report) => (
            <ReportCard key={report.id} report={report}>
              <AdminActions
                key={report.status}
                report={report}
                entities={entities}
                onUpdated={handleUpdated}
              />
            </ReportCard>
          ))}
        </div>
      )}
    </div>
  );
}
