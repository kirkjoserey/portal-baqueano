package ar.com.baqueano.repository;

import ar.com.baqueano.domain.Prospecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProspectoRepository extends JpaRepository<Prospecto, Long> {

    Page<Prospecto> findByActivoTrue(Pageable pageable);

    boolean existsByEmailAndActivoTrue(String email);

    Optional<Prospecto> findByIdAndActivoTrue(Long id);
}
