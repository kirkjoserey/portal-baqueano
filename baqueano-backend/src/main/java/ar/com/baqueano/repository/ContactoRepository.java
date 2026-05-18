package ar.com.baqueano.repository;

import ar.com.baqueano.domain.Contacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactoRepository
        extends JpaRepository<Contacto, Long>, JpaSpecificationExecutor<Contacto> {

    List<Contacto> findTop5ByOrderByFechaCreacionDesc();

    long countByEstado(Contacto.Estado estado);
}
