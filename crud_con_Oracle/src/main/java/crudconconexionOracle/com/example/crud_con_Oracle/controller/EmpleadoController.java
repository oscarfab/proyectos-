package crudconconexionOracle.com.example.crud_con_Oracle.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import manejoExepciones.EmpleadoNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import crudconconexionOracle.com.example.crud_con_Oracle.service.EmpleadoService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins="*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.DELETE,RequestMethod.PUT})
@Tag(name = "Gestión de Empleados",
        description = "API REST para operaciones CRUD de empleados")
public class EmpleadoController {

    @Autowired
    private EmpleadoService service;

    @PostMapping
    @Operation(
            summary = "Crear nuevo empleado",
            description = "Crea un nuevo empleado en el sistema. El ID se genera automáticamente."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Empleado creado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Empleado.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la solicitud",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    public ResponseEntity<Empleado> addEmpleado(
            @Valid @RequestBody
            @Parameter(description = "Datos del empleado a crear (sin ID)")
            Empleado empleado) {
        Empleado nuevoEmpleado = service.create(empleado);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoEmpleado);
    }

    @GetMapping
    @Operation(
            summary = "Obtener todos los empleados",
            description = "Retorna una lista con todos los empleados registrados en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de empleados obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Empleado.class)
                    )
            )
    })
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado> empleados = service.obtenerTodos();
        return ResponseEntity.ok(empleados);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        Optional<Empleado> empleado = Optional.ofNullable(service.obtener(id));
        if (empleado.isEmpty()) {
            throw new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id);
        }
        return ResponseEntity.ok(empleado.get());
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar empleado",
            description = "Actualiza los datos de un empleado existente. El ID debe existir en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Empleado actualizado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Empleado.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos",
                    content = @Content
            )
    })
    public ResponseEntity<Empleado> actualizarEmpleado(
            @Parameter(description = "ID del empleado a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody
            @Parameter(description = "Nuevos datos del empleado (sin incluir ID)")
            Empleado empleado) {
        Empleado actualizado = service.actualizar(id, empleado);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar empleado",
            description = "Elimina permanentemente un empleado del sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Empleado eliminado exitosamente"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Empleado no encontrado",
                    content = @Content
            )
    })
    public ResponseEntity<Void> eliminarEmpleado(
            @Parameter(description = "ID del empleado a eliminar", example = "1")
            @PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}