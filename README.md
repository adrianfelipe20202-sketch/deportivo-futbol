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

## Despliegue con Dockerfile

```bash
docker build -t deportivo-futbol .
docker run -p 9090:9090 \
  -e NEON_DB_PASSWORD=tu_clave \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://..." \
  deportivo-futbol
```

Compatible con Render, Railway, Fly.io, etc. (no Vercel para apps Java).

## Estructura

- `src/main/java/com/deportivo/` — controladores, entidades JPA, repositorios
- `src/main/resources/templates/` — vistas Thymeleaf
- `src/main/resources/neon/01-tablas-deportivo.sql` — esquema SQL de referencia
