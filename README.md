# mini-core-banking-system
Mini Core Banking System built with Spring Boot. Includes account management, deposit, withdraw, transfer, transaction history, and proper transactional handling.

# Mini Core Banking System

A simple core banking backend system built with **Spring Boot**.  
This project simulates basic banking operations such as account management and money transactions with proper transactional integrity.

## Features

- Create bank accounts (Savings / Current)
- Deposit money
- Withdraw money
- Transfer money between accounts
- View transaction history
- Freeze / Unfreeze account
- Global exception handling
- Unit tests and Controller tests

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Data JPA
- Spring Validation
- MariaDB / MySQL
- JUnit 5 + Mockito
- Maven

## Project Structure
├── Controller
├── Service
├── Repository
├── Entity
├── Enum
├── DTO
└── Exception


## Main Modules

### 1. Account Management
- Create new bank account
- Get account details
- Get accounts by user
- Freeze / Unfreeze account

### 2. Transaction Management
- Deposit
- Withdraw
- Transfer (with `@Transactional`)
- Transaction history

### 3. Exception Handling
- Custom business exceptions
- Global exception handler
- Clean error responses

## API Endpoints

### Account
- `POST /api/accounts` - Create account
- `GET /api/accounts/{accountNumber}` - Get account
- `GET /api/accounts/user/{userId}` - Get user accounts
- `PATCH /api/accounts/{accountNumber}/freeze`
- `PATCH /api/accounts/{accountNumber}/unfreeze`

### Transaction
- `POST /api/transactions/deposit`
- `POST /api/transactions/withdraw`
- `POST /api/transactions/transfer`
- `GET /api/transactions/{reference}`
- `GET /api/transactions/account/{accountId}`

## How to Run

1. Clone the repository
```bash
git clone https://github.com/YourUsername/mini-core-banking-system.git
