package rest;

import mt.MoneyTransfer;
import mt.model.AccountCreationRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Spark;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static mt.model.Currency.EUR;

public class TestRest {

    @BeforeEach
    void setUp() {
        MoneyTransfer.main(null);
    }

    @AfterEach
    void tearDown() {
        Spark.stop();
    }

    @Test
    void name() {
        given().body(new AccountCreationRequest(EUR, 10f))
                .when().post("http://localhost:4567/accounts")
                .then()
                    .statusCode(200)
                    .body("id", Matchers.notNullValue());
    }
}
