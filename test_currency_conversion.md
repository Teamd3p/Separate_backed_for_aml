# Currency Conversion Test Cases

## Test Scenario 1: Cross-Currency Transfer (USD to INR)

### Setup
- Sender Account: USD currency, balance $1000
- Receiver Account: INR currency, balance ₹0
- Transfer Amount: $100 USD
- Exchange Rate: 1 USD = 83.50 INR
- Conversion Fee: 2% (min ₹10, max ₹500)

### Expected Results
1. **Currency Conversion:**
   - Original Amount: $100 USD
   - Converted Amount: ₹8,350 INR
   - Conversion Fee: ₹167 INR (2% of ₹8,350)
   - Fee in Sender Currency: $2.00 USD (₹167 ÷ 83.50)

2. **Balance Updates:**
   - Sender Balance: $1000 - $100 - $2.00 = $898.00 USD
   - Receiver Balance: ₹0 + ₹8,350 = ₹8,350 INR

3. **Transaction Record:**
   - Amount: ₹8,350 (receiver currency)
   - Currency: INR
   - Currency Exchange ID: Set to the exchange rate record used
   - Country Code: Fetched from sender's nationality or request

### API Request Example
```json
POST /api/transactions/transfer
{
    "senderAccountNumber": "ACC001USD",
    "receiverAccountNumber": "ACC002INR", 
    "amount": 100.00,
    "description": "Cross-currency transfer test",
    "countryCode": "US"
}
```

## Test Scenario 2: Same Currency Transfer (INR to INR)

### Setup
- Sender Account: INR currency, balance ₹10,000
- Receiver Account: INR currency, balance ₹5,000
- Transfer Amount: ₹2,000 INR

### Expected Results
1. **No Currency Conversion:**
   - Original Amount: ₹2,000 INR
   - Converted Amount: ₹2,000 INR
   - Conversion Fee: ₹0
   - No currency exchange ID needed

2. **Balance Updates:**
   - Sender Balance: ₹10,000 - ₹2,000 = ₹8,000 INR
   - Receiver Balance: ₹5,000 + ₹2,000 = ₹7,000 INR

3. **Transaction Record:**
   - Amount: ₹2,000
   - Currency: INR
   - Currency Exchange ID: null (same currency)
   - Country Code: Fetched from sender's nationality or request

## Test Scenario 3: Deposit with Country Code

### Setup
- Account: USD currency, balance $500
- Deposit Amount: $200 USD
- Country Code: "IN" (from request or customer nationality)

### Expected Results
1. **No Currency Conversion:**
   - Deposit Amount: $200 USD
   - No conversion fees

2. **Balance Update:**
   - Account Balance: $500 + $200 = $700 USD

3. **Transaction Record:**
   - Amount: $200
   - Currency: USD
   - Country Code: "IN"
   - Transaction Type: CREDIT
   - Sender Account: "EXTERNAL"

## Verification Points

1. ✅ **Country Code Resolution:**
   - Request countryCode parameter takes priority
   - Falls back to customer.country field
   - Finally uses nationality-to-country mapping

2. ✅ **Currency Exchange ID Storage:**
   - Set when cross-currency conversion occurs
   - Null for same-currency transactions
   - References the CurrencyExchange entity used for conversion

3. ✅ **Fee Calculation:**
   - Percentage-based with min/max limits
   - Converted back to sender currency for deduction
   - Properly applied to sender account

4. ✅ **AML Rule Evaluation:**
   - Country code available for geographic rules
   - Transaction amounts in appropriate currencies
   - Risk scoring based on conversion and geography
