# Quick Start Guide - Customer Authentication

## Prerequisites
- ✅ Java 21 installed
- ✅ MySQL running on localhost:3306
- ✅ Maven installed
- ⚠️ Gmail account with App Password (for OTP emails)

---

## Step 1: Configure Email Settings

Open `src/main/resources/application.properties` and update:

```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### How to Get Gmail App Password:
1. Go to your Google Account: https://myaccount.google.com/
2. Security → 2-Step Verification (enable if not already)
3. Security → App Passwords
4. Select "Mail" and "Other (Custom name)"
5. Generate and copy the 16-character password
6. Paste it in `spring.mail.password`

---

## Step 2: Start the Application

```bash
cd aml
mvn clean install
mvn spring-boot:run
```

Wait for: `Started AmlApplication in X seconds`

---

## Step 3: Test Registration

### Using cURL:
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "password": "Test@123456",
    "firstName": "John",
    "lastName": "Doe",
    "dateOfBirth": "1990-01-01",
    "nationality": "Indian",
    "contactNumber": "9876543210"
  }'
```

### Using Postman:
1. Method: POST
2. URL: `http://localhost:8080/api/auth/register`
3. Headers: `Content-Type: application/json`
4. Body (raw JSON):
```json
{
  "email": "your-test-email@gmail.com",
  "password": "Test@123456",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-01-01",
  "nationality": "Indian",
  "contactNumber": "9876543210"
}
```

**Expected Response:**
```json
{
  "message": "Registration successful! Please check your email for OTP verification."
}
```

**Check your email for 6-digit OTP!**

---

## Step 4: Verify OTP

### Using cURL:
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "otp": "123456"
  }'
```

### Using Postman:
```json
{
  "email": "your-test-email@gmail.com",
  "otp": "123456"
}
```

**Expected Response:**
```json
{
  "message": "Email verified successfully! You can now login."
}
```

---

## Step 5: Login

### Using cURL:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "your-test-email@gmail.com",
    "password": "Test@123456"
  }'
```

### Using Postman:
```json
{
  "email": "your-test-email@gmail.com",
  "password": "Test@123456"
}
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiQ1VTVE9NRVIiLCJzdWIiOiJ5b3VyLXRlc3QtZW1haWxAZ21haWwuY29tIiwiaWF0IjoxNzI4MDM2NjY2LCJleHAiOjE3MjgxMjMwNjZ9.xyz...",
  "email": "your-test-email@gmail.com",
  "role": "CUSTOMER",
  "message": "Login successful"
}
```

**Save the token!** You'll need it for authenticated API calls.

---

## Step 6: Test Protected Endpoints (Future)

When calling protected endpoints, include the JWT token:

```bash
curl -X GET http://localhost:8080/api/customers/profile \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

---

## Common Issues & Solutions

### Issue 1: Email Not Sending
**Error:** "Failed to send OTP email"

**Solutions:**
- Verify Gmail credentials are correct
- Ensure 2FA is enabled on Gmail
- Use App Password, not regular password
- Check if "Less secure app access" is enabled (if not using App Password)
- Try with a different email provider (e.g., Outlook, SendGrid)

### Issue 2: OTP Expired
**Error:** "OTP has expired. Please request a new one."

**Solution:**
```bash
curl -X POST "http://localhost:8080/api/auth/resend-otp?email=your-test-email@gmail.com"
```

### Issue 3: Email Already Registered
**Error:** "Email already registered"

**Solution:**
- Use a different email
- Or delete the user from database and try again

### Issue 4: Cannot Login Before Verification
**Error:** "Email not verified. Please verify your email first."

**Solution:**
- Complete OTP verification first
- Or resend OTP and verify

### Issue 5: Invalid Password Format
**Error:** Validation errors

**Solution:**
Password must have:
- Minimum 8 characters
- At least 1 digit
- At least 1 lowercase letter
- At least 1 uppercase letter
- At least 1 special character (@#$%^&+=)

Example valid passwords:
- `Test@123456`
- `SecurePass#99`
- `MyP@ssw0rd`

---

## Database Verification

Check if user was created:

```sql
USE aml_new;

-- View all users
SELECT * FROM users;

-- View all customers
SELECT * FROM customers;

-- Check specific user
SELECT u.user_id, u.email, u.email_verified, u.status, 
       c.first_name, c.last_name, c.contact_number
FROM users u
JOIN customers c ON u.user_id = c.user_id
WHERE u.email = 'your-test-email@gmail.com';
```

---

## Testing Checklist

- [ ] Application starts without errors
- [ ] Registration endpoint works
- [ ] OTP email received
- [ ] OTP verification works
- [ ] Welcome email received
- [ ] Login works and returns JWT token
- [ ] Cannot login before email verification
- [ ] Cannot login with wrong password
- [ ] OTP expires after 10 minutes
- [ ] Resend OTP works

---

## Next Steps After Testing

1. **Integrate with Frontend**
   - Store JWT token in localStorage/sessionStorage
   - Add Authorization header to all API calls
   - Handle token expiration

2. **Add More Features**
   - Password reset functionality
   - Profile update endpoints
   - Change password endpoint
   - Account deactivation

3. **Production Deployment**
   - Use environment variables for sensitive data
   - Configure production email service (SendGrid, AWS SES)
   - Enable HTTPS
   - Add rate limiting
   - Implement logging and monitoring

---

## Support

For detailed API documentation, see: `AUTH_API_DOCUMENTATION.md`

For implementation details, see: `IMPLEMENTATION_SUMMARY.md`
