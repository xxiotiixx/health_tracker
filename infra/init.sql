CREATE TABLE IF NOT EXISTS users (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS profiles (
    user_id        INTEGER PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
    height_cm      NUMERIC(5,1) CHECK (height_cm > 0),
    weight_kg      NUMERIC(5,1) CHECK (weight_kg > 0),
    birth_date     DATE,
    gender         VARCHAR(10),
    activity_level VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS bmi_records (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weight_kg   NUMERIC(5,1) NOT NULL,
    height_cm   NUMERIC(5,1) NOT NULL,
    bmi         NUMERIC(4,1) NOT NULL,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS calorie_entries (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    description VARCHAR(255),
    calories    INTEGER NOT NULL CHECK (calories >= 0),
    entry_date  DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE TABLE IF NOT EXISTS step_entries (
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    steps      INTEGER NOT NULL CHECK (steps >= 0),
    entry_date DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (user_id, entry_date)
);