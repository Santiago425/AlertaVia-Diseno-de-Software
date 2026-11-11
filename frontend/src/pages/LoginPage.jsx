import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const EMPTY_FORM = { name: '', email: '', password: '', phone: '' };

const DEMO_ACCOUNTS = [
  { role: 'Administrador', email: 'admin@alertavia.com', password: 'admin123' },
  { role: 'Ciudadano', email: 'ciudadano@alertavia.com', password: 'ciudadano123' },
];

export default function LoginPage() {
  const { user, login, register } = useAuth();
  const navigate = useNavigate();
  const [mode, setMode] = useState('login');
  const [form, setForm] = useState(EMPTY_FORM);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  if (user) {
    return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/mis-reportes'} replace />;
  }

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value });

  const switchMode = (next) => {
    setMode(next);
    setError('');
  };

  const useDemoAccount = (account) => {
    setMode('login');
    setError('');
    setForm({ ...EMPTY_FORM, email: account.email, password: account.password });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    setSubmitting(true);
    try {
      const sessionUser = mode === 'login' ? await login(form.email, form.password) : await register(form);
      navigate(sessionUser.role === 'ADMIN' ? '/admin' : '/nuevo-reporte');
    } catch (err) {
      setError(err.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-page">
      <section className="auth-intro">
        <h1>AlertaVía</h1>
        <p>
          Reporta huecos, semáforos dañados, señalización deteriorada e inundaciones en tu ciudad. Adjunta una foto,
          marca la ubicación y haz seguimiento hasta que el problema quede resuelto.
        </p>
        <ul>
          <li>Reporta en menos de un minuto</li>
          <li>Recibe notificaciones de cada cambio de estado</li>
          <li>Tu reporte llega a la entidad de movilidad encargada</li>
        </ul>
        <div className="demo-accounts">
          <h2>Cuentas de prueba</h2>
          {DEMO_ACCOUNTS.map((account) => (
            <div key={account.email} className="demo-account">
              <div>
                <strong>{account.role}</strong>
                <span>Usuario: {account.email}</span>
                <span>Contraseña: {account.password}</span>
              </div>
              <button type="button" className="btn btn-secondary" onClick={() => useDemoAccount(account)}>
                Usar
              </button>
            </div>
          ))}
        </div>
      </section>
      <section className="card auth-card">
        <div className="tabs">
          <button type="button" className={mode === 'login' ? 'tab active' : 'tab'} onClick={() => switchMode('login')}>
            Iniciar sesión
          </button>
          <button
            type="button"
            className={mode === 'register' ? 'tab active' : 'tab'}
            onClick={() => switchMode('register')}
          >
            Crear cuenta
          </button>
        </div>
        <form onSubmit={handleSubmit} className="form">
          {mode === 'register' && (
            <label>
              Nombre completo
              <input name="name" value={form.name} onChange={handleChange} required />
            </label>
          )}
          <label>
            Correo electrónico
            <input type="email" name="email" value={form.email} onChange={handleChange} required />
          </label>
          <label>
            Contraseña
            <input type="password" name="password" value={form.password} onChange={handleChange} minLength={6} required />
          </label>
          {mode === 'register' && (
            <label>
              Teléfono (opcional)
              <input name="phone" value={form.phone} onChange={handleChange} />
            </label>
          )}
          {error && <p className="alert alert-error">{error}</p>}
          <button type="submit" className="btn btn-primary" disabled={submitting}>
            {submitting ? 'Procesando...' : mode === 'login' ? 'Ingresar' : 'Registrarme'}
          </button>
        </form>
      </section>
    </div>
  );
}
