package mt.controller;


import com.google.gson.Gson;
import mt.model.Account;
import mt.model.AccountCreationRequest;
import mt.service.AccountService;

import static spark.Spark.*;


public class AccountController {

    public AccountController(AccountService accountService, Gson gson) {

        get("/accounts",
                ((req, res) -> accountService.getAll()),
                gson::toJson
        );

        get("/accounts/:id", ((req, res) -> {
                    final Account account = accountService.get(req.params(":id"));
                    if (account == null) {
                        res.status(404);
                    }
                    return account;
                }),
                gson::toJson
        );

        delete("/accounts/:id",
                (req, res) -> {
                    final Account deleted = accountService.delete(req.params(":id"));
                    if (deleted == null) {
                        res.status(404);
                    } else {
                        res.status(204);
                    }
                    return "";

                }
        );

        post("/accounts",
                (req, res) -> {
                    final Account account = accountService.create(gson.fromJson(req.body(), AccountCreationRequest.class));
                    if (account != null) {
                        res.status(201);
                    }
                    return account;
                },
                gson::toJson
        );

    }
}
