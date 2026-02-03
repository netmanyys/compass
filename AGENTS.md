# 二手车商内部库存管理系统（MVP + 可扩展）— Spring Boot 

你是资深全栈工程师。请生成一个可运行的全栈项目代码（MVP 可扩展），严格遵循以下架构与约束，不要引入额外复杂方案（例如 GraphQL、Elasticsearch、Kubernetes、微服务拆分、复杂 DDD、Nested Router 变体等）。必须能按 README 在全新环境一键跑起来。

============================================================
1) 技术栈（必须）
============================================================
Frontend（必须独立项目）：
- React + TypeScript + Vite + React Router
- axios + interceptor
- 不允许使用后端模板渲染页面（后端仅提供 REST API）
- UI 可简单 CSS 或 MUI/AntD/Chakra（三选一即可）

Backend：
- Java 17（固定用 Java 17）
- Spring Boot 3.x
- Spring Web (REST)
- Spring Data JPA (Hibernate)
- PostgreSQL
- Flyway（必须，用于数据库迁移 + seed）
- Spring Security + JWT（必须）
- Bean Validation（jakarta validation）

Dev Deploy：
- Docker Compose：db + backend + frontend（必须）
- backend 上传文件目录必须通过 volume 挂载持久化（必须）

============================================================
2) 用户与 RBAC（最可靠方式，必须）
============================================================
- 不提供 public signup / register API
- 用户只能由管理员预先创建
- RBAC 只用两种角色：
  - ROLE_ADMIN：全 CRUD + 上传图片
  - ROLE_STAFF：只读（仅 GET）
- 写操作（POST/PUT/PATCH/DELETE）必须只允许 ADMIN
- 读操作（GET）允许 ADMIN 和 STAFF

强制实现方式（必须按这个写）：
- Spring Security：JWT 认证
- 用 SecurityFilterChain 配置实现：
  - /api/auth/login 与 /api/auth/refresh 放行
  - 其他 /api/** 必须登录
  - GET /api/** 允许 ADMIN 或 STAFF
  - 非 GET /api/** 只允许 ADMIN
- 用户表存储：username + bcrypt password + roles（ROLE_ADMIN/ROLE_STAFF）

重要：首次用户创建（必须实现且可运行）
- 必须使用 Flyway 的 V2__seed.sql（或类似）插入至少两个用户：
  - admin / admin123（ROLE_ADMIN）
  - staff / staff123（ROLE_STAFF）
- 密码必须 bcrypt：请在代码中提供一个可重复的方式生成 bcrypt hash（例如在 README 中给出 Spring Security BCryptPasswordEncoder 输出的 hash 值，或在 seed.sql 使用已生成的 hash）。禁止明文存储。

============================================================
3) 数据模型（必须）—— JPA Entities + Flyway
============================================================
必须实现以下实体与关系，并写 Flyway migrations（V1__init.sql、V2__seed.sql）。字段可小幅调整，但核心字段必须覆盖。

A) Vehicle（车辆主表）
- id: UUID（固定用 UUID）
- vin: VARCHAR(17) UNIQUE NOT NULL（索引+唯一约束）
- manufacture: VARCHAR NOT NULL
- model: VARCHAR NOT NULL
- year: INT NOT NULL（范围校验：例如 1980..nextYear）
- color: VARCHAR NOT NULL
- trim: VARCHAR NULL
- body_type: VARCHAR NOT NULL
- drivetrain: VARCHAR NULL
- engine: VARCHAR NULL
- transmission: VARCHAR NULL
- fuel_type: VARCHAR NULL
- mileage: INT NOT NULL (>=0)
- condition_grade: VARCHAR NOT NULL（例如 A/B/C 或 1-5，MVP 先用字符串）
- title_status: VARCHAR NOT NULL（Clean/Salvage/Lemon/Unknown）
- carfax_url: TEXT NULL
- status: VARCHAR NOT NULL（enum：IN_STOCK/RESERVED/SOLD/IN_SERVICE/IN_TRANSIT）
- location: VARCHAR NOT NULL
- purchase_price: NUMERIC(12,2) NOT NULL (>=0)
- list_price: NUMERIC(12,2) NOT NULL (>=0)
- market_price: NUMERIC(12,2) NULL (>=0)
- notes: TEXT NULL
- source/acquisition_channel: VARCHAR NULL
- purchase_date: DATE NULL
- expected_ready_date: DATE NULL
- documents: TEXT（MVP 用 TEXT，可扩展 JSONB）
- reservation: TEXT（MVP 用 TEXT，可扩展 JSONB）
- created_at, updated_at: TIMESTAMPTZ
- created_by, updated_by: VARCHAR（保存 username）

索引（必须在 migration 中实现）：
- unique index on vin
- index(manufacture, model, year)
- index(status)
- index(list_price)
- index(mileage)

B) Package（配置包）
- id: UUID
- name UNIQUE NOT NULL
- description TEXT NULL

Vehicle <-> Package：Many-to-Many（join table vehicle_packages）

C) ServiceRecord（维修记录）
- id: UUID
- vehicle_id FK NOT NULL
- service_date DATE NOT NULL
- vendor VARCHAR NOT NULL
- odometer INT NULL
- category VARCHAR NOT NULL（MAINTENANCE/REPAIR/DETAILING/TIRES...）
- description TEXT NOT NULL
- cost NUMERIC(12,2) NOT NULL (>=0)
- invoice_url TEXT NULL
- created_at TIMESTAMPTZ
- created_by VARCHAR

索引：index(vehicle_id), index(service_date)

D) VehicleImage（重点：管理员上传图片）
必须“一车多图”，并支持 primary 图。
- id: UUID
- vehicle_id FK NOT NULL
- file_path VARCHAR NOT NULL（存储在服务器文件系统中的相对路径，如 vehicles/2026/02/xxx.jpg）
- original_filename VARCHAR NULL
- content_type VARCHAR NULL
- file_size BIGINT NULL
- caption VARCHAR NULL
- is_primary BOOLEAN NOT NULL DEFAULT FALSE（同一辆车最多一个 primary：保存时要自动把其他置 false）
- sort_order INT NOT NULL DEFAULT 0
- uploaded_at TIMESTAMPTZ
- uploaded_by VARCHAR

索引：index(vehicle_id), index(is_primary)

E) VehicleComment（备注/跟进）
- id: UUID
- vehicle_id FK NOT NULL
- comment TEXT NOT NULL
- created_at TIMESTAMPTZ
- created_by VARCHAR

F) User / Role（必须）
- users 表：id(UUID), username unique, password_hash, enabled, created_at
- user_roles 表：user_id FK, role（ROLE_ADMIN/ROLE_STAFF）
- 或 users 表内 roles 数组也行，但必须清晰且可用 Spring Security 读取

============================================================
4) 后端 REST API（必须，固定风格，不要跑偏）
============================================================
必须是标准 REST JSON API（除上传接口为 multipart）。
路径统一前缀：/api

4.1 Auth（必须）
- POST /api/auth/login
  - body: { "username": "...", "password": "..." }
  - return: { "accessToken": "...", "refreshToken": "...", "expiresInSeconds": 900, "role": "admin|staff" }
- POST /api/auth/refresh
  - body: { "refreshToken": "..." }
  - return: { "accessToken": "...", "expiresInSeconds": 900 }
- GET /api/auth/me
  - return: { username, roles: ["ROLE_ADMIN"], role: "admin|staff" }

JWT 要求（必须实现）：
- access token 过期：15 min
- refresh token 过期：7 days
- token claims 含 roles
- refresh token 也需要签名校验与过期校验
- 密码必须 bcrypt

4.2 Vehicles（必须）
- GET /api/vehicles
  - 支持分页：page, size（Spring Pageable）
  - 支持排序：sort=year,desc 或 sort=listPrice,asc（Spring Pageable 标准）
  - 支持过滤 query params（必须全部实现）：
    - vin（精确）
    - manufacture（icontains：lower LIKE %x%）
    - model（icontains）
    - yearMin/yearMax
    - priceMin/priceMax（list_price）
    - mileageMin/mileageMax
    - color（精确/忽略大小写）
    - status（精确）
    - conditionGrade（精确）
    - packageIds（多选：packageIds=uuid1,uuid2）
    - keyword（可选：简单全字段 LIKE）
  - 实现方式（必须）：Spring Data JPA Specification（不要手写拼 SQL）
- POST /api/vehicles（ADMIN only）
- GET /api/vehicles/{id}
- PATCH /api/vehicles/{id}（ADMIN only，推荐 PATCH）
- DELETE /api/vehicles/{id}（ADMIN only）

4.3 Packages（必须）
- GET /api/packages（ADMIN/STAF）
- POST /api/packages（ADMIN only）
- PATCH /api/packages/{id}（ADMIN only）
- DELETE /api/packages/{id}（ADMIN only）

4.4 Service Records（必须）
不要做 nested router；用 vehicleId 过滤最稳：
- GET /api/service-records?vehicleId={vehicleId}
- POST /api/service-records（ADMIN only）
- GET /api/service-records/{id}
- PATCH /api/service-records/{id}（ADMIN only）
- DELETE /api/service-records/{id}（ADMIN only）

4.5 Vehicle Comments（必须）
- GET /api/vehicle-comments?vehicleId={vehicleId}
- POST /api/vehicle-comments（ADMIN only）
- DELETE /api/vehicle-comments/{id}（ADMIN only）

4.6 Vehicle Images（必须支持上传，最关键）
- GET /api/vehicle-images?vehicleId={vehicleId}
  - 返回每张图片：
    - id, vehicleId, imageUrl, isPrimary, caption, sortOrder, uploadedAt
- POST /api/vehicle-images（ADMIN only，multipart/form-data）
  - form fields:
    - vehicleId: uuid
    - image: file (MultipartFile)
    - caption (optional)
    - isPrimary (optional)
    - sortOrder (optional)
  - 服务器把文件保存到本地目录（必须）：
    - 基准目录：/app/uploads（可配置，默认 /app/uploads）
    - 相对路径：vehicles/YYYY/MM/<uuid>.<ext>
  - DB 存 file_path（相对路径）
  - 返回 imageUrl：后端提供静态访问 URL，如 /uploads/vehicles/...
- PATCH /api/vehicle-images/{id}（ADMIN only）
  - 可更新：caption, isPrimary, sortOrder
  - 若设置 isPrimary=true，必须自动把同 vehicle 的其他图 isPrimary=false
- DELETE /api/vehicle-images/{id}（ADMIN only）
  - 删除 DB 记录，并删除文件（如果文件存在）

静态文件服务（dev 必须可用）：
- 后端必须暴露静态路径：
  - /uploads/** -> file:/app/uploads/
- 必须使用 Spring MVC ResourceHandler 配置，不要依赖额外 nginx

错误处理（必须）：
- 返回统一错误格式，例如：
  - { "error": "VALIDATION_ERROR", "message": "...", "details": {...} }

============================================================
5) 前端（必须）
============================================================
必须提供以下页面并可运行：

Routes:
- /login
  - username/password 登录
  - 成功后保存 accessToken + refreshToken 到 localStorage
- /dashboard
  - 顶部 Filters：manufacture, model, year range, color, status, price range, mileage range, conditionGrade, packageIds, vin
  - Vehicle 列表（table 或 card）显示：
    - primary image（如有）
    - vin, manufacture, model, year, color, mileage, status, listPrice, location
  - 点击进入 /vehicles/:id
- /vehicles/:id
  - 展示 vehicle 详情 + packages + carfax link + comments + service records + images
  - role=admin 才显示：
    - 编辑 vehicle
    - 新增 service record
    - 新增 comment
    - 上传图片（FormData multipart）
    - 设置主图（调用 PATCH isPrimary）
    - 删除图片

axios 要求（必须）：
- baseURL 指向 backend（docker compose 下通过环境变量或固定 http://localhost:8080）
- request interceptor 自动加 Authorization header
- response interceptor：
  - 遇到 401：先调用 /api/auth/refresh（使用 refreshToken）获取新 accessToken，再重试原请求
  - refresh 失败则清理 token 并跳转 /login

============================================================
6) Docker Compose（必须，dev 一键启动）
============================================================
- db: postgres
- backend: spring boot (port 8080)
- frontend: vite (port 5173)

关键要求（必须）：
- backend uploads 目录必须持久化：挂载 volume
  - ./backend/uploads:/app/uploads
- 后端通过环境变量配置：
  - DB url/user/password
  - JWT secret
  - UPLOAD_DIR=/app/uploads
- Flyway 在启动时自动建表 + seed 初始用户与少量演示数据（至少 1 个 package + 2 台车可选）

README 必须包含：
- docker compose up --build
- 默认账号：
  - admin / admin123
  - staff / staff123
- 如何验证 RBAC：staff 调用 POST 会返回 403
- 上传图片存储路径与访问 URL：/uploads/**

============================================================
7) 工程结构（必须输出）
============================================================
输出完整目录结构：
- backend/
  - src/main/java/...（controller/service/repository/security/dto/specification/config）
  - src/main/resources/application.yml
  - src/main/resources/db/migration/V1__init.sql, V2__seed.sql
  - Dockerfile
- frontend/
  - src/pages/Login.tsx, Dashboard.tsx, VehicleDetail.tsx
  - src/api/axios.ts
  - Dockerfile
- docker-compose.yml
- .env.example
- README.md

实现要求（不要跑偏）：
- 后端分层：controller -> service -> repository
- DTO：请求/响应分离，不要直接暴露 Entity
- JPA Specification 用于 Vehicle 搜索过滤
- 只实现上述功能即可，不要额外加复杂组件

============================================================
8) 输出前自检清单（必须逐条确认，未通过不得输出）
============================================================
在输出代码之前，你必须逐条自检并在 README 中写明“已验证”的内容：

A. Compose 可运行
- docker compose up --build 后，db/back/front 都启动成功
- Flyway 自动执行 V1/V2，无报错

B. 鉴权可用
- admin/admin123 可以登录并获得 access+refresh
- staff/staff123 可以登录并获得 access+refresh
- /api/auth/me 返回 role 正确

C. RBAC 正确
- staff 对任意 POST/PATCH/DELETE /api/** 返回 403
- admin 对 CRUD 正常

D. Vehicles 过滤可用
- manufacture/model icontains 可用
- yearMin/yearMax、priceMin/priceMax、mileageMin/mileageMax 可用
- sort 与分页可用

E. 图片上传可用（最关键）
- admin 通过 POST /api/vehicle-images multipart 上传成功
- GET /api/vehicle-images?vehicleId=... 返回 imageUrl
- 访问 imageUrl（/uploads/**）浏览器能看到图片
- 设置 isPrimary=true 会自动清掉同车其他 primary
- DELETE 会删除 DB 记录且删除文件（若存在）

F. 前端功能可用
- 登录成功后进入 dashboard
- dashboard 过滤请求能更新列表
- 详情页能展示图片与记录
- admin 能上传图片并立即显示；staff 看不到上传按钮

最后输出：
1) 完整目录结构
2) 关键文件完整代码（不是伪代码）
3) README（按 README 能跑起来，包含自检项）
