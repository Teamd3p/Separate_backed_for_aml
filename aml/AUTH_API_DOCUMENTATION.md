# Customer Authentication API Documentation

## Overview
This document describes the Customer Registration and Login API with Email OTP Verification for the AML System.

## Base URL
```
http://localhost:8080/api/auth
```

## Authentication Flow

### 1. Registration Flow
1. Customer registers with email and details
2. System generates 6-digit OTP and sends to email
3. OTP is valid for 10 minutes
4. Customer verifies OTP
5. Account status changes from `PENDING_VERIFICATION` to `ACTIVE`
6. Customer can now login

### 2. Login Flow
1. Customer enters email and password
2. System checks if email is verified
3. System validates credentials
4. JWT token is generated and returned
5. Token is valid for 24 hours

---

## API Endpoints

### 1. Register Customer

**Endpoint:** `POST /api/auth/register`

**Description:** Register a new customer account. An OTP will be sent to the provided email.

**Request Body:**
```json
{
  "email": "customer@example.com",
  "password": "SecurePass@123",
  "firstName": "John",
  "middleName": "Robert",
  "lastName": "Doe",
  "dateOfBirth": "1990-05-15",
  "nationality": "Indian",
  "contactNumber": "9876543210",
  "street": "123 Main Street",
  "city": "Mumbai",
  "state": "Maharashtra",
  "nation": "India",
  "pincode": "400001"
}
```

**Validation Rules:**
- `email`: Required, valid email format
- `password`: Required, minimum 8 characters, must contain:
  - At least one digit
  - At least one lowercase letter
  - At least one uppercase letter
  - At least one special character (@#$%^&+=)
- `firstName`: Required
- `lastName`: Required
- `dateOfBirth`: Required, must be in the past
- `nationality`: Required
- `contactNumber`: Required, 10-15 digits
- `middleName`, `street`, `city`, `state`, `nation`, `pincode`: Optional

**Success Response (201 Created):**
```json
{
  "message": "Registration successful! Please check your email for OTP verification.",
  "token": null,
  "email": null,
  "role": null
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "message": "Email already registered",
  "timestamp": "2025-10-03T11:21:06"
}
```

---

### 2. Verify OTP

**Endpoint:** `POST /api/auth/verify-otp`

**Description:** Verify the OTP sent to the customer's email during registration.

**Request Body:**
```json
{
  "email": "customer@example.com",
  "otp": "123456"
}
```

**Validation Rules:**
- `email`: Required, valid email format
- `otp`: Required, exactly 6 digits

**Success Response (200 OK):**
```json
{
  "message": "Email verified successfully! You can now login.",
  "token": null,
  "email": null,
  "role": null
}
```

**Error Responses:**
```json
{
  "status": 400,
  "message": "Invalid OTP",
  "timestamp": "2025-10-03T11:21:06"
}
```

```json
{
  "status": 400,
  "message": "OTP has expired. Please request a new one.",
  "timestamp": "2025-10-03T11:21:06"
}
```

---

### 3. Login

**Endpoint:** `POST /api/auth/login`

**Description:** Login with verified email and password. Returns JWT token.

**Request Body:**
```json
{
  "email": "customer@example.com",
  "password": "SecurePass@123"
}
```

**Validation Rules:**
- `email`: Required, valid email format
- `password`: Required

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "customer@example.com",
  "role": "CUSTOMER",
  "message": "Login successful"
}
```

**Error Responses:**

Email not verified:
```json
{
  "status": 400,
  "message": "Email not verified. Please verify your email first.",
  "timestamp": "2025-10-03T11:21:06"
}
```

Invalid credentials:
```json
{
  "status": 400,
  "message": "Invalid email or password",
  "timestamp": "2025-10-03T11:21:06"
}
```

Account not active:
```json
{
  "status": 400,
  "message": "Account is not active. Please contact support.",
  "timestamp": "2025-10-03T11:21:06"
}
```

---

### 4. Resend OTP

**Endpoint:** `POST /api/auth/resend-otp?email={email}`

**Description:** Resend OTP to the customer's email if the previous one expired or was not received.

**Query Parameters:**
- `email`: Customer's email address

**Example:**
```
POST /api/auth/resend-otp?email=customer@example.com
```

**Success Response (200 OK):**
```json
{
  "message": "OTP has been resent to your email.",
  "token": null,
  "email": null,
  "role": null
}
```

**Error Response:**
```json
{
  "status": 400,
  "message": "Email already verified. Please login.",
  "timestamp": "2025-10-03T11:21:06"
}
```

---

## Database Schema Changes

### User Entity (Updated)
```java
- emailVerified: boolean (default: false)
- verificationOtp: String (6-digit OTP)
- otpExpiryTime: LocalDateTime (OTP valid for 10 minutes)
- status: UserStatus (default: PENDING_VERIFICATION)
```

### UserStatus Enum (Updated)
```java
ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION
```

---

## Configuration Required

### 1. Email Configuration (application.properties)
Update with your email credentials:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**For Gmail:**
1. Enable 2-Factor Authentication
2. Generate App Password: Google Account → Security → App Passwords
3. Use the generated password in `spring.mail.password`

### 2. JWT Configuration
Already configured with default values:
```properties
jwt.secret=amlSecretKeyForJWTTokenGenerationMustBeLongEnoughForHS256Algorithm
jwt.expiration=86400000
```

---

## Testing with Postman/cURL

### 1. Register
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123456",
    "firstName": "Test",
    "lastName": "User",
    "dateOfBirth": "1995-01-01",
    "nationality": "Indian",
    "contactNumber": "9876543210"
  }'
```

### 2. Verify OTP
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "otp": "123456"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Test@123456"
  }'
```

### 4. Resend OTP
```bash
curl -X POST "http://localhost:8080/api/auth/resend-otp?email=test@example.com"
```

---

## Security Features

1. **Password Encryption**: BCrypt hashing with salt
2. **JWT Authentication**: Stateless token-based authentication
3. **OTP Expiry**: 10-minute validity
4. **Email Verification**: Mandatory before login
5. **Status Management**: Account status tracking
6. **Validation**: Comprehensive input validation

---

## Error Handling

All errors follow a consistent format:

```json
{
  "status": 400,
  "message": "Error description",
  "timestamp": "2025-10-03T11:21:06"
}
```

Validation errors include field-specific details:

```json
{
  "status": 400,
  "errors": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters"
  },
  "timestamp": "2025-10-03T11:21:06"
}
```

---

## Next Steps

1. Configure email credentials in `application.properties`
2. Start the application: `mvn spring-boot:run`
3. Test the registration flow
4. Integrate JWT token in subsequent API calls using `Authorization: Bearer <token>` header
