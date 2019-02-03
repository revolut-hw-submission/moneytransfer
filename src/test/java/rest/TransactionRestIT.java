package rest;

import io.restassured.mapper.TypeRef;
import mt.model.*;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class TransactionRestIT extends BaseRestIt {

    @Test
    void transactionIsReturnedAfterCreation() {
        final Account account1 = createAccount(Currency.EUR, BigDecimal.TEN);
        final Account account2 = createAccount(Currency.EUR, BigDecimal.TEN);

        given().body(new TransactionRequest(account1.getId(), account2.getId(), Currency.EUR, BigDecimal.ONE))
            .when().post("http://localhost:4567/transactions")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("result", Matchers.equalTo("VALID"));
    }

    @Test
    void transactionsIsFilteredByAccountIdIfAsked() {
        final Account account1 = createAccount(Currency.EUR, BigDecimal.TEN);
        final Account account2 = createAccount(Currency.EUR, BigDecimal.TEN);
        final Account account3 = createAccount(Currency.EUR, BigDecimal.TEN);

        createTransaction(account1.getId(), account3.getId());
        createTransaction(account2.getId(), account3.getId());
        createTransaction(account1.getId(), account2.getId());

        final List<Transaction> transactions = given().queryParam("accountId", account1.getId())
                .when().get("http://localhost:4567/transactions")
                .as(new TypeRef<List<Transaction>>() {
                });

        assertThat(transactions).hasSize(2);
    }

    @Test
    void transactionWhichWillResultInNegativeValueRetuns400() {
        final Account account1 = createAccount(Currency.EUR, BigDecimal.ONE);
        final Account account2 = createAccount(Currency.EUR, BigDecimal.TEN);

        given().body(new TransactionRequest(account1.getId(), account2.getId(), Currency.EUR, new BigDecimal(1.5f)))
                .when().post("http://localhost:4567/transactions")
                .then()
                .statusCode(400)
                .body("id", notNullValue())
                .body("result", Matchers.equalTo("INVALID"));
    }

    @Test
    void transactionWithSameAccountReturns400() {
        final Account account1 = createAccount(Currency.EUR, BigDecimal.ONE);

        given().body(new TransactionRequest(account1.getId(), account1.getId(), Currency.EUR, new BigDecimal(1.5f)))
                .when().post("http://localhost:4567/transactions")
                .then()
                .statusCode(400)
                .body("id", notNullValue())
                .body("result", Matchers.equalTo("INVALID"));
    }


    private static Account createAccount(Currency currency, BigDecimal amount) {
        return given().body(new AccountCreationRequest(currency, amount))
                .when().post("http://localhost:4567/accounts")
                .as(Account.class);
    }

    private static void createTransaction(String id1, String id2) {
        given().body(new TransactionRequest(id1, id2, Currency.EUR, BigDecimal.ONE))
                .when().post("http://localhost:4567/transactions")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("result", Matchers.equalTo("VALID"));
    }
}
