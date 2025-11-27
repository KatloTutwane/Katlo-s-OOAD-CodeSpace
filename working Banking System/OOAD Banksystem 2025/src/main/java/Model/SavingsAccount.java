package Model;

public class SavingsAccount extends Account implements Interest,Withdrawable {
    private double interestRate;


    public SavingsAccount(String accountNumber, String branch, double initialDeposit, double interestRate) {
        super(accountNumber, branch, initialDeposit);
        this.interestRate = interestRate;
    }

    //ye ke ya di new account
    public SavingsAccount(String branch, double initialDeposit, double interestRate) {
        super(branch, initialDeposit); // UUID generated automatically
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }


    @Override
    public String getAccountType() {
        return "Savings";
    }


    @Override
    public void applyInterest() {
        if (interestRate > 0) {
            double interest = balance * interestRate;
            balance += interest;
        }
    }

    @Override
    public String getDetails() {
        return String.format("SavingsAccount[%s] branch=%s balance=%.2f rate=%.2f",
                accountNumber, branch, balance, interestRate);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (balance < amount) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        balance -= amount;
    }
}