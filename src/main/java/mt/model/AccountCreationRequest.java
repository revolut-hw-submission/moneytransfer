package mt.model;

import java.math.BigDecimal;

public class AccountCreationRequest {

    private final Currency currency;
    private final BigDecimal amount;

    public AccountCreationRequest(Currency currency, BigDecimal amount) {

        if(currency == null || amount == null) {
            throw new IllegalArgumentException();
        }
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
