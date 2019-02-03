package service;

import dao.AccountDao;
import dao.TransactionDao;
import model.Account;
import model.Currency;
import model.Transaction;
import model.TransactionRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;

public class TransactionService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final AccountLockProvider lockProvider;
    private final ExchangeRateProvider exchangeRateProvider;


    public TransactionService(AccountDao accountDao,
                              TransactionDao transactionDao,
                              AccountLockProvider lockProvider,
                              ExchangeRateProvider exchangeRateProvider) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.lockProvider = lockProvider;
        this.exchangeRateProvider = exchangeRateProvider;
    }

    public Collection<Transaction> getAll() {
        return transactionDao.findAllOrdered();
    }


    public Collection<Transaction> getForAccountId(String accountId) {
        return transactionDao.findAllOrdered()
                .stream()
                .filter(tr -> tr.getFrom().equals(accountId) || tr.getTo().equals(accountId))
                .collect(Collectors.toList());
    }

    public Transaction get(String id) {
        return transactionDao.findById(id);
    }

    public Transaction create(TransactionRequest transactionRequest) {
        final Transaction transaction;
        final String from = transactionRequest.getFrom();
        final String to = transactionRequest.getTo();
        if (!from.equals(to)) {
            transaction = transfer(from, to, transactionRequest.getAmount(), transactionRequest.getCurrency());
        } else {
            transaction = Transaction.invalid(from, to);
        }

        transactionDao.save(transaction);
        return transaction;
    }

    private Transaction transfer(String from, String to, BigDecimal amount, Currency currency) {
        acquireLockOrdered(from, to);
        try {
            final Account accFrom = accountDao.findById(from);
            final Account accTo = accountDao.findById(to);

            final BigDecimal fromAmount = accFrom.getAmount();
            final BigDecimal toAmount = accTo.getAmount();

            final BigDecimal amountInFromCurrency = convertTo(accFrom.getCurrency(), currency, amount);

            if (fromAmount.compareTo(amountInFromCurrency) < 0) {
                return Transaction.invalid(from, to);
            }
            final BigDecimal amountInToCurrency = convertTo(currency, accTo.getCurrency(), amount);

            accountDao.save(accFrom.createWithNewAmount(fromAmount.subtract(amountInFromCurrency)));
            accountDao.save(accTo.createWithNewAmount(toAmount.add(amountInToCurrency)));
            return Transaction.valid(from, to);
        } finally {
            releaseLocks(from, to);
        }
    }

    private BigDecimal convertTo(Currency from, Currency to, BigDecimal amount) {
        final BigDecimal rate = exchangeRateProvider.getExchangeRate(from, to);
        return amount.multiply(rate);
    }


    private void acquireLockOrdered(String first, String second) {
        if (first.compareTo(second) < 0) {
            acquireLockOrdered(second, first);
            return;
        }
        lockProvider.getLockByAccountId(first).lock();
        lockProvider.getLockByAccountId(second).lock();
    }

    private void releaseLocks(String first, String second) {
        lockProvider.getLockByAccountId(first).unlock();
        lockProvider.getLockByAccountId(second).unlock();
    }
}
