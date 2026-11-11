import { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import ReportCard, { formatDate } from '../components/ReportCard';
import { getMyReports, getNotifications, markNotificationAsRead } from '../services/reportApi';

export default function MyReportsPage() {
  const { state } = useLocation();
  const [reports, setReports] = useState([]);
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    Promise.all([getMyReports(), getNotifications()])
      .then(([myReports, myNotifications]) => {
        setReports(myReports);
        setNotifications(myNotifications);
      })
      .catch((err) => setError(err.message))
      .finally(() => setLoading(false));
  }, []);

  const markAsRead = async (id) => {
    const updated = await markNotificationAsRead(id);
    setNotifications(notifications.map((n) => (n.id === id ? updated : n)));
  };

  const unread = notifications.filter((n) => !n.read);

  return (
    <div className="page">
      <div className="page-header">
        <div>
          <h1>Mis reportes</h1>
          <p className="subtitle">Revisa el estado de los problemas que has reportado.</p>
        </div>
        <Link to="/nuevo-reporte" className="btn btn-primary">
          + Nuevo reporte
        </Link>
      </div>

      {state?.created && <p className="alert alert-success">Tu reporte #{state.created} fue enviado correctamente.</p>}
      {error && <p className="alert alert-error">{error}</p>}

      {unread.length > 0 && (
        <section className="card notifications">
          <h2>Notificaciones ({unread.length})</h2>
          <ul>
            {unread.map((notification) => (
              <li key={notification.id}>
                <div>
                  <p>{notification.message}</p>
                  <span className="timeline-date">{formatDate(notification.sentAt)}</span>
                </div>
                <button type="button" className="link-button" onClick={() => markAsRead(notification.id)}>
                  Marcar como leída
                </button>
              </li>
            ))}
          </ul>
        </section>
      )}

      {loading ? (
        <p className="loading">Cargando reportes...</p>
      ) : reports.length === 0 ? (
        <div className="card empty">
          <p>Aún no has hecho ningún reporte.</p>
          <Link to="/nuevo-reporte" className="btn btn-primary">
            Reportar un problema
          </Link>
        </div>
      ) : (
        <div className="report-list">
          {reports.map((report) => (
            <ReportCard key={report.id} report={report} />
          ))}
        </div>
      )}
    </div>
  );
}
