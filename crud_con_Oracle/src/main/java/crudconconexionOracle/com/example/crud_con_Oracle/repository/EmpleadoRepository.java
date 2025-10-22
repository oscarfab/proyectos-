package crudconconexionOracle.com.example.crud_con_Oracle.repository;

import jakarta.persistence.metamodel.SingularAttribute;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findById(SingularAttribute<AbstractPersistable, Serializable> id);
    // En EmpleadoRepository.java
    boolean existsByCorreo_electronico(String correo_electronico);
}
