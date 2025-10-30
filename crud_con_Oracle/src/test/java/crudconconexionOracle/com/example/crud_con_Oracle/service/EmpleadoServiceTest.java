package crudconconexionOracle.com.example.crud_con_Oracle.service;

import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DatabaseException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DuplicateMailException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.EmpleadoNoEncontrado;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.repository.EmpleadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Empleados con SPs")
class EmpleadoServiceTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private StoredProcedureQuery storedProcedureQuery;

    @InjectMocks
    private EmpleadoService service;

    private Empleado empleadoMock;

    @BeforeEach
    void setUp() {
        empleadoMock = new Empleado();
        empleadoMock.setId(1L);
        empleadoMock.setNombre("Juan Pérez");
        empleadoMock.setArea("Desarrollo");
        empleadoMock.setEdad(30);
        empleadoMock.setCorreoElectronico("juan@test.com");
        empleadoMock.setSueldo(50000.0);
    }

    // ==================== TESTS CREAR ====================

    @Test
    @DisplayName("Crear empleado exitosamente")
    void testCrearEmpleadoExitoso() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_CREAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_id"))
                .thenReturn(BigDecimal.valueOf(1L));
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Empleado creado exitosamente");

        // Act
        Empleado resultado = service.create(empleadoMock);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(entityManager).createStoredProcedureQuery("SP_CREAR_EMPLEADO");
        verify(storedProcedureQuery).execute();
    }

    @Test
    @DisplayName("Crear empleado con correo duplicado lanza excepción")
    void testCrearEmpleadoCorreoDuplicado() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_CREAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_id"))
                .thenReturn(BigDecimal.valueOf(-1L));
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Ya existe un empleado con el correo: juan@test.com");

        // Act & Assert
        assertThrows(DuplicateMailException.class, () -> service.create(empleadoMock));
    }

    @Test
    @DisplayName("Crear empleado con error de BD lanza DatabaseException")
    void testCrearEmpleadoErrorBD() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_CREAR_EMPLEADO"))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> service.create(empleadoMock));
    }

    // ==================== TESTS OBTENER TODOS ====================

    @Test
    @DisplayName("Obtener todos los empleados exitosamente")
    void testObtenerTodosExitoso() {
        // Arrange
        Object[] fila1 = {
                BigDecimal.valueOf(1L),
                "Juan Pérez",
                "Desarrollo",
                BigDecimal.valueOf(30),
                "juan@test.com",
                BigDecimal.valueOf(50000.0)
        };
        Object[] fila2 = {
                BigDecimal.valueOf(2L),
                "María García",
                "Marketing",
                BigDecimal.valueOf(28),
                "maria@test.com",
                BigDecimal.valueOf(45000.0)
        };

        when(entityManager.createStoredProcedureQuery("SP_OBTENER_TODOS_EMPLEADOS"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getResultList())
                .thenReturn(Arrays.asList(fila1, fila2));

        // Act
        List<Empleado> resultado = service.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("María García", resultado.get(1).getNombre());
    }

    @Test
    @DisplayName("Obtener todos sin empleados retorna lista vacía")
    void testObtenerTodosVacio() {
        // Arrange
        List<Object[]> resultadosVacios = new ArrayList<>();

        when(entityManager.createStoredProcedureQuery("SP_OBTENER_TODOS_EMPLEADOS"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getResultList()).thenReturn(resultadosVacios);

        // Act
        List<Empleado> resultado = service.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Obtener todos con error de BD lanza DatabaseException")
    void testObtenerTodosErrorBD() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_OBTENER_TODOS_EMPLEADOS"))
                .thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> service.obtenerTodos());
    }

    // ==================== TESTS OBTENER POR ID ====================

    @Test
    @DisplayName("Obtener empleado por ID exitosamente")
    void testObtenerPorIdExitoso() {
        // Arrange
        Object[] fila = {
                BigDecimal.valueOf(1L),
                "Juan Pérez",
                "Desarrollo",
                BigDecimal.valueOf(30),
                "juan@test.com",
                BigDecimal.valueOf(50000.0)
        };

        List<Object[]> resultados = new ArrayList<>();
        resultados.add(fila);

        when(entityManager.createStoredProcedureQuery("SP_OBTENER_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getResultList())
                .thenReturn(resultados);

        // Act
        Empleado resultado = service.obtener(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
    }

    @Test
    @DisplayName("Obtener empleado inexistente lanza EmpleadoNoEncontrado")
    void testObtenerPorIdNoExiste() {
        // Arrange
        List<Object[]> resultadosVacios = new ArrayList<>();

        when(entityManager.createStoredProcedureQuery("SP_OBTENER_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getResultList()).thenReturn(resultadosVacios);

        // Act & Assert
        assertThrows(EmpleadoNoEncontrado.class, () -> service.obtener(999L));
    }

    // ==================== TESTS ACTUALIZAR ====================

    @Test
    @DisplayName("Actualizar empleado exitosamente")
    void testActualizarEmpleadoExitoso() {
        // Arrange
        Empleado actualizado = new Empleado();
        actualizado.setNombre("Juan Pérez Actualizado");
        actualizado.setArea("DevOps");
        actualizado.setEdad(31);
        actualizado.setCorreoElectronico("juan.perez@test.com");
        actualizado.setSueldo(55000.0);

        when(entityManager.createStoredProcedureQuery("SP_ACTUALIZAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(1);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Empleado actualizado exitosamente");

        // Act
        Empleado resultado = service.actualizar(1L, actualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez Actualizado", resultado.getNombre());
    }

    @Test
    @DisplayName("Actualizar empleado inexistente lanza EmpleadoNoEncontrado")
    void testActualizarEmpleadoNoExiste() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_ACTUALIZAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(0);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Empleado no encontrado con ID: 999");

        // Act & Assert
        assertThrows(EmpleadoNoEncontrado.class, () -> service.actualizar(999L, empleadoMock));
    }

    @Test
    @DisplayName("Actualizar con correo duplicado lanza DuplicateMailException")
    void testActualizarCorreoDuplicado() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_ACTUALIZAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(-1);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Ya existe un empleado con el correo: juan@test.com");

        // Act & Assert
        assertThrows(DuplicateMailException.class, () -> service.actualizar(1L, empleadoMock));
    }

    // ==================== TESTS ELIMINAR ====================

    @Test
    @DisplayName("Eliminar empleado exitosamente")
    void testEliminarEmpleadoExitoso() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_ELIMINAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(1);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Empleado eliminado exitosamente");

        // Act & Assert
        assertDoesNotThrow(() -> service.eliminar(1L));
        verify(storedProcedureQuery).execute();
    }

    @Test
    @DisplayName("Eliminar empleado inexistente lanza EmpleadoNoEncontrado")
    void testEliminarEmpleadoNoExiste() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_ELIMINAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(0);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Empleado no encontrado con ID: 999");

        // Act & Assert
        assertThrows(EmpleadoNoEncontrado.class, () -> service.eliminar(999L));
    }

    @Test
    @DisplayName("Eliminar con error de BD lanza DatabaseException")
    void testEliminarErrorBD() {
        // Arrange
        when(entityManager.createStoredProcedureQuery("SP_ELIMINAR_EMPLEADO"))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.registerStoredProcedureParameter(anyString(), any(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.setParameter(anyString(), any()))
                .thenReturn(storedProcedureQuery);
        when(storedProcedureQuery.execute()).thenReturn(true);
        when(storedProcedureQuery.getOutputParameterValue("p_filas_afectadas"))
                .thenReturn(-1);
        when(storedProcedureQuery.getOutputParameterValue("p_mensaje"))
                .thenReturn("Error al eliminar empleado");

        // Act & Assert
        assertThrows(DatabaseException.class, () -> service.eliminar(1L));
    }
}