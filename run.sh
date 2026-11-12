#!/usr/bin/env bash
set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

cleanup() {
  echo ""
  echo "Deteniendo AlertaVía..."
  kill "$BACKEND_PID" "$FRONTEND_PID" 2>/dev/null || true
}
trap cleanup EXIT

echo "[1/4] Liberando los puertos 8080 y 5173..."
for port in 8080 5173; do
  pid=$(lsof -ti:"$port" 2>/dev/null || true)
  if [ -n "$pid" ]; then
    kill -9 $pid 2>/dev/null || true
  fi
done

echo "[2/4] Iniciando PostgreSQL..."
if pg_isready -h localhost -p 5432 >/dev/null 2>&1; then
  echo "      PostgreSQL ya está corriendo en el puerto 5432."
else
  docker compose -f "$PROJECT_DIR/docker-compose.yml" up -d postgres
  until docker exec alertavia-postgres pg_isready -U alertavia -d alertavia_db >/dev/null 2>&1; do
    sleep 2
  done
fi

echo "[3/4] Iniciando el backend (Spring Boot)..."
cd "$PROJECT_DIR/backend"
./mvnw -q spring-boot:run > "$PROJECT_DIR/backend.log" 2>&1 &
BACKEND_PID=$!
until curl -s -o /dev/null http://localhost:8080/v3/api-docs; do
  sleep 3
done

echo "[4/4] Iniciando el frontend (React + Vite)..."
cd "$PROJECT_DIR/frontend"
[ -d node_modules ] || npm install
npm run dev -- --port 5173 > "$PROJECT_DIR/frontend.log" 2>&1 &
FRONTEND_PID=$!
sleep 3

echo ""
echo "AlertaVía está corriendo:"
echo "  Frontend: http://localhost:5173"
echo "  Backend:  http://localhost:8080"
echo "  Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
echo "Cuentas de prueba:"
echo "  Administrador: admin@alertavia.com / admin123"
echo "  Ciudadano:     ciudadano@alertavia.com / ciudadano123"
echo ""
echo "Presiona Ctrl+C para detener todo."
wait
