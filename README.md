# Deportivo Fútbol — CRUD Java (Spring Boot)

Aplicación web CRUD de gestión deportiva (asociaciones, entrenadores, clubes, competiciones, jugadores). Stack: **Java 21**, **Spring Boot 3**, **Thymeleaf**, **PostgreSQL** (Neon en producción o Docker local).

## Requisitos

- JDK 21+
- Docker Desktop (opcional, para BD local)
- Maven Wrapper incluido (`mvnw.cmd`)

## Base de datos

### Opción A — Docker local (recomendado si borraste la BD en la nube)

```powershell
cd deportivo-futbol
docker compose up -d
$env:NEON_DB_USER = "deportivo"
$env:NEON_DB_PASSWORD = "deportivo"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/deportivo?sslmode=disable"
.\mvnw.cmd spring-boot:run
```

Al primer arranque, Hibernate crea las tablas y `SupabaseDemoDataLoader` inserta datos de ejemplo.

### Opción B — Neon (PostgreSQL en la nube)

1. Crea o restaura el proyecto en [Neon](https://neon.tech).
2. Copia la contraseña y guárdala en `%USERPROFILE%\.deportivo\supabase-db.properties`:

```properties
spring.datasource.password=TU_CLAVE_NEON
```

O define la variable de entorno:

```powershell
$env:NEON_DB_PASSWORD = "TU_CLAVE_NEON"
```

3. (Opcional) Ejecuta `src/main/resources/neon/01-tablas-deportivo.sql` en el SQL Editor de Neon.
4. Arranca la app: `.\mvnw.cmd spring-boot:run`

La URL por defecto está en `src/main/resources/application-neon.properties`.

## Uso

Abre [http://localhost:9090](http://localhost:9090) (puerto por defecto; en Docker/cloud usa la variable `PORT`).

## Tablas en Neon (SQL Editor)

El script está en `src/main/resources/neon/01-tablas-deportivo.sql`. Pégalo en **Neon → SQL Editor** y ejecuta, o:

```powershell
$env:NEON_DATABASE_URL = "postgresql://neondb_owner:TU_CLAVE@ep-lingering-glitter-ap6kg8a1-pooler.c-7.us-east-1.aws.neon.tech/neondb?sslmode=require"
pip install psycopg2-binary
python scripts/aplicar-sql-neon.py
```

Si no ejecutas el SQL, Hibernate también crea las tablas al arrancar (`ddl-auto=update`).

## Docker (app + Neon)

1. Instala [Docker Desktop](https://www.docker.com/products/docker-desktop/).
2. `copy .env.example .env` y pon tu `NEON_DB_PASSWORD`.
3. `docker compose -f docker-compose.neon.yml up --build`
4. Abre http://localhost:9090

## Despliegue en Render

1. Repo en GitHub: https://github.com/adrianfelipe20202-sketch/deportivo-futbol
2. [Render Dashboard](https://dashboard.render.com) → **New** → **Blueprint** → conecta el repo.
3. Render lee `render.yaml`. En el servicio, añade la variable secreta:
   - `NEON_DB_PASSWORD` = contraseña de Neon (la de tu connection string).
4. Tras el deploy, abre la URL que te da Render (puerto `PORT` lo inyecta Render automáticamente).

También puedes crear el servicio manualmente: **New Web Service** → Runtime **Docker** → mismo repo y variables del `render.yaml`.

## Despliegue con Dockerfile (manual)

```bash
docker build -t deportivo-futbol .
docker run -p 9090:9090 \
  -e NEON_DB_PASSWORD=tu_clave \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://ep-lingering-glitter-ap6kg8a1-pooler.c-7.us-east-1.aws.neon.tech:5432/neondb?sslmode=require" \
  deportivo-futbol
```

## Estructura

- `src/main/java/com/deportivo/` — controladores, entidades JPA, repositorios
- `src/main/resources/templates/` — vistas Thymeleaf
- `src/main/resources/neon/01-tablas-deportivo.sql` — esquema SQL de referencia
