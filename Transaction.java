
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

    public Transaction(TransactionType type, String fromAccountNumber, String toAccountNumber, double amount, String details) {
        this.transactionId = UUID.randomUUID().toString();
        this.date = Instant.now();
        this.type = type;
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.details = details;
    }

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
}