package crudconconexionOracle.com.example.crud_con_Oracle.service;

import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DuplicateMailException;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.repository.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private EmpleadoService empleadoService;

    @Test
    void testCreateEmpleado_Exitoso() {
        Empleado empleado = new Empleado();
        empleado.setCorreoElectronico("correo@empresa.com");

        when(empleadoRepository.existsByCorreoElectronico(anyString())).thenReturn(false);
        when(empleadoRepository.save(any(Empleado.class))).thenReturn(empleado);

        Empleado resultado = empleadoService.create(empleado);

        assertNotNull(resultado);
        verify(empleadoRepository).save(any(Empleado.class));
    }

    @Test
    void testCreateEmpleado_DuplicateMail() {
        Empleado empleado = new Empleado();
        empleado.setCorreoElectronico("repetido@empresa.com");

        when(empleadoRepository.existsByCorreoElectronico(anyString())).thenReturn(true);

        assertThrows(DuplicateMailException.class, () -> empleadoService.create(empleado));
    }
}