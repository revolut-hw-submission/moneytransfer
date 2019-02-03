package service;

import dao.AccountDao;
import dao.TransactionDao;
import model.Account;
import model.Transaction;
import model.TransactionRequest;

import java.math.BigDecimal;
import java.util.List;

public class TransactionService {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;
    private final AccountLockProvider lockProvider;


    public TransactionService(AccountDao accountDao,
                              TransactionDao transactionDao,
                              AccountLockProvider lockProvider) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.lockProvider = lockProvider;
    }

    public List<Transaction> getAll() {
        return transactionDao.findAll();
    }

    public Transaction get(String id) {
        return transactionDao.findById(id);
    }

    public Transaction create(TransactionRequest transactionRequest) {
        final Transaction transaction;
        final String from = transactionRequest.getFrom();
        final String to = transactionRequest.getTo();
        if (!from.equals(to)) {
            transaction = transfer(from, to, transactionRequest.getAmount());
        } else {
            transaction = Transaction.invalid();
        }

        transactionDao.save(transaction);
        return transaction;
    }



    private Transaction transfer(String from, String to, BigDecimal amount) {
        acquireLockOrdered(from, to);
        try {
            final Account accFrom = accountDao.findById(from);
            final Account accTo = accountDao.findById(to);

            final BigDecimal fromAmount = accFrom.getAmount();
            final BigDecimal toAmount = accTo.getAmount();
            if (fromAmount.compareTo(amount) < 0) {
                return Transaction.invalid();
            }
            accountDao.save(accFrom.createWithNewAmount(fromAmount.subtract(amount)));
            accountDao.save(accTo.createWithNewAmount(toAmount.add(amount)));
            return Transaction.valid();
        } finally {
            releaseLockOrdered(from, to);
        }
    }

    private void acquireLockOrdered(String first, String second) {
        if (first.compareTo(second) < 0) {
            acquireLockOrdered(second, first);
            return;
        }
        lockProvider.getLockByAccountId(first).lock();
        lockProvider.getLockByAccountId(second).lock();
    }

    private void releaseLockOrdered(String first, String second) {
        if (first.compareTo(second) < 0) {
            releaseLockOrdered(second, first);
            return;
        }
        lockProvider.getLockByAccountId(first).unlock();
        lockProvider.getLockByAccountId(second).unlock();
    }



}
