
public class InvestmentAccount extends Account implements Interest, Withdrawable {
    private double interestRate;
    private String maturityDate;

    public InvestmentAccount(String branch, double initialDeposit, double interestRate, String maturityDate) {
        super(branch, initialDeposit);
        this.interestRate = interestRate;
        this.maturityDate = maturityDate;
    }

    public double getInterestRate() { return interestRate; }
    public String getMaturityDate() { return maturityDate; }

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



}
