package mt.dao;

import mt.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static mt.model.Currency.GPB;
import static org.assertj.core.api.Assertions.assertThat;

class DefaultAccountDaoTest {

    private DefaultAccountDao dao;

    @BeforeEach
    void setUp() {
        dao = new DefaultAccountDao();
    }

    @Test
    void defaultDaoReturnsSavedData() {
        final Account account1 = new Account("1", GPB, new BigDecimal(0.1f));
        final Account account2 = new Account("2", GPB, new BigDecimal(0.2f));
        final Account account3 = new Account("3", GPB, new BigDecimal(0.3f));
        final Account account4 = new Account("1", GPB, new BigDecimal(0.4f));
        dao.save(account1);
        dao.save(account2);
        dao.save(account3);
        dao.save(account4);


        final Collection<Account> allAccounts = dao.findAll();
        assertThat(allAccounts).hasSize(3);
        assertThat(allAccounts).containsExactlyInAnyOrder(account2, account3, account4);
    }


    @Test
    void defaultDaoDeletesData() {
        final Account account1 = new Account("1", GPB, new BigDecimal(0.1f));
        dao.save(account1);

        assertThat(dao.findById("1")).isNotNull();
        assertThat(dao.deleteById("1")).isEqualTo(account1);
        assertThat(dao.findById("1")).isNull();
        assertThat(dao.findById("NOT_EXISTS")).isNull();

    }
}