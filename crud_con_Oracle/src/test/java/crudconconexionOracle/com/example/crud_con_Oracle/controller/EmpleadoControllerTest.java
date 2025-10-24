package crudconconexionOracle.com.example.crud_con_Oracle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DuplicateMailException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.EmpleadoNoEncontrado;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.GlobalExceptionHandler;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.service.EmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.match.ContentRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@WebMvcTest(controllers = EmpleadoController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("Pruebas del Controlador de Empleados")
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpleadoService empleadoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Empleado empleadoValido;

    @BeforeEach
    void setUp() {
        empleadoValido = new Empleado();
        empleadoValido.setId(1L);
        empleadoValido.setNombre("Juan Pérez");
        empleadoValido.setArea("IT");
        empleadoValido.setEdad(30);
        empleadoValido.setCorreoElectronico("juan.perez@empresa.com");
        empleadoValido.setSueldo(50000.0);
    }

    // ==================== POST /api/empleados ====================

    @Test
    @DisplayName("POST /api/empleados - Crear empleado exitosamente retorna 201")
    void testAddEmpleado_Created() throws Exception {
        when(empleadoService.create(any(Empleado.class))).thenReturn(empleadoValido);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.correoElectronico").value("juan.perez@empresa.com"));

        verify(empleadoService, times(1)).create(any(Empleado.class));
    }

    @Test
    @DisplayName("POST /api/empleados - Nombre vacío retorna 400 con mensaje de error")
    void testAddEmpleado_NombreVacio() throws Exception {
        empleadoValido.setNombre("");

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Solicitud inválida"))
                .andExpect(jsonPath("$.errores.nombre").exists());

        verify(empleadoService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/empleados - Nombre con 1 carácter retorna 400")
    void testAddEmpleado_NombreMuyCorto() throws Exception {
        empleadoValido.setNombre("A");

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value(containsString("2")));

        verify(empleadoService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/empleados - Email inválido retorna 400")
    void testAddEmpleado_EmailInvalido() throws Exception {
        empleadoValido.setCorreoElectronico("correo-sin-arroba");

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.correoElectronico").exists());

        verify(empleadoService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/empleados - Edad menor a 18 retorna 400")
    void testAddEmpleado_EdadMenorA18() throws Exception {
        empleadoValido.setEdad(17);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.edad").value(containsString("18")));

        verify(empleadoService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/empleados - Sueldo negativo retorna 400")
    void testAddEmpleado_SueldoNegativo() throws Exception {
        empleadoValido.setSueldo(-1000.0);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.sueldo").exists());

        verify(empleadoService, never()).create(any());
    }

    @Test
    @DisplayName("POST /api/empleados - Correo duplicado retorna 409")
    void testAddEmpleado_CorreoDuplicado() throws Exception {
        when(empleadoService.create(any(Empleado.class)))
                .thenThrow(new DuplicateMailException("Ya existe un empleado con el correo: juan.perez@empresa.com"));

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoValido)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflicto de datos"))
                .andExpect(jsonPath("$.message").value(containsString("juan.perez@empresa.com")));
    }

    // ==================== GET /api/empleados ====================

    @Test
    @DisplayName("GET /api/empleados - Obtener todos los empleados retorna 200")
    void testGetAllEmpleados() throws Exception {
        Empleado empleado2 = new Empleado();
        empleado2.setId(2L);
        empleado2.setNombre("María García");
        empleado2.setCorreoElectronico("maria@empresa.com");

        when(empleadoService.obtenerTodos()).thenReturn(Arrays.asList(empleadoValido, empleado2));

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombre").value("María García"));

        verify(empleadoService, times(1)).obtenerTodos();
    }

    // ==================== GET /api/empleados/{id} ====================

    @Test
    @DisplayName("GET /api/empleados/{id} - Obtener empleado por ID retorna 200")
    void testGetEmpleadoById_Encontrado() throws Exception {
        when(empleadoService.obtener(1L)).thenReturn(empleadoValido);

        mockMvc.perform(get("/api/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.area").value("IT"));

        verify(empleadoService, times(1)).obtener(1L);
    }

    @Test
    @DisplayName("GET /api/empleados/{id} - Empleado no encontrado retorna 404")
    void testGetEmpleadoById_NoEncontrado() throws Exception {
        when(empleadoService.obtener(999L))
                .thenThrow(new EmpleadoNoEncontrado("Empleado no encontrado con ID: 999"));

        mockMvc.perform(get("/api/empleados/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Empleado no encontrado"))
                .andExpect(jsonPath("$.message").value(containsString("999")))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // ==================== PUT /api/empleados/{id} ====================

    @Test
    @DisplayName("PUT /api/empleados/{id} - Actualizar empleado retorna 200")
    void testActualizarEmpleado_Exitoso() throws Exception {
        Empleado empleadoActualizado = new Empleado();
        empleadoActualizado.setId(1L);
        empleadoActualizado.setNombre("Juan Pérez Actualizado");
        empleadoActualizado.setArea("Recursos Humanos");
        empleadoActualizado.setEdad(31);
        empleadoActualizado.setCorreoElectronico("juan.perez.nuevo@empresa.com");
        empleadoActualizado.setSueldo(55000.0);

        when(empleadoService.actualizar(eq(1L), any(Empleado.class))).thenReturn(empleadoActualizado);

        mockMvc.perform(put("/api/empleados/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoActualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez Actualizado"))
                .andExpect(jsonPath("$.area").value("Recursos Humanos"))
                .andExpect(jsonPath("$.sueldo").value(55000.0));

        verify(empleadoService, times(1)).actualizar(eq(1L), any(Empleado.class));
    }

    // ==================== DELETE /api/empleados/{id} ====================

    @Test
    @DisplayName("DELETE /api/empleados/{id} - Eliminar empleado retorna 204")
    void testEliminarEmpleado_Exitoso() throws Exception {
        doNothing().when(empleadoService).eliminar(1L);

        mockMvc.perform(delete("/api/empleados/1"))
                .andExpect(status().isNoContent());

        verify(empleadoService, times(1)).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE /api/empleados/{id} - Empleado no encontrado retorna 404")
    void testEliminarEmpleado_NoEncontrado() throws Exception {
        doThrow(new EmpleadoNoEncontrado("Empleado no encontrado con ID: 999"))
                .when(empleadoService).eliminar(999L);

        mockMvc.perform(delete("/api/empleados/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Empleado no encontrado"));
    }
}