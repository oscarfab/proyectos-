package crudconconexionOracle.com.example.crud_con_Oracle.service;


import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DatabaseException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.DuplicateMailException;
import crudconconexionOracle.com.example.crud_con_Oracle.manejoExepciones.EmpleadoNoEncontrado;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import crudconconexionOracle.com.example.crud_con_Oracle.repository.EmpleadoRepository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class EmpleadoService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    /**
     * Crear nuevo empleado usando SP_CREAR_EMPLEADO
     */
    @Transactional
    public Empleado create(Empleado empleado) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_CREAR_EMPLEADO");

            // Parámetros de entrada
            query.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_area", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_edad", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_sueldo", Double.class, ParameterMode.IN);

            // Parámetros de salida
            query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            // Establecer valores
            query.setParameter("p_nombre", empleado.getNombre());
            query.setParameter("p_area", empleado.getArea());
            query.setParameter("p_edad", empleado.getEdad());
            query.setParameter("p_correo", empleado.getCorreoElectronico());
            query.setParameter("p_sueldo", empleado.getSueldo());

            // Ejecutar
            query.execute();

            // Obtener resultados
            Long nuevoId = ((BigDecimal) query.getOutputParameterValue("p_id")).longValue();
            String mensaje = (String) query.getOutputParameterValue("p_mensaje");

            log.info("Resultado SP_CREAR_EMPLEADO: ID={}, Mensaje={}", nuevoId, mensaje);

            if (nuevoId == -1) {
                throw new DuplicateMailException(mensaje);
            }

            empleado.setId(nuevoId);
            return empleado;

        } catch (DuplicateMailException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al crear empleado: ", e);
            throw new DatabaseException("Error al crear empleado: " + e.getMessage());
        }
    }

    /**
     * Obtener todos los empleados usando SP_OBTENER_TODOS_EMPLEADOS
     */
    @Transactional(readOnly = true)
    public List<Empleado> obtenerTodos() {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_OBTENER_TODOS_EMPLEADOS");
            query.registerStoredProcedureParameter("p_cursor", void.class, ParameterMode.REF_CURSOR);

            query.execute();

            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();

            List<Empleado> empleados = new ArrayList<>();
            for (Object[] fila : resultados) {
                Empleado emp = new Empleado();
                emp.setId(((BigDecimal) fila[0]).longValue());
                emp.setNombre((String) fila[1]);
                emp.setArea((String) fila[2]);
                emp.setEdad(fila[3] != null ? ((BigDecimal) fila[3]).intValue() : null);
                emp.setCorreoElectronico((String) fila[4]);
                emp.setSueldo(fila[5] != null ? ((BigDecimal) fila[5]).doubleValue() : null);
                empleados.add(emp);
            }

            log.info("Se obtuvieron {} empleados", empleados.size());
            return empleados;

        } catch (Exception e) {
            log.error("Error al obtener empleados: ", e);
            throw new DatabaseException("Error al obtener la lista de empleados: " + e.getMessage());
        }
    }

    /**
     * Obtener empleado por ID usando SP_OBTENER_EMPLEADO
     */
    @Transactional(readOnly = true)
    public Empleado obtener(Long id) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_OBTENER_EMPLEADO");
            query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_cursor", void.class, ParameterMode.REF_CURSOR);

            query.setParameter("p_id", id);
            query.execute();

            @SuppressWarnings("unchecked")
            List<Object[]> resultados = query.getResultList();

            if (resultados.isEmpty()) {
                throw new EmpleadoNoEncontrado("Empleado no encontrado con ID: " + id);
            }

            Object[] fila = resultados.get(0);
            Empleado empleado = new Empleado();
            empleado.setId(((BigDecimal) fila[0]).longValue());
            empleado.setNombre((String) fila[1]);
            empleado.setArea((String) fila[2]);
            empleado.setEdad(fila[3] != null ? ((BigDecimal) fila[3]).intValue() : null);
            empleado.setCorreoElectronico((String) fila[4]);
            empleado.setSueldo(fila[5] != null ? ((BigDecimal) fila[5]).doubleValue() : null);

            log.info("Empleado encontrado: {}", empleado.getNombre());
            return empleado;

        } catch (EmpleadoNoEncontrado e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al obtener empleado: ", e);
            throw new DatabaseException("Error al obtener empleado: " + e.getMessage());
        }
    }

    /**
     * Actualizar empleado usando SP_ACTUALIZAR_EMPLEADO
     */
    @Transactional
    public Empleado actualizar(Long id, Empleado empleadoActualizado) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_ACTUALIZAR_EMPLEADO");

            query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_nombre", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_area", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_edad", Integer.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_correo", String.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_sueldo", Double.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_filas_afectadas", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            query.setParameter("p_id", id);
            query.setParameter("p_nombre", empleadoActualizado.getNombre());
            query.setParameter("p_area", empleadoActualizado.getArea());
            query.setParameter("p_edad", empleadoActualizado.getEdad());
            query.setParameter("p_correo", empleadoActualizado.getCorreoElectronico());
            query.setParameter("p_sueldo", empleadoActualizado.getSueldo());

            query.execute();

            Integer filasAfectadas = (Integer) query.getOutputParameterValue("p_filas_afectadas");
            String mensaje = (String) query.getOutputParameterValue("p_mensaje");

            log.info("Resultado SP_ACTUALIZAR_EMPLEADO: Filas={}, Mensaje={}", filasAfectadas, mensaje);

            if (filasAfectadas == 0) {
                throw new EmpleadoNoEncontrado(mensaje);
            }
            if (filasAfectadas == -1) {
                throw new DuplicateMailException(mensaje);
            }

            empleadoActualizado.setId(id);
            return empleadoActualizado;

        } catch (EmpleadoNoEncontrado | DuplicateMailException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al actualizar empleado: ", e);
            throw new DatabaseException("Error al actualizar el empleado: " + e.getMessage());
        }
    }

    /**
     * Eliminar empleado usando SP_ELIMINAR_EMPLEADO
     */
    @Transactional
    public void eliminar(Long id) {
        try {
            StoredProcedureQuery query = entityManager.createStoredProcedureQuery("SP_ELIMINAR_EMPLEADO");

            query.registerStoredProcedureParameter("p_id", Long.class, ParameterMode.IN);
            query.registerStoredProcedureParameter("p_filas_afectadas", Integer.class, ParameterMode.OUT);
            query.registerStoredProcedureParameter("p_mensaje", String.class, ParameterMode.OUT);

            query.setParameter("p_id", id);
            query.execute();

            Integer filasAfectadas = (Integer) query.getOutputParameterValue("p_filas_afectadas");
            String mensaje = (String) query.getOutputParameterValue("p_mensaje");

            log.info("Resultado SP_ELIMINAR_EMPLEADO: Filas={}, Mensaje={}", filasAfectadas, mensaje);

            if (filasAfectadas == 0) {
                throw new EmpleadoNoEncontrado(mensaje);
            }
            if (filasAfectadas == -1) {
                throw new DatabaseException(mensaje);
            }

        } catch (EmpleadoNoEncontrado e) {
            throw e;
        } catch (Exception e) {
            log.error("Error al eliminar empleado: ", e);
            throw new DatabaseException("Error al eliminar el empleado: " + e.getMessage());
        }
    }
}