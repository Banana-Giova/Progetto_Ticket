package it.degroup.it_tickets.common.models;

public class OperationResult<T> {

    private static final String OK_MESSAGE = "Operazione avvenuta con successo.";
    private static final String KO_MESSAGE = "Operazione fallita.";

    private T data;
    private String message;
    private boolean ok;

    public static <T> OperationResult<T> ok(T data, String message) {
        OperationResult<T> result = new OperationResult<>();
        result.ok = true;
        result.data = data;
        result.message = OperationResult.OK_MESSAGE + " " + message;
        return result;
    }

    public static <T> OperationResult<T> ko(String message) {
        OperationResult<T> result = new OperationResult<>();
        result.ok = false;
        result.data = null;
        result.message = OperationResult.KO_MESSAGE + " " + message;
        return result;
    }
}
