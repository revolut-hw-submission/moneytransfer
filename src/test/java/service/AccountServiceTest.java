package service;

import dao.AccountDao;
import model.Account;
import model.AccountCreationRequest;
import model.Currency;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class AccountServiceTest {

    @Test
    void accountIsCreatedAccordingToRequest() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountService accountService = new AccountService(accountDaoMock, mock(AccountLockProvider.class));


        final Account account = accountService.create(new AccountCreationRequest(Currency.EUR, 10.1f));
        verify(accountDaoMock, times(1)).save(account);

        assertThat(account.getId()).isNotBlank();
        assertThat(account.getCurrency()).isEqualByComparingTo(Currency.EUR);
        assertThat(account.getAmount()).isEqualByComparingTo(new BigDecimal(10.1f));
    }

    @Test
    void deleteOperationPerformedUnderLock() {
        final AccountDao accountDaoMock = mock(AccountDao.class);
        final AccountLockProvider lockProviderMock = mock(AccountLockProvider.class);
        final AccountService accountService = new AccountService(accountDaoMock, lockProviderMock);
        when(accountDaoMock.deleteById(eq("1"))).thenReturn(new Account("1", Currency.EUR, new BigDecimal(1f)));
        final Lock mockLock = mock(Lock.class);
        when(lockProviderMock.getLockByAccountId(eq("1"))).thenReturn(mockLock);

        accountService.delete("1");

        final InOrder order = inOrder(mockLock, accountDaoMock);
        order.verify(mockLock).lock();
        order.verify(accountDaoMock).deleteById(eq("1"));
        order.verify(mockLock).unlock();
    }
}