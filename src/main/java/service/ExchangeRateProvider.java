package service;

import model.Currency;

import java.math.BigDecimal;

public interface ExchangeRateProvider {

    BigDecimal getExchangeRate(Currency from, Currency to);
}
