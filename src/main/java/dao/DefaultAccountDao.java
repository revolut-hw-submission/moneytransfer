package dao;

import model.Account;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultAccountDao implements AccountDao {

    private ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();

    @Override
    public Collection<Account> findAll() {
        return accounts.values();
    }

    @Override
    public Account findById(String id) {
        return accounts.get(id);
    }

    @Override
    public Account deleteById(String id) {
        return accounts.remove(id);
    }

    @Override
    public void save(Account account) {
        accounts.put(account.getId(), account);
    }
}
