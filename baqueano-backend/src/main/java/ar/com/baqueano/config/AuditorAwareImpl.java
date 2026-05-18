package ar.com.baqueano.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Fase 3: devuelve "system" como auditor por defecto.
 * Fase 5 (Spring Security): se sobreescribe para leer del SecurityContext.
 */
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of("system");
    }
}
