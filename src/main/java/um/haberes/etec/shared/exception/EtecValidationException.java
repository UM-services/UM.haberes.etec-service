package um.haberes.etec.shared.exception;

public class EtecValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EtecValidationException(String message) {
        super(message);
    }
}
