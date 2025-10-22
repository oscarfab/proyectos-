package crudconconexionOracle.com.example.crud_con_Oracle.service;


import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import manejoExepciones.DatabaseException;
import manejoExepciones.DuplicateMailExeption;
import manejoExepciones.EmpleadoNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crudconconexionOracle.com.example.crud_con_Oracle.repository.EmpleadoRepository;

import java.util.List;
import java.util.Optional;
@Service
public class EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    public Empleado create(Empleado empleado) {
        // Verificar si el correo ya existe
        if (empleadoRepository.existsByCorreo_electronico(empleado.getCorreo_electronico())) {
            throw new DuplicateMailExeption("Ya existe un empleado con el correo: " + empleado.getCorreo_electronico());
        }
        try {
            return empleadoRepository.save(empleado);
        } catch (Exception e) {
            throw new DatabaseException("Error al guardar el empleado en la base de datos", e);
        }
    }

    public List<Empleado> obtenerTodos() {
        try {
            return empleadoRepository.findAll();
        } catch (Exception e) {
            throw new DatabaseException("Error al obtener la lista de empleados", e);
        }
    }

    public Empleado obtener(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id));
    }

    public Empleado actualizar(Long id, Empleado empleadoActualizado) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id));

        // Verificar duplicado de correo (solo si cambia)
        if (!empleado.getCorreo_electronico().equals(empleadoActualizado.getCorreo_electronico()) &&
                empleadoRepository.existsByCorreo_electronico(empleadoActualizado.getCorreo_electronico())) {
            throw new DuplicateMailExeption("Ya existe un empleado con el correo: " + empleadoActualizado.getCorreo_electronico());
        }

        empleado.setNombre(empleadoActualizado.getNombre());
        empleado.setArea(empleadoActualizado.getArea());
        empleado.setEdad(empleadoActualizado.getEdad());
        empleado.setCorreo_electronico(empleadoActualizado.getCorreo_electronico());
        empleado.setSueldo(empleadoActualizado.getSueldo());

        try {
            return empleadoRepository.save(empleado);
        } catch (Exception e) {
            throw new DatabaseException("Error al actualizar el empleado", e);
        }
    }

    public void eliminar(Long id) {
        if (!empleadoRepository.existsById(id)) {
            throw new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id);
        }
        try {
            empleadoRepository.deleteById(id);
        } catch (Exception e) {
            throw new DatabaseException("Error al eliminar el empleado", e);
        }
    }
}