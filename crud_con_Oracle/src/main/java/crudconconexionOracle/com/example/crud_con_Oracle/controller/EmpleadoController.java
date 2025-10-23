package crudconconexionOracle.com.example.crud_con_Oracle.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import crudconconexionOracle.com.example.crud_con_Oracle.service.EmpleadoService;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@Tag(name = "Gestión de Empleados", description = "API REST para operaciones CRUD de empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService service;

    @PostMapping
    @Operation(summary = "Crear nuevo empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Empleado creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<Empleado> addEmpleado(@Valid @RequestBody Empleado empleado) {
        Empleado nuevoEmpleado = service.create(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @GetMapping
    @Operation(summary = "Obtener todos los empleados")
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = service.obtenerTodos();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empleado por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado encontrado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        // El service lanza la excepción automáticamente si no existe
        Empleado empleado = service.obtener(id);
        return ResponseEntity.ok(empleado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Empleado actualizado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    public ResponseEntity<Empleado> actualizarEmpleado(
            @PathVariable Long id,
            @Valid @RequestBody Empleado empleado) {
        Empleado actualizado = service.actualizar(id, empleado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar empleado")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Empleado eliminado"),
            @ApiResponse(responseCode = "404", description = "Empleado no encontrado")
    })
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}