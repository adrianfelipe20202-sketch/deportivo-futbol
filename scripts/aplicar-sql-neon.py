#!/usr/bin/env python3
"""Aplica 01-tablas-deportivo.sql en Neon. Uso:
  set NEON_DATABASE_URL=postgresql://user:pass@host/neondb?sslmode=require
  python scripts/aplicar-sql-neon.py
"""
from __future__ import annotations

import os
import sys
from pathlib import Path

try:
    import psycopg2
except ImportError:
    print("Instala: pip install psycopg2-binary", file=sys.stderr)
    sys.exit(1)

ROOT = Path(__file__).resolve().parents[1]
SQL_FILE = ROOT / "src" / "main" / "resources" / "neon" / "01-tablas-deportivo.sql"


def main() -> None:
    uri = os.environ.get("NEON_DATABASE_URL") or os.environ.get("DATABASE_URL")
    if not uri:
        print("Define NEON_DATABASE_URL (URI postgres de Neon).", file=sys.stderr)
        sys.exit(1)
    sql = SQL_FILE.read_text(encoding="utf-8")
    conn = psycopg2.connect(uri)
    conn.autocommit = True
    with conn.cursor() as cur:
        cur.execute(sql)
    conn.close()
    print(f"OK: {SQL_FILE.name} aplicado en Neon.")


if __name__ == "__main__":
    main()
