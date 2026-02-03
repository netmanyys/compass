INSERT INTO users (id, username, password_hash, enabled, created_at)
VALUES
  ('11111111-1111-1111-1111-111111111111', 'admin', '$2y$05$Gc2JQuTksq9k5kHV3jKTce9IUOISgvJxXehPpN2AbkI0Fq/cHhYu2', true, CURRENT_TIMESTAMP),
  ('22222222-2222-2222-2222-222222222222', 'staff', '$2y$05$Wp6vfuohl1H7kMoaaN2jkedeMkgzH0QpCNWBDQGw3/SteeoodFUWC', true, CURRENT_TIMESTAMP);

INSERT INTO user_roles (id, user_id, role)
VALUES
  ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111', 'ROLE_ADMIN'),
  ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '22222222-2222-2222-2222-222222222222', 'ROLE_STAFF');

INSERT INTO packages (id, name, description)
VALUES
  ('33333333-3333-3333-3333-333333333333', 'Premium Audio', 'Upgraded sound system');

INSERT INTO vehicles (
    id, vin, manufacture, model, year, color, trim, body_type, drivetrain, engine, transmission, fuel_type,
    mileage, condition_grade, title_status, carfax_url, status, location, purchase_price, list_price, market_price,
    notes, acquisition_channel, purchase_date, expected_ready_date, documents, reservation, created_at, updated_at, created_by, updated_by
) VALUES
  (
    '44444444-4444-4444-4444-444444444444',
    '1HGCM82633A123456', 'Honda', 'Accord', 2019, 'Black', 'EX', 'Sedan', 'FWD', '2.0L', 'Automatic', 'Gas',
    45000, 'B', 'Clean', NULL, 'IN_STOCK', 'Lot A', 15000.00, 18500.00, 19000.00,
    'Intake completed', 'Auction', CURRENT_DATE, NULL, NULL, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'
  );

INSERT INTO vehicle_packages (vehicle_id, package_id)
VALUES
  ('44444444-4444-4444-4444-444444444444', '33333333-3333-3333-3333-333333333333');

INSERT INTO service_records (id, vehicle_id, service_date, vendor, odometer, category, description, cost, invoice_url, created_at, created_by)
VALUES
  ('66666666-6666-6666-6666-666666666666', '44444444-4444-4444-4444-444444444444', CURRENT_DATE, 'QuickFix Auto', 45000, 'MAINTENANCE', 'Oil change', 120.00, NULL, CURRENT_TIMESTAMP, 'admin');

INSERT INTO vehicle_comments (id, vehicle_id, comment, created_at, created_by)
VALUES
  ('77777777-7777-7777-7777-777777777777', '44444444-4444-4444-4444-444444444444', 'Initial intake completed.', CURRENT_TIMESTAMP, 'admin');
