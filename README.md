# Vulnerable Spring Boot E-Commerce Storefront

This is a simple e-commerce storefront built with Java, Spring Boot, PostgreSQL, and Thymeleaf.
It is **intentionally vulnerable** and designed for security benchmarking purposes.

## Setup and Run Instructions

### Prerequisites
- Java 17+
- Maven
- PostgreSQL

### Database Setup
1. Ensure PostgreSQL is running on `localhost:5432`.
2. Create a database named `storefront`:
   ```sql
   CREATE DATABASE storefront;
   ```
3. The application will automatically create the schema (using Hibernate `update`) and seed initial data using the `DataLoader` class when it starts up. The default PostgreSQL credentials in `application.properties` are `postgres` / `postgres123`.

### Running the Application
Run the application using the Spring Boot Maven Plugin:
```bash
mvn spring-boot:run
```

The app will be available at `http://localhost:8080`.

### Initial Seed Data
The `DataLoader` creates the following default users:
- **Admin**: `admin` / `admin123`
- **User 1**: `johndoe` / `password123`
- **User 2**: `janedoe` / `password123`

It also adds 12 sample products and a few dummy orders.

---

## VULNERABILITY NOTES

This application contains intentional security flaws for testing and benchmarking tools.

### 1. Insecure Password Hashing (MD5)
- **Where it is implemented**: `src/main/java/com/example/storefront/services/UserService.java` in the `hashPasswordMD5()` method.
- **OWASP Category**: A02:2021 - Cryptographic Failures.
- **Impact**: MD5 is a cryptographically broken and extremely fast algorithm. A real attacker who compromises the database can easily crack these passwords using rainbow tables or brute-force tools (like Hashcat) in seconds. Modern approaches should use bcrypt, Argon2, or PBKDF2.

### 2. Plaintext Credit Card Storage
- **Where it occurs**: `src/main/java/com/example/storefront/models/Order.java` (fields: `creditCardNumber`, `expiry`, `cvv`) which map directly to the `orders` table in the PostgreSQL database. The data is saved via the `CartController.java` during checkout.
- **OWASP Category**: A02:2021 - Cryptographic Failures (Sensitive Data Exposure).
- **Impact**: If an attacker gains access to the database via SQL Injection or other means, they will have direct access to raw credit card details, leading to massive financial fraud and PCI-DSS compliance violations. Credit card data must be strongly encrypted at rest or tokenized by a payment provider.

### 3. Hardcoded Secrets
- **Where it occurs**: `src/main/resources/application.properties` (`PAYMENT_API_KEY` and the database credentials).
- **OWASP Category**: A02:2021 - Cryptographic Failures (and generally poor secrets management).
- **Impact**: If the source code repository is compromised or leaked, attackers will immediately gain valid credentials to the database and third-party APIs. Secrets should be loaded securely at runtime via environment variables or a secret manager.

### 4. Lack of Transport Layer Security (TLS/HTTPS)
- **Where it occurs**: Configured in `application.properties` (`server.ssl.enabled=false`), all traffic is over HTTP.
- **OWASP Category**: A02:2021 - Cryptographic Failures (Insecure Communication).
- **Impact**: Attackers on the same network (e.g., public Wi-Fi) can perform Man-in-the-Middle (MITM) attacks and capture plaintext credentials, session cookies, and credit card details during checkout using packet sniffers like Wireshark.
