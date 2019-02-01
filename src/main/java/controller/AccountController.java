package controller;


import com.google.gson.Gson;
import model.AccountCreationRequest;
import service.AccountService;

import static spark.Spark.*;


public class AccountController {

    public AccountController(AccountService accountService, Gson gson) {

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(gson.toJson(e));
        });

        get("/accounts",
                ((req, res) -> accountService.getAll()),
                gson::toJson
        );

        get("/accounts/:id",
                ((req, res) -> accountService.get(req.params(":id"))),
                gson::toJson
        );

        delete("/accounts/:id",
                (req, res) -> accountService.delete(req.params(":id"))
        );

        post("/accounts",
                (req, res) -> accountService.create(gson.fromJson(req.body(), AccountCreationRequest.class)),
                gson::toJson
        );
    }
}
