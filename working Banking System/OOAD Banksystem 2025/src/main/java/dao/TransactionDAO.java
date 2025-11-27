package dao;

import Model.Transaction;
import Model.TransactionType;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionDAO {


    private final Connection conn; // shared connection

    // SQL queries
    private static final String INSERT_SQL =
            "INSERT INTO transactions (transaction_id, transaction_type, from_account_number, " +
                    "to_account_number, amount, transaction_date, details) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_SQL =
            "SELECT * FROM transactions WHERE transaction_id = ?";

    private static final String SELECT_BY_ACCOUNT_SQL =
            "SELECT * FROM transactions WHERE from_account_number = ? OR to_account_number = ? " +
                    "ORDER BY transaction_date DESC";

    private static final String SELECT_BY_ACCOUNT_AND_TYPE_SQL =
            "SELECT * FROM transactions WHERE (from_account_number = ? OR to_account_number = ?) " +
                    "AND transaction_type = ? ORDER BY transaction_date DESC";

    private static final String SELECT_ALL_SQL =
            "SELECT * FROM transactions ORDER BY transaction_date DESC";

    private static final String SELECT_BY_DATE_RANGE_SQL =
            "SELECT * FROM transactions WHERE transaction_date BETWEEN ? AND ? " +
                    "ORDER BY transaction_date DESC";

    private static final String SELECT_RECENT_BY_ACCOUNT_SQL =
            "SELECT * FROM transactions WHERE from_account_number = ? OR to_account_number = ? " +
                    "ORDER BY transaction_date DESC FETCH FIRST ? ROWS ONLY";

    private static final String SELECT_BY_TYPE_SQL =
            "SELECT * FROM transactions WHERE transaction_type = ? ORDER BY transaction_date DESC";

    private static final String GET_ACCOUNT_BALANCE_SQL =
            "SELECT " +
                    "  COALESCE(SUM(CASE WHEN transaction_type = 'DEPOSIT' OR to_account_number = ? THEN amount ELSE 0 END), 0) - " +
                    "  COALESCE(SUM(CASE WHEN transaction_type = 'WITHDRAWAL' OR from_account_number = ? THEN amount ELSE 0 END), 0) " +
                    "FROM transactions " +
                    "WHERE from_account_number = ? OR to_account_number = ?";

    // Constructor accepting shared connection
    public TransactionDAO(Connection conn) {
        this.conn = conn;
    }

    // Default constructor (optional)
    public TransactionDAO() {
        this.conn = null;
    }

    public boolean createTransaction(Transaction transaction) {
        try (PreparedStatement statement = conn.prepareStatement(INSERT_SQL)) {
            statement.setString(1, transaction.getTransactionId());
            statement.setString(2, transaction.getType().name());
            statement.setString(3, transaction.getFromAccountNumber());
            statement.setString(4, transaction.getToAccountNumber());
            statement.setDouble(5, transaction.getAmount());
            statement.setTimestamp(6, Timestamp.from(transaction.getDate()));
            statement.setString(7, transaction.getDetails());

            int rowsAffected = statement.executeUpdate();
            conn.commit();
            return rowsAffected > 0;
        } catch (SQLException e) {
            rollbackConnection();
            System.err.println("Error creating transaction: " + e.getMessage());
            return false;
        }
    }

    public Optional<Transaction> findById(String transactionId) {
        try (PreparedStatement statement = conn.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setString(1, transactionId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error finding transaction by ID: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Transaction> getTransactionsByAccount(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_BY_ACCOUNT_SQL)) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by account: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getRecentTransactionsByAccount(String accountNumber, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_RECENT_BY_ACCOUNT_SQL)) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);
            statement.setInt(3, limit);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent transactions: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByAccountAndType(String accountNumber, TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_BY_ACCOUNT_AND_TYPE_SQL)) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);
            statement.setString(3, type.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by account and type: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByType(TransactionType type) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_BY_TYPE_SQL)) {
            statement.setString(1, type.name());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by type: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all transactions: " + e.getMessage());
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByDateRange(Instant startDate, Instant endDate) {
        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement statement = conn.prepareStatement(SELECT_BY_DATE_RANGE_SQL)) {
            statement.setTimestamp(1, Timestamp.from(startDate));
            statement.setTimestamp(2, Timestamp.from(endDate));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                transactions.add(mapResultSetToTransaction(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting transactions by date range: " + e.getMessage());
        }
        return transactions;
    }

    public double getAccountBalance(String accountNumber) {
        try (PreparedStatement statement = conn.prepareStatement(GET_ACCOUNT_BALANCE_SQL)) {
            statement.setString(1, accountNumber);
            statement.setString(2, accountNumber);
            statement.setString(3, accountNumber);
            statement.setString(4, accountNumber);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error calculating account balance: " + e.getMessage());
        }
        return 0.0;
    }

    private Transaction mapResultSetToTransaction(ResultSet resultSet) throws SQLException {
        String transactionId = resultSet.getString("transaction_id");
        TransactionType type = TransactionType.valueOf(resultSet.getString("transaction_type"));
        String fromAccount = resultSet.getString("from_account_number");
        String toAccount = resultSet.getString("to_account_number");
        double amount = resultSet.getDouble("amount");
        Instant date = resultSet.getTimestamp("transaction_date").toInstant();
        String details = resultSet.getString("details");

        return new Transaction(transactionId, date, type, fromAccount, toAccount, amount, details);
    }

    public boolean createTransactions(List<Transaction> transactions) {
        try (PreparedStatement statement = conn.prepareStatement(INSERT_SQL)) {
            for (Transaction transaction : transactions) {
                statement.setString(1, transaction.getTransactionId());
                statement.setString(2, transaction.getType().name());
                statement.setString(3, transaction.getFromAccountNumber());
                statement.setString(4, transaction.getToAccountNumber());
                statement.setDouble(5, transaction.getAmount());
                statement.setTimestamp(6, Timestamp.from(transaction.getDate()));
                statement.setString(7, transaction.getDetails());
                statement.addBatch();
            }

            int[] results = statement.executeBatch();
            conn.commit();

            for (int result : results) {
                if (result <= 0) return false;
            }
            return true;
        } catch (SQLException e) {
            rollbackConnection();
            System.err.println("Error creating transactions in batch: " + e.getMessage());
            return false;
        }
    }

    private void rollbackConnection() {
        try {
            if (conn != null) conn.rollback();
        } catch (SQLException e) {
            System.err.println("Rollback failed: " + e.getMessage());
        }
    }


}
