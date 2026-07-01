# 🏦 Problem 37: Banking System

> **Difficulty**: ⭐⭐⭐ | **Company Fit**: Any bank, fintech  
> **Est. Time**: 120 min | **Patterns**: Transaction, Account, Observer

---

## 🧠 The Intuition Journey

### Step 1: What is this problem really asking?

"Design a simple banking system."

**What the interviewer tests**:
```
1. Can you handle concurrent transactions? (Thread-safe)
2. Can you ensure data consistency? (ACID)
3. Can you prevent race conditions? (Double withdrawal)
4. Can you handle different account types? (Savings, current, fixed deposit)
```

### Step 2: The "Aha!" Moment

The key insight: **Transactions must be atomic and isolated.**

```
SCENARIO: Alice and Bob share a joint account ($1000)
  Thread 1: Alice withdraws $600
  Thread 2: Bob withdraws $600
  
Without locking:
  T1: balance = 1000 - 600 = 400 ✓
  T2: balance = 1000 - 600 = 400 ✓  (should fail, only $400 left!)
  Final balance: $400 (should be $400, but both succeeded!)

With locking (synchronized):
  T1: balance = 1000 - 600 = 400 ✓
  T2: balance = 400 - 600 = -200 ✗ (insufficient funds)
  Final balance: $400 ✓
```

### Step 3: How to handle different account types?

```
ACCOUNT TYPES:
  SAVINGS:    Min balance $100, max withdrawal $10K/day, interest 4%
  CURRENT:    No min balance, no withdrawal limit, no interest
  FIXED DEPOSIT: Locked for N years, penalty for early withdrawal

Each type has different RULES for withdrawal/interest.
Use Strategy pattern for rules.
```

---

## 💻 Core Implementation

```java
package com.bank;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * INTUITION: BankService manages accounts and transactions.
 * 
 * Thread-safety is CRITICAL:
 * - Multiple concurrent withdrawals from same account
 * - Atomic transfers between accounts
 * - Consistent balance reads
 */
public class BankService {
    
    private final Map<String, Account> accounts;
    private final TransactionLedger ledger;
    private final ReentrantLock bankLock = new ReentrantLock();

    public BankService() {
        this.accounts = new ConcurrentHashMap<>();
        this.ledger = new TransactionLedger();
    }

    /**
     * INTUITION: Create account.
     */
    public synchronized Account createAccount(String userId, String accountType, 
                                               double initialDeposit) {
        Account account = AccountFactory.createAccount(userId, accountType, initialDeposit);
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    /**
     * INTUITION: Deposit money.
     * 
     * 1. Lock account
     * 2. Add amount to balance
     * 3. Record transaction
     * 4. Unlock
     */
    public void deposit(String accountNumber, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        
        Account account = accounts.get(accountNumber);
        if (account == null) throw new AccountNotFoundException(accountNumber);
        
        bankLock.lock();
        try {
            account.deposit(amount);
            ledger.record(new Transaction(accountNumber, TransactionType.DEPOSIT, amount));
        } finally {
            bankLock.unlock();
        }
    }

    /**
     * INTUITION: Withdraw money.
     * 
     * 1. Lock account
     * 2. Check if sufficient balance
     * 3. Subtract amount
     * 4. Record transaction
     * 5. Unlock
     */
    public void withdraw(String accountNumber, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        
        Account account = accounts.get(accountNumber);
        if (account == null) throw new AccountNotFoundException(accountNumber);
        
        bankLock.lock();
        try {
            // Check withdrawal rules (min balance, daily limit, etc.)
            if (!account.canWithdraw(amount)) {
                throw new InsufficientFundsException("Cannot withdraw: " + amount);
            }
            
            account.withdraw(amount);
            ledger.record(new Transaction(accountNumber, TransactionType.WITHDRAWAL, amount));
        } finally {
            bankLock.unlock();
        }
    }

    /**
     * INTUITION: Transfer between accounts.
     * 
     * Critical section: must lock BOTH accounts to prevent deadlock.
     * Use consistent lock ordering (lock smaller account number first).
     */
    public void transfer(String fromAccount, String toAccount, double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
        if (fromAccount.equals(toAccount)) throw new IllegalArgumentException("Same account");
        
        Account sender = accounts.get(fromAccount);
        Account receiver = accounts.get(toAccount);
        
        if (sender == null || receiver == null) {
            throw new AccountNotFoundException("Account not found");
        }
        
        // Prevent deadlock: lock in consistent order (sort by account number)
        String firstLock = fromAccount.compareTo(toAccount) < 0 ? fromAccount : toAccount;
        String secondLock = fromAccount.compareTo(toAccount) < 0 ? toAccount : fromAccount;
        
        bankLock.lock();
        try {
            // Lock first account
            ReentrantLock lock1 = sender.getLock();
            lock1.lock();
            try {
                // Lock second account
                ReentrantLock lock2 = receiver.getLock();
                lock2.lock();
                try {
                    // Check sender balance
                    if (sender.getBalance() < amount) {
                        throw new InsufficientFundsException("Insufficient balance");
                    }
                    
                    // Execute transfer
                    sender.withdraw(amount);
                    receiver.deposit(amount);
                    
                    // Record both sides
                    ledger.record(new Transaction(fromAccount, TransactionType.TRANSFER_OUT, amount));
                    ledger.record(new Transaction(toAccount, TransactionType.TRANSFER_IN, amount));
                    
                } finally {
                    lock2.unlock();
                }
            } finally {
                lock1.unlock();
            }
        } finally {
            bankLock.unlock();
        }
    }

    /**
     * INTUITION: Get account balance (read-only, no lock needed if we use atomic reads).
     */
    public double getBalance(String accountNumber) {
        Account account = accounts.get(accountNumber);
        return account != null ? account.getBalance() : -1;
    }

    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return ledger.getTransactions(accountNumber);
    }
}
```

```java
package com.bank;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * INTUITION: Account is the core entity.
 * 
 * Each account has a lock for thread-safe operations.
 */
public abstract class Account {
    protected final String accountNumber;
    protected final String userId;
    protected double balance;
    protected final AccountType type;
    protected final ReentrantLock lock = new ReentrantLock();
    protected final List<Transaction> transactions;
    protected final LocalDateTime createdAt;

    public Account(String accountNumber, String userId, AccountType type, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.userId = userId;
        this.type = type;
        this.balance = initialDeposit;
        this.transactions = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public void deposit(double amount) {
        this.balance += amount;
    }

    public void withdraw(double amount) {
        this.balance -= amount;
    }

    /**
     * Check if withdrawal is allowed.
     * Rules differ by account type.
     */
    public abstract boolean canWithdraw(double amount);

    public ReentrantLock getLock() {
        return lock;
    }

    // Getters
    public String getAccountNumber() { return accountNumber; }
    public String getUserId() { return userId; }
    public double getBalance() { return balance; }
    public AccountType getType() { return type; }
}

enum AccountType {
    SAVINGS(100, 10000, 4.0),      // min balance, daily limit, interest rate
    CURRENT(0, -1, 0.0),           // no min, no limit, no interest
    FD(1000, -1, 6.5);             // high min, locked, high interest

    private final double minBalance;
    private final double dailyWithdrawalLimit;
    private final double interestRate;

    AccountType(double minBalance, double dailyWithdrawalLimit, double interestRate) {
        this.minBalance = minBalance;
        this.dailyWithdrawalLimit = dailyWithdrawalLimit;
        this.interestRate = interestRate;
    }

    public double getMinBalance() { return minBalance; }
    public double getDailyWithdrawalLimit() { return dailyWithdrawalLimit; }
    public double getInterestRate() { return interestRate; }
}
```

```java
package com.bank;

import java.time.LocalDateTime;

/**
 * INTUITION: SavingsAccount has withdrawal rules.
 */
class SavingsAccount extends Account {
    private double dailyWithdrawn;
    private LocalDateTime lastWithdrawalDate;

    public SavingsAccount(String accountNumber, String userId, double initialDeposit) {
        super(accountNumber, userId, AccountType.SAVINGS, initialDeposit);
        this.dailyWithdrawn = 0;
        this.lastWithdrawalDate = LocalDateTime.now();
    }

    @Override
    public boolean canWithdraw(double amount) {
        // Rule 1: Min balance after withdrawal
        if (balance - amount < AccountType.SAVINGS.getMinBalance()) {
            return false;
        }
        
        // Rule 2: Daily limit
        if (!LocalDateTime.now().toLocalDate().equals(lastWithdrawalDate.toLocalDate())) {
            dailyWithdrawn = 0;  // Reset on new day
        }
        
        if (AccountType.SAVINGS.getDailyWithdrawalLimit() > 0 && 
            dailyWithdrawn + amount > AccountType.SAVINGS.getDailyWithdrawalLimit()) {
            return false;
        }
        
        dailyWithdrawn += amount;
        lastWithdrawalDate = LocalDateTime.now();
        return true;
    }
}

/**
 * Current account: no restrictions.
 */
class CurrentAccount extends Account {
    public CurrentAccount(String accountNumber, String userId, double initialDeposit) {
        super(accountNumber, userId, AccountType.CURRENT, initialDeposit);
    }

    @Override
    public boolean canWithdraw(double amount) {
        return balance >= amount;  // Just check sufficient balance
    }
}

/**
 * Fixed Deposit: locked for term, penalty for early withdrawal.
 */
class FixedDepositAccount extends Account {
    private final LocalDateTime maturityDate;
    private final double penaltyRate = 0.01;  // 1% penalty

    public FixedDepositAccount(String accountNumber, String userId, double initialDeposit, 
                                int tenureYears) {
        super(accountNumber, userId, AccountType.FD, initialDeposit);
        this.maturityDate = LocalDateTime.now().plusYears(tenureYears);
    }

    @Override
    public boolean canWithdraw(double amount) {
        if (balance < amount) return false;
        
        // Early withdrawal penalty
        if (LocalDateTime.now().isBefore(maturityDate)) {
            double penalty = amount * penaltyRate;
            balance -= penalty;
            System.out.println("Early withdrawal penalty: " + penalty);
        }
        
        return true;
    }

    public LocalDateTime getMaturityDate() { return maturityDate; }
}
```

```java
package com.bank;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * INTUITION: TransactionLedger records all transactions.
 * 
 * Immutable log - append only.
 */
class TransactionLedger {
    private final Map<String, List<Transaction>> accountTransactions;
    private final Map<Long, Transaction> allTransactions;
    private final AtomicLong txnCounter = new AtomicLong(0);

    TransactionLedger() {
        this.accountTransactions = new ConcurrentHashMap<>();
        this.allTransactions = new ConcurrentHashMap<>();
    }

    void record(Transaction txn) {
        long id = txnCounter.incrementAndGet();
        txn.setId(id);
        
        allTransactions.put(id, txn);
        accountTransactions.computeIfAbsent(txn.getAccountNumber(), k -> new CopyOnWriteArrayList<>())
                          .add(txn);
    }

    List<Transaction> getTransactions(String accountNumber) {
        return accountTransactions.getOrDefault(accountNumber, Collections.emptyList());
    }

    public Transaction getTransaction(long id) {
        return allTransactions.get(id);
    }
}

class Transaction {
    private long id;
    private final String accountNumber;
    private final TransactionType type;
    private final double amount;
    private final double balanceAfter;
    private final LocalDateTime timestamp;
    private String description;

    Transaction(String accountNumber, TransactionType type, double amount) {
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.description = "";
    }

    void setId(long id) { this.id = id; }
    void setDescription(String desc) { this.description = desc; }

    public long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

enum TransactionType {
    DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, INTEREST
}

class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String account) {
        super("Account not found: " + account);
    }
}

class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String msg) {
        super(msg);
    }
}

/**
 * Factory for creating different account types.
 */
class AccountFactory {
    static Account createAccount(String userId, String accountType, double initialDeposit) {
        String accountNumber = generateAccountNumber();
        
        switch (accountType.toUpperCase()) {
            case "SAVINGS":
                return new SavingsAccount(accountNumber, userId, initialDeposit);
            case "CURRENT":
                return new CurrentAccount(accountNumber, userId, initialDeposit);
            case "FD":
                return new FixedDepositAccount(accountNumber, userId, initialDeposit, 5);
            default:
                throw new IllegalArgumentException("Unknown account type: " + accountType);
        }
    }

    private static String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis();
    }
}
```

---

## ❓ Follow-up Questions

### Q1: "How to handle interest calculation?"
> "Batch job runs nightly. Calculate interest = balance × rate / 365. Credit to account. Use compound interest formula."

### Q2: "How to prevent money laundering?"
> "Transaction monitoring: flag > $10K. Velocity checks: 10 transactions in 1 hour. KYC verification. Report to authorities."

### Q3: "How to handle refunds?"
> "Reverse transaction: create negative transaction. Linked to original. Time window: 30 days. Approval workflow for large refunds."

### Q4: "How to support loans/credit?"
> "Separate Loan entity. Credit score check. EMI calculation. Auto-debit on due date. Penalty for late payment."