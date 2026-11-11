import { useEffect, useMemo } from 'react';

const MAX_PHOTOS = 3;

export default function PhotoUploader({ photos, onChange }) {
  const previews = useMemo(() => photos.map((photo) => URL.createObjectURL(photo)), [photos]);

  useEffect(() => () => previews.forEach((url) => URL.revokeObjectURL(url)), [previews]);

  const handleFiles = (event) => {
    const selected = Array.from(event.target.files).filter((file) => file.type.startsWith('image/'));
    onChange([...photos, ...selected].slice(0, MAX_PHOTOS));
    event.target.value = '';
  };

  const removePhoto = (index) => onChange(photos.filter((_, i) => i !== index));

  return (
    <div className="photo-uploader">
      <div className="photo-grid">
        {previews.map((url, index) => (
          <div className="photo-item" key={url}>
            <img src={url} alt={`Foto ${index + 1}`} />
            <button type="button" className="photo-remove" onClick={() => removePhoto(index)} aria-label="Quitar foto">
              ×
            </button>
          </div>
        ))}
        {photos.length < MAX_PHOTOS && (
          <label className="photo-add">
            <input type="file" accept="image/*" capture="environment" multiple onChange={handleFiles} />
            <span>+ Agregar foto</span>
          </label>
        )}
      </div>
      <p className="hint">Puedes adjuntar hasta {MAX_PHOTOS} fotos del problema.</p>
    </div>
  );
}
