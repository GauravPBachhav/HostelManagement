package in.gw.main.Config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WEB MVC CONFIGURATION
 * ----------------------
 * Maps the /uploads/** URL path to the physical "uploads/" directory on disk.
 * This allows uploaded profile photos and query photos to be served directly.
 *
 * Example:
 *   DB stores:  "profiles/abc-123.jpg"
 *   URL:        /uploads/profiles/abc-123.jpg
 *   Disk:       uploads/profiles/abc-123.jpg
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get("uploads").toAbsolutePath().normalize();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath.toString() + "/");
    }
}
