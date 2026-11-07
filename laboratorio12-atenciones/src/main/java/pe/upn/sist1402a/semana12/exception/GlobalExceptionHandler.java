package pe.upn.sist1402a.semana12.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> respuesta(
            HttpStatus status, String mensaje, HttpServletRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensaje);
        body.put("path", request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(
            NoSuchElementException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> malParametro(
            IllegalArgumentException ex, HttpServletRequest req) {
        return respuesta(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst().map(error -> error.getDefaultMessage())
                .orElse("Datos invalidos");
        return respuesta(HttpStatus.BAD_REQUEST, mensaje, req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> fallo(
            Exception ex, HttpServletRequest req) {
        return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }
}
