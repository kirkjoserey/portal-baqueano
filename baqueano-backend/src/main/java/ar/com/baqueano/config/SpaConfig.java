package ar.com.baqueano.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * Sirve la SPA empaquetada en classpath:/static/ con fallback a /index.html
 * para rutas que no corresponden a un archivo real (deep-linking de React
 * Router como /dashboard, /usuarios, /usuarios/42, etc.).
 *
 * Estrategia:
 *  - Primero intenta resolver el path como un archivo bajo classpath:/static/
 *    (index.html, /assets/index-*.js, /favicon.ico, etc.).
 *  - Si NO existe Y la ruta tiene un punto (extension), devuelve null
 *    -> el cliente recibe 404 (lo correcto para un asset que no esta).
 *  - Si NO existe Y la ruta NO tiene punto, devuelve /static/index.html
 *    -> React Router toma la ruta del lado cliente.
 *
 * Los @RequestMapping de @RestController (/api/v1/**, /actuator/**, etc.) se
 * resuelven ANTES que el ResourceHandler, asi que no se ven afectados.
 *
 * Nota: en versiones anteriores usabamos ViewControllers con un regex sobre el
 * primer segmento del path, pero el regex se tragaba /assets/index-*.css
 * porque "assets" no tiene punto. El ResourceResolver es mas robusto porque
 * pregunta por la existencia del archivo real antes de decidir el fallback.
 */
@Configuration
public class SpaConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location)
                            throws IOException {
                        Resource resource = location.createRelative(resourcePath);
                        if (resource.exists() && resource.isReadable()) {
                            return resource;
                        }
                        // Si el path apunta a algo con extension (asset que no esta),
                        // devolver null -> 404, no fallback a la SPA.
                        if (resourcePath.contains(".")) {
                            return null;
                        }
                        // Ruta sin extension: la maneja React Router del lado cliente.
                        return new ClassPathResource("/static/index.html");
                    }
                });
    }
}
