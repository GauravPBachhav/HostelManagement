package in.gw.main.Config;

import jakarta.servlet.MultipartConfigElement;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.tomcat.servlet.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MULTIPART / TOMCAT CONFIGURATION
 * ---------------------------------
 * Fixes HTTP 413 (Payload Too Large) errors on file upload forms.
 *
 * Two things are done here:
 * 1. MultipartConfigElement bean — sets max file size (10MB) and request size (50MB)
 *    at the Servlet level. This is what actually controls multipart parsing limits.
 * 2. TomcatServletWebServerFactory — removes Tomcat connector POST size limit
 *    so Tomcat doesn't reject the request before Spring can process it.
 */
@Configuration
public class MultipartConfig {

    /**
     * Explicitly configure multipart limits using raw Jakarta Servlet API.
     * Parameters: location (temp dir), maxFileSize, maxRequestSize, fileSizeThreshold
     *
     * maxFileSize   = 10 MB per file
     * maxRequestSize = 50 MB total (form fields + all files)
     * fileSizeThreshold = 0 (write to disk immediately)
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(
                System.getProperty("java.io.tmpdir"),   // temp directory
                10L * 1024 * 1024,                      // 10 MB max file size
                50L * 1024 * 1024,                      // 50 MB max request size
                0                                       // write to disk immediately
        );
    }

    /**
     * Customize embedded Tomcat connector to remove POST size limits.
     * Without this, the Tomcat connector rejects multipart requests
     * before they even reach Spring's multipart resolver.
     */
    @Bean
    public TomcatServletWebServerFactory tomcatFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> {
            connector.setMaxPostSize(-1);
            if (connector.getProtocolHandler() instanceof AbstractHttp11Protocol<?> protocol) {
                protocol.setMaxSwallowSize(-1);
            }
        });
        return factory;
    }
}
