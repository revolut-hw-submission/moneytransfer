package model;

import java.util.UUID;

public class Transaction {

    private final String id;
    private final Result result;


    private Transaction(Result result) {
        this.id = UUID.randomUUID().toString();
        this.result = result;
    }

    public static Transaction invalid() {
        return new Transaction(Result.INVALID);
    }

    public static Transaction valid() {
        return new Transaction(Result.VALID);
    }


    public String getId() {
        return id;
    }

    public Result getResult() {
        return result;
    }

    public enum Result {
        VALID, INVALID
    }
}
