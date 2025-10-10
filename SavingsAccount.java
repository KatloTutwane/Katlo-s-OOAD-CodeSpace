public class SavingsAccount extends Account implements Interest {
    private double interestRate;

    public SavingsAccount(String branch, double initialDeposit, double interestRate) {
        super(branch, initialDeposit);
        this.interestRate = interestRate;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }



    @Override
    public void applyInterest() { // Fixed: Capital 'I' to match interface
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
}