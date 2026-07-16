package main.java.com.lldtop16.atm;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ATMSystemMyImpl {

/*
    Think we are building ATM for transactions so what all different things
     we need I am adding one by one

*/

  /*  1. Card - Authentication Entity
    Attributes:

    cardNumber (String) - Unique card identifier
    pin (int) - 4-digit PIN
    bankName (String) - Issuing bank
    expiryDate (LocalDate) - Card validity
    type (CardType) - DEBIT, CREDIT

    Methods:

    validatePin(int pin) - Verify PIN
    isExpired() - Check card validity
    getCardNumber() - Get card details*/



    enum CardType {
        DEBIT,
        CREDIT
    }



    static class Card{
        String cardNumber;
        CardType cardType;
        int pin;
        String bankName;
        LocalDate expiryDate;

        public Card(String cardNumber, CardType cardType, int pin, String bankName, LocalDate expiryDate) {
            this.cardNumber = cardNumber;
            this.cardType = cardType;
            this.pin = pin;
            this.bankName = bankName;
            this.expiryDate = expiryDate;
        }


        public String getCardNumber() {
            return cardNumber;
        }

        public boolean validatePin(int pin) {
            return this.pin == pin;
        }

        public String getBankName() {
            return bankName;
        }
        public LocalDate getExpiryDate() {
            return expiryDate;
        }


    }


   /*
   2. Account - Financial Entity

    Attributes:

    accountNumber (int) - Unique account ID
    balance (double) - Available balance
    accountHolderName (String) - Owner name
    type (AccountType) - SAVINGS, CURRENT
    transactions (List) - Transaction history
    Methods:

    withdraw(double amount) - Deduct money
    deposit(double amount) - Add money
    getBalance() - Check balance
    addTransaction(Transaction) - Record

    */

    enum AccountType {
        SAVINGS,
        CURRENT
    }


    static class Account{
        String accountNumber;
        AccountType accountType;
        String AccountHolderName;
        double balance;
        List<Transaction> transactions;

        public Account(String accountNumber, AccountType accountType, String AccountHolderName, double balance, List<Transaction> transactions) {
            this.accountNumber = accountNumber;
            this.accountType = accountType;
            this.AccountHolderName = AccountHolderName;
            this.balance = balance;
            this.transactions = transactions;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public AccountType getAccountType() {
            return accountType;
        }

        public String getAccountHolderName() {
            return AccountHolderName;
        }

        public double getBalance() {
            return balance;
        }



/*
        Now Add what all transactions we can add
        In a account we can deposit , withdraw , check. balance, check transaction
        So Lets add transaction class details in downside and then will add here
*/


        public boolean withdraw(double amount) {
            if(balance >= amount) {
                balance -= amount;
                return true;
            }

            return false;

        }

        public boolean deposit(double amount) {
            balance += amount;
            return true;
        }

    }

    static class Transaction{
        String transactionId;
        AccountType accountType;
        LocalDate transactionDate;
        boolean status;
        double amount;

        public Transaction(String transactionId, AccountType accountType, LocalDate transactionDate, boolean status, double amount) {
            this.accountType = accountType;
            this.transactionId = transactionId;
            this.transactionDate = transactionDate;
            this.status = status;
            this.amount = amount;

        }


        public String getTransactionId() {
            return transactionId;
        }

    }

/*
    3. CashDispenser - Hardware Component
    Attributes:

    cashNotes (Map<Integer, Integer>) - Denominations and counts
    totalCash (int) - Total available cash
    Methods:

    dispense(int amount) - Dispense cash using greedy algorithm
    refillCash(Map<Integer, Integer>) - Add cash
    getAvailableBalance() - Check ATM cash
    Algorithm: Uses highest denominations first (500, 200, 100)

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
        void insertCard(ATMSystemMyImpl.ATM atm, ATMSystemMyImpl.Card card);
        void enterPin(ATMSystemMyImpl.ATM atm, int pin);
        void withdraw(ATMSystemMyImpl.ATM atm, double amount);
    }

    static class ATM {
        ATMSystemMyImpl.ATMState state;
        ATMSystemMyImpl.Card card;
        ATMSystemMyImpl.Account account;
        ATMSystemMyImpl.CashDispenser dispenser;
        ATM(ATMSystemMyImpl.Account account) {
            this.account = account;
            dispenser = new ATMSystemMyImpl.CashDispenser();
            state = new ATMSystemMyImpl.IdleState();
        }
        void insertCard(ATMSystemMyImpl.Card card) {
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
   IDLE STATE
   ========================================================
   */
    static class IdleState implements ATMSystemMyImpl.ATMState {
        public void insertCard(ATMSystemMyImpl.ATM atm, ATMSystemMyImpl.Card card) {
            atm.card = card;
            atm.state = new ATMSystemMyImpl.HasCardState();
            System.out.println("Card inserted");
        }
        public void enterPin(ATMSystemMyImpl.ATM atm, int pin) {

        }
        public void withdraw(ATMSystemMyImpl.ATM atm, double amount) {
        }
    }

    /*
    ========================================================
    HAS CARD STATE
    ========================================================
    */
    static class HasCardState implements ATMSystemMyImpl.ATMState {
        public void insertCard(ATMSystemMyImpl.ATM atm, ATMSystemMyImpl.Card card) {
        }
        public void enterPin(ATMSystemMyImpl.ATM atm, int pin) {
            if (atm.card.pin == pin) {
                atm.state = new ATMSystemMyImpl.AuthenticatedState();
                System.out.println("PIN verified");
            } else {
                System.out.println("Invalid PIN");
            }
        }
        public void withdraw(ATMSystemMyImpl.ATM atm, double amount) {
        }
    }


    /*
   ========================================================
   AUTHENTICATED STATE
   ========================================================
   */
    static class AuthenticatedState implements ATMSystemMyImpl.ATMState {
        public void insertCard(ATMSystemMyImpl.ATM atm, ATMSystemMyImpl.Card card) {
        }
        public void enterPin(ATMSystemMyImpl.ATM atm, int pin) {
        }
        public void withdraw(ATMSystemMyImpl.ATM atm, double amount) {
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
    MAIN
    ========================================================
    */
    public static void main(String[] args) {
        ATMSystem.Account account = new ATMSystem.Account(101, 2000);
        ATMSystem.Card card = new ATMSystem.Card("12345", 1234);
        ATMSystem.ATM atm = new ATMSystem.ATM(account);
        atm.insertCard(card);
        atm.enterPin(1234);
        atm.withdraw(500);
    }









}
