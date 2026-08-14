package io.github.nesmachnydn.secureexchange.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.exchange")
public record ExchangeProperties(String root) {
    public ExchangeProperties {
        if (root == null || root.isBlank()) {
            root = "./exchange";
        }
    }
}
