package mt.service;

import mt.dao.AccountDao;
import mt.model.Account;
import mt.model.AccountCreationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.locks.Lock;

import static mt.model.Currency.EUR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountServiceTest {

    @Test
    void accountIsCreatedAccordingToRequest() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountService accountService = new AccountService(accountDaoMock, mock(AccountLockProvider.class));


        final Account account = accountService.create(new AccountCreationRequest(EUR, 10.1f));
        verify(accountDaoMock, times(1)).save(account);

        assertThat(account.getId()).isNotBlank();
        assertThat(account.getCurrency()).isEqualByComparingTo(EUR);
        assertThat(account.getAmount()).isEqualByComparingTo(new BigDecimal(10.1f));
    }

    @Test
    void deleteOperationPerformedUnderLock() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountLockProvider lockProviderMock = mock(AccountLockProvider.class);
        final AccountService accountService = new AccountService(accountDaoMock, lockProviderMock);
        when(accountDaoMock.deleteById(eq("1"))).thenReturn(new Account("1", EUR, new BigDecimal(1f)));
        final Lock mockLock = mock(Lock.class);
        when(lockProviderMock.getLockByAccountId(eq("1"))).thenReturn(mockLock);

        accountService.delete("1");

        final InOrder order = inOrder(mockLock, accountDaoMock);
        order.verify(mockLock).lock();
        order.verify(accountDaoMock).deleteById(eq("1"));
        order.verify(mockLock).unlock();
    }

    @Test
    void accountServicePassthroughsFindCallsToDao() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountService accountService = new AccountService(accountDaoMock, null);

        when(accountDaoMock.findById(eq("1")))
                .thenReturn(new Account("1", EUR, new BigDecimal(1.2f)));

        final Account account = accountService.get("1");
        assertThat(account).isNotNull();
        assertThat(account).extracting(Account::getId).isEqualTo("1");
        assertThat(account).extracting(Account::getCurrency).isEqualTo(EUR);
        assertThat(account).extracting(Account::getAmount).isEqualTo(new BigDecimal(1.2f));
    }

    @Test
    void accountServicePassthroughsFindAllCallsToDao() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountService accountService = new AccountService(accountDaoMock, null);

        final Account account1 = new Account("1", EUR, new BigDecimal(1.2f));
        final Account account2 = new Account("2", EUR, new BigDecimal(1.2f));
        when(accountDaoMock.findAll())
                .thenReturn(Arrays.asList(account1, account2))
                .thenReturn(Collections.emptyList());

        final Collection<Account> accounts = accountService.getAll();
        assertThat(accounts).isNotNull();
        assertThat(accounts).contains(account1, account2);
    }
}