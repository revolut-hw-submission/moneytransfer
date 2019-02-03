package service;

import model.Currency;

import java.math.BigDecimal;

public interface ExchangeService {
    BigDecimal getExchangeRate(Currency from, Currency to);
}
