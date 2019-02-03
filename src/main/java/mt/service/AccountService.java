package mt.service;

import mt.dao.AccountDao;
import mt.model.Account;
import mt.model.AccountCreationRequest;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

public class AccountService {

    private final AccountDao accountDao;
    private final AccountLockProvider accountLockProvider;

    public AccountService(AccountDao accountDao, AccountLockProvider accountLockProvider) {
        this.accountDao = accountDao;
        this.accountLockProvider = accountLockProvider;
    }

    public Collection<Account> getAll() {
        return accountDao.findAll();
    }

    public Account get(String id) {
        return accountDao.findById(id);
    }

    //TODO: Return response which shows that acc cannot be deleted.
    public Account delete(String id) {
        final Lock lockByAccountId = accountLockProvider.getLockByAccountId(id);
        lockByAccountId.lock();
        try {
            return accountDao.deleteById(id);
        } finally {
            lockByAccountId.unlock();
        }
    }

    public Account create(AccountCreationRequest creationRequest) {
        final Account account = new Account(
                UUID.randomUUID().toString(),
                creationRequest.getCurrency(),
                new BigDecimal(creationRequest.getAmount())
        );
        accountDao.save(account);
        return account;
    }
}
