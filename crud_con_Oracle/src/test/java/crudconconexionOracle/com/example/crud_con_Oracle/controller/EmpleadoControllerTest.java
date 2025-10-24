package crudconconexionOracle.com.example.crud_con_Oracle.controller;

import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.GlobalExceptionHandler;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.client.match.ContentRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = EmpleadoController.class)
@Import(GlobalExceptionHandler.class)
class EmpleadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal") // elimina el warning deprecado
    @MockBean
    private EmpleadoService empleadoService;

    @Test
    void testGetAllEmpleados() throws Exception {
        when(empleadoService.obtenerTodos()).thenReturn(List.of(new Empleado()));

        mockMvc.perform((RequestBuilder) get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$").isArray());
    }

    @Test
    void testAddEmpleado_Created() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan Pérez");
        empleado.setCorreoElectronico("juan@empresa.com");

        when(empleadoService.create(any(Empleado.class))).thenReturn(empleado);

        mockMvc.perform(post("/api/empleados")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content("""
                            {
                              "nombre": "Juan Pérez",
                              "correoElectronico": "juan@empresa.com",
                              "edad": 25
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect((ResultMatcher) jsonPath("$.nombre").value("Juan Pérez"));
    }
}