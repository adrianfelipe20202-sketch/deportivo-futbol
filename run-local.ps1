# Arranque local con PostgreSQL en Docker (docker compose up -d antes).
$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Host "Docker no está en el PATH. Instala Docker Desktop o usa Neon con NEON_DB_PASSWORD." -ForegroundColor Yellow
    exit 1
}

docker compose up -d
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$env:NEON_DB_USER = "deportivo"
$env:NEON_DB_PASSWORD = "deportivo"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/deportivo?sslmode=disable"

Write-Host "Iniciando app en http://localhost:9090 ..." -ForegroundColor Green
.\mvnw.cmd spring-boot:run
