package service;

import model.Currency;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/*
This class actually exists just to have some implementation of rate provider,
I would rather use some external service, but its not polite to do it in hw-project
*/

public class StaticMapExchangeRateProvider implements ExchangeRateProvider {

    private static final Map<Currency, Map<Currency, BigDecimal>> rateMap = new HashMap<>();

    static {
        for(Currency outer: Currency.values()) {
            rateMap.put(outer, new HashMap<>());
            for (Currency inner: Currency.values()) {
                rateMap.computeIfPresent(outer, (k, v) -> {
                    v.put(inner, BigDecimal.ONE);
                    return v;
                });
            }
        }
    }

    @Override
    public BigDecimal getExchangeRate(Currency from, Currency to) {
        return rateMap.get(from).get(to);
    }
}
