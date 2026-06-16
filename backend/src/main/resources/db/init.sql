CREATE TABLE IF NOT EXISTS weather_snapshots (
    id              BIGSERIAL PRIMARY KEY,
    fetched_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    temperature     DECIMAL(5, 2),
    feels_like      DECIMAL(5, 2),
    humidity        INTEGER,
    rainfall_mm     DECIMAL(6, 2) DEFAULT 0,
    wind_speed      DECIMAL(5, 2),
    description     VARCHAR(200),
    location        VARCHAR(100) NOT NULL DEFAULT 'Colombo'
);

CREATE TABLE IF NOT EXISTS outage_reports (
    id              BIGSERIAL PRIMARY KEY,
    reported_at     TIMESTAMP NOT NULL DEFAULT NOW(),
    issue_type      VARCHAR(50) NOT NULL,
    district        VARCHAR(100) NOT NULL,
    area            VARCHAR(200),
    description     TEXT,
    duration_hours  INTEGER,
    is_resolved     BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_reports_district ON outage_reports(district);
CREATE INDEX IF NOT EXISTS idx_reports_type     ON outage_reports(issue_type);
CREATE INDEX IF NOT EXISTS idx_reports_date     ON outage_reports(reported_at);

INSERT INTO outage_reports (issue_type, district, area, description) VALUES
    ('WATER', 'Colombo',    'Maharagama',  'No water supply since morning'),
    ('POWER', 'Colombo',    'Nugegoda',    'Power outage 2 hours'),
    ('WATER', 'Kandy',      'City center', 'Low water pressure all day'),
    ('POWER', 'Gampaha',    'Negombo',     'Intermittent power cuts'),
    ('WATER', 'Kurunegala', 'Town area',   'Water shortage reported');