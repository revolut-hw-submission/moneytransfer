package mt.service;

import mt.dao.AccountDao;
import mt.dao.TransactionDao;
import mt.model.Account;
import mt.model.Transaction;
import mt.model.TransactionRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static java.util.Arrays.asList;
import static mt.model.Currency.EUR;
import static mt.model.Currency.USD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    private TransactionService transactionService;

    private AccountDao accountDaoMock = mock(AccountDao.class);
    private TransactionDao transactionDaoMock = mock(TransactionDao.class);
    private AccountLockProvider accountLockProviderMock = mock(AccountLockProvider.class);
    private ExchangeRateProvider exchangeRateProviderMock = mock(ExchangeRateProvider.class);

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(accountDaoMock, transactionDaoMock, accountLockProviderMock, exchangeRateProviderMock);
    }

    @AfterEach
    void tearDown() {
        reset(accountDaoMock, transactionDaoMock, accountLockProviderMock, exchangeRateProviderMock);
    }

    @Test
    void transferHappenUnderOrderedLock() {
        final Lock lock1 = mock(Lock.class);
        final Lock lock2 = mock(Lock.class);
        when(accountLockProviderMock.getLockByAccountId(eq("1"))).thenReturn(lock1);
        when(accountLockProviderMock.getLockByAccountId(eq("2"))).thenReturn(lock2);
        when(exchangeRateProviderMock.getExchangeRate(any(), any())).thenReturn(BigDecimal.ONE);

        final Account acc1 = new Account("1", EUR, new BigDecimal(1.2f));
        final Account acc2 = new Account("2", EUR, new BigDecimal(1.2f));
        when(accountDaoMock.findById(eq("1"))).thenReturn(acc1);
        when(accountDaoMock.findById(eq("2"))).thenReturn(acc2);

        transactionService.create(new TransactionRequest("2", "1", EUR, new BigDecimal(1f)));

        final InOrder order = inOrder(lock1, lock2, accountDaoMock);
        order.verify(lock2, times(1)).lock();
        order.verify(lock1, times(1)).lock();
        order.verify(accountDaoMock, times(2)).save(any(Account.class));
        verify(lock1).unlock();
        verify(lock2).unlock();
    }


    @Test
    void invalidTransactionIsReturnedWhenNotEnoughBalance() {
        when(accountLockProviderMock.getLockByAccountId(any())).thenReturn(mock(Lock.class));
        when(exchangeRateProviderMock.getExchangeRate(any(), any())).thenReturn(BigDecimal.ONE);

        final Account acc1 = new Account("1", EUR, new BigDecimal(0.9f));
        final Account acc2 = new Account("2", EUR, new BigDecimal(1.2f));
        when(accountDaoMock.findById(eq("1"))).thenReturn(acc1);
        when(accountDaoMock.findById(eq("2"))).thenReturn(acc2);

        final Transaction transaction = transactionService.create(new TransactionRequest("1", "2", EUR, new BigDecimal(1f)));

        assertThat(transaction.getResult()).isEqualTo(Transaction.Result.INVALID);
        verify(transactionDaoMock, times(1)).save(eq(transaction));
    }

    @Test
    void validTransactionResultIsSaved() {
        when(accountLockProviderMock.getLockByAccountId(any())).thenReturn(mock(Lock.class));
        when(exchangeRateProviderMock.getExchangeRate(any(), any())).thenReturn(BigDecimal.ONE);
        final Account acc1 = new Account("1", EUR, new BigDecimal(1.9f));
        final Account acc2 = new Account("2", EUR, new BigDecimal(1.2f));
        when(accountDaoMock.findById(eq("1"))).thenReturn(acc1);
        when(accountDaoMock.findById(eq("2"))).thenReturn(acc2);

        final Transaction transaction = transactionService.create(new TransactionRequest("1", "2", EUR, new BigDecimal(1f)));

        assertThat(transaction.getResult()).isEqualTo(Transaction.Result.VALID);
        verify(transactionDaoMock, times(1)).save(eq(transaction));
    }

    @Test
    void transactionForSameAccIsInvalid() {

        final Account acc1 = new Account("1", EUR, new BigDecimal(1.9f));
        when(accountDaoMock.findById(any())).thenReturn(acc1);

        final Transaction transaction = transactionService.create(new TransactionRequest("1", "1", EUR, new BigDecimal(1f)));

        assertThat(transaction.getResult()).isEqualTo(Transaction.Result.INVALID);
        verify(transactionDaoMock, times(1)).save(eq(transaction));
    }

    @Test
    void transactionServicePassthroughsFindCallsToDao() {
        final Transaction valid = Transaction.valid("1", "2");
        when(transactionDaoMock.findById(eq("1"))).thenReturn(valid);
        assertThat(transactionService.get("1")).isEqualTo(valid);
    }

    @Test
    void transactionServicePassthroughsFindAllCallsToDao() {
        final Transaction valid = Transaction.valid("1", "2");
        final Transaction valid2 = Transaction.valid("1", "2");
        final Transaction invalid = Transaction.invalid("1", "2");
        when(transactionDaoMock.findAllOrdered())
                .thenReturn(asList(valid, valid2, invalid));
        assertThat(transactionService.getAll())
                .containsExactly(valid, valid2, invalid);
    }


    @Test
    void getByAccountIdFiltersBothAccountInvolved() {
        final Transaction valid = Transaction.valid("1", "2");
        final Transaction valid2 = Transaction.valid("3", "4");
        final Transaction invalid = Transaction.invalid("3", "1");

        when(transactionDaoMock.findAllOrdered())
                .thenReturn(asList(valid, valid2, invalid));
        assertThat(transactionService.getForAccountId("1"))
                .containsExactly(valid, invalid);
    }


    @Test
    void transferHappenWithRespectOfConversionRate() {
        when(accountLockProviderMock.getLockByAccountId(any())).thenReturn(mock(Lock.class));

        when(exchangeRateProviderMock.getExchangeRate(eq(USD), eq(EUR))).thenReturn(new BigDecimal(2f));
        when(exchangeRateProviderMock.getExchangeRate(eq(EUR), eq(USD))).thenReturn(new BigDecimal(0.5f));
        when(exchangeRateProviderMock.getExchangeRate(eq(EUR), eq(EUR))).thenReturn(BigDecimal.ONE);

        final Account acc1 = new Account("1", EUR, new BigDecimal(3f));
        final Account acc2 = new Account("2", USD, new BigDecimal(4f));
        when(accountDaoMock.findById(eq("1"))).thenReturn(acc1);
        when(accountDaoMock.findById(eq("2"))).thenReturn(acc2);

        final Transaction transaction = transactionService.create(new TransactionRequest("1", "2", EUR, new BigDecimal(1f)));


        final ArgumentCaptor<Account> accountArgumentCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountDaoMock, times(2)).save(accountArgumentCaptor.capture());

        final List<Account> captured = accountArgumentCaptor.getAllValues();
        assertThat(captured).hasSize(2);
        assertThat(captured).containsExactly(acc1.createWithNewAmount(new BigDecimal(2f)),
                acc2.createWithNewAmount(new BigDecimal(6f)));

        assertThat(transaction.getResult()).isEqualTo(Transaction.Result.VALID);
        verify(transactionDaoMock, times(1)).save(eq(transaction));
    }

}