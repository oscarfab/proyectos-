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

@WebMvcTest(EmpleadoController.class)
@DisplayName("Pruebas del Controlador de Empleados")
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpleadoService service;

    @Autowired
    private ObjectMapper objectMapper;

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

    // ==================== TESTS POST (Crear) ====================

    @Test
    @DisplayName("POST /api/empleados - Crear empleado exitosamente")
    void testCrearEmpleadoExitoso() throws Exception {
        when(service.create(any(Empleado.class))).thenReturn(empleadoMock);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.correoElectronico").value("juan@test.com"));

        verify(service, times(1)).create(any(Empleado.class));
    }

    @Test
    @DisplayName("POST /api/empleados - Validación de nombre vacío")
    void testCrearEmpleadoNombreVacio() throws Exception {
        empleadoMock.setNombre("");

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists());
    }

    @Test
    @DisplayName("POST /api/empleados - Validación de edad menor a 18")
    void testCrearEmpleadoEdadInvalida() throws Exception {
        empleadoMock.setEdad(17);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.edad").exists());
    }

    @Test
    @DisplayName("POST /api/empleados - Correo duplicado retorna 409")
    void testCrearEmpleadoCorreoDuplicado() throws Exception {
        when(service.create(any(Empleado.class)))
                .thenThrow(new DuplicateMailException("Ya existe un empleado con el correo: juan@test.com"));

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflicto de datos"))
                .andExpect(jsonPath("$.message").value(containsString("Ya existe un empleado")));
    }

    // ==================== TESTS GET (Obtener todos) ====================

    @Test
    @DisplayName("GET /api/empleados - Obtener todos los empleados")
    void testObtenerTodosEmpleados() throws Exception {
        Empleado empleado2 = new Empleado();
        empleado2.setId(2L);
        empleado2.setNombre("María García");
        empleado2.setCorreoElectronico("maria@test.com");

        List<Empleado> empleados = Arrays.asList(empleadoMock, empleado2);
        when(service.obtenerTodos()).thenReturn(empleados);

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[1].nombre").value("María García"));
    }

    @Test
    @DisplayName("GET /api/empleados - Lista vacía retorna 200")
    void testObtenerTodosVacio() throws Exception {
        when(service.obtenerTodos()).thenReturn(List.of());

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ==================== TESTS GET (Obtener por ID) ====================

    @Test
    @DisplayName("GET /api/empleados/{id} - Obtener empleado existente")
    void testObtenerEmpleadoPorId() throws Exception {
        when(service.obtener(1L)).thenReturn(empleadoMock);

        mockMvc.perform(get("/api/empleados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }

    @Test
    @DisplayName("GET /api/empleados/{id} - Empleado no encontrado retorna 404")
    void testObtenerEmpleadoNoExiste() throws Exception {
        when(service.obtener(999L))
                .thenThrow(new EmpleadoNoEncontrado("Empleado no encontrado con ID: 999"));

        mockMvc.perform(get("/api/empleados/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Empleado no encontrado"))
                .andExpect(jsonPath("$.message").value(containsString("999")));
    }

    // ==================== TESTS PUT (Actualizar) ====================

    @Test
    @DisplayName("PUT /api/empleados/{id} - Actualizar empleado exitosamente")
    void testActualizarEmpleadoExitoso() throws Exception {
        Empleado actualizado = new Empleado();
        actualizado.setId(1L);
        actualizado.setNombre("Juan Pérez Actualizado");
        actualizado.setCorreoElectronico("juan.nuevo@test.com");

        when(service.actualizar(eq(1L), any(Empleado.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/empleados/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Pérez Actualizado"))
                .andExpect(jsonPath("$.correoElectronico").value("juan.nuevo@test.com"));
    }

    @Test
    @DisplayName("PUT /api/empleados/{id} - Actualizar empleado inexistente retorna 404")
    void testActualizarEmpleadoNoExiste() throws Exception {
        when(service.actualizar(eq(999L), any(Empleado.class)))
                .thenThrow(new EmpleadoNoEncontrado("Empleado no encontrado con ID: 999"));

        mockMvc.perform(put("/api/empleados/999")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/empleados/{id} - Actualizar con correo duplicado retorna 409")
    void testActualizarCorreoDuplicado() throws Exception {
        when(service.actualizar(eq(1L), any(Empleado.class)))
                .thenThrow(new DuplicateMailException("Ya existe un empleado con el correo"));

        mockMvc.perform(put("/api/empleados/1")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(empleadoMock)))
                .andExpect(status().isConflict());
    }

    // ==================== TESTS DELETE (Eliminar) ====================

    @Test
    @DisplayName("DELETE /api/empleados/{id} - Eliminar empleado exitosamente")
    void testEliminarEmpleadoExitoso() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/empleados/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    @DisplayName("DELETE /api/empleados/{id} - Eliminar empleado inexistente retorna 404")
    void testEliminarEmpleadoNoExiste() throws Exception {
        doThrow(new EmpleadoNoEncontrado("Empleado no encontrado con ID: 999"))
                .when(service).eliminar(999L);

        mockMvc.perform(delete("/api/empleados/999"))
                .andExpect(status().isNotFound());
    }
}