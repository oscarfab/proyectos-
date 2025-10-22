package crudconconexionOracle.com.example.crud_con_Oracle.repository;

import jakarta.persistence.metamodel.SingularAttribute;
import crudconconexionOracle.com.example.crud_con_Oracle.modelo.Empleado;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    // En EmpleadoRepository.java
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM Empleado e WHERE e.correo_electronico = :correo")
    boolean existsByCorreo_electronico(@Param("correo") String correo);
}
