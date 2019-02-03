package mt.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Transaction {

    private final String id;
    private final Result result;
    private final String from;
    private final String to;
    private final Currency currency;
    private final BigDecimal amount;


    private Transaction(Result result, String from, String to, Currency currency, BigDecimal amount) {
        if (result == null || from == null || to == null || currency == null || amount == null) {
            throw new IllegalArgumentException();
        }

        this.from = from;
        this.to = to;
        this.currency = currency;
        this.amount = amount;
        this.id = UUID.randomUUID().toString();
        this.result = result;
    }

    public static Transaction invalid(String from, String to, Currency currency, BigDecimal amount) {
        return new Transaction(Result.INVALID, from, to, currency, amount);
    }

    public static Transaction valid(String from, String to, Currency currency, BigDecimal amount) {
        return new Transaction(Result.VALID, from, to,  currency, amount);
    }


    public String getId() {
        return id;
    }

    public Result getResult() {
        return result;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public enum Result {
        VALID, INVALID
    }
}
