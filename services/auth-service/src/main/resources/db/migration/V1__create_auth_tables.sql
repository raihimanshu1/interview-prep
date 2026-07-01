-- ============================================
-- Auth Service Database Schema
-- ============================================

-- Drop existing objects (idempotent for migrations)
-- Drop exists in correct order due to foreign key constraints
DO $$ 
BEGIN
    -- Drop triggers first
    DROP TRIGGER IF EXISTS update_user_updated_at ON users CASCADE;
    DROP TRIGGER IF EXISTS update_user_modtime ON users CASCADE;
    
    -- Drop function
    DROP FUNCTION IF EXISTS update_modtime() CASCADE;
    
    -- Drop tables (reverse order of creation)
    DROP TABLE IF EXISTS user_roles CASCADE;
    DROP TABLE IF EXISTS users CASCADE;
    DROP TABLE IF EXISTS refresh_tokens CASCADE;
    DROP TABLE IF EXISTS failed_login_attempts CASCADE;
END $$;

-- ============================================
-- Function to auto-update updated_at timestamp
-- ============================================

CREATE OR REPLACE FUNCTION update_modtime()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- Users Table (Main authentication table)
-- ============================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    
    full_name VARCHAR(255) NOT NULL,
    
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    CONSTRAINT valid_email CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT password_length CHECK (LENGTH(password) >= 6),
    CONSTRAINT name_length CHECK (LENGTH(full_name) >= 2 AND LENGTH(full_name) <= 100)
);

-- Unique index on email for faster lookups
CREATE UNIQUE INDEX idx_users_email ON users(email);

-- ============================================
-- User Roles Table (Many-to-Many: User -> Roles)
-- ============================================

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role VARCHAR(30) NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    PRIMARY KEY (user_id, role),
    
    CONSTRAINT valid_role CHECK (role IN ('USER', 'ADMIN', 'MANAGER'))
);

-- Index for fast role lookups
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role ON user_roles(role);

-- ============================================
-- Refresh Tokens Table
-- ============================================

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    
    token VARCHAR(500) UNIQUE NOT NULL,
    
    expires_at TIMESTAMP NOT NULL,
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    revoked BOOLEAN NOT NULL DEFAULT FALSE
);

-- Index for finding active refresh tokens
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens(expires_at);
CREATE INDEX idx_refresh_tokens_revoked ON refresh_tokens(revoked) WHERE revoked = FALSE;

-- ============================================
-- Failed Login Attempts Table (Account lockout)
-- ============================================

CREATE TABLE failed_login_attempts (
    id SERIAL PRIMARY KEY,
    
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    email VARCHAR(255) NOT NULL,
    
    attempt_at TIMESTAMP NOT NULL DEFAULT NOW(),
    
    ip_address INET,
    user_agent TEXT
);

-- Composite index for finding failed attempts within a time window
CREATE INDEX idx_failed_login_user_time ON failed_login_attempts(user_id, attempt_at);
CREATE INDEX idx_failed_login_email_time ON failed_login_attempts(email, attempt_at) WHERE user_id IS NULL;

-- ============================================
-- Triggers
-- ============================================

CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_modtime();

-- ============================================
-- Views for detailed user info
-- ============================================

CREATE OR REPLACE VIEW v_user_summary AS
SELECT 
    u.id,
    u.email,
    u.full_name,
    u.enabled,
    ARRAY_AGG(ur.role) AS roles,
    u.created_at,
    u.updated_at
FROM users u
LEFT JOIN user_roles ur ON u.id = ur.user_id
GROUP BY u.id, u.email, u.full_name, u.enabled, u.created_at, u.updated_at;

-- ============================================
-- Indexes for v_user_summary
-- ============================================

CREATE INDEX idx_users_status ON users(enabled) WHERE enabled = TRUE;

-- ============================================
-- Data: Seed initial admin user (optional)
-- Password: Admin@123 (BCrypt encoded)
-- ============================================

-- Uncomment to auto-seed admin user during initial migration
-- INSERT INTO users (email, password, full_name, enabled, roles)
-- VALUES (
--     'admin@myapp.com',
--     '$2a$12$LQv3Y8tX9qH5Wc7eF6J1dO4i8Y3S9hR2bL0Z8X7Y5uQ6R9z1X3yA',
--     'System Administrator',
--     TRUE,
--     ARRAY['ADMIN', 'USER']::user_role_enum
-- );

COMMENT ON TABLE users IS 'Main authentication users table';
COMMENT ON TABLE user_roles IS 'User roles (many-to-many)';
COMMENT ON TABLE refresh_tokens IS 'Refresh token storage for JWT session management';
COMMENT ON TABLE failed_login_attempts IS 'Audit trail for failed logins (for account lockout policy)';