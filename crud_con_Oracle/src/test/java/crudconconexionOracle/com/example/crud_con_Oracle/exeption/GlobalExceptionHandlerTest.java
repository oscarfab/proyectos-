package crudconconexionOracle.com.example.crud_con_Oracle.exeption;

import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Nested
@DisplayName("Pruebas del Manejador Global de Excepciones")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Maneja EmpleadoNoEncontrado con status 404")
    void testHandleEmpleadoNoEncontrado() {
        // Arrange
        EmpleadoNoEncontrado exception = new EmpleadoNoEncontrado("Empleado no encontrado con ID: 1");

        // Act
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleNotFound(exception);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Empleado no encontrado", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("ID: 1"));
        assertNotNull(response.getBody().get("timestamp")); // ✅ corregido
        assertTrue(response.getBody().get("timestamp") instanceof LocalDateTime);
    }

    @Test
    @DisplayName("Maneja MethodArgumentNotValidException con status 400")
    void testHandleValidation() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("empleado", "nombre", "El nombre es obligatorio");
        FieldError fieldError2 = new FieldError("empleado", "edad", "La edad mínima es 18 años");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleValidation(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Solicitud inválida", response.getBody().get("error"));

        @SuppressWarnings("unchecked")
        Map<String, String> errores = (Map<String, String>) response.getBody().get("errores");

        assertEquals(2, errores.size());
        assertEquals("El nombre es obligatorio", errores.get("nombre"));
        assertEquals("La edad mínima es 18 años", errores.get("edad"));
    }

    @Test
    @DisplayName("Maneja EmpleadoBadRequest con status 400")
    void testHandleBadRequest() {
        EmpleadoBadRequest exception = new EmpleadoBadRequest("Solicitud incorrecta: datos faltantes");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBadRequest(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Solicitud incorrecta", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("datos faltantes"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Maneja DuplicateMailException con status 409")
    void testHandleDuplicateEmail() {
        DuplicateMailException exception =
                new DuplicateMailException("Ya existe un empleado con el correo: test@empresa.com");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDuplicateEmail(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflicto de datos", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("test@empresa.com"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Maneja DataIntegrityViolationException con status 409")
    void testHandleDataIntegrityViolation() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Constraint violation");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDataIntegrityViolation(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflicto de datos", response.getBody().get("error"));
        assertEquals("El correo electrónico ya está registrado en el sistema.", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Maneja DatabaseException con status 500")
    void testHandleDatabase() {
        DatabaseException exception = new DatabaseException("Error al conectar con la base de datos");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleDatabase(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Error de base de datos", response.getBody().get("error"));
        assertTrue(response.getBody().get("message").toString().contains("base de datos"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Maneja Exception genérica con status 500")
    void testHandleGeneric() {
        Exception exception = new RuntimeException("Error inesperado del sistema");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleGeneric(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Error interno", response.getBody().get("error"));
        assertEquals("Ocurrió un error inesperado", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Todas las respuestas incluyen timestamp")
    void testTodasLasRespuestasIncluyenTimestamp() {
        ResponseEntity<Map<String, Object>> response1 =
                exceptionHandler.handleNotFound(new EmpleadoNoEncontrado("Test"));
        ResponseEntity<Map<String, Object>> response2 =
                exceptionHandler.handleDuplicateEmail(new DuplicateMailException("Test"));
        ResponseEntity<Map<String, Object>> response3 =
                exceptionHandler.handleDatabase(new DatabaseException("Test"));

        assertNotNull(response1.getBody().get("timestamp"));
        assertNotNull(response2.getBody().get("timestamp"));
        assertNotNull(response3.getBody().get("timestamp"));
    }
}