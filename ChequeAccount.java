public class ChequeAccount extends Account implements Withdrawable {
    private double overdraftLimit;

    public ChequeAccount(String branch, double initialDeposit, double overdraftLimit) {
        super(branch, initialDeposit);
        this.overdraftLimit = Math.max(0.0, overdraftLimit);
    }

    public double getOverdraftLimit() { return overdraftLimit; }
    public void setOverdraftLimit(double overdraftLimit) { this.overdraftLimit = overdraftLimit; }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) throw new IllegalArgumentException("Withdraw amount must be positive");
        if (balance - amount < -overdraftLimit) {
            throw new InsufficientFundsException("Insufficient funds (overdraft limit reached)");
        }
        balance -= amount;
    }

    @Override
    public String getDetails() {
        return String.format("ChequeAccount[%s] branch=%s balance=%.2f overdraft=%.2f",
                accountNumber, branch, balance, overdraftLimit);
    }
}