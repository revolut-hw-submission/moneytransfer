package mt;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import mt.controller.AccountController;
import mt.controller.TransactionController;
import mt.dao.AccountDao;
import mt.dao.DefaultAccountDao;
import mt.dao.DefaultTransactionDao;
import mt.dao.TransactionDao;
import mt.service.AccountLockProvider;
import mt.service.AccountService;
import mt.service.StaticMapExchangeRateProvider;
import mt.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;

public class MoneyTransfer {

    private static Logger LOG = LoggerFactory.getLogger(MoneyTransfer.class);

    public static void main(String[] args) {
        before(((request, response) -> LOG.info("request: {}, method: {}", request.pathInfo(), request.requestMethod())));

        //wiring up. Should probably use Dagger instead.
        Gson gson = new Gson();
        final AccountDao accountDao = new DefaultAccountDao();
        final AccountLockProvider accountLockProvider = new AccountLockProvider();
        new AccountController(new AccountService(accountDao, accountLockProvider), gson);
        final TransactionDao transactionDao = new DefaultTransactionDao();
        final StaticMapExchangeRateProvider exchangeRateProvider = new StaticMapExchangeRateProvider();
        new TransactionController(new TransactionService(accountDao, transactionDao, accountLockProvider, exchangeRateProvider), gson);

        after((request, response) -> response.type("application/json"));
        exception(JsonParseException.class, (e, req, res) -> {
            res.status(400);
            res.body(gson.toJson(e.getMessage()));
        });

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body("");
        });

        awaitInitialization();
        LOG.info("Started on port {}", port());
    }
}
