你是资深全栈工程师。请生成一个“二手车商内部库存管理系统”的可运行项目代码（MVP 可扩展），严格按以下约束实现，不要改变架构，不要引入额外复杂方案（例如 nested routers、GraphQL、S3、复杂状态机等）。

========================
技术栈（必须）
========================
- Frontend: React + TypeScript + Vite + React Router
- Backend: Django + Django REST Framework
- DB: PostgreSQL
- Auth: JWT (djangorestframework-simplejwt)
- RBAC: Django Groups + DRF permission classes（必须这样做）
- Dev deploy: Docker Compose (db + backend + frontend)

========================
用户与权限（必须）
========================
- 不提供 public signup
- 只允许内部用户登录（用户由 Django admin/createsuperuser 创建）
- 创建两个 Django Group：admin, staff
- 权限规则：
  - staff：只允许 GET（list/retrieve/search）
  - admin：允许全 CRUD（POST/PUT/PATCH/DELETE）
- 必须实现 DRF permission class：IsAdminOrReadOnly
  - 判定逻辑：安全方法(GET/HEAD/OPTIONS)放行；否则必须属于 group=admin 或 is_superuser

========================
数据模型（必须实现 + migrations）
========================
A) Vehicle
- vin (CharField 17, unique, db_index)
- manufacture, model, year, color, trim(optional)
- body_type, drivetrain(optional), engine(optional), transmission(optional), fuel_type(optional)
- mileage
- condition_grade
- title_status
- carfax_url (nullable)
- status (choices: IN_STOCK, RESERVED, SOLD, IN_SERVICE, IN_TRANSIT)
- location
- purchase_price, list_price, market_price(optional)
- notes
- packages ManyToMany -> Package
- source/acquisition_channel, purchase_date, expected_ready_date
- documents (JSONField 或 TextField)
- reservation (JSONField 或 TextField)
- created_at, updated_at
- created_by, updated_by (FK->User, nullable but recommended)
索引：
- vin unique
- manufacture+model+year 组合索引
- status index, list_price index, mileage index

B) Package
- name unique
- description optional

C) ServiceRecord
- vehicle FK related_name="service_records"
- service_date, vendor, odometer(optional)
- category choices
- description
- cost
- invoice_url optional
- created_at, created_by

D) VehicleImage（重点：必须可上传）
- vehicle FK related_name="images"
- image ImageField(upload_to="vehicles/%Y/%m/%d/")
- caption optional
- is_primary boolean（同车最多一个 primary：保存时自动把其他 primary 置 False）
- sort_order int default 0
- uploaded_at, uploaded_by(optional recommended)

E) VehicleComment
- vehicle FK related_name="comments"
- comment text
- created_by
- created_at

依赖：
- requirements.txt 必须包含 Pillow

========================
后端 API（必须）
========================
必须使用 ModelViewSet + DefaultRouter（不要用 nested routers）。
列表过滤全部使用 query params（vehicle=<id>）。

Auth:
- POST /api/auth/login/  -> jwt token pair
- POST /api/auth/refresh/
- GET /api/auth/me/ -> {username, groups, role}

Vehicles:
- /api/vehicles/ (ViewSet)
- GET 支持过滤/分页/排序（django-filter + OrderingFilter）
  - vin exact
  - manufacture icontains
  - model icontains
  - year_min/year_max
  - price_min/price_max (list_price)
  - mileage_min/mileage_max
  - color exact/iexact
  - status exact
  - condition_grade exact
  - packages 多选（按 package id）
- ordering: ?ordering=year,-list_price

ServiceRecords:
- /api/service-records/
- GET 支持 ?vehicle=<vehicle_id> 过滤
- RBAC：staff GET only，admin CRUD

VehicleImages（必须支持 multipart 上传）:
- /api/vehicle-images/
- GET 支持 ?vehicle=<vehicle_id> 过滤
- POST 使用 multipart/form-data 上传字段 image + vehicle + 可选 caption/is_primary/sort_order
- serializer 必须返回 image_url（绝对 URL，使用 request.build_absolute_uri(image.url)）
- RBAC：staff GET only，admin CRUD

VehicleComments:
- /api/vehicle-comments/
- GET 支持 ?vehicle=<vehicle_id> 过滤
- RBAC：staff GET only，admin CRUD（或 admin create + staff read）

CORS:
- 允许 frontend dev 访问（配置 django-cors-headers）

Media（dev 必须可用）:
- settings.py: MEDIA_URL=/media/ ; MEDIA_ROOT=/app/media
- urls.py 在 DEBUG 下提供 media 服务：static(MEDIA_URL, document_root=MEDIA_ROOT)

========================
前端页面（必须）
========================
Routes:
- /login: username/password 登录，保存 access token 到 localStorage
- /dashboard:
  - Filters: manufacture/model/year range/color/status/price range/mileage range/condition/packages/vin
  - Vehicles table: vin, manufacture, model, year, color, mileage, status, list_price, location, primary image(如有)
  - 点击进入详情 /vehicles/:id
- /vehicles/:id:
  - 显示 vehicle fields + packages + carfax link + comments + service records + images
  - 若 role=admin:
    - 显示按钮：编辑 vehicle、添加 service record、添加 comment、上传图片、设置主图、删除图片
  - staff：只读，不显示编辑按钮

前端实现约束：
- axios 封装 + interceptor:
  - 自动加 Authorization header
  - 401 自动跳转 /login
- 图片上传（admin）必须使用 FormData + multipart

========================
Docker Compose（必须）
========================
- services: db(postgres), backend(django), frontend(vite)
- backend 启动流程：等待 db -> migrate -> runserver 0.0.0.0:8000
- backend 必须挂载 media volume：./backend/media:/app/media
- 提供 .env.example
- README 必须包含：
  - docker compose up --build
  - createsuperuser
  - 创建 staff 用户并加入 staff group 的命令/步骤（也可在 Django admin 操作）
  - 创建 admin group 并给用户加组的步骤
  - 访问地址（frontend/backend）
  - 图片上传后文件落地路径与 media 说明

========================
输出格式（必须）
========================
- 给出完整目录结构
- 给出关键文件的完整代码（不是伪代码）
- 给出 README
- 确保按 README 在全新环境可运行
