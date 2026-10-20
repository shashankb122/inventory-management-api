# Inventory Management API

A production-grade REST API for inventory management built with **Spring Boot 3**, **PostgreSQL**, and **JWT authentication**. Features full CRUD across products, categories, and suppliers, plus stock movement tracking with low-stock alerting.

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Register a new user |
| `POST` | `/api/auth/login` | Login and receive JWT |
| `GET` | `/api/products` | List products (search, filter, paginate, sort) |
| `POST` | `/api/products` | Create a product |
| `GET` | `/api/products/{id}` | Get product by ID |
| `PUT` | `/api/products/{id}` | Update a product |
| `DELETE` | `/api/products/{id}` | Delete a product |
| `GET` | `/api/products/low-stock` | Get all low-stock products |
| `POST` | `/api/products/{id}/stock` | Record stock movement |
| `GET` | `/api/products/{id}/stock` | Get stock movement history |
| `GET/POST/PUT/DELETE` | `/api/categories` | Category management |
| `GET/POST/PUT/DELETE` | `/api/suppliers` | Supplier management |

Full interactive docs available at `/swagger-ui.html` when running.

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.3 |
| Language | Java 21 |
| Database | PostgreSQL 16 |
| Auth | Spring Security + JWT (jjwt) |
| Docs | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit 5, Mockito, H2 (in-memory) |
| Build | Maven |
| Container | Docker, Docker Compose |

## Getting Started

### Run with Docker (recommended)

```bash
git clone <your-repo-url>
cd inventory-api
docker-compose up --build
```

API available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

### Run locally

```bash
# Prerequisites: Java 21, Maven, PostgreSQL

# Start a PostgreSQL instance then:
export DATABASE_URL=jdbc:postgresql://localhost:5432/inventory
export DB_USER=postgres
export DB_PASSWORD=postgres
export JWT_SECRET=c2VjcmV0a2V5Zm9yaW52ZW50b3J5YXBpMTIzNDU2Nzg5MA==

mvn spring-boot:run
```

### Run tests

```bash
mvn test
```

## Authentication Flow

```
POST /api/auth/register   → create account
POST /api/auth/login      → receive JWT token
GET  /api/products        → Authorization: Bearer <token>
```

All endpoints except `/api/auth/**` require a valid JWT in the `Authorization` header.

## Stock Movements

The API tracks every inventory change through the `POST /api/products/{id}/stock` endpoint:

```json
{
  "type": "RESTOCK",
  "quantity": 50,
  "note": "Weekly supplier delivery"
}
```

Movement types: `RESTOCK` · `SALE` · `ADJUSTMENT` · `RETURN`

Each movement records `stockBefore` and `stockAfter` for a full audit trail.

## Project Structure

```
src/main/java/com/jamesbeech/inventory/
├── controller/       # REST controllers
├── service/          # Business logic
├── repository/       # Spring Data JPA repositories
├── model/            # JPA entities
├── security/         # JWT filter, UserDetailsService, SecurityConfig
├── exception/        # Global exception handler + custom exceptions
└── config/           # OpenAPI / Swagger config
```
