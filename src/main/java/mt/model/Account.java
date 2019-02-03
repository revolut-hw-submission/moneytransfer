package mt.model;


import java.math.BigDecimal;

public class Account {

    private final String id;
    private final Currency currency;
    private final BigDecimal amount;

    public Account(String id, Currency currency, BigDecimal amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Account createWithNewAmount(BigDecimal newAmount) {
        return new Account(id, currency, newAmount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return id.equals(account.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
