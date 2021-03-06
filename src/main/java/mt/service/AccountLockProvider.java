package mt.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AccountLockProvider {

    private final ConcurrentMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    Lock getLockByAccountId(String accountId) {
        return lockMap.computeIfAbsent(accountId, k -> new ReentrantLock());
    }

}
