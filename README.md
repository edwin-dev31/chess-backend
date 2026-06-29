<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 3.5.5">
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/WebSocket-010101?style=for-the-badge&logo=socketdotio&logoColor=white" alt="WebSocket">
  <img src="https://img.shields.io/badge/MIT-License-green?style=for-the-badge" alt="MIT License">
</p>

# ♟ Real-Time Chess Backend

A production-grade backend for a real-time chess application. Built with **Spring Boot 3**, it provides a secure REST API and WebSocket-based communication for live multiplayer chess games.

## ✨ Features

| Feature | Description |
|---------|-------------|
| **Authentication** | JWT-based registration/login + Google OAuth2 |
| **Live Presence** | See connected players in real time |
| **Invitations** | Challenge other players to a game |
| **Real-Time Moves** | Bidirectional WebSocket (STOMP) communication with FEN updates |
| **Server Validation** | All moves are validated server-side by chess rules |
| **Game Persistence** | Full game state and move history in PostgreSQL |
| **TLS Security** | End-to-end encryption via HTTPS / WSS |

## 🛠 Tech Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 17** | Language |
| **Spring Boot 3.5.5** | Core framework |
| **Spring Security** | JWT + OAuth2 authentication |
| **Spring Data JPA / Hibernate** | ORM & persistence |
| **Spring WebSocket (STOMP)** | Real-time bidirectional communication |
| **PostgreSQL** | Relational database |
| **Flyway** | Database migrations |
| **Maven** | Build & dependency management |
| **Lombok** | Boilerplate reduction |
| **JJWT** | JSON Web Token handling |
| **Hashids** | ID obfuscation in URLs |

### Testing
| Technology | Purpose |
|------------|---------|
| **JUnit 5 + Mockito + AssertJ** | Unit & integration tests |
| **Spring Test** | Spring test support |
| **H2 Database** | In-memory DB for tests |

## 📋 Prerequisites

- JDK 17+
- Maven 3.8+
- PostgreSQL instance (or Docker)
- [Optional] Google OAuth credentials

## ⚙️ Getting Started

### 1. Clone & enter

```bash
git clone https://github.com/edwin-dev31/chess-backend.git
cd chess-backend
```

### 2. Start the database

```bash
docker compose up -d
```

### 3. Configure Google OAuth (optional)

Set environment variables:
```bash
GOOGLE_CLIENT_ID=your-client-id
GOOGLE_CLIENT_SECRET=your-client-secret
```

### 4. Generate a local SSL certificate

```bash
keytool -genkeypair -alias chessapp -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore src/main/resources/chess-keystore.p12 -validity 365
```

### 5. Run

```bash
./mvnw spring-boot:run
```

Server starts at **`https://localhost:8443/chess`**.

## 🧪 Tests

```bash
./mvnw test
```

## 🌐 Frontend

This backend pairs with the [**Chess Frontend**](https://github.com/edwin-dev31/chess-frontend) — a React 19 + Vite app with real-time WebSocket gameplay.

## 📄 License

Distributed under the **MIT License**. See [LICENSE](./LICENSE) for more information.

---

<p align="center">
  <a href="https://github.com/edwin-dev31/chess-backend/issues">Report a bug</a> ·
  <a href="https://github.com/edwin-dev31/chess-backend/pulls">Request a feature</a>
</p>
