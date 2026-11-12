package um.haberes.etec.shared.exception;

public class EtecConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EtecConflictException(String message) {
        super(message);
    }
}
