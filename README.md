# 🧮 GitHub Repository Popularity Scoring Service

## 📘 Project Overview
**GitHub Score** is a Spring Boot backend application.  
The application integrates with the **GitHub Search API** to fetch public repositories based on user-defined filters (language and creation date) and computes a **popularity score** for each repository.

This score reflects the repository’s popularity using:
- ⭐ Stars
- 🍴 Forks
- 🕒 Recency of last update

---

## 🚀 Features
- Fetch repositories from **GitHub REST API**
- Filter by **language** and **earliest created date**
- Compute and return a **popularity score**
- Sort results by popularity
- Provide paginated JSON responses
- Built using **Clean Architecture** and **SOLID** principles

---

## 🧩 Architecture Overview

This project is structured using **Clean Architecture** to ensure separation of concerns, testability, and scalability.

### 🗂️ **Package Structure**

```
com.rpassignment.githubscore
├── api/               # REST API layer (controllers, DTOs, exception handling)
├── application/       # Business use cases (services, orchestrators)
├── domain/            # Core business logic and scoring algorithm
├── infrastructure/    # External integrations (GitHub API, configuration)
└── GithubScoreApplication.java
```

---

## 🧠 Clean Architecture Flow

Below is how data flows through the system when a user calls the API:

### 🧩 Layer Responsibilities

| Layer | Description |
|--------|-------------|
| **API Layer (`api`)** | Handles REST requests/responses. Converts user input to application DTOs. |
| **Application Layer (`application`)** | Contains business use cases. Coordinates between domain logic and infrastructure. |
| **Domain Layer (`domain`)** | Holds core business rules — entities and scoring logic. No external dependencies. |
| **Infrastructure Layer (`infrastructure`)** | Handles API calls to GitHub and other external systems. Implements interfaces defined in the domain/application layer. |

---

## ⚙️ Getting Started

### 1️⃣ Prerequisites
- Java 21+
- Spring Boot 4.0.0
- Maven 3.8+
- Internet connection (for GitHub API access)

### 2️⃣ Clone the Repository
```bash
git clone https://github.com/your-username/github-score.git
cd github-score
```

### 3️⃣ Build and Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

The app runs at:  
👉 `http://localhost:8080`

---

## 🌐 API Usage

### **Endpoint**
`GET /api/repositories`

### **Parameters**
| Name | Type | Required | Description |
|------|------|-----------|-------------|
| `language` | String | ✅ | Filter repositories by language |
| `earliestCreatedDate` | String (ISO date) | ✅ | Include repos created after this date |
| `page` | Integer | ❌ | Page number |
| `size` | Integer | ❌ | Page size |

### **Example Request**
```bash
GET http://localhost:8080/api/repositories?language=java&earliestCreatedDate=2024-01-01
```

### **Example Response**
```json
[
  {
    "name": "spring-framework",
    "owner": "spring-projects",
    "stars": 64000,
    "forks": 38000,
    "lastUpdated": "2025-10-28T10:00:00Z",
    "popularityScore": 84500.2
  }
]
```

---

## 🧪 Testing

Run all unit and integration tests:
```bash
./mvnw test
```

Includes:
- Unit tests for domain logic (scoring algorithm)
- Integration tests for GitHub API client
- Controller tests for API endpoints

---

## 🧰 Tech Stack

| Component | Technology |
|------------|-------------|
| Language | Java 17 |
| Framework | Spring Boot 3.x |
| Build Tool | Maven |
| HTTP Client | WebClient / RestTemplate |
| Testing | JUnit 5, Mockito |
| API Docs | Swagger / Springdoc OpenAPI (optional) |

---

## 🏗️ Future Enhancements
- Add caching for GitHub responses
- Implement configurable scoring weights
- Handle GitHub API rate limits gracefully
- Add Swagger UI documentation
- Dockerize the application for deployment

---

## 👤 Author
**[Your Name]**  
Backend Developer | Clean Architecture Enthusiast  
