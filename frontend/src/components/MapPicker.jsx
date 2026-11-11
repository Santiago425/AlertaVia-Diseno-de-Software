import { useEffect, useRef, useState } from 'react';

const DEFAULT_CENTER = { lat: 1.2136, lng: -77.2811 };
const MAPS_KEY = import.meta.env.VITE_GOOGLE_MAPS_API_KEY;

let mapsPromise;

function loadGoogleMaps() {
  if (window.google?.maps) {
    return Promise.resolve(window.google.maps);
  }
  if (!mapsPromise) {
    mapsPromise = new Promise((resolve, reject) => {
      const script = document.createElement('script');
      script.src = `https://maps.googleapis.com/maps/api/js?key=${MAPS_KEY}&language=es`;
      script.async = true;
      script.onload = () => resolve(window.google.maps);
      script.onerror = () => reject(new Error('No se pudo cargar Google Maps'));
      document.head.appendChild(script);
    });
  }
  return mapsPromise;
}

export default function MapPicker({ value, onChange, readOnly = false, height = 320 }) {
  const containerRef = useRef(null);
  const mapRef = useRef(null);
  const markerRef = useRef(null);
  const [mapError, setMapError] = useState(!MAPS_KEY);
  const [locating, setLocating] = useState(false);

  useEffect(() => {
    if (!MAPS_KEY) {
      return;
    }
    let cancelled = false;
    loadGoogleMaps()
      .then((maps) => {
        if (cancelled || !containerRef.current) {
          return;
        }
        const center = value ?? DEFAULT_CENTER;
        mapRef.current = new maps.Map(containerRef.current, {
          center,
          zoom: value ? 16 : 14,
          streetViewControl: false,
          mapTypeControl: false,
        });
        markerRef.current = new maps.Marker({ map: mapRef.current, position: value ?? null });
        if (!readOnly) {
          mapRef.current.addListener('click', (event) => {
            onChange({ lat: event.latLng.lat(), lng: event.latLng.lng() });
          });
        }
      })
      .catch(() => setMapError(true));
    return () => {
      cancelled = true;
    };
  }, []);

  useEffect(() => {
    if (markerRef.current && value) {
      markerRef.current.setPosition(value);
      mapRef.current.panTo(value);
    }
  }, [value]);

  const useMyLocation = () => {
    if (!navigator.geolocation) {
      return;
    }
    setLocating(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setLocating(false);
        onChange({ lat: position.coords.latitude, lng: position.coords.longitude });
      },
      () => setLocating(false),
      { enableHighAccuracy: true, timeout: 10000 },
    );
  };

  const updateCoordinate = (field, text) => {
    const number = Number(text);
    if (!Number.isNaN(number)) {
      onChange({ ...(value ?? DEFAULT_CENTER), [field]: number });
    }
  };

  return (
    <div className="map-picker">
      {mapError ? (
        <div className="map-fallback" style={{ height: readOnly ? 'auto' : undefined }}>
          {value ? (
            <a href={`https://www.google.com/maps/search/?api=1&query=${value.lat},${value.lng}`} target="_blank" rel="noreferrer">
              Ver ubicación en Google Maps
            </a>
          ) : (
            <p>Usa tu ubicación actual o escribe las coordenadas del lugar.</p>
          )}
        </div>
      ) : (
        <div ref={containerRef} className="map" style={{ height }} />
      )}
      {!readOnly && (
        <div className="map-controls">
          <button type="button" className="btn btn-secondary" onClick={useMyLocation} disabled={locating}>
            {locating ? 'Obteniendo ubicación...' : 'Usar mi ubicación'}
          </button>
          <label>
            Latitud
            <input type="number" step="any" value={value?.lat ?? ''} onChange={(e) => updateCoordinate('lat', e.target.value)} />
          </label>
          <label>
            Longitud
            <input type="number" step="any" value={value?.lng ?? ''} onChange={(e) => updateCoordinate('lng', e.target.value)} />
          </label>
        </div>
      )}
      {!readOnly && !mapError && <p className="hint">Haz clic en el mapa para marcar el lugar exacto del problema.</p>}
    </div>
  );
}
