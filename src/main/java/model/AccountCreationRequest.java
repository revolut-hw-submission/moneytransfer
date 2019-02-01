package model;

public class AccountCreationRequest {

    final String userId;
    final Currency currency;
    final float amount;

    public AccountCreationRequest(String userId, Currency currency, float amount) {
        this.userId = userId;
        this.currency = currency;
        this.amount = amount;
    }
}
