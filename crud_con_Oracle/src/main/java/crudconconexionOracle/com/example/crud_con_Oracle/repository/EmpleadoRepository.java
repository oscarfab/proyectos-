package crudconconexionOracle.com.example.crud_con_Oracle.repository;

import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    @Procedure(name="SP_EXISTE_CORREO")
    Integer existeCorreo(@Param("p_correo") String correo);
    //para pruebas se deja el que se tenia
    boolean existsByCorreoElectronico(String correoElectronico);
}
