package model;


import java.math.BigDecimal;

public class Account {
    final String id;
    final String userId;
    final Currency currency;
    final BigDecimal amount;


    public Account(String id, String userId, Currency currency, BigDecimal amount) {
        this.id = id;
        this.userId = userId;
        this.currency = currency;
        this.amount = amount;
    }


}
