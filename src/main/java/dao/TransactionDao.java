package dao;

import model.Transaction;

import java.util.List;

public interface TransactionDao {
    List<Transaction> findAll();

    Transaction findById(String id);

    void save(Transaction transaction);
}
