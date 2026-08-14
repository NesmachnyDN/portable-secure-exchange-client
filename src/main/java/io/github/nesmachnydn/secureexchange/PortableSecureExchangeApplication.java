package io.github.nesmachnydn.secureexchange;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PortableSecureExchangeApplication {
    public static void main(String[] args) {
        SpringApplication.run(PortableSecureExchangeApplication.class, args);
    }
}
