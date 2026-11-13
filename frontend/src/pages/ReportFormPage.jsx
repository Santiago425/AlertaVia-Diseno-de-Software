import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MapPicker from '../components/MapPicker';
import PhotoUploader from '../components/PhotoUploader';
import { createReport, REPORT_TYPES } from '../services/reportApi';

export default function ReportFormPage() {
  const navigate = useNavigate();
  const [type, setType] = useState('POTHOLE');
  const [description, setDescription] = useState('');
  const [location, setLocation] = useState(null);
  const [address, setAddress] = useState('');
  const [photos, setPhotos] = useState([]);
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError('');
    if (!location) {
      setError('Marca en el mapa o con tu ubicación dónde está el problema.');
      return;
    }
    if (photos.length === 0) {
      setError('Adjunta al menos una foto del problema.');
      return;
    }
    setSubmitting(true);
    try {
      const report = await createReport(
        { type, description, latitude: location.lat, longitude: location.lng, address: address || null },
        photos,
      );
      navigate('/mis-reportes', { state: { created: report.code } });
    } catch (err) {
      setError(err.message);
      setSubmitting(false);
    }
  };

  return (
    <div className="page">
      <h1>Nuevo reporte</h1>
      <p className="subtitle">Cuéntanos qué problema encontraste en la vía.</p>
      <form className="card form" onSubmit={handleSubmit}>
        <fieldset>
          <legend>Tipo de problema</legend>
          <div className="type-grid">
            {Object.entries(REPORT_TYPES).map(([value, label]) => (
              <label key={value} className={type === value ? 'type-option selected' : 'type-option'}>
                <input type="radio" name="type" value={value} checked={type === value} onChange={() => setType(value)} />
                {label}
              </label>
            ))}
          </div>
        </fieldset>
        <label>
          Descripción
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            maxLength={1000}
            rows={4}
            placeholder="Ej: hueco de aproximadamente un metro frente al colegio, ocupa todo el carril derecho."
            required
          />
        </label>
        <fieldset>
          <legend>Ubicación</legend>
          <MapPicker value={location} onChange={setLocation} />
          <label>
            Dirección o punto de referencia (opcional)
            <input value={address} onChange={(e) => setAddress(e.target.value)} placeholder="Ej: Calle 18 con Carrera 25" />
          </label>
        </fieldset>
        <fieldset>
          <legend>Fotos</legend>
          <PhotoUploader photos={photos} onChange={setPhotos} />
        </fieldset>
        {error && <p className="alert alert-error">{error}</p>}
        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? 'Enviando reporte...' : 'Enviar reporte'}
        </button>
      </form>
    </div>
  );
}
