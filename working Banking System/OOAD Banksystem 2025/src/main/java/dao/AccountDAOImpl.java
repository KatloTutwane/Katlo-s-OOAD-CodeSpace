package dao;

import Model.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

public class AccountDAOImpl implements AccountDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(AccountDAOImpl.class.getName());


    // Default interest rates used on creation; business logic (BankSystem) may still manage interest application
    private static final double SAVINGS_INTEREST_RATE = 0.0005; // 0.05% monthly
    private static final double INVESTMENT_INTEREST_RATE = 0.05; // 5% monthly

    public AccountDAOImpl(Connection connection) {
        this.connection = connection;
    }

// -------------------- Helpers --------------------

    private boolean customerExists(String customerId) throws SQLException {
        String sql = "SELECT COUNT(*) AS count FROM customers WHERE cust_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("count") > 0;
                return false;
            }
        }
    }

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String accountType = rs.getString("account_type");
        String branch = rs.getString("branch");
        double balance = rs.getDouble("balance");

        switch (accountType) {
            case "SAVINGS": {
                double savingsRate = rs.getDouble("interest_rate");
                return new SavingsAccount(accountNumber, branch, balance, savingsRate);
            }
            case "CHEQUE": {
                double overdraftLimit = rs.getDouble("overdraft_limit");
                return new ChequeAccount(accountNumber, branch, balance, overdraftLimit);
            }
            case "INVESTMENT": {
                double investmentRate = rs.getDouble("interest_rate");
                String maturityDate = rs.getString("maturity_date");
                return new InvestmentAccount(accountNumber, branch, balance, investmentRate, maturityDate);
            }
            default:
                throw new SQLException("Unknown account type: " + accountType);
        }
    }

// -------------------- Account creation (DB only) --------------------

    @Override
    public Account openSavingsAccount(String customerId, String branch, double initialDeposit) throws Exception {
        if (!customerExists(customerId)) {
            throw new Exception("Customer does not exist: " + customerId);
        }

        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, interest_rate, status, created_date) " +
                "VALUES (?, 'SAVINGS', ?, ?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)";
        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, SAVINGS_INTEREST_RATE);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Created savings account " + accountNumber + " for customer " + customerId);
                return new SavingsAccount(accountNumber, branch, initialDeposit, SAVINGS_INTEREST_RATE);
            } else {
                throw new Exception("Failed to create savings account");
            }
        } catch (SQLException e) {
            throw new Exception("Error creating savings account", e);
        }
    }

    @Override
    public Account openChequeAccount(String customerId, String branch, double initialDeposit, double overdraftLimit) throws Exception {
        if (!customerExists(customerId)) {
            throw new Exception("Customer does not exist: " + customerId);
        }

        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, overdraft_limit, status, created_date) " +
                "VALUES (?, 'CHEQUE', ?, ?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)";
        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, overdraftLimit);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Created cheque account " + accountNumber + " for customer " + customerId);
                return new ChequeAccount(accountNumber, branch, initialDeposit, overdraftLimit);
            } else {
                throw new Exception("Failed to create cheque account");
            }
        } catch (SQLException e) {
            throw new Exception("Error creating cheque account", e);
        }
    }

    @Override
    public Account openInvestmentAccount(String customerId, String branch, double initialDeposit, String maturityDate) throws Exception {
        if (!customerExists(customerId)) {
            throw new Exception("Customer does not exist: " + customerId);
        }

        String sql = "INSERT INTO accounts (account_number, account_type, branch, balance, cust_id, interest_rate, maturity_date, status, created_date) " +
                "VALUES (?, 'INVESTMENT', ?, ?, ?, ?, ?, 'ACTIVE', CURRENT_TIMESTAMP)";
        String accountNumber = UUID.randomUUID().toString();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, branch);
            stmt.setDouble(3, initialDeposit);
            stmt.setString(4, customerId);
            stmt.setDouble(5, INVESTMENT_INTEREST_RATE);
            stmt.setString(6, maturityDate);

            int updated = stmt.executeUpdate();
            if (updated > 0) {
                logger.info("Created investment account " + accountNumber + " for customer " + customerId);
                return new InvestmentAccount(accountNumber, branch, initialDeposit, INVESTMENT_INTEREST_RATE, maturityDate);
            } else {
                throw new Exception("Failed to create investment account");
            }
        } catch (SQLException e) {
            throw new Exception("Error creating investment account", e);
        }
    }

// -------------------- Read operations --------------------

    @Override
    public List<Account> getCustomerAccounts(String customerId) throws Exception {
        String sql = "SELECT * FROM accounts WHERE cust_id = ? AND status = 'ACTIVE' ORDER BY created_date";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) accounts.add(mapResultSetToAccount(rs));
            }
        } catch (SQLException e) {
            throw new Exception("Error fetching customer accounts", e);
        }
        return accounts;
    }

    @Override
    public Optional<Account> findAccount(String accountNumber) throws Exception {
        String sql = "SELECT * FROM accounts WHERE account_number = ? AND status = 'ACTIVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapResultSetToAccount(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new Exception("Error finding account", e);
        }
    }

    @Override
    public double getCustomerTotalBalance(String customerId) throws Exception {
        String sql = "SELECT COALESCE(SUM(balance),0) AS total FROM accounts WHERE cust_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getDouble("total");
                return 0.0;
            }
        } catch (SQLException e) {
            throw new Exception("Error getting total balance", e);
        }
    }

// -------------------- Interest persistence helpers --------------------
// These methods read accounts, compute interest using model objects, and persist new balances.
// Business policy for WHEN to call these remains in BankSystem.

    @Override
    public void applyMonthlyInterest() throws Exception {
        String sql = "SELECT * FROM accounts WHERE status = 'ACTIVE' AND account_type IN ('SAVINGS', 'INVESTMENT')";
        List<Account> accounts = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) accounts.add(mapResultSetToAccount(rs));
        }

        for (Account account : accounts) {
            if (account instanceof Interest) {
                double oldBal = account.getBalance();
                ((Interest) account).applyInterest();
                double newBal = account.getBalance();
                updateBalance(account.getAccountNumber(), newBal);
                logger.info(String.format("Applied interest %s: %.2f -> %.2f", account.getAccountNumber(), oldBal, newBal));
            }
        }
    }

    @Override
    public void applyInterestToAccount(String accountNumber) throws Exception {
        Optional<Account> opt = findAccount(accountNumber);
        if (opt.isEmpty()) throw new Exception("Account not found: " + accountNumber);

        Account account = opt.get();
        if (!(account instanceof Interest)) throw new Exception("Account does not earn interest: " + accountNumber);

        double oldBal = account.getBalance();
        ((Interest) account).applyInterest();
        updateBalance(accountNumber, account.getBalance());
        logger.info(String.format("Applied interest to %s: %.2f -> %.2f", accountNumber, oldBal, account.getBalance()));
    }

// -------------------- Persistence utility --------------------

    @Override
    public void updateBalance(String accountNumber, double newBalance) throws Exception {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception("Error updating balance for account: " + accountNumber, e);
        }
    }


}