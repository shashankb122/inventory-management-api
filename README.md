# Inventory Management System — REST API

A production-grade, secure RESTful API for inventory and warehouse stock management built with **Spring Boot 3.3**, **Java 21**, **PostgreSQL**, and **JWT Authentication**.

Designed for enterprise reliability, it features full catalog management across products, categories, and suppliers, an immutable stock movement audit engine (`RESTOCK`, `SALE`, `ADJUSTMENT`, `RETURN`), and automated low-stock alerting.

---

## Architecture & Tech Stack

| Layer | Technology |
|---|---|
| **Language & JDK** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.3 |
| **Security** | Spring Security 6 + Stateless JWT (jjwt 0.12.5) |
| **Database** | PostgreSQL 16 (H2 in-memory for testing) |
| **ORM / Data Access** | Spring Data JPA / Hibernate |
| **API Documentation** | SpringDoc OpenAPI 2.5.0 / Swagger UI |
| **Build & Testing** | Maven, JUnit 5, Mockito |
| **Containerization** | Docker, Docker Compose |

---

## Key Features

- **Stateless JWT Security**: Role-based access control (`ROLE_USER`, `ROLE_ADMIN`), BCrypt password hashing, and token authorization.
- **Stock Movement & Audit Engine**: Tracks every inventory mutation with exact before-and-after quantity snapshots and timestamped movement logs.
- **Overdraw Protection**: Prevents negative stock counts on sales with automatic rejection and validation.
- **Low-Stock Alerting**: Dynamic detection of items reaching or falling below custom threshold levels.
- **Pagination, Sorting & Search**: High-performance querying with case-insensitive name search and relational filtering by category or supplier.
- **Interactive Documentation**: Embedded Swagger UI at `/swagger-ui.html` with Bearer auth support.

---

## API Endpoints Reference

### Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register new user account | Public |
| `POST` | `/api/auth/login` | Login and receive signed JWT token | Public |

### Products (`/api/products`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/products` | List products (search, filter by category/supplier, paginate, sort) | Bearer JWT |
| `POST` | `/api/products` | Create a product with initial stock and thresholds | Bearer JWT |
| `GET` | `/api/products/{id}` | Get product details by ID | Bearer JWT |
| `PUT` | `/api/products/{id}` | Update product details | Bearer JWT |
| `DELETE`| `/api/products/{id}` | Delete a product | Bearer JWT |
| `GET` | `/api/products/low-stock`| Retrieve all products at or below low-stock threshold | Bearer JWT |

### Stock Movement & Audit (`/api/products/{id}/stock`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/products/{id}/stock`| Record inventory transaction (`RESTOCK`, `SALE`, `ADJUSTMENT`, `RETURN`) | Bearer JWT |
| `GET` | `/api/products/{id}/stock`| Get complete historical stock movement audit trail | Bearer JWT |

### Categories & Suppliers (`/api/categories`, `/api/suppliers`)
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `GET` | `/api/categories` | List all categories | Bearer JWT |
| `POST` | `/api/categories` | Create new category | Bearer JWT |
| `GET` | `/api/categories/{id}` | Get category by ID | Bearer JWT |
| `PUT` | `/api/categories/{id}` | Update category | Bearer JWT |
| `DELETE`| `/api/categories/{id}` | Delete category | Bearer JWT |
| `GET` | `/api/suppliers` | List all suppliers | Bearer JWT |
| `POST` | `/api/suppliers` | Create new supplier | Bearer JWT |
| `GET` | `/api/suppliers/{id}` | Get supplier by ID | Bearer JWT |
| `PUT` | `/api/suppliers/{id}` | Update supplier | Bearer JWT |
| `DELETE`| `/api/suppliers/{id}` | Delete supplier | Bearer JWT |

---

## Stock Movement State Machine

All inventory changes are logged through `POST /api/products/{id}/stock`:

```json
{
  "type": "RESTOCK",
  "quantity": 50,
  "note": "Shipment from vendor PO-4820"
}
```

- **`RESTOCK` / `RETURN`**: Adds quantity (`after = before + quantity`)
- **`SALE`**: Deducts quantity (`after = before - quantity`). Throws `400 Bad Request` if stock is insufficient.
- **`ADJUSTMENT`**: Sets current count directly to the specified quantity (`after = quantity`).

---

## Getting Started

### Option 1: Run with Docker Compose (Recommended)

```bash
docker-compose up --build
```

- API Base URL: `http://localhost:8080`
- Interactive Swagger UI: `http://localhost:8080/swagger-ui.html`

### Option 2: Run Locally with Maven

#### Prerequisites
- Java 21+
- Apache Maven 3.8+
- PostgreSQL 16 running on port 5432

#### Configure & Run
```bash
# Windows PowerShell
$env:DATABASE_URL="jdbc:postgresql://localhost:5432/inventory"
$env:DB_USER="postgres"
$env:DB_PASSWORD="your_postgres_password"
$env:JWT_SECRET="c2VjcmV0a2V5Zm9yaW52ZW50b3J5YXBpMTIzNDU2Nzg5MA=="

mvn spring-boot:run
```

### Run Unit & Integration Tests

```bash
mvn clean test
```

---

## Project Structure

```
src/
├── main/
│   ├── java/com/inventory/
│   │   ├── config/          # OpenAPI & Swagger documentation configuration
│   │   ├── controller/      # REST API controllers & validation DTOs
│   │   ├── exception/       # Centralized @RestControllerAdvice exception handler
│   │   ├── model/           # JPA entities (Product, Category, Supplier, StockMovement, User)
│   │   ├── repository/      # Spring Data JPA repositories & custom JPQL queries
│   │   ├── security/        # JWT utilities, OncePerRequestFilter, SecurityFilterChain
│   │   ├── service/         # Transactional business logic & stock state machine
│   │   └── InventoryApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/inventory/
        └── ProductServiceTest.java
```

---

## Author

**Shashank** — Backend Engineer
