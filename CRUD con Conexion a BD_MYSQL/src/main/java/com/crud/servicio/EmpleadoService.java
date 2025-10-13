package com.crud.servicio;

import com.crud.modelo.Empleado;
import com.crud.repositorio.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class EmpleadoService {
    @Autowired
    private EmpleadoRepository empleadoRepository;

    //CREAMOS EL NUEVO EMPLEADO
    public Empleado create(Empleado empleado) {
        return empleadoRepository.save(empleado);
    }

    //Obtener todos los empleados
    public List<Empleado> obtenerTodos() {
        return empleadoRepository.findAll();
    }

    //obtyener emnpleaod por id
    public Optional<Empleado> obtener(Long id) {
        return empleadoRepository.findById(id);
    }
// Actualizar empleado
        public Empleado actualizar(Long id, Empleado empleadoActualizado) {
            // Buscar el empleado existente por ID
            Optional<Empleado> empleadoExistente = empleadoRepository.findById(id);

            // Si el empleado existe, actualizar sus datos
            if (empleadoExistente.isPresent()) {
                Empleado empleado = empleadoExistente.get();
                // Aquí asignamos los datos del empleado actualizado
                empleado.setNombre(empleadoActualizado.getNombre());
                empleado.setArea(empleadoActualizado.getArea());
                empleado.setEdad(empleadoActualizado.getEdad());
                empleado.setCorreo_electronico(empleadoActualizado.getCorreo_electronico());
                empleado.setSueldo(empleadoActualizado.getSueldo());
                // Guardamos el empleado actualizado
                return empleadoRepository.save(empleado);
            }

            // Si el empleado no existe, lanzar una excepción
            throw new RuntimeException("Empleado no encontrado con ID: " + id);
        }
        public void eliminar(Long id) {
            if (empleadoRepository.existsById(id)) {
                    empleadoRepository.deleteById(id);
            } else {
                throw new RuntimeException("Empleado no encontrado con ID: " + id);
            }
    }
}