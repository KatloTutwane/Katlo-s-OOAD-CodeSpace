package dao;

import Model.*;
import java.util.List;
import java.util.Optional;

public interface AccountDAO {
    // Core account operations
    Account openSavingsAccount(String customerId, String branch, double initialDeposit) throws Exception;
    Account openChequeAccount(String customerId, String branch, double initialDeposit, double overdraftLimit) throws Exception;
    Account openInvestmentAccount(String customerId, String branch, double initialDeposit, String maturityDate) throws Exception;

    // Customer operations
    List<Account> getCustomerAccounts(String customerId) throws Exception;

    // Transaction operations
    void deposit(String accountNumber, double amount) throws Exception;
    void withdraw(String accountNumber, double amount) throws Exception;

    // Interest operations
    void applyMonthlyInterest() throws Exception;
    void applyInterestToAccount(String accountNumber) throws Exception;

    // Utility methods
    Optional<Account> findAccount(String accountNumber) throws Exception;
    double getCustomerTotalBalance(String customerId) throws Exception;
}