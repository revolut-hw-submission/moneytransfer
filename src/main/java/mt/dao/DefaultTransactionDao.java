package dao;

import model.Transaction;

import java.util.Collection;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultTransactionDao implements TransactionDao {

    private Queue<Transaction> transactions = new ConcurrentLinkedQueue<>();

    @Override
    public Collection<Transaction> findAllOrdered() {
        return Collections.unmodifiableCollection(transactions);
    }

    @Override
    public Transaction findById(String id) {
        return transactions.stream()
                .filter(transaction -> transaction.getId().equals(id))
                .findAny()
                .orElse(null);
    }

    @Override
    public void save(Transaction transaction) {
        transactions.add(transaction);
    }
}
