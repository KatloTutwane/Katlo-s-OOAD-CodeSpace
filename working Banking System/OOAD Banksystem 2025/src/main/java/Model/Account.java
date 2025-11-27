package Model;

import java.util.UUID;

public abstract class Account {
    protected final String accountNumber;
    protected double balance;
    protected final String branch;

    //contruct e tsile go thusa go displayer exsisting account ko table
    public Account(String accountNumber, String branch, double initialDeposit) {
        this.accountNumber = accountNumber;
        this.branch = branch;
        this.balance = Math.max(0.0, initialDeposit);
    }

    public Account(String branch, double initialDeposit) {
        this.accountNumber = UUID.randomUUID().toString();
        this.branch = branch;
        this.balance = Math.max(0.0, initialDeposit);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public String getBranch() {
        return branch;
    }

    public abstract String getAccountType();


    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be positive");
        balance += amount;
    }


    public String getDetails() {
        return String.format("Account[%s] branch=%s balance=%.2f", accountNumber, branch, balance);
    }

    @Override
    public String toString() {
        return getAccountType() + " (P" + String.format("%.2f", balance) + ")";
    }
}

