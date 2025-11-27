package Model;

public interface Withdrawable {
    void withdraw(double amount) throws InsufficientFundsException;
}