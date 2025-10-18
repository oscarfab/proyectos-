package repository;

import jakarta.persistence.metamodel.SingularAttribute;
import modelo.Empleado;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Optional;
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Integer> {
    Optional<Empleado> findById(SingularAttribute<AbstractPersistable, Serializable> id);
}
