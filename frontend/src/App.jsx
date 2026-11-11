import { Link, Navigate, NavLink, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import ProtectedRoute from './auth/ProtectedRoute';
import AdminDashboardPage from './pages/AdminDashboardPage';
import LoginPage from './pages/LoginPage';
import MyReportsPage from './pages/MyReportsPage';
import ReportFormPage from './pages/ReportFormPage';

function Navbar() {
  const { user, isAdmin, logout } = useAuth();

  return (
    <header className="navbar">
      <Link to="/" className="brand">
        <img src="/favicon.svg" alt="" />
        AlertaVía
      </Link>
      {user && (
        <nav>
          {isAdmin ? (
            <NavLink to="/admin">Panel</NavLink>
          ) : (
            <>
              <NavLink to="/nuevo-reporte">Reportar</NavLink>
              <NavLink to="/mis-reportes">Mis reportes</NavLink>
            </>
          )}
          <span className="user-name">{user.name}</span>
          <button type="button" className="btn btn-outline" onClick={logout}>
            Cerrar sesión
          </button>
        </nav>
      )}
    </header>
  );
}

function HomeRedirect() {
  const { user, loading } = useAuth();
  if (loading) {
    return <p className="loading">Cargando...</p>;
  }
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  return <Navigate to={user.role === 'ADMIN' ? '/admin' : '/mis-reportes'} replace />;
}

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<HomeRedirect />} />
          <Route path="/login" element={<LoginPage />} />
          <Route
            path="/nuevo-reporte"
            element={
              <ProtectedRoute role="CITIZEN">
                <ReportFormPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/mis-reportes"
            element={
              <ProtectedRoute role="CITIZEN">
                <MyReportsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminDashboardPage />
              </ProtectedRoute>
            }
          />
          <Route path="*" element={<HomeRedirect />} />
        </Routes>
      </main>
      <footer className="footer">AlertaVía · Diseño de Software · Universidad Cooperativa de Colombia</footer>
    </>
  );
}
