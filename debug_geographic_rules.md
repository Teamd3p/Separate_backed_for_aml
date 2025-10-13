# Geographic Rule Debugging Guide

## Issues Fixed

### 1. Enhanced Logging in Transaction Processing
- Added detailed logging in `TransactionServiceImpl.transferFunds()` to track country code resolution
- Logs show: request country code → customer.country → nationality mapping → final country code

### 2. Enhanced Logging in Geographic Rule Evaluator
- Added comprehensive logging in `GeographicRuleEvaluator.evaluate()`
- Shows: rule name, transaction country code, risky country database lookup results

## Testing Steps

### Step 1: Check Risky Countries Database
First verify what countries are in your risky countries database:

```sql
SELECT country_code, country_name, risk_level FROM risky_countries;
```

Common high-risk countries that should be there:
- AF (Afghanistan)
- IR (Iran) 
- KP (North Korea)
- SY (Syria)
- PK (Pakistan)

### Step 2: Test Transaction with High-Risk Country

#### Option A: Via API Request with Country Code
```json
POST /api/transactions/transfer
{
    "senderAccountNumber": "ACC001",
    "receiverAccountNumber": "ACC002", 
    "amount": 1000.00,
    "description": "Test high-risk transfer",
    "countryCode": "AF"
}
```

#### Option B: Via Customer Nationality
If customer has nationality "Afghan", it should map to country code "AF"

### Step 3: Check Logs
Look for these log patterns:

**Country Code Resolution:**
```
🌍 Country code from request: AF
🌍 Final country code set for transaction: AF
```

**Geographic Rule Evaluation:**
```
🌍 GEOGRAPHIC RULE EVALUATION - Rule: High Risk Country Check
🌍 Transaction country code: 'AF'
🌍 Checking if country 'AF' exists in risky countries database
⚠️ GEOGRAPHIC RULE TRIGGERED: High Risk Country Check | country=AF | riskLevel=HIGH
```

**Rule Engine Summary:**
```
🚨 RULE TRIGGERED: High Risk Country Check | Risk Impact: 80 | Running Total: 80
Transaction Action: FLAG (or BLOCK if score >= 90)
```

## Common Issues & Solutions

### Issue 1: Country Code is NULL
**Symptoms:** Logs show `Transaction country code: 'null'`
**Solutions:**
- Pass `countryCode` in API request
- Ensure customer has `country` field populated
- Verify customer has valid `nationality` that maps to country code

### Issue 2: Country Not in Risky Database
**Symptoms:** Logs show `Country 'XX' not found in risky countries database`
**Solutions:**
- Add the country to risky_countries table
- Verify country code format (2-letter ISO codes like "AF", "IR")

### Issue 3: Geographic Rule Not Active
**Symptoms:** No geographic rule evaluation logs
**Solutions:**
- Check if GEOGRAPHIC rule exists and is active in rules table
- Verify GeographicRuleEvaluator is registered as a Spring component

### Issue 4: Risk Score Too Low
**Symptoms:** Rule triggers but transaction still approved
**Solutions:**
- Check rule's `risk_score_impact` value (should be >= 60 for flagging)
- Verify FLAG_THRESHOLD (60) and BLOCK_THRESHOLD (90) in RuleEngineServiceImpl

## Sample Database Setup

```sql
-- Add high-risk countries
INSERT INTO risky_countries (country_code, country_name, risk_level) VALUES
('AF', 'Afghanistan', 'HIGH'),
('IR', 'Iran', 'HIGH'),
('KP', 'North Korea', 'HIGH'),
('SY', 'Syria', 'HIGH'),
('PK', 'Pakistan', 'MEDIUM');

-- Ensure geographic rule exists
INSERT INTO rules (name, type, description, conditions, risk_score_impact, is_active) VALUES
('High Risk Country Check', 'GEOGRAPHIC', 'Flag transactions from high-risk countries', '{}', 80, true);
```

## Expected Behavior

1. **High-Risk Country (AF, IR, KP, SY):** Should FLAG (score 80) or BLOCK if combined with other rules
2. **Medium-Risk Country (PK):** Should add moderate risk score
3. **Low-Risk Country (US, GB, CA):** Should not trigger geographic rules
4. **No Country Code:** Geographic rule should not evaluate (logged as warning)
