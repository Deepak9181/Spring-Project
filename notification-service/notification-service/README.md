# Notification & Alert Management Service

A Spring Boot microservice for managing user notifications with automated retry logic and delivery status tracking.

---

## ⚙️ Setup

### 1. Generate Gmail App Password
1. Go to myaccount.google.com → Security
2. Enable 2-Step Verification
3. Search "App Passwords" → Select Mail → Generate
4. Copy the 16-character password

### 2. Update application.properties
```
spring.mail.username=yourgmail@gmail.com
spring.mail.password=xxxx xxxx xxxx xxxx
```

### 3. Run
```bash
mvn spring-boot:run
```
App starts at: http://localhost:8080

---

## 📮 Postman API Reference

### Send a Notification (real email)
```
POST http://localhost:8080/api/notifications
Content-Type: application/json

{
  "recipient": "someone@gmail.com",
  "subject": "Your subscription is expiring",
  "message": "Hi, your subscription expires in 3 days. Please renew.",
  "type": "EMAIL"
}
```

### Test Retry Logic (use invalid email)
```
POST http://localhost:8080/api/notifications

{
  "recipient": "invalid@fakedomainthatdoesnotexist.com",
  "subject": "Test retry",
  "message": "This will fail and retry",
  "type": "EMAIL"
}
```
Watch retryCount increment every 2 minutes in the DB.

### Get All Notifications
```
GET http://localhost:8080/api/notifications
```

### Get by ID
```
GET http://localhost:8080/api/notifications/1
```

### Get by Status
```
GET http://localhost:8080/api/notifications/status/SENT
GET http://localhost:8080/api/notifications/status/FAILED
GET http://localhost:8080/api/notifications/status/RETRYING
GET http://localhost:8080/api/notifications/status/PENDING
```

### Get by Recipient
```
GET http://localhost:8080/api/notifications/recipient/someone@gmail.com
```

### Delete
```
DELETE http://localhost:8080/api/notifications/1
```

---

## 🔄 Retry Flow
1. POST notification → immediate send attempt
2. Fails → status = RETRYING, retryCount = 1
3. Every 2 mins → scheduler retries automatically
4. After 3 failures → status = FAILED, failureReason saved

---

## 🗂️ Project Structure
```
src/main/java/com/deepak/notification/
├── NotificationApplication.java
├── controller/
│   └── NotificationController.java
├── service/
│   ├── NotificationService.java
│   └── EmailService.java
├── repository/
│   └── NotificationRepository.java
├── entity/
│   └── Notification.java
├── dto/
│   ├── NotificationRequestDTO.java
│   └── NotificationResponseDTO.java
├── enums/
│   ├── NotificationStatus.java
│   └── NotificationType.java
└── exception/
    ├── GlobalExceptionHandler.java
    └── ResourceNotFoundException.java
```

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Data JPA
- Spring Mail (JavaMailSender)
- Spring Scheduler
- H2 (dev) / MySQL (prod)
- Lombok
- Maven
