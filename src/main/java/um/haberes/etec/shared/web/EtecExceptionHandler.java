package um.haberes.etec.shared.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import um.haberes.etec.shared.exception.EtecConflictException;
import um.haberes.etec.shared.exception.EtecValidationException;

@RestControllerAdvice
public class EtecExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        String detail = extractDetail(exception.contentUTF8());
        return ResponseEntity.status(resolved)
                .body(ProblemDetail.forStatusAndDetail(resolved, detail));
    }

    /**
     * Core devuelve un cuerpo JSON (ProblemDetail o error estándar) cuando rechaza una
     * escritura. Se extrae el mensaje legible para no mostrar el JSON crudo al usuario.
     */
    private String extractDetail(String body) {
        if (body == null || body.isBlank()) {
            return "Error al comunicarse con haberes-core";
        }
        try {
            JsonNode node = objectMapper.readTree(body);
            for (String field : new String[] {"detail", "message", "error"}) {
                JsonNode value = node.get(field);
                if (value != null && !value.asText().isBlank()) {
                    return value.asText();
                }
            }
        } catch (JsonProcessingException e) {
            // El cuerpo no es JSON: se devuelve tal cual.
        }
        return body;
    }
}
