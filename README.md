# InternFlow API

**A production-style REST API for managing internships, built from scratch with Spring Boot.**

Internships · Students · Mentors · Tasks — validation, pagination, relationships, and tests.

## Tech Stack

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=flat&logo=postgresql&logoColor=white)
![Liquibase](https://img.shields.io/badge/Liquibase-2962FF?style=flat&logo=liquibase&logoColor=white)
![Tests](https://img.shields.io/badge/tests-JUnit%205%20%2B%20MockMvc-25A162?style=flat)
![License](https://img.shields.io/badge/license-MIT-blue.svg)



---

## Why This Project

Most tutorial projects stop at "CRUD with Spring Boot." InternFlow goes further — it handles real backend concerns:
structured validation errors, paginated responses with allow-listed sorting, JPA relationship management, database
migrations with Liquibase, and integration tests running on H2. This is a learning project built to production
standards.

## Table of Contents

- [Domain Model](#domain-model)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Running Tests](#running-tests)
- [Roadmap](#roadmap)

## Domain Model

<div align="center">

```
                        ┌──────────┐       ┌──────────────┐       ┌──────────┐
                        │  Student │ 1───* │  Internship  │ *───1 │  Mentor  │
                        └──────────┘       └──────┬───────┘       └──────────┘
                         │ 1
                       |
                        * 
                        ┌──────────┐
                        │   Task   │
                        └──────────┘
                    
```

</div>
An internship can have one assigned student, one assigned mentor, and multiple tasks.

## Features

**Core API**

- Full CRUD on internships
- Create and retrieve students and mentors
- Task creation and completion tracking per internship
- Status workflow updates on internships

**Data integrity & querying**

- Pagination with allow-listed sort fields — no arbitrary column access
- Filtering by status and company
- Structured validation error responses (field-level feedback, not generic 400s)

**Persistence & ops**

- PostgreSQL with Liquibase-managed schema migrations
- JPA relationship mapping between internships, students, mentors, and tasks
- Dockerized database for local development

**Testing**

- Integration tests with H2 in-memory database
- MockMvc for controller-level testing
- JUnit 5 + Mockito

## API Endpoints

### Internships

| Resource    | Method | Endpoint                                          | Description                    |
|-------------|--------|---------------------------------------------------|--------------------------------|
| Internships | GET    | `/internships`                                    | List, paginated and filterable |
| Internships | GET    | `/internships/{id}`                               | Get by ID                      |
| Internships | POST   | `/internships`                                    | Create                         |
| Internships | PUT    | `/internships/{id}`                               | Update                         |
| Internships | PATCH  | `/internships/{id}/status`                        | Update status                  |
| Internships | PATCH  | `/internships/{internshipId}/student/{studentId}` | Assign student                 |
| Internships | PATCH  | `/internships/{internshipId}/mentor/{mentorId}`   | Assign mentor                  |
| Internships | DELETE | `/internships/{id}`                               | Delete                         |

### Students

| Resource | Method | Endpoint         | Description |
|----------|--------|------------------|-------------|
| Students | GET    | `/students`      | List all    |
| Students | GET    | `/students/{id}` | Get by ID   |
| Students | POST   | `/students`      | Create      |

### Mentors

| Resource | Method | Endpoint        | Description |
|----------|--------|-----------------|-------------|
| Mentors  | GET    | `/mentors`      | List all    |
| Mentors  | GET    | `/mentors/{id}` | Get by ID   |
| Mentors  | POST   | `/mentors`      | Create      |

### Tasks

| Resource | Method | Endpoint                            | Description               |
|----------|--------|-------------------------------------|---------------------------|
| Tasks    | GET    | `/internships/{internshipId}/tasks` | List tasks for internship |
| Tasks    | POST   | `/internships/{internshipId}/tasks` | Create task               |
| Tasks    | PATCH  | `/tasks/{taskId}/completed`         | Mark task completed       |

### Example query

```http
GET /internships?status=OPEN&company=bm&page=0&size=10&sort=company,desc
```

## Getting Started

**Prerequisites:** Docker, JDK 21, Maven.

**1. Start the database**

```bash
docker compose up -d
```

**2. Run the API**

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

> On Windows, use `mvnw.cmd spring-boot:run` instead.

## Running Tests

```bash
./mvnw test
```

Tests run against an embedded H2 database — no external dependencies needed.

> On Windows: `mvnw.cmd test`. Make sure `JAVA_HOME` points to a JDK 21 installation.

## Roadmap

- [ ] Authentication & role-based access control
- [ ] Stable paginated response DTO
- [ ] API documentation (OpenAPI / Swagger)
