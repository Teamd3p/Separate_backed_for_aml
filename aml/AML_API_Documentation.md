# AML System - Complete API Documentation

## Overview
This document contains all the API endpoints for the comprehensive AML (Anti-Money Laundering) System, including Authentication, Account Management, Transaction Processing, Customer Management, Compliance Officer Operations, KYC Document Management, Currency Operations, and Administrative functions.

---

## Table of Contents
1. [Authentication APIs](#1-authentication-apis)
2. [Account Management APIs](#2-account-management-apis)
3. [Transaction Management APIs](#3-transaction-management-apis)
4. [Customer Management APIs](#4-customer-management-apis)
5. [Compliance Officer APIs](#5-compliance-officer-apis)
6. [KYC Document Management APIs](#6-kyc-document-management-apis)
7. [Currency Management APIs](#7-currency-management-apis)
8. [Admin Management APIs](#8-admin-management-apis)
9. [Audit Management APIs](#9-audit-management-apis)
10. [Error Responses](#10-error-responses)
11. [Authentication](#11-authentication)
12. [Status Enums](#12-status-enums)

---

## 1. Customer Management APIs

### 1.1 Get All Customers
**Endpoint:** `GET /api/admin/customers`  
**Description:** Retrieve all customers for admin view  
**Authorization:** Bearer Token (Admin role required)

**Request Headers:**
```json
{
  "Authorization": "Bearer <jwt_token>",
  "Content-Type": "application/json"
}
```

**Response (200 OK):**
```json
[
  {
    "userId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "contactNumber": "+1234567890",
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "nationality": "US",
    "status": "ACTIVE",
    "kycStatus": "VERIFIED",
    "createdAt": "2024-01-15T10:30:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  }
]
```

### 1.2 Update Customer Account Status
**Endpoint:** `PUT /api/admin/customers/{customerId}/account-status`  
**Description:** Update customer account status  
**Authorization:** Bearer Token (Admin role required)

**Request Body:**
```json
{
  "status": "SUSPENDED",
  "reason": "Suspicious activity detected"
}
```

**Response (200 OK):**
```json
"Account status updated successfully"
```

### 1.3 Update Customer Status (Alternative)
**Endpoint:** `PUT /api/admin/customers/{customerId}/status`  
**Description:** Alternative endpoint for customer status updates  
**Authorization:** Bearer Token (Admin role required)

**Request Body:**
```json
{
  "status": "ACTIVE",
  "reason": "Account review completed",
  "isActive": true
}
```

**Response (200 OK):**
```json
"Customer status updated successfully"
```

---

## 2. Compliance Officer Management APIs

### 2.1 Get All Officers
**Endpoint:** `GET /api/admin/officers`  
**Description:** Retrieve all compliance officers  
**Authorization:** Bearer Token (Admin role required)

**Response (200 OK):**
```json
[
  {
    "officerId": 1,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@aml-admin.com",
    "phone": "+1234567890",
    "employeeId": "EMP001",
    "department": "Compliance",
    "isActive": true,
    "createdAt": "2024-01-01T08:00:00Z"
  }
]
```

### 2.2 Create Officer
**Endpoint:** `POST /api/admin/officers`  
**Description:** Create a new compliance officer  
**Authorization:** Bearer Token (Admin role required)

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Officer",
  "email": "john.officer@aml-admin.com",
  "phone": "+1234567890",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "officerId": 2,
  "firstName": "John",
  "lastName": "Officer",
  "email": "john.officer@aml-admin.com",
  "phone": "+1234567890",
  "employeeId": "EMP002",
  "department": "Compliance",
  "isActive": true,
  "createdAt": "2024-01-15T10:30:00Z"
}
```

### 2.3 Update Officer Status
**Endpoint:** `PUT /api/admin/officers/{officerId}/status`  
**Description:** Update officer active/inactive status  
**Authorization:** Bearer Token (Admin role required)

**Request Body:**
```json
{
  "isActive": false,
  "reason": "Temporary suspension"
}
```

**Alternative Request Body:**
```json
{
  "active": true
}
```

**Alternative Request Body:**
```json
{
  "status": "INACTIVE"
}
```

**Response (200 OK):**
```json
"Officer status updated successfully"
```

---

## 3. KYC Compliance APIs

### 3.1 Get Customer Status for KYC Compliance
**Endpoint:** `GET /api/kyc/compliance/customers/status`  
**Description:** Get customers with KYC compliance status  
**Authorization:** Bearer Token (Admin/Officer role required)

**Response (200 OK):**
```json
{
  "content": [
    {
      "userId": 1,
      "firstName": "John",
      "lastName": "Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "accountStatus": "ACTIVE",
      "kycStatus": "VERIFIED",
      "riskScore": 25,
      "lastLogin": "2024-01-15T10:30:00Z",
      "createdAt": "2024-01-10T08:00:00Z"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

---

## 4. User Management APIs

### 4.1 Get All Users
**Endpoint:** `GET /api/users`  
**Description:** Get all users (customers and officers)  
**Authorization:** Bearer Token (Admin role required)

**Response (200 OK):**
```json
[
  {
    "userId": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "role": "CUSTOMER",
    "status": "ACTIVE",
    "isActive": true,
    "createdAt": "2024-01-15T10:30:00Z",
    "lastLogin": "2024-01-15T10:30:00Z"
  },
  {
    "userId": 2,
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@aml-admin.com",
    "role": "COMPLIANCE_OFFICER",
    "status": "ACTIVE",
    "isActive": true,
    "createdAt": "2024-01-01T08:00:00Z"
  }
]
```

### 4.2 Update User Status
**Endpoint:** `PUT /api/users/{userId}/status`  
**Description:** Update user status  
**Authorization:** Bearer Token (Admin role required)

**Request Body:**
```json
{
  "status": "SUSPENDED",
  "isActive": false,
  "reason": "Policy violation"
}
```

**Response (200 OK):**
```json
"User status updated successfully"
```

---

## 5. Dashboard Statistics API

### 5.1 Get Dashboard Stats
**Endpoint:** `GET /api/admin/dashboard/stats`  
**Description:** Get comprehensive dashboard statistics  
**Authorization:** Bearer Token (Admin role required)

**Response (200 OK):**
```json
{
  "totalCustomers": 150,
  "activeCustomers": 142,
  "pendingAlerts": 8,
  "sarGenerated": 3,
  "openHelpTickets": 12,
  "totalTransactions": 1250,
  "flaggedTransactions": 25,
  "complianceOfficers": 5,
  "activeOfficers": 4,
  "systemHealth": "HEALTHY",
  "lastUpdated": "2024-01-15T10:30:00Z"
}
```

---

## 6. Alert Management APIs

### 6.1 Get Alert Count by Customer
**Endpoint:** `GET /api/admin/alerts/count/customer/{customerId}`  
**Description:** Get alert count for specific customer  
**Authorization:** Bearer Token (Admin/Officer role required)

**Response (200 OK):**
```json
5
```

---

## 7. Audit Log APIs

### 7.1 Get All Audit Logs
**Endpoint:** `GET /api/admin/audit-logs`  
**Description:** Get paginated audit logs  
**Authorization:** Bearer Token (Admin role required)

**Query Parameters:**
- `page` (optional, default: 0)
- `size` (optional, default: 50)

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "action": "ACCOUNT_STATUS_UPDATE",
    "resourceType": "CUSTOMER",
    "resourceId": 123,
    "performedBy": 1,
    "details": "Account status updated to SUSPENDED. Reason: Suspicious activity",
    "timestamp": "2024-01-15T10:30:00Z",
    "status": "SUCCESS",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0..."
  }
]
```

---

## 8. System Health API

### 8.1 Get System Health
**Endpoint:** `GET /api/admin/system/health`  
**Description:** Get system health status  
**Authorization:** Bearer Token (Admin role required)

**Response (200 OK):**
```json
{
  "status": "HEALTHY",
  "database": "OK",
  "customerCount": 150,
  "transactionCount": 1250,
  "alertCount": 25,
  "rulesLoaded": 15,
  "memoryUsageMB": "512 / 1024",
  "availableProcessors": 4,
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 9. Error Responses

### Common Error Responses

**400 Bad Request:**
```json
{
  "error": "Bad Request",
  "message": "Invalid status: INVALID_STATUS",
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/admin/customers/123/status"
}
```

**401 Unauthorized:**
```json
{
  "error": "Unauthorized",
  "message": "JWT token is expired or invalid",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**403 Forbidden:**
```json
{
  "error": "Forbidden",
  "message": "Access denied. Admin role required",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**404 Not Found:**
```json
{
  "error": "Not Found",
  "message": "Customer not found",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**500 Internal Server Error:**
```json
{
  "error": "Internal Server Error",
  "message": "Database connection failed",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

---

## 10. Authentication

All API endpoints require JWT authentication via Bearer token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Structure:
```json
{
  "sub": "admin@aml-system.com",
  "role": "ADMIN",
  "userId": 1,
  "exp": 1642248000,
  "iat": 1642161600
}
```

---

## 11. Status Enums

### Account Status:
- `ACTIVE`
- `INACTIVE` 
- `SUSPENDED`
- `CLOSED`
- `FROZEN`

### User Status:
- `ACTIVE`
- `INACTIVE`
- `SUSPENDED`
- `PENDING_VERIFICATION`

### KYC Status:
- `PENDING`
- `IN_REVIEW`
- `VERIFIED`
- `REJECTED`
- `EXPIRED`

### Alert Status:
- `PENDING`
- `OPEN`
- `NEW`
- `IN_PROGRESS`
- `RESOLVED`
- `CLOSED`

---

## 12. Implementation Notes

1. **Database Persistence**: All status changes are persisted to the database with audit logging
2. **Error Handling**: Comprehensive error handling with user-friendly messages
3. **Security**: All endpoints require proper authentication and authorization
4. **Audit Trail**: All administrative actions are logged for compliance
5. **Validation**: Input validation on all request bodies
6. **Pagination**: Large datasets support pagination (page, size parameters)

---

## 13. Frontend Integration

The frontend uses multiple endpoint fallback strategy:
1. Primary endpoint (e.g., `/api/admin/customers`)
2. Alternative endpoints (e.g., `/api/users` filtered by role)
3. Fallback to mock data if all endpoints fail

This ensures robust operation even if some endpoints are unavailable.
