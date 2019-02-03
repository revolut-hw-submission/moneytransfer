package mt.service;

import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;


class AccountLockProviderTest {

    @Test
    void uniqueLockIsCreatedForAccount() {
        final AccountLockProvider accountLockProvider = new AccountLockProvider();

        final Lock lock1 = accountLockProvider.getLockByAccountId("1");
        final Lock lock2 = accountLockProvider.getLockByAccountId("1");
        final Lock lock3 = accountLockProvider.getLockByAccountId("2");

        assertThat(lock1).isSameAs(lock2);
        assertThat(lock1).isNotSameAs(lock3);
    }
}