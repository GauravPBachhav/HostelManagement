package in.gw.main.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WEB MVC CONFIGURATION
 * ----------------------
 * Tells Spring Boot how to serve uploaded files.
 *
 * PROBLEM: By default, Spring only serves files from src/main/resources/static/.
 *          Our uploaded files are in the "uploads/" folder outside of /static/.
 *
 * SOLUTION: We register "uploads/" as a resource location.
 *           So when browser requests /uploads/profiles/photo.jpg,
 *           Spring looks for it in the "uploads/" folder on disk.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL path "/uploads/**" → files in the "uploads/" folder on disk
        // "file:" prefix tells Spring to look on the filesystem (not classpath)
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
