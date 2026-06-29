# Real-Time Chess Backend

This is the backend for a real-time chess application, built with Spring Boot. It provides a REST API for user and game management, and uses WebSockets for in-game communication.

## Features

- **User Authentication:** Register and login with a custom JWT-based system, plus Google OAuth2 login.
- **Presence Management:** Shows connected players in real time.
- **Invitation System:** Allows players to invite each other to a game.
- **Real-Time Gameplay:** Bidirectional communication via WebSockets (STOMP) for moves and board state updates (FEN).
- **Move Validation:** Server-side chess logic validates every move according to the rules.
- **Persistence:** Stores game state and moves in a PostgreSQL database.
- **Security:** Communication is secured via TLS/SSL (HTTPS and WSS).

---

## 🚀 Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3:** Core framework.
- **Spring Security:** Authentication & authorization (JWT + OAuth2).
- **Spring Data JPA & Hibernate:** Data persistence.
- **Spring WebSocket:** Real-time communication via STOMP.
- **PostgreSQL:** Relational database.
- **Flyway:** Database migration management.
- **Maven:** Build and dependency management.
- **Lombok:** Boilerplate code reduction.
- **JJWT (Java JWT):** JSON Web Token creation and validation.
- **Hashids:** Numeric ID obfuscation in URLs.

### Frontend (Inferred)
- **TypeScript**
- **React** (or similar framework)
- **SockJS & Stomp.js:** WebSocket communication with the backend.
- **Axios:** REST API calls.

### Testing
- **JUnit 5, Mockito, AssertJ:** Unit and integration tests.
- **Spring Test:** Spring ecosystem test support.
- **H2 Database:** In-memory database for tests.

---

## 📋 Prerequisites

- **JDK 17** or higher.
- **Maven 3.8** or higher.
- **PostgreSQL:** A running database instance.
- **Docker (Optional):** To easily spin up a database with `docker-compose`.

---

## ⚙️ Setup & Run

### 1. Clone the Repository

```bash
git clone <REPOSITORY_URL>
cd chess-backend
```

### 2. Configure the Database

The easiest way is using Docker:

```bash
# Starts a PostgreSQL container with default config
docker-compose up -d
```

If you prefer a local PostgreSQL instance, make sure it matches the configuration in `src/main/resources/application-dev.properties` or create your own profile.

### 3. Configure Environment Variables (Optional)

For Google login to work, create a `client-id` and `client-secret` in the [Google Cloud Console](https://console.cloud.google.com/apis/credentials) and set them as environment variables:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

### 4. Configure HTTPS (TLS/SSL) for Development

The application is configured to run over HTTPS. If this is your first time setting up the project, you need to generate a local certificate.

1.  **Generate the Keystore:**
    - Open a terminal in the project root.
    - Run the following command (make sure your JDK path is configured or use the full path to `keytool`).
    ```bash
    keytool -genkeypair -alias chessapp -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore chess-keystore.p12 -validity 365
    ```
    - When prompted for a password, enter one (e.g. `password`). It must match the one in `application.properties`.

2.  **Move the Keystore:**
    - Move `chess-keystore.p12` to `src/main/resources`.

3.  **Verify `application.properties`:**
    - Make sure the following properties are set in `src/main/resources/application.properties` and that the password is correct.
    ```properties
    server.port=8443
    server.ssl.enabled=true
    server.ssl.key-store=classpath:chess-keystore.p12
    server.ssl.key-store-password=password
    server.ssl.key-alias=chessapp
    ```

### 5. Run the Application

Use the Maven wrapper to compile and run:

```bash
./mvnw spring-boot:run
```

The server will start at **`https://localhost:8443/chess`**.

> **Note:** When accessing for the first time from your browser, you will see a security warning. Click "Advanced" and "Proceed to localhost" to accept the self-signed certificate.

---

## 🧪 Running Tests

To run the full test suite, including end-to-end tests:

```bash
./mvnw test
```
