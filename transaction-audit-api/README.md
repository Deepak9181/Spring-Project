# Transaction Audit & Reconciliation API

A Spring Boot REST API to detect duplicate transactions, flag dataset mismatches, and generate audit reports.

---

## ⚙️ Setup — Do This First

### 1. Prerequisites
- Java 17+
- Maven
- MySQL running locally

### 2. Create the Database
Open MySQL and run:
```sql
CREATE DATABASE audit_db;
```
> The app will auto-create the tables on first run.

### 3. Update Your Password
Open `src/main/resources/application.properties` and change:
```
spring.datasource.password=yourpassword
```
to your actual MySQL root password.

### 4. Run the Project
```bash
mvn spring-boot:run
```
App starts at: `http://localhost:8080`

---

## 📮 Postman API Reference

### ➕ Add a Transaction
```
POST http://localhost:8080/api/transactions
Content-Type: application/json

{
  "transactionId": "TXN-001",
  "userId": "USER-1",
  "amount": 500.00,
  "status": "SUCCESS",
  "source": "SYSTEM_A"
}
```

### 📋 Get All Transactions
```
GET http://localhost:8080/api/transactions
```

### 🔍 Get Transaction by ID
```
GET http://localhost:8080/api/transactions/1
```

### 🔴 Get by Status
```
GET http://localhost:8080/api/transactions/status/SUCCESS
GET http://localhost:8080/api/transactions/status/FAILED
GET http://localhost:8080/api/transactions/status/PENDING
```

### ⚠️ Find Duplicate Transaction IDs
```
GET http://localhost:8080/api/transactions/duplicates
```

### 🗑️ Delete a Transaction
```
DELETE http://localhost:8080/api/transactions/1
```

---

### 🔄 Reconcile Two Systems
```
POST http://localhost:8080/api/reconcile
Content-Type: application/json

{
  "systemA": [
    { "transactionId": "TXN-001", "userId": "USER-1", "amount": 500.00, "status": "SUCCESS" },
    { "transactionId": "TXN-002", "userId": "USER-2", "amount": 300.00, "status": "FAILED" },
    { "transactionId": "TXN-003", "userId": "USER-3", "amount": 700.00, "status": "SUCCESS" }
  ],
  "systemB": [
    { "transactionId": "TXN-001", "userId": "USER-1", "amount": 500.00, "status": "SUCCESS" },
    { "transactionId": "TXN-002", "userId": "USER-2", "amount": 999.00, "status": "SUCCESS" },
    { "transactionId": "TXN-004", "userId": "USER-4", "amount": 200.00, "status": "SUCCESS" }
  ]
}
```

**Expected result:**
- TXN-001 → ✅ Matched
- TXN-002 → ❌ Mismatch (amount and status differ)
- TXN-003 → ❌ Missing in System B
- TXN-004 → ❌ Missing in System A

### 📊 Get All Audit Reports
```
GET http://localhost:8080/api/reconcile/reports
```

---

## 🗂️ Project Structure
```
src/main/java/com/deepak/audit/
├── AuditApplication.java
├── controller/
│   ├── TransactionController.java
│   └── ReconcileController.java
├── service/
│   ├── TransactionService.java
│   └── ReconcileService.java
├── repository/
│   ├── TransactionRepository.java
│   └── AuditReportRepository.java
├── entity/
│   ├── Transaction.java
│   └── AuditReport.java
├── dto/
│   ├── TransactionDTO.java
│   ├── ReconcileRequestDTO.java
│   └── ReconcileResponseDTO.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- MySQL
- Lombok
- Maven
