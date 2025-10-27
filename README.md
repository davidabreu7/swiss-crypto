# Swiss Crypto - Crypto Wallet Management

A Spring Boot application for managing cryptocurrency wallets with automatic price updates from CoinCap API.

## Tech Stack

- **Java 24** with Spring Boot 3.5.7
- **PostgreSQL** for data persistence
- **Gradle** build system
- **CoinCap API** for cryptocurrency pricing

## Features

- Create and manage crypto wallets
- Add cryptocurrency assets to wallets
- Automatic price updates every 15 minutes (configurable)
- Concurrent price fetching (3 threads maximum)
- Real-time wallet valuation in USD
- Portfolio performance simulation (current prices or historical dates)

## Prerequisites

- Java 24 or higher
- Docker (for PostgreSQL)
- Gradle (included via wrapper)

## Setup & Running

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

### 3. Run Tests

```bash
./gradlew test
```

### 4. Build

```bash
./gradlew build
```

## API Endpoints

### Create Wallet

```http
POST /api/wallets
Content-Type: application/json

{
  "email": "user@example.com"
}
```

**Response:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "total": 0.00,
  "assets": []
}
```

### Get Wallet

```http
GET /api/wallets/{walletId}
```

**Response:**
```json
{
  "id": "uuid",
  "email": "user@example.com",
  "total": 158000.00,
  "assets": [
    {
      "id": "uuid",
      "symbol": "BTC",
      "quantity": 1.5,
      "price": 100000.00,
      "value": 150000.00
    }
  ]
}
```

### Add Asset to Wallet

```http
POST /api/wallets/{walletId}/assets
Content-Type: application/json

{
  "symbol": "BTC",
  "quantity": 1.5,
  "price": 100000.00
}
```

**Response:** Updated wallet with new asset

### Simulate Wallet Performance

Simulate portfolio performance by comparing original asset values with current or historical market prices.

**Current Price Simulation (default):**
```http
POST /api/wallets/simulate
Content-Type: application/json

{
  "assets": [
    {
      "symbol": "bitcoin",
      "quantity": 1.5,
      "value": 75000.00
    },
    {
      "symbol": "ethereum",
      "quantity": 10.0,
      "value": 30000.00
    }
  ]
}
```

**Historical Date Simulation:**
```http
POST /api/wallets/simulate
Content-Type: application/json

{
  "assets": [
    {
      "symbol": "bitcoin",
      "quantity": 0.5,
      "value": 35000.00
    },
    {
      "symbol": "ethereum",
      "quantity": 4.25,
      "value": 15310.71
    }
  ],
  "date": "2025-01-07"
}
```

**Response:**
```json
{
  "total": 158000.00,
  "bestAsset": "bitcoin",
  "bestPerformance": 110.50,
  "worstAsset": "ethereum",
  "worstPerformance": 5.25
}
```

**Parameters:**
- `assets` (required): List of assets with symbol, quantity, and original value
- `date` (optional): ISO date format (YYYY-MM-DD). If omitted, uses current prices

**Note:** This endpoint does not persist any data to the database. Performance is calculated as: `((targetPrice - originalPrice) / originalPrice) * 100`

## Configuration

Adjust settings in `src/main/resources/application.properties`:

```properties
# Price update interval (milliseconds)
swiss-crypto.price-update.interval=900000

# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/swissdb
spring.datasource.username=postgres
spring.datasource.password=secret
```

## Supported Cryptocurrencies

BTC, ETH, USDT, USDC, BNB, XRP, ADA, SOL, DOT, DOGE

## Project Structure

```
src/main/java/com/swisspost/swisscrypto/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data transfer objects
├── entity/          # JPA entities
├── model/           # Domain models
├── repository/      # Data access layer
├── scheduler/       # Scheduled tasks
└── service/         # Business logic
```

## Key Implementation Details

- **Concurrent Price Updates**: Uses `CompletableFuture` with `SimpleAsyncTaskExecutor` (max 3 concurrent threads)
- **Scheduled Updates**: `@Scheduled` with `fixedDelay` to prevent overlapping executions
- **Transaction Management**: Each symbol update is independently transactional for fault tolerance
- **Error Handling**: Global exception handling with `@RestControllerAdvice`

## Database Schema

- **User**: email (unique)
- **Wallet**: one-to-one with User
- **Asset**: symbol, quantity, price (many-to-one with Wallet)

## Notes

- Price updates run automatically every 15 minutes after application startup
- The scheduler prevents overlapping executions for API safety
- Each asset price update is isolated (one failure doesn't affect others)
- All endpoints return JSON responses
