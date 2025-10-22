package crudconconexionOracle.com.example.crud_con_Oracle.service;


import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
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

    // Crear nuevo empleado
    public Empleado create(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    // Obtener todos los empleados
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    // Obtener empleado por id - LANZA EXCEPCIÓN SI NO EXISTE
    public Empleado obtener(Long id) {
        return empleadoRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id));
    }

    // Actualizar empleado
    public Empleado actualizar(Long id, Empleado empleadoActualizado) {
        Empleado empleado = empleadoRepository.findById(Math.toIntExact(id))
                .orElseThrow(() -> new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id));

        empleado.setNombre(empleadoActualizado.getNombre());
        empleado.setArea(empleadoActualizado.getArea());
        empleado.setEdad(empleadoActualizado.getEdad());
        empleado.setCorreo_electronico(empleadoActualizado.getCorreo_electronico());
        empleado.setSueldo(empleadoActualizado.getSueldo());

        return empleadoRepository.save(empleado);
    }

    // Eliminar empleado
    public void eliminar(Long id) {
        if (!empleadoRepository.existsById(Math.toIntExact(id))) {
            throw new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id);
        }
        empleadoRepository.deleteById(Math.toIntExact(id));
    }
}