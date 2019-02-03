package rest;

import mt.MoneyTransfer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import spark.Spark;

import java.io.IOException;
import java.net.URL;

public class BaseRestIt {

    @BeforeAll
    static void setUp() throws InterruptedException {
        MoneyTransfer.main(null);
        Spark.awaitInitialization();
        //Following part is dirty.
        // spark.Spark.awaitInitialization does not help. Discussable.
        try {
            new URL("http://localhost:4567/accounts").getContent();
        } catch (IOException e) {
            Thread.sleep(1000);
            setUp();
        }
    }

    @AfterAll
    static void tearDown() {
        Spark.stop();
    }

}
