import com.google.gson.Gson;
import controller.AccountController;
import controller.TransactionController;
import dao.AccountDao;
import dao.DefaultAccountDao;
import dao.DefaultTransactionDao;
import dao.TransactionDao;
import service.AccountLockProvider;
import service.AccountService;
import service.StaticMapExchangeRateProvider;
import service.TransactionService;
import spark.Spark;

public class MoneyTransfer {

    public static void main(String[] args) {
        //wiring up. Should probably use Dagger instead.
        Gson gson = new Gson();
        final AccountDao accountDao = new DefaultAccountDao();
        final AccountLockProvider accountLockProvider = new AccountLockProvider();
        new AccountController(new AccountService(accountDao, accountLockProvider), gson);
        final TransactionDao transactionDao = new DefaultTransactionDao();
        final StaticMapExchangeRateProvider exchangeRateProvider = new StaticMapExchangeRateProvider();
        new TransactionController(new TransactionService(accountDao, transactionDao, accountLockProvider, exchangeRateProvider), gson);

        System.out.println("Started on port " + Spark.port());
    }
}
