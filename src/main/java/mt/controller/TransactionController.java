package mt.controller;

import com.google.gson.Gson;
import mt.model.TransactionRequest;
import mt.service.TransactionService;
import static spark.Spark.*;

public class TransactionController {

    public TransactionController(TransactionService transactionService, Gson gson) {

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(gson.toJson(e));
        });

        get("/transactions",
                (req, res) -> transactionService.getAll(),
                gson::toJson
        );

        get("/transactions/:id",
                (req, res) -> transactionService.get(req.params(":id")),
                gson::toJson
        );

        post("/transactions",
                (req, res) -> transactionService.create(gson.fromJson(req.body(), TransactionRequest.class)),
                gson::toJson
        );
    }
}
