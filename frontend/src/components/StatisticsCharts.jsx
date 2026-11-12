import { Bar, BarChart, CartesianGrid, Cell, Legend, Pie, PieChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { STATUS_LABELS } from './StatusBadge';
import { REPORT_TYPES } from '../services/reportApi';

const STATUS_COLORS = {
  REPORTED: '#6366f1',
  IN_REVIEW: '#f59e0b',
  IN_PROGRESS: '#3b82f6',
  RESOLVED: '#16a34a',
  REJECTED: '#dc2626',
};

const SHORT_TYPES = {
  POTHOLE: 'Huecos',
  BROKEN_TRAFFIC_LIGHT: 'Semáforos',
  DAMAGED_SIGNAGE: 'Señalización',
  FLOODING: 'Inundaciones',
};

export default function StatisticsCharts({ statistics }) {
  const statusData = Object.entries(statistics.byStatus)
    .filter(([, count]) => count > 0)
    .map(([status, count]) => ({ name: STATUS_LABELS[status], value: count, status }));
  const typeData = Object.entries(statistics.byType).map(([type, count]) => ({
    name: SHORT_TYPES[type],
    fullName: REPORT_TYPES[type],
    reportes: count,
  }));

  return (
    <section className="charts">
      <div className="card chart-card">
        <h2>Reportes por estado</h2>
        {statusData.length === 0 ? (
          <p className="hint">Todavía no hay reportes.</p>
        ) : (
          <ResponsiveContainer width="100%" height={260}>
            <PieChart>
              <Pie data={statusData} dataKey="value" nameKey="name" innerRadius={55} outerRadius={90} paddingAngle={2}>
                {statusData.map((entry) => (
                  <Cell key={entry.status} fill={STATUS_COLORS[entry.status]} />
                ))}
              </Pie>
              <Tooltip formatter={(value) => [value, 'Reportes']} />
              <Legend verticalAlign="bottom" iconType="circle" />
            </PieChart>
          </ResponsiveContainer>
        )}
      </div>
      <div className="card chart-card">
        <h2>Reportes por tipo de problema</h2>
        <ResponsiveContainer width="100%" height={260}>
          <BarChart data={typeData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" vertical={false} />
            <XAxis dataKey="name" tick={{ fontSize: 12 }} />
            <YAxis allowDecimals={false} tick={{ fontSize: 12 }} />
            <Tooltip cursor={{ fill: '#f3f4f6' }} labelFormatter={(_, payload) => payload?.[0]?.payload.fullName ?? ''} />
            <Bar dataKey="reportes" name="Reportes" fill="#f59e0b" radius={[6, 6, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </section>
  );
}
