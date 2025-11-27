package Model;

public class InvestmentAccount extends Account implements Interest, Withdrawable {
    private double interestRate;
    private String maturityDate;

    // ye ke ya go laoder existing accout from the Dao
    public InvestmentAccount(String accountNumber, String branch, double initialDeposit, double interestRate, String maturityDate) {
        super(accountNumber, branch, initialDeposit);
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
    }
    //new account
    public InvestmentAccount(String branch, double initialDeposit, double interestRate, String maturityDate) {
        super(branch, initialDeposit); // UUID generated automatically
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
    }


    public double getInterestRate() { return interestRate; }
    public String getMaturityDate() { return maturityDate; }

    @Override
    public String getAccountType() {
        return "Investment";
    }

    @Override
    public void applyInterest() {
        if (interestRate > 0) {
            balance += balance * interestRate;
        }
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive");
        if (amount > balance) throw new InsufficientFundsException("Insufficient funds");
        balance -= amount;
    }

    @Override
    public String getDetails() {
        return String.format("InvestmentAccount[%s] branch=%s balance=%.2f rate=%.2f",
                accountNumber, branch, balance, interestRate);
    }







}
