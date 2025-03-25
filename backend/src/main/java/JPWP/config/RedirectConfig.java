package JPWP.config;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedirectConfig {
    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
        return factory -> {
            factory.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
        };
    }

    private org.apache.catalina.connector.Connector httpToHttpsRedirectConnector() {
        var connector = new org.apache.catalina.connector.Connector();
        connector.setPort(5000); // Listen on HTTP
        connector.setRedirectPort(5001); // Redirect to HTTPS
        return connector;
    }
}