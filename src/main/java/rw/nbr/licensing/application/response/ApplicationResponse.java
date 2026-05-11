package rw.nbr.licensing.application.response;

public class ApplicationResponse<T> {

    private String message;
    private T data;
    private boolean success;

    public ApplicationResponse(String message, T data, boolean success) {
        this.message = message;
        this.data = data;
        this.success = success;
    }
}