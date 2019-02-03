package rest;

import io.restassured.mapper.TypeRef;
import mt.model.Account;
import mt.model.AccountCreationRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static mt.model.Currency.EUR;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class AccountRestIT extends BaseRestIt {

    @Test
    void idGeneratedForNewAccount() {
        given().body(new AccountCreationRequest(EUR, BigDecimal.TEN))
                .when().post("http://localhost:4567/accounts")
                .then()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    void invalidJsonReturns400() {
        given().body("{invalidJson}")
                .when().post("http://localhost:4567/accounts")
                .then()
                .statusCode(400);
    }

    @Test
    void notExistedIdReturns404() {
        when().get("http://localhost:4567/accounts/{id}", "1")
                .then().statusCode(404);
    }

    @Test
    void serviceReturnsSavedAccounts() {
        given().body(new AccountCreationRequest(EUR, new BigDecimal(9f)))
                .when().post("http://localhost:4567/accounts")
                .then()
                .statusCode(201)
                .body("id", notNullValue());

        given().body(new AccountCreationRequest(EUR, new BigDecimal(5f)))
                .when().post("http://localhost:4567/accounts")
                .then()
                .statusCode(201)
                .body("id", notNullValue());


        final List<Account> list = when().get("http://localhost:4567/accounts").as(new TypeRef<List<Account>>() {});

        assertThat(list).hasSize(2);
        assertThat(list).extracting(Account::getAmount).contains(new BigDecimal(9f), new BigDecimal(5f));
    }
}
