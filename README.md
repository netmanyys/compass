# Compass Inventory (MVP)

Internal inventory system for a used car dealership.

## Stack

- Frontend: React + TypeScript + React Router (Vite)
- Backend: Django + DRF + JWT (simplejwt)
- DB: PostgreSQL
- Auth: JWT
- RBAC: Django Groups (`admin`, `staff`)
- Dev deployment: Docker Compose

## Quick Start (Docker)

1) Copy env file

```bash
cp .env.example .env
```

2) Build and start

```bash
docker compose up --build
```

3) Run migrations (automatically in backend container). Optionally seed data:

```bash
docker compose exec backend python manage.py seed_demo
```

4) Create an admin user (Django admin):

```bash
docker compose exec backend python manage.py createsuperuser
```

5) Create a staff user and assign group:

```bash
docker compose exec backend python manage.py shell
```

```python
from django.contrib.auth.models import Group, User
staff_group, _ = Group.objects.get_or_create(name="staff")
user = User.objects.create_user("staff_user", password="changeme")
user.groups.add(staff_group)
```

## Access

- Frontend: http://localhost:5173
- Backend API: http://localhost:8000/api
- Admin: http://localhost:8000/admin

## Auth Endpoints

- POST `/api/auth/login/` (username/password) -> access/refresh
- POST `/api/auth/refresh/`
- GET `/api/auth/me/`

## API Notes

- Vehicles: `/api/vehicles/` supports filtering, ordering, pagination
- Packages: `/api/packages/`
- Service records: `/api/vehicles/:id/service-records/`
- Comments: `/api/vehicles/:id/comments/`
- Images: `/api/vehicles/:id/images/`

## RBAC Rules

- `admin`: CRUD all resources
- `staff`: read-only (GET, search, filter)

## Tests

Backend tests:

```bash
docker compose exec backend python manage.py test
```
