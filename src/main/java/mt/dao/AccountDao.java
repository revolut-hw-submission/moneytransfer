package mt.dao;

import mt.model.Account;

import java.util.Collection;

public interface AccountDao {

    Collection<Account> findAll();

    Account findById(String id);

    Account deleteById(String id);

    void save(Account account);
}
