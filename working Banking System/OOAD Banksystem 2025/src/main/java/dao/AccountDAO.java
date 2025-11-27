package dao;

import Model.*;
import java.util.List;
import java.util.Optional;

public interface AccountDAO {
    // Core account creation (persists accounts)
    Account openSavingsAccount(String customerId, String branch, double initialDeposit) throws Exception;
    Account openChequeAccount(String customerId, String branch, double initialDeposit, double overdraftLimit) throws Exception;
    Account openInvestmentAccount(String customerId, String branch, double initialDeposit, String maturityDate) throws Exception;


    // Read operations
    List<Account> getCustomerAccounts(String customerId) throws Exception;
    Optional<Account> findAccount(String accountNumber) throws Exception;
    double getCustomerTotalBalance(String customerId) throws Exception;

    // Interest persistence helpers (DAO applies interest values, but business logic may call these)
    void applyMonthlyInterest() throws Exception;
    void applyInterestToAccount(String accountNumber) throws Exception;

    // Persistence utility
    void updateBalance(String accountNumber, double newBalance) throws Exception;


}
