package main.java.com.lldtop16.atm;

import java.util.*;
public class ATMSystem {
    /*
    ========================================================
    CARD
    ========================================================
    */

    static class Card {
        String cardNumber;
        int pin;
        Card(String cardNumber, int pin) {
            this.cardNumber = cardNumber;
            this.pin = pin;
        }
    }
    /*
    ========================================================
    ACCOUNT
    ========================================================
    */
    static class Account {
        int accountNumber;
        double balance;
        Account(int accountNumber, double balance) {
            this.accountNumber = accountNumber;
            this.balance = balance;
        }
        public boolean withdraw(double amount) {
            if (balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        }
    }
    /*
    ========================================================
    TRANSACTION
    Stores operation details
    ========================================================
    */
    static class Transaction {
        String type;
        double amount;
        Transaction(String type, double amount) {
            this.type = type;
            this.amount = amount;
        }
    }
    /*
    ========================================================
    CASH DISPENSER
    Responsible for cash
    ========================================================
    */
    static class CashDispenser {
        Map<Integer, Integer> cash;
        CashDispenser() {
            cash = new HashMap<>();
            cash.put(500, 10);
            cash.put(100, 20);
        }
        public boolean dispense(int amount) {
            int remaining = amount;
            List<Integer> notes = List.of(500, 100);
            for (int note : notes) {
                int available = cash.get(note);
                int required = Math.min(remaining / note, available);
                remaining -= required * note;
            }
            if (remaining != 0) {
                return false;
            }
            System.out.println("Cash dispensed : " + amount);
            return true;
        }
    }
    /*
    ========================================================
    ATM STATE
    State Pattern
    ========================================================
    */
    interface ATMState {
        void insertCard(ATM atm, Card card);

        void enterPin(ATM atm, int pin);
        void withdraw(ATM atm, double amount);
    }
    /*
    ========================================================
    IDLE STATE
    ========================================================
    */
    static class IdleState implements ATMState {
        public void insertCard(ATM atm, Card card) {
            atm.card = card;
            atm.state = new HasCardState();
            System.out.println("Card inserted");
        }
        public void enterPin(ATM atm, int pin) {

        }
        public void withdraw(ATM atm, double amount) {
        }
    }
    /*
    ========================================================
    HAS CARD STATE
    ========================================================
    */
    static class HasCardState implements ATMState {
        public void insertCard(ATM atm, Card card) {
        }
        public void enterPin(ATM atm, int pin) {
            if (atm.card.pin == pin) {
                atm.state = new AuthenticatedState();
                System.out.println("PIN verified");
            } else {
                System.out.println("Invalid PIN");
            }
        }
        public void withdraw(ATM atm, double amount) {
        }
    }
    /*
    ========================================================
    AUTHENTICATED STATE
    ========================================================
    */
    static class AuthenticatedState implements ATMState {
        public void insertCard(ATM atm, Card card) {
        }
        public void enterPin(ATM atm, int pin) {
        }
        public void withdraw(ATM atm, double amount) {
            if (atm.account.withdraw(amount)) {
                atm.dispenser.dispense((int) amount);
                System.out.println("Remaining balance : " + atm.account.balance);
            } else {
                System.out.println("Insufficient balance");
            }
        }
    }
    /*
    ========================================================
    ATM
    Main controller
    ========================================================
    */
    static class ATM {
        ATMState state;
        Card card;
        Account account;
        CashDispenser dispenser;
        ATM(Account account) {
            this.account = account;
            dispenser = new CashDispenser();
            state = new IdleState();
        }
        void insertCard(Card card) {
            state.insertCard(this, card);
        }
        void enterPin(int pin) {
            state.enterPin(this, pin);
        }
        void withdraw(double amount) {
            state.withdraw(this, amount);
        }
    }
    /*
    ========================================================
    MAIN
    ========================================================
    */
    public static void main(String[] args) {
        Account account = new Account(101, 2000);
        Card card = new Card("12345", 1234);
        ATM atm = new ATM(account);
        atm.insertCard(card);
        atm.enterPin(1234);
        atm.withdraw(500);
    }
}
//```java id="atm_system_lld"
/*
============================================================
ATM SYSTEM
============================================================
PROBLEM STATEMENT
============================================================
Design an ATM system.
The ATM should support:
- Insert card
- Validate PIN
- Check balance
- Withdraw money
- Deposit money
- Complete transaction
- Eject card
Example:
User inserts card
        |
Enter PIN
        |
Validate
        |
Withdraw 500
        |
ATM dispenses cash
============================================================
CORE ENTITIES
============================================================
ATM
        |
        |
        +----------------+
        |                |
      Card            Account
        |
        |
   Transaction
============================================================
DESIGN DECISIONS
============================================================
1. STATE DESIGN PATTERN
ATM behavior changes based on current state.
Example:
No Card:
Waiting for card
Card Inserted:
Waiting for PIN
Authenticated:
Allow operations
Dispensing Cash:
Giving money
Instead of:
if(cardInserted)
else if(pinVerified)
Use:
ATMState interface
        |
        |
-----------------------------
IdleState
HasCardState
AuthenticatedState
DispenseState
============================================================
2. SEPARATION OF RESPONSIBILITY
ATM:
Controls workflow
Account:
Stores money
Card:
Stores card details
CashDispenser:
Handles notes
============================================================
FLOW
============================================================
ATM Idle
        |
Insert Card
        |
Validate PIN
        |
Select Operation
        |
Withdraw
        |
Check Balance
        |
Dispense Cash
        |
Eject Card
============================================================
WITHDRAW FLOW
============================================================
Request:
500
Account balance:
1000
Check:
Enough balance?
        |
Check ATM cash
        |
Deduct account
        |
Dispense notes
============================================================
TIME COMPLEXITY
============================================================
PIN validation:
O(1)
Balance check:
O(1)
Withdrawal:
O(number of denominations)
Example:
100,500 notes
Small constant.
============================================================
SPACE COMPLEXITY
============================================================
O(accounts + cards)
============================================================
INTERVIEW FOLLOW UPS
============================================================
1. Multiple banks
Add:
BankService
------------------------------------------------------------
2. Multiple currencies
Add:
Currency enum
------------------------------------------------------------
3. Transaction history
Store:
Transaction table
------------------------------------------------------------
4. Concurrency
Two withdrawals together.
Use:
Database transaction
Lock account row
============================================================
*/
