package manejoExepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - Empleado no encontrado
    @ExceptionHandler(EmpleadoNoEncontrado.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EmpleadoNoEncontrado ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Empleado no encontrado");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 400 - Validación fallida (usado por @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Solicitud inválida");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        body.put("errores", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - Para casos manuales (si decides usar EmpleadoBadRequest)
    @ExceptionHandler(EmpleadoBadRequest.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(EmpleadoBadRequest ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Solicitud incorrecta");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    // 409 - Correo duplicado
    @ExceptionHandler(DuplicateMailExeption.class)
    public ResponseEntity<Map<String, Object>> handleDuplicateEmail(DuplicateMailExeption ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.CONFLICT.value()); // 409
        body.put("error", "Conflicto de datos");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    // 500 - Errores de base de datos
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Map<String, Object>> handleDatabase(DatabaseException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error de base de datos");
        body.put("message", ex.getMessage());
        // En producción, NO expongas el stack trace
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // Cualquier otra excepción no esperada → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Error interno");
        body.put("message", "Ocurrió un error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}