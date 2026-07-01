# Auth Service — JWT Authentication Microservice

## Table of Contents
- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Docker Deployment](#docker-deployment)
- [Troubleshooting](#troubleshooting)

---

## Overview

Auth Service is a standalone authentication microservice built with Spring Boot 3.x. It provides JWT-based authentication with refresh tokens, role-based access control (RBAC), and production-ready security configurations.

**Core Capabilities:**
- User registration and login
- JWT access tokens (24h) + refresh tokens (7d)
- BCrypt password hashing (strength 12)
- Role-based authorization (USER, ADMIN, MANAGER)
- PostgreSQL with Flyway migrations
- CORS configuration for frontend integration
- Spring Security integration
- Global exception handling with validation

---

## Architecture

### High-Level Flow

```
┌─────────────┐      ┌──────────────┐      ┌──────────────┐
│   Client    │─────▶│  Auth API    │─────▶│  PostgreSQL  │
│ (Frontend)  │◀─────│  Controller  │◀─────│  Database    │
└─────────────┘      └──────────────┘      └──────────────┘
                         │
                         ▼
                   ┌──────────┐
                   │   JWT   │
                   │  Utils   │
                   └──────────┘
```

### Authentication Flow

#### 1. Registration
```
Client                Auth Service               PostgreSQL
  │                        │                        │
  │──POST /api/auth/register──────────────────────▶│
  │                        │──Validate input────────│
  │                        │──BCrypt password──────│
  │                        │──Save user────────────▶│
  │                        │                        │
  │◀─201 Created───────────────────────────────────│
  │   {accessToken, refreshToken, user}             │
```

#### 2. Login
```
Client                Auth Service               PostgreSQL
  │                        │                        │
  │──POST /api/auth/login (credentials)──────────▶│
  │                        │──Authenticate─────────│
  │                        │──Generate JWT─────────│
  │                        │                        │
  │◀─200 OK───────────────────────────────────────│
  │   {accessToken, refreshToken, user}             │
```

#### 3. Access Protected Resource
```
Client                Auth Service               Other Service
  │                        │                        │
  │──GET /api/users/me    │                        │
  │   (Authorization: Bearer {token})              │
  │───────────────────────▶│                        │
  │                        │──Validate JWT─────────│
  │                        │──Extract authorities──│
  │                        │──Pass to controller───│
  │                        │───────────────────────▶│
  │◀─200 OK────────────────│◀──────────────────────│
```

---

## Features

| Feature | Implementation |
|---------|-----------------|
| **Authentication** | JWT + BCrypt (strength 12) |
| **Authorization** | Role-based (USER, ADMIN, MANAGER) |
| **Token Expiry** | Access: 24h, Refresh: 7d |
| **Password Hashing** | BCrypt with cost factor 12 |
| **Database** | PostgreSQL with Flyway migrations |
| **Validation** | Jakarta Validation (Bean Validation) |
| **Exception Handling** | Global exception handler with RFC 7807 errors |
| **CORS** | Configurable allowed origins |
| **Password Reset** | (Coming soon) |
| **Email Verification** | (Coming soon) |

---

## Technology Stack

| Component | Technology |
|-----------|-----------|
| **Runtime** | Java 21+ |
| **Framework** | Spring Boot 3.4.x |
| **Security** | Spring Security 6.x |
| **Database** | PostgreSQL 15+ |
| **ORM** | Spring Data JPA (Hibernate 6) |
| **Migrations** | Flyway |
| **JWT** | jjwt (Java JWT) |
| **Validation** | Jakarta Validation |
| **Build Tool** | Maven |
| **Container** | Docker |

---

## Quick Start

### Prerequisites

- Java 21+
- PostgreSQL 15+ (or Docker)
- Maven 3.8+
- Docker (optional)

### 1. Clone & Build

```bash
git clone <repo-url>
cd services/auth-service

mvn clean install -DskipTests
```

### 2. Database Setup

**Option A: Docker (Recommended)**
```bash
docker run -d \
  --name auth-db \
  -e POSTGRES_DB=auth_db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  postgres:15-alpine

docker exec -i auth-db psql -U postgres -d auth_db -f /dev/stdin \
  < src/main/resources/db/migration/V1__create_auth_tables.sql
```

**Option B: Local PostgreSQL**
```sql
CREATE DATABASE auth_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE auth_db TO postgres;

-- Then run migration:
psql -U postgres -d auth_db -f src/main/resources/db/migration/V1__create_auth_tables.sql
```

### 3. Configuration

Edit `src/main/resources/application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET:your-secret-key-min-256-bits}
    access-token-expiration: 86400000      # 24 hours
    refresh-token-expiration: 604800000    # 7 days
```

### 4. Run

```bash
mvn spring-boot:run
```

Service starts at `http://localhost:8081/api`.

---

## API Documentation

### Base URL
```
http://localhost:8081/api
```

### Authentication Headers
```
Authorization: Bearer {access_token}
```

### Endpoints

#### 1. Register New User

**Endpoint:** `POST /api/auth/register`  
**Access:** Public  
**Rate Limit:** 5 requests/minute

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "roles": ["USER"]
}
```

**Validation Rules:**
- `fullName`: 2-100 characters, required
- `email`: Valid email format, required, unique
- `password`: 6-100 characters, required
- `roles`: Optional, defaults to `["USER"]`

**cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "SecurePass123",
    "roles": ["USER"]
  }'
```

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "email": "john@example.com",
  "fullName": "John Doe",
  "roles": ["USER"]
}
```

---

#### 2. Login

**Endpoint:** `POST /api/auth/login`  
**Access:** Public  
**Rate Limit:** 10 requests/minute

**Request:**
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**cURL:**
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "SecurePass123"
  }'
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "email": "john@example.com",
  "fullName": "John Doe",
  "roles": ["USER"]
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "Invalid credentials",
  "details": null,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

#### 3. Get Current User Profile

**Endpoint:** `GET /api/users/me`  
**Access:** Authenticated  
**Required Role:** Any authenticated user

**Headers:**
```
Authorization: Bearer {access_token}
```

**cURL:**
```bash
curl -X GET http://localhost:8081/api/users/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "john@example.com",
  "fullName": "John Doe",
  "roles": ["USER"],
  "createdAt": "2025-01-15T10:00:00Z"
}
```

---

#### 4. Admin Endpoint (Example)

**Endpoint:** `GET /api/admin/dashboard`  
**Access:** Authenticated  
**Required Role:** `ADMIN` only

**Headers:**
```
Authorization: Bearer {access_token}
```

**cURL:**
```bash
curl -X GET http://localhost:8081/api/admin/dashboard \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

#### 5. Refresh Token

**Endpoint:** `POST /api/auth/refresh`  
**Access:** Public  
**Description:** Get new access token using refresh token

**Request (Body):**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "email": "john@example.com",
  "fullName": "John Doe",
  "roles": ["USER"]
}
```

---

### Error Responses

**400 Bad Request — Validation Failed:**
```json
{
  "status": 400,
  "message": "Validation failed",
  "details": [
    "email: Email should be valid",
    "password: Password must be between 6 and 100 characters"
  ],
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**404 Not Found — Resource Not Found:**
```json
{
  "status": 404,
  "message": "User not found",
  "details": null,
  "timestamp": "2025-01-15T10:30:00Z"
}
```

**403 Forbidden — Insufficient Permissions:**
```json
{
  "status": 403,
  "message": "Access Denied",
  "details": "Requires role: ADMIN",
  "timestamp": "2025-01-15T10:30:00Z"
}
```

---

## Database Schema

### ER Diagram

```
┌─────────────┐       ┌──────────────┐
│    users    │───┐   │  user_roles  │
├─────────────┤   │   ├──────────────┤
│ id UUID (PK)│◀──┼──▶│ user_id (FK) │
│ email       │   │   │ role VARCHAR │
│ password    │   │   └──────────────┘
│ full_name   │   │
│ enabled     │   │   ┌──────────────────┐
│ created_at  │   │   │  refresh_tokens  │
│ updated_at  │   │   ├──────────────────┤
└─────────────┘   └─▶│ user_id (FK)     │
                    │ token VARCHAR     │
                    │ expires_at        │
                    │ revoked BOOLEAN   │
                    └──────────────────┘

┌────────────────────┐
│  failed_login_...  │
├────────────────────┤
│ user_id (FK)       │
│ email VARCHAR      │
│ attempt_at         │
│ ip_address INET    │
└────────────────────┘
```

### Tables

#### `users` (Main authentication table)
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID | Primary key |
| `email` | VARCHAR(255) | Unique, not null |
| `password` | VARCHAR(255) | BCrypt hashed |
| `full_name` | VARCHAR(255) | User's full name |
| `enabled` | BOOLEAN | Account active status |
| `account_non_expired` | BOOLEAN | Account expiry check |
| `account_non_locked` | BOOLEAN | Account lockout check |
| `credentials_non_expired` | BOOLEAN | Password expiry check |
| `created_at` | TIMESTAMP | Account creation time |
| `updated_at` | TIMESTAMP | Last update time |

#### `user_roles` (Many-to-many: User -> Roles)
| Column | Type | Description |
|--------|------|-------------|
| `user_id` | UUID | Foreign key to users |
| `role` | VARCHAR(30) | USER, ADMIN, MANAGER |
| `created_at` | TIMESTAMP | Role assignment time |

**Primary Key:** `(user_id, role)`

#### `refresh_tokens`
| Column | Type | Description |
|--------|------|-------------|
| `id` | UUID | Primary key |
| `user_id` | UUID | Foreign key to users |
| `token` | VARCHAR(500) | Unique refresh token |
| `expires_at` | TIMESTAMP | Token expiry |
| `created_at` | TIMESTAMP | Token creation time |
| `revoked` | BOOLEAN | Revocation flag |

#### `failed_login_attempts`
| Column | Type | Description |
|--------|------|-------------|
| `id` | SERIAL | Primary key |
| `user_id` | UUID | Foreign key to users |
| `email` | VARCHAR(255) | Email attempted |
| `attempt_at` | TIMESTAMP | Attempt time |
| `ip_address` | INET | Client IP |
| `user_agent` | TEXT | Client user agent |

---

## Security

### JWT Token Structure

**Access Token (HS256):**
```json
{
  "sub": "john@example.com",
  "roles": ["USER"],
  "iat": 1705338600,
  "exp": 1705425000,
  "type": "access"
}
```

**Refresh Token:**
```json
{
  "sub": "john@example.com",
  "roles": ["USER"],
  "iat": 1705338600,
  "exp": 1705943400,
  "type": "refresh"
}
```

### Password Hashing

- Algorithm: BCrypt
- Strength: 12 (2^12 iterations)
- Example: `$2a$12$LQv3Y8tX9qH5Wc7eF6J1dO4i8Y3S9hR2bL0Z8X7Y5uQ6R9z1X3yA`

### CORS Policy

Allowed Origins (configurable):
- `http://localhost:3000` (Development)
- `https://myapp.com` (Production)
- `https://admin.myapp.com` (Admin)

Allowed Methods: `GET, POST, PUT, DELETE, PATCH, OPTIONS`

Allowed Headers: `*`

Exposed Headers: `Authorization, X-Total-Count, X-Page-Number`

---

## Configuration

### Application Properties

**`application.yml` structure:**
```yaml
server:
  port: 8081
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/auth_db
    username: postgres
    password: postgres
  
  jpa:
    hibernate:
      ddl-auto: validate  # Use 'update' for dev, 'validate' for prod
  
  flyway:
    enabled: true
    locations: classpath:db/migration

app:
  jwt:
    secret: ${JWT_SECRET}
    access-token-expiration: 86400000      # 24h
    refresh-token-expiration: 604800000    # 7d
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | JWT signing secret (min 256 bits) | `default-secret-CHANGE-ME` |
| `DB_URL` | PostgreSQL connection URL | `jdbc:postgresql://localhost:5432/auth_db` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASS` | Database password | `postgres` |

---

## Running Tests

### Unit Tests
```bash
mvn test -Dtest=AuthServiceTest
```

### Integration Tests
```bash
mvn verify
```

### With Coverage
```bash
mvn clean test jacoco:report
# Open target/site/jacoco/index.html
```

---

## Docker Deployment

### Dockerfile

```dockerfile
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/auth-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Build & Run

```bash
# Build image
docker build -t auth-service:latest .

# Run container
docker run -d \
  -p 8081:8081 \
  -e JWT_SECRET=your-secret-key-min-256-bits \
  -e DB_URL=jdbc:postgresql://host.docker.internal:5432/auth_db \
  -e DB_USER=postgres \
  -e DB_PASS=postgres \
  auth-service:latest
```

### Docker Compose

```yaml
version: '3.8'

services:
  auth-db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: auth_db
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./db/migration:/docker-entrypoint-initdb.d

  auth-service:
    build: .
    ports:
      - "8081:8081"
    environment:
      JWT_SECRET: your-secret-key-min-256-bits
      DB_URL: jdbc:postgresql://auth-db:5432/auth_db
      DB_USER: postgres
      DB_PASS: postgres
    depends_on:
      - auth-db
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  postgres_data:
```

---

## Troubleshooting

### Common Issues

**1. `BeanCurrentlyInCreationException` — Circular dependency**
```
Error: Requested bean is currently in creation: Is there an unresolvable circular reference?
```
Fix: Ensure `AuthService` constructor injection uses interfaces properly. Check for circular dependencies in service layer.

**2. `JWT signature does not match`**
```bash
# Ensure JWT_SECRET is consistent across restarts
export JWT_SECRET="mySecretKey12345678901234567890123456789012345678901234567890"
mvn spring-boot:run
```

**3. `CORS policy: No 'Access-Control-Allow-Origin'`**
```
Fix: Add frontend URL to SecurityConfig.corsConfigurationSource().setAllowedOrigins()
```

**4. `Password encoding does not match`**
```
Fix: Ensure PasswordEncoder bean is used consistently for passwords.
BCrypt strength 12 is configured in SecurityConfig.
```

**5. `Token expired` after server restart**
```
Fix: Verify system clock. JWT uses system time for expiry checks.
In Docker, ensure container timezone matches host.
```

---

## Production Checklist

- [ ] Change `JWT_SECRET` to strong random key (min 256 bits)
- [ ] Set `spring.jpa.hibernate.ddl-auto=validate` (never `update`)
- [ ] Enable HTTPS (TLS termination at load balancer)
- [ ] Configure Redis for refresh token storage (scale horizontally)
- [ ] Set up monitoring (Prometheus + Grafana)
- [ ] Configure log aggregation (ELK, Datadog)
- [ ] Enable Spring Security audit logging
- [ ] Set up rate limiting (Bucket4j or Redis)
- [ ] Configure database connection pool (HikariCP)
- [ ] Add API gateway (Kong, AWS API Gateway)
- [ ] Implement refresh token rotation and revocation
- [ ] Add account lockout after N failed attempts

---

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

---

## License

[MIT License](LICENSE)

---

## Support

For issues and questions:
- Documentation: [wiki]
- Issues: [GitHub Issues]
- Email: support@myapp.com