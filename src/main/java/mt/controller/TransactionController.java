package mt.controller;

import com.google.gson.Gson;
import mt.model.Transaction;
import mt.model.TransactionRequest;
import mt.service.TransactionService;
import static spark.Spark.*;

public class TransactionController {

    public TransactionController(TransactionService transactionService, Gson gson) {
        get("/transactions",
                (req, res) -> {
                    final String accountId = req.queryParams("accountId");
                    if (accountId != null && !accountId.isEmpty()) {
                        return transactionService.getForAccountId(accountId);
                    }
                    return transactionService.getAll();
                },
                gson::toJson
        );

        get("/transactions/:id",
                (req, res) -> transactionService.get(req.params(":id")),
                gson::toJson
        );

        post("/transactions",
                (req, res) -> {
                    final Transaction transaction = transactionService.create(gson.fromJson(req.body(), TransactionRequest.class));
                    if (transaction.getResult().equals(Transaction.Result.INVALID)) {
                        res.status(400);
                    } else {
                        res.status(201);
                    }
                    return transaction;
                },
                gson::toJson
        );
    }
}
