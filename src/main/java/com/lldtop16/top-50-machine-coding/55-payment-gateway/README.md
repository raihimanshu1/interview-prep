# 💳 Problem 55: Payment Gateway (Like Stripe/PayPal)

> **Difficulty**: ⭐⭐⭐⭐ | **Company Fit**: Fintech, payment companies  
> **Est. Time**: 120 min | **Patterns**: Chain of Responsibility, State, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a payment gateway for processing transactions."

**What the interviewer tests**:
```
1. Can you handle payment methods? (Card, UPI, Wallet)
2. Can you ensure security? (Encryption, tokenization)
3. Can you handle failures? (Retry, refund)
4. Can you track status? (Pending, succeeded, failed)
```

### Step 2: The "Aha!" Moment

The key insight: **Payment is a state machine with idempotency.**

```
PAYMENT FLOW:
  [INITIATED] → [PROCESSING] → [AUTHORIZED] → [CAPTURED] → [SETTLED]
                    ↓
                 [FAILED] → [REFUNDED]
                 
IDEMPOTENCY:
  Same request ID = same result
  Prevents double-charging
  
  Request 1: charge $100 (id: abc123)
  Request 2: charge $100 (id: abc123) ← duplicate
  Result: Only one $100 charge
```

### Step 3: How to handle failures?

```
RETRY STRATEGY:
  - Network error: retry 3x with backoff
  - Insufficient funds: immediate failure
  - Card expired: don't retry, ask user
  
REFUND:
  Full refund: capture → refund
  Partial refund: partial capture → partial refund
  
  Time limits:
  - < 24h: instant refund
  - < 7 days: normal refund
  - > 30 days: manual refund
```

---

## 💻 Core Implementation

```java
package com.payment;

import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: PaymentGateway processes payments.
 * 
 * Ensures atomicity and idempotency.
 */
public class PaymentGateway {
    
    private final Map<String, Payment> payments;
    private final Map<String, PaymentMethod> paymentMethods;
    private final List<PaymentProcessor> processors;
    private final ScheduledExecutorService scheduler;

    public PaymentGateway() {
        this.payments = new ConcurrentHashMap<>();
        this.paymentMethods = new ConcurrentHashMap<>();
        this.processors = new ArrayList<>();
        this.scheduler = Executors.newScheduledThreadPool(5);
        
        // Register processors
        processors.add(new CardProcessor());
        processors.add(new UPIProcessor());
        processors.add(new WalletProcessor());
    }

    /**
     * INTUITION: Create payment intent.
     * 
     * 1. Validate amount
     * 2. Generate payment ID
     * 3. Store in pending state
     * 4. Return payment URL/details
     */
    public synchronized PaymentIntent createPaymentIntent(String userId, double amount, 
                                                           String currency, 
                                                           Map<String, String> metadata) {
        // Validate
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }
        
        // Create payment
        String paymentId = generatePaymentId();
        PaymentIntent intent = new PaymentIntent(paymentId, userId, amount, currency);
        intent.setStatus(PaymentStatus.PENDING);
        
        // Store
        payments.put(paymentId, intent);
        
        return intent;
    }

    /**
     * INTUITION: Process payment.
     * 
     * 1. Check idempotency (same request ID)
     * 2. Find appropriate processor
     * 3. Process payment
     * 4. Update status
     */
    public synchronized PaymentResult processPayment(String paymentId, String paymentMethodId, 
                                                      String requestId) {
        // Check idempotency
        if (processedRequests.contains(requestId)) {
            Payment payment = payments.get(paymentId);
            return new PaymentResult(payment.getStatus(), payment.getIdempotencyKey());
        }
        
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new PaymentNotFoundException(paymentId);
        }
        
        PaymentMethod method = paymentMethods.get(paymentMethodId);
        if (method == null) {
            throw new PaymentMethodNotFoundException(paymentMethodId);
        }
        
        // Find processor
        PaymentProcessor processor = findProcessor(method.getType());
        if (processor == null) {
            throw new UnsupportedPaymentMethodException(method.getType());
        }
        
        // Process
        try {
            PaymentResult result = processor.process(payment, method);
            
            // Update payment status
            payment.setStatus(result.getStatus());
            payment.setIdempotencyKey(requestId);
            
            // Mark request as processed
            processedRequests.add(requestId);
            
            // Schedule cleanup
            scheduleCleanup(paymentId);
            
            return result;
            
        } catch (PaymentException e) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            throw e;
        }
    }

    /**
     * INTUITION: Refund payment.
     */
    public synchronized RefundResult refund(String paymentId, double amount, String reason) {
        Payment payment = payments.get(paymentId);
        if (payment == null) {
            throw new PaymentNotFoundException(paymentId);
        }
        
        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new InvalidPaymentStatusException("Can only refund succeeded payments");
        }
        
        if (amount > payment.getAmount()) {
            throw new IllegalArgumentException("Refund amount exceeds payment");
        }
        
        // Create refund
        String refundId = generateRefundId();
        Refund refund = new Refund(refundId, paymentId, amount, reason);
        
        // Process refund
        PaymentProcessor processor = findProcessor(payment.getPaymentMethodType());
        RefundResult result = processor.refund(payment, refund);
        
        // Update payment
        if (result.isSuccess()) {
            payment.setStatus(PaymentStatus.REFUNDED);
        }
        
        return result;
    }

    /**
     * Find processor for payment method type.
     */
    private PaymentProcessor findProcessor(PaymentMethodType type) {
        for (PaymentProcessor processor : processors) {
            if (processor.supports(type)) {
                return processor;
            }
        }
        return null;
    }

    private String generatePaymentId() {
        return "PAY" + System.currentTimeMillis();
    }

    private String generateRefundId() {
        return "REF" + System.currentTimeMillis();
    }

    /**
     * Cleanup old payments after 30 days.
     */
    private void scheduleCleanup(String paymentId) {
        scheduler.schedule(() -> {
            Payment payment = payments.remove(paymentId);
            if (payment != null) {
                processedRequests.remove(payment.getIdempotencyKey());
            }
        }, 30, TimeUnit.DAYS);
    }

    // --- Helpers ---

    public void addPaymentMethod(String userId, PaymentMethod method) {
        paymentMethods.put(method.getId(), method);
    }

    public Payment getPayment(String paymentId) {
        return payments.get(paymentId);
    }

    private Set<String> processedRequests = ConcurrentHashMap.newKeySet();
}

/**
 * Payment intent (created but not yet processed).
 */
class PaymentIntent {
    private final String paymentId;
    private final String userId;
    private final double amount;
    private final String currency;
    private final Map<String, String> metadata;
    private PaymentStatus status;
    private final LocalDateTime createdAt;

    PaymentIntent(String paymentId, String userId, double amount, String currency) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.metadata = new HashMap<>();
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public void setMetadata(String key, String value) { metadata.put(key, value); }
}

enum PaymentStatus {
    PENDING, PROCESSING, AUTHORIZED, CAPTURED, SETTLED, FAILED, REFUNDED, CANCELLED
}
```

```java
package com.payment;

import java.time.LocalDateTime;

/**
 * Payment result.
 */
class PaymentResult {
    private final PaymentStatus status;
    private final String transactionId;
    private final String message;

    PaymentResult(PaymentStatus status, String transactionId) {
        this(status, transactionId, "");
    }

    PaymentResult(PaymentStatus status, String transactionId, String message) {
        this.status = status;
        this.transactionId = transactionId;
        this.message = message;
    }

    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public String getMessage() { return message; }
}

/**
 * Refund result.
 */
class RefundResult {
    private final boolean success;
    private final String refundId;
    private final String message;

    RefundResult(boolean success, String refundId, String message) {
        this.success = success;
        this.refundId = refundId;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getRefundId() { return refundId; }
}

/**
 * Refund.
 */
class Refund {
    private final String refundId;
    private final String paymentId;
    private final double amount;
    private final String reason;
    private final LocalDateTime createdAt;

    Refund(String refundId, String paymentId, double amount, String reason) {
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
        this.createdAt = LocalDateTime.now();
    }

    public String getRefundId() { return refundId; }
    public double getAmount() { return amount; }
}

/**
 * Payment processor interface.
 */
interface PaymentProcessor {
    boolean supports(PaymentMethodType type);
    PaymentResult process(Payment payment, PaymentMethod method) throws PaymentException;
    RefundResult refund(Payment payment, Refund refund);
}

/**
 * Card processor.
 */
class CardProcessor implements PaymentProcessor {
    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.CREDIT_CARD || type == PaymentMethodType.DEBIT_CARD;
    }

    @Override
    public PaymentResult process(Payment payment, PaymentMethod method) throws PaymentException {
        System.out.println("Processing card payment: " + payment.getAmount());
        // Call payment gateway (Stripe, etc.)
        return new PaymentResult(PaymentStatus.SUCCEEDED, "TXN" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(Payment payment, Refund refund) {
        System.out.println("Refunding card: " + refund.getAmount());
        return new RefundResult(true, refund.getRefundId(), "Refund successful");
    }
}

/**
 * UPI processor.
 */
class UPIProcessor implements PaymentProcessor {
    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.UPI;
    }

    @Override
    public PaymentResult process(Payment payment, PaymentMethod method) throws PaymentException {
        System.out.println("Processing UPI payment: " + payment.getAmount());
        return new PaymentResult(PaymentStatus.SUCCEEDED, "TXN" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(Payment payment, Refund refund) {
        System.out.println("Refunding UPI: " + refund.getAmount());
        return new RefundResult(true, refund.getRefundId(), "Refund successful");
    }
}

/**
 * Wallet processor.
 */
class WalletProcessor implements PaymentProcessor {
    @Override
    public boolean supports(PaymentMethodType type) {
        return type == PaymentMethodType.WALLET;
    }

    @Override
    public PaymentResult process(Payment payment, PaymentMethod method) throws PaymentException {
        System.out.println("Processing wallet payment: " + payment.getAmount());
        return new PaymentResult(PaymentStatus.SUCCEEDED, "TXN" + System.currentTimeMillis());
    }

    @Override
    public RefundResult refund(Payment payment, Refund refund) {
        System.out.println("Refunding wallet: " + refund.getAmount());
        return new RefundResult(true, refund.getRefundId(), "Refund successful");
    }
}

enum PaymentMethodType {
    CREDIT_CARD, DEBIT_CARD, UPI, WALLET, BANK_TRANSFER
}

class Payment {
    private final String paymentId;
    private final String userId;
    private final double amount;
    private final String currency;
    private String paymentMethodType;
    private PaymentStatus status;
    private String failureReason;
    private String idempotencyKey;

    public Payment(String paymentId, String userId, double amount, String currency) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.currency = currency;
        this.status = PaymentStatus.PENDING;
    }

    public String getPaymentId() { return paymentId; }
    public double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String reason) { this.failureReason = reason; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public void setIdempotencyKey(String key) { this.idempotencyKey = key; }
    public String getPaymentMethodType() { return paymentMethodType; }
    public void setPaymentMethodType(String type) { this.paymentMethodType = type; }
}

class PaymentMethod {
    private final String methodId;
    private final String userId;
    private final PaymentMethodType type;
    private final Map<String, String> details;  // Encrypted

    PaymentMethod(String methodId, String userId, PaymentMethodType type, 
                  Map<String, String> details) {
        this.methodId = methodId;
        this.userId = userId;
        this.type = type;
        this.details = details;
    }

    public String getId() { return methodId; }
    public PaymentMethodType getType() { return type; }
}

class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String id) {
        super("Payment not found: " + id);
    }
}

class PaymentMethodNotFoundException extends RuntimeException {
    public PaymentMethodNotFoundException(String id) {
        super("Payment method not found: " + id);
    }
}

class UnsupportedPaymentMethodException extends RuntimeException {
    public UnsupportedPaymentMethodException(String type) {
        super("Unsupported payment method: " + type);
    }
}

class PaymentException extends Exception {
    public PaymentException(String message) {
        super(message);
    }
}

class InvalidPaymentStatusException extends RuntimeException {
    public InvalidPaymentStatusException(String msg) {
        super(msg);
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle PCI compliance?"
> "Tokenization: never store card numbers. Use payment gateway tokens. TLS 1.3 for transmission."

### Q2: "How to handle 3D Secure (SCA)?"
> "Redirect to issuer. Challenge if needed. Callback with status. Fraud detection."

### Q3: "How to handle subscriptions?"
> "Recurring billing: save payment method. Charge on schedule. Retry failed charges."

### Q4: "How to prevent fraud?"
> "Velocity checks. Device fingerprinting. ML fraud detection. OTP for high-risk."