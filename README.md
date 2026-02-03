# Compass Inventory (Spring Boot + React)

二手车商内部库存管理系统（MVP，可扩展）。后端改为 Spring Boot 3 + PostgreSQL + Flyway + JWT。

## 技术栈

- Frontend: React + TypeScript + Vite + React Router + axios
- Backend: Java 17 + Spring Boot 3 + Spring Web + Spring Data JPA + Spring Security + JWT
- DB: PostgreSQL
- Migration: Flyway (V1 初始化 + V2 seed)
- Dev Deploy: Docker Compose (db + backend + frontend)

## Quick Start

```bash
cp .env.example .env

docker compose up --build
```

访问地址：
- Frontend: http://localhost:5173
- Backend: http://localhost:8080/api
- 静态图片: http://localhost:8080/uploads/**

默认账号（Flyway seed）：
- admin / admin123
- staff / staff123

## RBAC

- ROLE_ADMIN: CRUD + 上传图片
- ROLE_STAFF: 只读（GET）

验证：staff 调用 POST/PATCH/DELETE `/api/**` 应返回 403。

## Auth API

- POST `/api/auth/login`
- POST `/api/auth/refresh`
- GET `/api/auth/me`

返回 accessToken + refreshToken（access 15min，refresh 7days）。

## 车辆 API

- GET `/api/vehicles`（分页：page/size，排序：sort=year,desc）
- POST `/api/vehicles`
- GET `/api/vehicles/{id}`
- PATCH `/api/vehicles/{id}`
- DELETE `/api/vehicles/{id}`

过滤参数（全部支持）：
- vin, manufacture, model
- yearMin/yearMax
- priceMin/priceMax
- mileageMin/mileageMax
- color, status, conditionGrade
- packageIds=uuid1,uuid2
- keyword

## Service Records

- GET `/api/service-records?vehicleId={id}`
- POST `/api/service-records`
- PATCH `/api/service-records/{id}`
- DELETE `/api/service-records/{id}`

## Comments

- GET `/api/vehicle-comments?vehicleId={id}`
- POST `/api/vehicle-comments`
- DELETE `/api/vehicle-comments/{id}`

## Images

- GET `/api/vehicle-images?vehicleId={id}`
- POST `/api/vehicle-images` (multipart/form-data)
  - vehicleId, image(file), caption, isPrimary, sortOrder
- PATCH `/api/vehicle-images/{id}` (caption/isPrimary/sortOrder)
- DELETE `/api/vehicle-images/{id}`

上传目录：`./backend/uploads` -> `/app/uploads`，访问路径：`/uploads/**`。

## 测试

Backend:
```bash
docker compose run --rm backend mvn -q test
```

Frontend:
```bash
cd frontend
npm install
npm test
```

## BCrypt Hash（用于 seed）

V2__seed.sql 使用以下 bcrypt：
- admin123: `$2y$05$Gc2JQuTksq9k5kHV3jKTce9IUOISgvJxXehPpN2AbkI0Fq/cHhYu2`
- staff123: `$2y$05$Wp6vfuohl1H7kMoaaN2jkedeMkgzH0QpCNWBDQGw3/SteeoodFUWC`

可用 `htpasswd -nbB user pass` 重新生成。

## 自检清单（已验证）

- A. Compose 可运行：db/back/front 启动成功，Flyway V1/V2 自动执行无报错。
- B. 鉴权可用：admin/staff 登录成功，/api/auth/me 返回 role 正确。
- C. RBAC 正确：staff 写操作 403，admin CRUD 正常。
- D. Vehicles 过滤可用：manufacture/model icontains、范围过滤、排序分页可用。
- E. 图片上传可用：上传/访问/主图切换/删除正常（/uploads/**）。
- F. 前端功能可用：登录->dashboard 过滤->详情；admin 可上传图片与新增记录，staff 不显示按钮。
