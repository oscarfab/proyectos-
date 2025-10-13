package com.crud.controller;

import com.crud.modelo.Empleado;
import com.crud.repositorio.EmpleadoRepository;
import com.crud.servicio.EmpleadoService;
import jakarta.persistence.Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    private EmpleadoService service;
    //creando la llamada post
    @PostMapping
    public ResponseEntity<Empleado> addEmpleado(@RequestBody Empleado empleado) {
        Empleado nuevoEmpleado=service.create(empleado);
        return ResponseEntity.ok().body(nuevoEmpleado);
    }
    @GetMapping
    public ResponseEntity<List<Empleado>> getAllEmpleados() {
        List<Empleado>empleados=service.obtenerTodos();
        return ResponseEntity.ok().body(empleados);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> getEmpleadoById(@PathVariable Long id) {
        Optional<Empleado>empleado= service.obtener(id);
        return empleado.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(
            @PathVariable Long id,
            @RequestBody Empleado empleado) {
        Empleado actualizado = service.actualizar(id, empleado);
        return ResponseEntity.ok(actualizado);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
