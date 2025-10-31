package dao;

import Model.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class AccountDAOImpl implements AccountDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(AccountDAOImpl.class.getName());

    // Interest rates as per requirements
    private static final double SAVINGS_INTEREST_RATE = 0.0005; // 0.05% monthly
    private static final double INVESTMENT_INTEREST_RATE = 0.05; // 5% monthly

    public AccountDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // 1. Allow a customer to open an account with a bank
    @Override
    public Account openSavingsAccount(String customerId, String branch, double initialDeposit) throws Exception {
        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, interest_rate, status) " +
                "VALUES (?, 'SAVINGS', ?, ?, ?, ?, 'ACTIVE')";

        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, SAVINGS_INTEREST_RATE);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Savings account created: " + accountNumber + " for customer: " + customerId);
                return new SavingsAccount(branch, initialDeposit, SAVINGS_INTEREST_RATE) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };
            }
            throw new Exception("Failed to create savings account");
        } catch (SQLException e) {
            throw new Exception("Error creating savings account for customer: " + customerId, e);
        }
    }

    @Override
    public Account openChequeAccount(String customerId, String branch, double initialDeposit, double overdraftLimit) throws Exception {
        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, overdraft_limit, status) " +
                "VALUES (?, 'CHEQUE', ?, ?, ?, ?, 'ACTIVE')";

        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, overdraftLimit);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Cheque account created: " + accountNumber + " for customer: " + customerId);
                return new ChequeAccount(branch, initialDeposit, overdraftLimit) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };
            }
            throw new Exception("Failed to create cheque account");
        } catch (SQLException e) {
            throw new Exception("Error creating cheque account for customer: " + customerId, e);
        }
    }

    @Override
    public Account openInvestmentAccount(String customerId, String branch, double initialDeposit, String maturityDate) throws Exception {
        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, interest_rate, maturity_date, status) " +
                "VALUES (?, 'INVESTMENT', ?, ?, ?, ?, ?, 'ACTIVE')";

        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, INVESTMENT_INTEREST_RATE);
            stmt.setString(6, maturityDate);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Investment account created: " + accountNumber + " for customer: " + customerId);
                return new InvestmentAccount(branch, initialDeposit, INVESTMENT_INTEREST_RATE, maturityDate) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };
            }
            throw new Exception("Failed to create investment account");
        } catch (SQLException e) {
            throw new Exception("Error creating investment account for customer: " + customerId, e);
        }
    }

    // 2. Allow customer to make deposits in any or all of the accounts they hold
    @Override
    public void deposit(String accountNumber, double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        Optional<Account> accountOpt = findAccount(accountNumber);
        if (!accountOpt.isPresent()) {
            throw new Exception("Account not found: " + accountNumber);
        }

        Account account = accountOpt.get();
        account.deposit(amount);

        // Update database
        updateBalance(accountNumber, account.getBalance());
        logger.info("Deposited " + amount + " to account: " + accountNumber);
    }

    @Override
    public void withdraw(String accountNumber, double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be positive");
        }

        Optional<Account> accountOpt = findAccount(accountNumber);
        if (!accountOpt.isPresent()) {
            throw new Exception("Account not found: " + accountNumber);
        }

        Account account = accountOpt.get();
        if (account instanceof Withdrawable) {
            ((Withdrawable) account).withdraw(amount);
            updateBalance(accountNumber, account.getBalance());
            logger.info("Withdrew " + amount + " from account: " + accountNumber);
        } else {
            throw new Exception("Account type does not support withdrawals: " + accountNumber);
        }
    }

    // 3. Ensure interest is paid to the appropriate accounts
    @Override
    public void applyMonthlyInterest() throws Exception {
        String sql = "SELECT * FROM accounts WHERE status = 'ACTIVE' AND account_type IN ('SAVINGS', 'INVESTMENT')";
        List<Account> interestAccounts = new ArrayList<>();

        // First, load all accounts that earn interest
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                interestAccounts.add(mapResultSetToAccount(rs));
            }
        }

        // Apply interest to each account
        for (Account account : interestAccounts) {
            if (account instanceof Interest) {
                double oldBalance = account.getBalance();
                ((Interest) account).applyInterest();
                double newBalance = account.getBalance();
                double interestEarned = newBalance - oldBalance;

                updateBalance(account.getAccountNumber(), newBalance);
                logger.info(String.format(
                        "Applied interest to %s: %.2f -> %.2f (earned: %.2f)",
                        account.getAccountNumber(), oldBalance, newBalance, interestEarned
                ));
            }
        }

        logger.info("Applied monthly interest to " + interestAccounts.size() + " accounts");
    }

    @Override
    public void applyInterestToAccount(String accountNumber) throws Exception {
        Optional<Account> accountOpt = findAccount(accountNumber);
        if (!accountOpt.isPresent()) {
            throw new Exception("Account not found: " + accountNumber);
        }

        Account account = accountOpt.get();
        if (account instanceof Interest) {
            double oldBalance = account.getBalance();
            ((Interest) account).applyInterest();
            double newBalance = account.getBalance();

            updateBalance(accountNumber, newBalance);
            logger.info(String.format(
                    "Applied interest to account %s: %.2f -> %.2f",
                    accountNumber, oldBalance, newBalance
            ));
        } else {
            throw new Exception("Account type does not earn interest: " + accountNumber);
        }
    }

    @Override
    public List<Account> getCustomerAccounts(String customerId) throws Exception {
        String sql = "SELECT * FROM accounts WHERE cust_id = ? AND status = 'ACTIVE' ORDER BY created_date";
        List<Account> accounts = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error retrieving accounts for customer: " + customerId, e);
        }

        return accounts;
    }

    @Override
    public Optional<Account> findAccount(String accountNumber) throws Exception {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToAccount(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new Exception("Error finding account: " + accountNumber, e);
        }
    }

    @Override
    public double getCustomerTotalBalance(String customerId) throws Exception {
        String sql = "SELECT SUM(balance) as total FROM accounts WHERE cust_id = ? AND status = 'ACTIVE'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
            return 0.0;
        } catch (SQLException e) {
            throw new Exception("Error calculating total balance for customer: " + customerId, e);
        }
    }

    // Private helper methods
    private void updateBalance(String accountNumber, double newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String accountType = rs.getString("account_type");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");

        switch (accountType) {
            case "SAVINGS":
                double savingsRate = rs.getDouble("interest_rate");
                return new SavingsAccount(branch, balance, savingsRate) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };

            case "CHEQUE":
                double overdraftLimit = rs.getDouble("overdraft_limit");
                return new ChequeAccount(branch, balance, overdraftLimit) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };

            case "INVESTMENT":
                double investmentRate = rs.getDouble("interest_rate");
                String maturityDate = rs.getString("maturity_date");
                return new InvestmentAccount(branch, balance, investmentRate, maturityDate) {
                    @Override public String getAccountNumber() { return accountNumber; }
                };

            default:
                throw new SQLException("Unknown account type: " + accountType);
        }
    }
}