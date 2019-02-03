package mt.model;

import java.math.BigDecimal;

public class TransactionRequest {

    private final String from;
    private final String to;
    private final Currency currency;
    private final BigDecimal amount;

    public TransactionRequest(String from, String to, Currency currency, BigDecimal amount) {
        if (from == null || to == null || currency == null || amount == null) {
            throw new IllegalArgumentException();
        }
        this.from = from;
        this.to = to;
        this.currency = currency;
        this.amount = amount;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
