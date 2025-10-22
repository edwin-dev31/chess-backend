# Proyecto de Ajedrez en Tiempo Real

Este es el backend para una aplicación de ajedrez en tiempo real, construida con Spring Boot. Proporciona una API REST para la gestión de usuarios y partidas, y utiliza WebSockets para la comunicación durante el juego.

## Características Principales

- **Autenticación de Usuarios:** Registro y login con sistema propio basado en JWT, y opción de login con Google (OAuth2).
- **Gestión de Presencia:** Muestra los jugadores conectados en tiempo real.
- **Sistema de Invitaciones:** Permite a los jugadores invitarse mutuamente a una partida.
- **Juego en Tiempo Real:** Comunicación bidireccional mediante WebSockets (STOMP) para el envío de movimientos y actualizaciones del estado del tablero (FEN).
- **Validación de Movimientos:** La lógica del juego en el backend valida cada movimiento según las reglas del ajedrez.
- **Persistencia:** Guarda el estado de las partidas y los movimientos en una base de datos PostgreSQL.
- **Seguridad:** La comunicación está asegurada mediante TLS/SSL (HTTPS y WSS).

---

## 🚀 Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3:** Framework principal de la aplicación.
- **Spring Security:** Para la gestión de autenticación y autorización (JWT y OAuth2).
- **Spring Data JPA & Hibernate:** Para la persistencia de datos y comunicación con la base de datos.
- **Spring WebSocket:** Para la comunicación en tiempo real con el protocolo STOMP.
- **PostgreSQL:** Base de datos relacional para almacenar la información.
- **Flyway:** Para la gestión de migraciones de la base de datos.
- **Maven:** Como gestor de dependencias y construcción del proyecto.
- **Lombok:** Para reducir el código repetitivo en las clases de dominio y DTOs.
- **JJWT (Java JWT):** Para la creación y validación de JSON Web Tokens.
- **Hashids:** Para ofuscar los IDs numéricos en las URLs.

### Frontend (Inferido)
- **TypeScript**
- **React** (o un framework similar)
- **SockJS & Stomp.js:** Para la comunicación con el backend a través de WebSockets.
- **Axios:** Para las llamadas a la API REST.

### Testing
- **JUnit 5, Mockito, AssertJ:** Para las pruebas unitarias y de integración.
- **Spring Test:** Para el soporte de pruebas en el ecosistema Spring.
- **H2 Database:** Base de datos en memoria para la ejecución de los tests.

---

## 📋 Requisitos Previos

- **JDK 17** o superior.
- **Maven 3.8** o superior.
- **PostgreSQL:** Una instancia de base de datos en ejecución.
- **Docker (Opcional):** Para levantar fácilmente una base de datos con `docker-compose`.

---

## ⚙️ Configuración y Ejecución del Backend

### 1. Clonar el Repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
cd chess-backend
```

### 2. Configurar la Base de Datos

La forma más sencilla es usar Docker.

```bash
# Inicia un contenedor de PostgreSQL con los datos de configuración por defecto
docker-compose up -d
```

Si prefieres usar una instancia local de PostgreSQL, asegúrate de que coincida con la configuración en `src/main/resources/application-dev.properties` o crea tu propio perfil.

### 3. Configurar Variables de Entorno (Opcional)

Para que el login con Google funcione, necesitas crear un `client-id` y un `client-secret` en la [Consola de Google Cloud](https://console.cloud.google.com/apis/credentials) y configurarlos como variables de entorno:

- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`

### 4. Configurar HTTPS (TLS/SSL) para Desarrollo

La aplicación está configurada para ejecutarse sobre HTTPS. Si es la primera vez que configuras el proyecto, necesitas generar un certificado local.

1.  **Generar el Keystore:**
    -   Abre una terminal en la raíz del proyecto.
    -   Ejecuta el siguiente comando (asegúrate de que la ruta a tu JDK esté configurada o usa la ruta completa a `keytool`).
    ```bash
    keytool -genkeypair -alias chessapp -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore chess-keystore.p12 -validity 365
    ```
    -   Cuando te pida una contraseña, introduce una (ej. `password`). Esta debe coincidir con la que está en `application.properties`.

2.  **Mover el Keystore:**
    -   Mueve el archivo `chess-keystore.p12` a la carpeta `src/main/resources`.

3.  **Verificar `application.properties`:**
    -   Asegúrate de que las siguientes propiedades estén configuradas en `src/main/resources/application.properties` y que la contraseña sea la correcta.
    ```properties
    server.port=8443
    server.ssl.enabled=true
    server.ssl.key-store=classpath:chess-keystore.p12
    server.ssl.key-store-password=password
    server.ssl.key-alias=chessapp
    ```

### 5. Ejecutar la Aplicación

Utiliza el wrapper de Maven para compilar y ejecutar la aplicación:

```bash
./mvnw spring-boot:run
```

El servidor se iniciará en **`https://localhost:8443/chess`**.

> **Nota:** Al acceder por primera vez desde tu navegador, verás una advertencia de seguridad. Debes hacer clic en "Avanzado" y "Proceder a localhost" para aceptar el certificado autofirmado.

---

## 🧪 Ejecutar los Tests

Para ejecutar todo el conjunto de pruebas, incluyendo los tests end-to-end, usa el siguiente comando:

```bash
./mvnw test
```
