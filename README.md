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
- Spring Boot 3.3.5
- Internet connection (for GitHub API access)

### 2️⃣ Clone the Repository
```bash
git clone git@github.com:sanepr/githubscore.git
cd github-score
```

### 3️⃣ Build and Run
```bash
./mvnw clean install
./mvnw spring-boot:run
```

The app runs at:  
👉 `http://localhost:8080/swagger-ui/index.html`

---

## 🌐 API Usage

### **Endpoint**
`GET /api/repositories`

### **Parameters**

### **Example Request**
```bash
GET http://127.0.0.1:8080/api/repositories?query=validation&language=c++&earliestCreatedDate=2022-01-01&page=1
```

### **Example Response**
```json
[
  {
    "name": "unbound",
    "owner": "NLnetLabs",
    "stars": 3945,
    "forks": 410,
    "lastUpdated": "2025-11-01",
    "popularityScore": 2115.5
  }
]
```

---
## ⚙️ Dockerization

### **Build Docker Image**
```bash
docker build -t githubscore:latest .
```
### **Run Docker Container**
```bash
docker run -p 8080:8080 githubscore:latest
docker run -d -p 8080:8080  --name githubscore-app githubscore:latest
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

| Component | Technology                     |
|------------|--------------------------------|
| Language | Java 21                        |
| Framework | Spring Boot 3.x                |
| Build Tool | Maven                          |
| HTTP Client | WebClient / RestTemplate       |
| Testing | JUnit 5, Mockito               |
| API Docs | Swagger / Springdoc OpenAPI |

---

## 🏗️ Future Enhancements
- Implement configurable scoring weights
- Handle GitHub API rate limits gracefully
- Add database migration support
- Add persistence layer for database storage
- Extend dockerization support database integration
- Implement caching for frequent queries

---

## 👤 Author
**Ashish Ranjan**  
Backend Developer
