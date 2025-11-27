package Model;

import java.time.Instant;
import java.util.UUID;

public class Transaction {
    private final String transactionId;
    private final Instant date;
    private final TransactionType type;
    private final String fromAccountNumber;
    private final String toAccountNumber;
    private final double amount;
    private final String details;

    // Constructor for new transactions (generates ID and date automatically)
    public Transaction(TransactionType type, String fromAccountNumber, String toAccountNumber, double amount, String details) {
        this(UUID.randomUUID().toString(), Instant.now(), type, fromAccountNumber, toAccountNumber, amount, details);
    }

    // Full constructor for loading from database
    public Transaction(String transactionId, Instant date, TransactionType type,
                       String fromAccountNumber, String toAccountNumber, double amount, String details) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Transaction type cannot be null");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        this.transactionId = transactionId;
        this.date = date;
        this.type = type;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.details = details;
    }

    // Getters
    public String getTransactionId() { return transactionId; }
    public Instant getDate() { return date; }
    public TransactionType getType() { return type; }
    public String getFromAccountNumber() { return fromAccountNumber; }
    public String getToAccountNumber() { return toAccountNumber; }
    public double getAmount() { return amount; }
    public String getDetails() { return details; }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + transactionId + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", from='" + fromAccountNumber + '\'' +
                ", to='" + toAccountNumber + '\'' +
                ", amount=" + amount +
                ", details='" + details + '\'' +
                '}';
    }

    // Helper method to check if transaction involves a specific account
    public boolean involvesAccount(String accountNumber) {
        return accountNumber.equals(fromAccountNumber) || accountNumber.equals(toAccountNumber);
    }

    // Helper method to get the other account in the transaction
    public String getOtherAccount(String accountNumber) {
        if (accountNumber.equals(fromAccountNumber)) {
            return toAccountNumber;
        } else if (accountNumber.equals(toAccountNumber)) {
            return fromAccountNumber;
        }
        return null;
    }
}