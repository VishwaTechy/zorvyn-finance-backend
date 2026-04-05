# Zorvyn Finance Backend

A **Finance Data Processing and Access Control Backend** built with Java Spring Boot for the Zorvyn FinTech Backend Developer Intern assignment. The system supports financial record management, role-based access control, JWT authentication, dashboard analytics, and a fully responsive frontend dashboard.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | H2 (file-based, persists across restarts) |
| ORM | Spring Data JPA / Hibernate 6 |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Frontend | HTML + CSS + Vanilla JS + Chart.js |

---

## Project Structure

```
src/
└── main/
    ├── java/com/zorvyn/finance/
    │   ├── config/
    │   │   ├── SecurityConfig.java       → JWT filter chain, role-based route protection
    │   │   └── DataInitializer.java      → Seeds default users on startup
    │   ├── controller/
    │   │   ├── AuthController.java       → Login and Register endpoints
    │   │   ├── RecordController.java     → Financial record CRUD + filtering
    │   │   ├── DashboardController.java  → Summary, trend, and insights APIs
    │   │   └── AdminController.java      → User management endpoints
    │   ├── dto/
    │   │   ├── AuthRequest.java          → Login request body
    │   │   ├── AuthResponse.java         → Login response with JWT token
    │   │   ├── UserRequest.java          → Register request body
    │   │   └── RecordRequest.java        → Create/update record body
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java → Unified error responses
    │   ├── model/
    │   │   ├── User.java                 → User entity with role and status
    │   │   ├── FinancialRecord.java      → Financial record entity
    │   │   └── Role.java                 → ADMIN, ANALYST, VIEWER enum
    │   ├── repository/
    │   │   ├── UserRepository.java       → User DB queries
    │   │   └── FinancialRecordRepository.java → Record queries with filters
    │   ├── security/
    │   │   ├── JwtUtil.java              → Token generation and validation
    │   │   └── JwtFilter.java            → Per-request token verification
    │   └── service/
    │       ├── AuthService.java          → Login and registration logic
    │       ├── RecordService.java        → Record CRUD business logic
    │       └── DashboardService.java     → Analytics and aggregation logic
    └── resources/
        ├── application.properties        → Database and JWT configuration
        └── static/
            └── index.html               → Responsive frontend dashboard
```

---

## Getting Started

### Prerequisites
- Java 17 or higher
- No other installation required — H2 database is embedded and auto-configures

### Run the application

```bash
./mvnw spring-boot:run
```

The server starts at `http://localhost:8080`

### Open the dashboard

```
http://localhost:8080
```

### Default users (auto-created on first startup)

| Username | Password | Role |
|---|---|---|
| admin | admin123 | ADMIN |
| analyst | analyst123 | ANALYST |
| viewer | viewer123 | VIEWER |

---

## API Reference

### Authentication

**Login**
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN"
}
```

Use the token in all subsequent requests:
```
Authorization: Bearer <token>
```

**Register new user**
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "newuser",
  "password": "pass123",
  "email": "user@example.com",
  "role": "VIEWER"
}
```

---

### Financial Records

| Method | Endpoint | Role Required | Description |
|---|---|---|---|
| GET | /api/records | All roles | List all records |
| POST | /api/records | ADMIN, ANALYST | Create a record |
| PUT | /api/records/{id} | ADMIN, ANALYST | Update a record |
| DELETE | /api/records/{id} | ADMIN only | Soft delete a record |

**Filter records**
```
GET /api/records?type=INCOME
GET /api/records?type=EXPENSE
GET /api/records?category=Salary
GET /api/records?from=2026-01-01&to=2026-03-31
```

**Record body**
```json
{
  "amount": 5000.00,
  "type": "INCOME",
  "category": "Salary",
  "date": "2026-04-01",
  "notes": "Monthly salary"
}
```

---

### Dashboard

| Method | Endpoint | Role Required | Description |
|---|---|---|---|
| GET | /api/dashboard/summary | ADMIN, ANALYST | Total income, expenses, net balance, category totals, recent activity |
| GET | /api/dashboard/trend | ADMIN, ANALYST | Daily cumulative balance trend data |
| GET | /api/dashboard/insights | ADMIN, ANALYST | Spending by category, cash flow, savings rate, top expense |

---

### Admin

| Method | Endpoint | Role Required | Description |
|---|---|---|---|
| GET | /api/admin/users | ADMIN only | List all users |
| PATCH | /api/admin/users/{id}/status | ADMIN only | Toggle user active/inactive |

---

## Role-Based Access Control

| Action | VIEWER | ANALYST | ADMIN |
|---|---|---|---|
| Login / Register | ✅ | ✅ | ✅ |
| View records | ✅ | ✅ | ✅ |
| Filter records | ✅ | ✅ | ✅ |
| Create records | ❌ | ✅ | ✅ |
| Update records | ❌ | ✅ | ✅ |
| Delete records | ❌ | ❌ | ✅ |
| View dashboard analytics | ❌ | ✅ | ✅ |
| View key insights | ❌ | ✅ | ✅ |
| Export CSV / PDF | ✅ | ✅ | ✅ |
| Manage users | ❌ | ❌ | ✅ |
| Activate / Deactivate users | ❌ | ❌ | ✅ |

Access control is enforced at two levels:
- **Route level** via Spring Security `requestMatchers`
- **Method level** via `@PreAuthorize` annotations on controller methods

---

## Key Features

- **JWT Authentication** — Stateless token-based auth, tokens expire in 24 hours
- **Role-Based Access Control** — Enforced at both route and method level
- **Soft Delete** — Records are marked deleted, not permanently removed, preserving the financial audit trail
- **Dashboard Analytics** — Total income, expenses, net balance, category-wise totals, and recent activity
- **Balance Trend Chart** — Daily cumulative net balance rendered as a line chart
- **Spending by Category** — Donut chart showing expense distribution per category
- **Key Insights** — Auto-generated smart cards: cash flow status, top expense category, largest transaction, savings rate
- **Export Records** — Download records as CSV (Excel-compatible) or branded PDF with totals summary
- **Input Validation** — All inputs validated with user-friendly error messages
- **Global Error Handling** — Consistent error response format across all endpoints
- **Auto Data Seeding** — Default users created automatically on first startup
- **Responsive Frontend** — Works on desktop, tablet, and mobile with hamburger menu
- **Dark / Light Mode** — Theme toggle with preference saved in localStorage
- **H2 Database Console** — Browse live database at `/h2-console`

---

## Database Console

Access the H2 browser console while the app is running:

```
URL:       http://localhost:8080/h2-console
JDBC URL:  jdbc:h2:file:./finance-db
Username:  sa
Password:  (leave empty)
```

---

## Assumptions Made

1. Financial record types are restricted to `INCOME` or `EXPENSE` only
2. Soft delete is used for records — deleted entries are hidden from all APIs but preserved in the database for audit purposes
3. Role assignment is done at registration time; Admins can deactivate users but cannot change roles
4. JWT tokens expire after 24 hours (86,400,000 ms)
5. H2 file-based database (`finance-db`) is used — data persists across application restarts
6. VIEWER role can read financial records but cannot access dashboard analytics, insights, or export
7. The frontend is served as a static file by Spring Boot for simplicity

## Tradeoffs Considered

- **H2 over PostgreSQL** — Zero setup required for assessment; easily swappable by changing `application.properties` and adding the PostgreSQL Maven dependency
- **Stateless JWT over sessions** — Better suited for API-based clients and future microservice scaling
- **Soft delete over hard delete** — Preserves audit trail which is critical in fintech systems
- **Single-file frontend** — Served directly by Spring Boot; can be separated into a standalone React/Vue app without any backend changes
- **In-memory H2 avoided** — File-based H2 chosen so data persists between restarts during evaluation

---

## Development Notes

- Built and tested using **VS Code** with the Java Extension Pack
- All development done locally — no cloud deployment required
- The server auto-restarts cleanly with `./mvnw spring-boot:run`
