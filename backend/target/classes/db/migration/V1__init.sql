CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE user_roles (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL
);

CREATE TABLE packages (
    id UUID PRIMARY KEY,
    name VARCHAR(120) UNIQUE NOT NULL,
    description TEXT
);

CREATE TABLE vehicles (
    id UUID PRIMARY KEY,
    vin VARCHAR(17) UNIQUE NOT NULL,
    manufacture VARCHAR(120) NOT NULL,
    model VARCHAR(120) NOT NULL,
    year INT NOT NULL,
    color VARCHAR(80) NOT NULL,
    trim VARCHAR(120),
    body_type VARCHAR(80) NOT NULL,
    drivetrain VARCHAR(40),
    engine VARCHAR(120),
    transmission VARCHAR(80),
    fuel_type VARCHAR(40),
    mileage INT NOT NULL,
    condition_grade VARCHAR(20) NOT NULL,
    title_status VARCHAR(20) NOT NULL,
    carfax_url TEXT,
    status VARCHAR(20) NOT NULL,
    location VARCHAR(120) NOT NULL,
    purchase_price NUMERIC(12,2) NOT NULL,
    list_price NUMERIC(12,2) NOT NULL,
    market_price NUMERIC(12,2),
    notes TEXT,
    acquisition_channel VARCHAR(60),
    purchase_date DATE,
    expected_ready_date DATE,
    documents TEXT,
    reservation TEXT,
    created_at TIMESTAMPTZ,
    updated_at TIMESTAMPTZ,
    created_by VARCHAR(100),
    updated_by VARCHAR(100)
);

CREATE TABLE vehicle_packages (
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    package_id UUID NOT NULL REFERENCES packages(id) ON DELETE CASCADE,
    PRIMARY KEY (vehicle_id, package_id)
);

CREATE TABLE service_records (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    service_date DATE NOT NULL,
    vendor VARCHAR(120) NOT NULL,
    odometer INT,
    category VARCHAR(40) NOT NULL,
    description TEXT NOT NULL,
    cost NUMERIC(12,2) NOT NULL,
    invoice_url TEXT,
    created_at TIMESTAMPTZ,
    created_by VARCHAR(100)
);

CREATE TABLE vehicle_images (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    file_path VARCHAR(255) NOT NULL,
    original_filename VARCHAR(255),
    content_type VARCHAR(100),
    file_size BIGINT,
    caption VARCHAR(255),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    sort_order INT NOT NULL DEFAULT 0,
    uploaded_at TIMESTAMPTZ,
    uploaded_by VARCHAR(100)
);

CREATE TABLE vehicle_comments (
    id UUID PRIMARY KEY,
    vehicle_id UUID NOT NULL REFERENCES vehicles(id) ON DELETE CASCADE,
    comment TEXT NOT NULL,
    created_at TIMESTAMPTZ,
    created_by VARCHAR(100)
);

CREATE INDEX idx_vehicle_manufacture_model_year ON vehicles (manufacture, model, year);
CREATE INDEX idx_vehicle_status ON vehicles (status);
CREATE INDEX idx_vehicle_list_price ON vehicles (list_price);
CREATE INDEX idx_vehicle_mileage ON vehicles (mileage);
CREATE INDEX idx_service_vehicle ON service_records (vehicle_id);
CREATE INDEX idx_service_date ON service_records (service_date);
CREATE INDEX idx_vehicle_images_vehicle ON vehicle_images (vehicle_id);
CREATE INDEX idx_vehicle_images_primary ON vehicle_images (is_primary);
