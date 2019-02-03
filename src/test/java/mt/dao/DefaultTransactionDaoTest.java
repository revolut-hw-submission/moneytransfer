package mt.dao;

import mt.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultTransactionDaoTest {

    private DefaultTransactionDao dao;

    @BeforeEach
    void setUp() {
        dao = new DefaultTransactionDao();
    }

    @Test
    void defaultDaoSavesData() {
        dao.save(Transaction.valid("1", "2"));
        dao.save(Transaction.valid("1", "2"));
        dao.save(Transaction.invalid("1", "2"));

        assertThat(dao.findAllOrdered()).hasSize(3);

        assertThat(dao.findAllOrdered())
                .extracting(Transaction::getResult)
                .contains(Transaction.Result.VALID, Transaction.Result.VALID, Transaction.Result.INVALID);
    }


    @Test
    void defaultReturnsDataById() {
        final Transaction valid = Transaction.valid("1", "2");
        dao.save(valid);

        assertThat(dao.findById(valid.getId())).extracting(Transaction::getResult).isEqualTo(Transaction.Result.VALID);

        assertThat(dao.findById("NOT_EXIST"))
                .isNull();
    }
}