package rw.nbr.licensing.exception;


public class ApiException extends RuntimeException {

    private final int status;
    private final String code;
    private final String details;

    public ApiException(int status, String message, String code, String details) {
        super(message);
        this.status = status;
        this.code = code;
        this.details = details;
    }

    public int getStatus() {

        return status;
    }

    public String getCode() {
        return code;
    }

    public String getDetails() {
        return details;
    }
}
