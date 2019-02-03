package mt.model;

public class AccountCreationRequest {

    private final Currency currency;
    private final float amount;

    public AccountCreationRequest(Currency currency, float amount) {
        this.currency = currency;
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public float getAmount() {
        return amount;
    }
}
