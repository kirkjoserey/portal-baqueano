package ar.com.baqueano.repository;

import ar.com.baqueano.domain.Parametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParametroRepository extends JpaRepository<Parametro, Long> {

    Optional<Parametro> findByClave(String clave);

    boolean existsByClave(String clave);
}
