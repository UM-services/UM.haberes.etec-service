package um.haberes.etec.shared.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import feign.FeignException;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

@RestControllerAdvice
public class EtecExceptionHandler {

    @ExceptionHandler(EtecValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidation(EtecValidationException exception) {
        return ResponseEntity.badRequest()
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage()));
    }

    @ExceptionHandler(EtecConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(EtecConflictException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleResponseStatus(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        HttpStatus resolved = status == null ? HttpStatus.BAD_REQUEST : status;
        return ResponseEntity.status(resolved)
                .body(ProblemDetail.forStatusAndDetail(resolved, exception.getReason()));
    }

    /**
     * Propaga el status que devolvió core (400, 404, 409, 500) para no enmascarar sus
     * validaciones de persistencia.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ProblemDetail> handleFeign(FeignException exception) {
        HttpStatus status = HttpStatus.resolve(exception.status());
        HttpStatus resolved = status == null ? HttpStatus.BAD_GATEWAY : status;
        String detail = exception.contentUTF8() == null || exception.contentUTF8().isBlank()
                ? "Error al comunicarse con haberes-core"
                : exception.contentUTF8();
        return ResponseEntity.status(resolved)
                .body(ProblemDetail.forStatusAndDetail(resolved, detail));
    }
}
