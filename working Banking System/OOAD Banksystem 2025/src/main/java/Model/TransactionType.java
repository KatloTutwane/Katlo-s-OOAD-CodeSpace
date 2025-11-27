package Model;

public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER;

    public String getDisplayName() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}