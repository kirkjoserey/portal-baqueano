package ar.com.baqueano.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cuando el WAR sirve la SPA, las rutas tipo /dashboard, /usuarios, /login no
 * tienen extension ni mapean a un @RestController. Esta config las forwardea
 * a /index.html para que React Router se haga cargo del routing del lado
 * cliente (incluyendo el deep-linking via F5).
 *
 * Los view controllers se evaluan DESPUES de @RestController, asi que /api/v1/**
 * no se ve afectado. Y los recursos estaticos (con extension .js, .css, .ico, ...)
 * caen al resource handler porque el regex excluye paths con punto.
 */
@Configuration
public class SpaConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/{path:^(?!api$)[^\\.]*}")
                .setViewName("forward:/index.html");
        registry.addViewController("/{path:^(?!api$)[^\\.]*}/**")
                .setViewName("forward:/index.html");
    }
}
