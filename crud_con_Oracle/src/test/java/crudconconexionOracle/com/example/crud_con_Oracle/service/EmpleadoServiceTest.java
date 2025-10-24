package crudconconexionOracle.com.example.crud_con_Oracle.service;

import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DatabaseException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DuplicateMailException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.EmpleadoNoEncontrado;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.repository.EmpleadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas del Servicio de Empleados")
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    private Empleado empleadoEjemplo;

    @BeforeEach
    void setUp() {
        empleadoEjemplo = new Empleado();
        empleadoEjemplo.setId(1L);
        empleadoEjemplo.setNombre("Juan Pérez");
        empleadoEjemplo.setArea("IT");
        empleadoEjemplo.setEdad(30);
        empleadoEjemplo.setCorreoElectronico("juan.perez@empresa.com");
        empleadoEjemplo.setSueldo(50000.0);
    }

    @Test
    @DisplayName("Crear empleado exitosamente")
    void testCrearEmpleadoExitoso() {
        // Arrange: configurar el escenario
        when(empleadoRepository.existsByCorreoElectronico(empleadoEjemplo.getCorreoElectronico()))
                .thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class)))
                .thenReturn(empleadoEjemplo);

        // Act: ejecutar el método a probar
        Empleado resultado = empleadoService.create(empleadoEjemplo);

        // Assert: verificar resultados
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(empleadoRepository, times(1)).existsByCorreoElectronico(empleadoEjemplo.getCorreoElectronico());
        verify(empleadoRepository, times(1)).save(empleadoEjemplo);
    }

    @Test
    @DisplayName("Crear empleado con correo duplicado lanza excepción")
    void testCrearEmpleadoCorreoDuplicado() {
        // Arrange
        when(empleadoRepository.existsByCorreoElectronico(empleadoEjemplo.getCorreoElectronico()))
                .thenReturn(true);

        // Act & Assert
        DuplicateMailException exception = assertThrows(
                DuplicateMailException.class,
                () -> empleadoService.create(empleadoEjemplo)
        );

        assertTrue(exception.getMessage().contains("juan.perez@empresa.com"));
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener todos los empleados")
    void testObtenerTodosLosEmpleados() {
        // Arrange
        Empleado empleado2 = new Empleado();
        empleado2.setId(2L);
        empleado2.setNombre("María García");
        empleado2.setCorreoElectronico("maria@empresa.com");

        List<Empleado> listaEmpleados = Arrays.asList(empleadoEjemplo, empleado2);
        when(empleadoRepository.findAll()).thenReturn(listaEmpleados);

        // Act
        List<Empleado> resultado = empleadoService.obtenerTodos();

        // Assert
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("María García", resultado.get(1).getNombre());
        verify(empleadoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Obtener todos lanza DatabaseException en caso de error")
    void testObtenerTodosConError() {
        // Arrange
        when(empleadoRepository.findAll()).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        assertThrows(DatabaseException.class, () -> empleadoService.obtenerTodos());
    }

    @Test
    @DisplayName("Obtener empleado por ID exitosamente")
    void testObtenerEmpleadoPorId() {
        // Arrange
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoEjemplo));

        // Act
        Empleado resultado = empleadoService.obtener(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(empleadoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Obtener empleado por ID no encontrado lanza excepción")
    void testObtenerEmpleadoNoEncontrado() {
        // Arrange
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EmpleadoNoEncontrado exception = assertThrows(
                EmpleadoNoEncontrado.class,
                () -> empleadoService.obtener(999L)
        );

        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    @DisplayName("Actualizar empleado exitosamente")
    void testActualizarEmpleado() {
        // Arrange
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setNombre("Juan Pérez Actualizado");
        empleadoActualizado.setArea("Recursos Humanos");
        empleadoActualizado.setEdad(31);
        empleadoActualizado.setCorreoElectronico("juan.perez@empresa.com");
        empleadoActualizado.setSueldo(55000.0);

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoEjemplo));
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleadoEjemplo);

        // Act
        Empleado resultado = empleadoService.actualizar(1L, empleadoActualizado);

        // Assert
        assertNotNull(resultado);
        verify(empleadoRepository, times(1)).findById(1L);
        verify(empleadoRepository, times(1)).save(empleadoEjemplo);
    }

    @Test
    @DisplayName("Actualizar empleado con correo duplicado lanza excepción")
    void testActualizarEmpleadoCorreoDuplicado() {
        // Arrange
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setCorreoElectronico("otro@empresa.com");

        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoEjemplo));
        when(empleadoRepository.existsByCorreoElectronico("otro@empresa.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateMailException.class,
                () -> empleadoService.actualizar(1L, empleadoActualizado));
        verify(empleadoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar empleado no encontrado lanza excepción")
    void testActualizarEmpleadoNoEncontrado() {
        // Arrange
        when(empleadoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EmpleadoNoEncontrado.class,
                () -> empleadoService.actualizar(999L, empleadoEjemplo));
    }

    @Test
    @DisplayName("Actualizar lanza DatabaseException en caso de error al guardar")
    void testActualizarConErrorDeBaseDatos() {
        // Arrange
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleadoEjemplo));
        when(empleadoRepository.save(any())).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> empleadoService.actualizar(1L, empleadoEjemplo));
    }

    @Test
    @DisplayName("Eliminar empleado exitosamente")
    void testEliminarEmpleado() {
        // Arrange
        when(empleadoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(empleadoRepository).deleteById(1L);

        // Act
        empleadoService.eliminar(1L);

        // Assert
        verify(empleadoRepository, times(1)).existsById(1L);
        verify(empleadoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar empleado no encontrado lanza excepción")
    void testEliminarEmpleadoNoEncontrado() {
        // Arrange
        when(empleadoRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(EmpleadoNoEncontrado.class,
                () -> empleadoService.eliminar(999L));
        verify(empleadoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar lanza DatabaseException en caso de error")
    void testEliminarConErrorDeBaseDatos() {
        // Arrange
        when(empleadoRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Error de BD")).when(empleadoRepository).deleteById(1L);

        // Act & Assert
        assertThrows(DatabaseException.class,
                () -> empleadoService.eliminar(1L));
    }
}