package mt.model;

import java.util.UUID;

public class Transaction {

    private final String id;
    private final Result result;
    private final String from;
    private final String to;


    private Transaction(Result result, String from, String to) {
        this.from = from;
        this.to = to;
        this.id = UUID.randomUUID().toString();
        this.result = result;
    }

    public static Transaction invalid(String from, String to) {
        return new Transaction(Result.INVALID, from, to);
    }

    public static Transaction valid(String from, String to) {
        return new Transaction(Result.VALID, from, to);
    }


    public String getId() {
        return id;
    }

    public Result getResult() {
        return result;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public enum Result {
        VALID, INVALID
    }
}
