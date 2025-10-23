package crudconconexionOracle.com.example.crud_con_Oracle.repository;

import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    boolean existsByCorreoElectronico(String correoElectronico);
}
