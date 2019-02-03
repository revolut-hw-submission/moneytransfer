package mt.dao;

import mt.model.Transaction;

import java.util.Collection;

public interface TransactionDao {
    Collection<Transaction> findAllOrdered();

    Transaction findById(String id);

    void save(Transaction transaction);
}
