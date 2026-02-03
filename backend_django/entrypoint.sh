#!/bin/sh
set -e

python - <<'PY'
import os
import time
import psycopg

host = os.getenv("POSTGRES_HOST", "db")
port = int(os.getenv("POSTGRES_PORT", "5432"))
name = os.getenv("POSTGRES_DB", "compass")
user = os.getenv("POSTGRES_USER", "compass")
password = os.getenv("POSTGRES_PASSWORD", "compass")

for attempt in range(30):
    try:
        conn = psycopg.connect(host=host, port=port, dbname=name, user=user, password=password)
        conn.close()
        print("Database ready.")
        break
    except Exception:
        time.sleep(1)
else:
    raise SystemExit("Database not ready after 30s")
PY

python manage.py migrate
python manage.py createcachetable || true
python manage.py runserver 0.0.0.0:8000
