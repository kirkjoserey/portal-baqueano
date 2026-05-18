package ar.com.baqueano.config;

import ar.com.baqueano.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * V2__seed.sql deja un placeholder en password_hash del usuario admin
 * (cualquier string que no empiece con $2 no es un hash BCrypt valido).
 * Este runner detecta ese caso al arranque y regenera el hash con
 * BCryptPasswordEncoder.encode("admin123").
 *
 * En produccion: o se ejecuta una vez para inicializar y luego se cambia la
 * password via UI, o se reemplaza el seed por un hash ya calculado.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminPasswordRunner implements ApplicationRunner {

    private static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        usuarioRepo.findByUsername("admin").ifPresent(u -> {
            String hash = u.getPasswordHash();
            if (hash == null || !hash.startsWith("$2")) {
                log.warn("password_hash del usuario admin no es BCrypt valido: regenerando con clave por defecto");
                u.setPasswordHash(passwordEncoder.encode(DEFAULT_ADMIN_PASSWORD));
                usuarioRepo.save(u);
            }
        });
    }
}
