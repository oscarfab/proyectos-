package crudconconexionOracle.com.example.crud_con_Oracle.exeption;

import crudconconexionOracle.com.example.crud_con_Oracle.controller.EmpleadoController;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.EmpleadoNoEncontrado;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.GlobalExceptionHandler;
import crudconconexionOracle.com.example.crud_con_Oracle.service.EmpleadoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(controllers = EmpleadoController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal") // Quita el warning de deprecation temporalmente
    @MockBean
    private EmpleadoService service;

    @Test
    void testEmpleadoNoEncontrado() throws Exception {
        when(service.obtener(anyLong())).thenThrow(new EmpleadoNoEncontrado("No existe el empleado"));

        mockMvc.perform(get("/api/empleados/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Empleado no encontrado"));
    }
}