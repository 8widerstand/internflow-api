# InternFlow API

InternFlow is a Spring Boot REST API for managing internships with clean backend fundamentals: persistence, validation, filtering, pagination, sorting, and automated tests.

The project is built as a practical Java backend learning project, but with the kind of structure expected in a real API: controllers, services, repositories, DTOs, database migrations, and clear error responses.

## What It Shows

- REST API design with Spring Boot
- CRUD operations for internships
- Status workflow with `OPEN`, `IN_PROGRESS`, `COMPLETED`, and `CANCELLED`
- Request validation and consistent JSON errors
- PostgreSQL persistence with Liquibase migrations
- Filtering by status and company
- Pagination and allow-listed sorting
- Unit, controller, and integration tests

## Tech Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Liquibase
- H2 for integration tests
- JUnit 5, Mockito, MockMvc

## Quick Start

Start PostgreSQL:

```bash
docker compose up -d
```

Run the API:

```bash
./mvnw spring-boot:run
```

On Windows:

```cmd
mvnw.cmd spring-boot:run
```

The API starts on:

```text
http://localhost:8080
```

## Run Tests

```bash
./mvnw test
```

On Windows:

```cmd
mvnw.cmd test
```

## API Snapshot

| Method | Endpoint                   | Description                    |
|--------|----------------------------|--------------------------------|
| GET    | `/health`                  | Check API health               |
| GET    | `/internships`             | List internships               |
| GET    | `/internships/{id}`        | Get one internship             |
| POST   | `/internships`             | Create an internship           |
| PUT    | `/internships/{id}`        | Update an internship           |
| PATCH  | `/internships/{id}/status` | Update internship status       |
| DELETE | `/internships/{id}`        | Delete an internship           |

Example list query:

```http
GET /internships?status=OPEN&company=bm&page=0&size=10&sort=company,desc
```

Example create request:

```json
{
  "title": "Cloud Internship",
  "company": "AzureLab",
  "durationInMonths": 4
}
```

## Current Status

The internship module is functional and tested. The next planned step is to add JPA relationships with students, mentors, and tasks.
