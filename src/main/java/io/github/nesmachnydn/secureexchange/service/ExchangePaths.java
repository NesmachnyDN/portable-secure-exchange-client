package io.github.nesmachnydn.secureexchange.service;

import io.github.nesmachnydn.secureexchange.config.ExchangeProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ExchangePaths {
    private final Path root;

    public ExchangePaths(ExchangeProperties properties) {
        this.root = Path.of(properties.root()).toAbsolutePath().normalize();
    }

    @PostConstruct
    void initialize() throws IOException {
        Files.createDirectories(clientIn());
        Files.createDirectories(clientOut());
        Files.createDirectories(clientArchiveInbound());
        Files.createDirectories(clientArchiveOutbound());
        Files.createDirectories(clientQuarantine());
        Files.createDirectories(remoteIn());
        Files.createDirectories(remoteOut());
    }

    public Path root() { return root; }
    public Path clientIn() { return root.resolve("client/in"); }
    public Path clientOut() { return root.resolve("client/out"); }
    public Path clientArchiveInbound() { return root.resolve("client/archive/inbound"); }
    public Path clientArchiveOutbound() { return root.resolve("client/archive/outbound"); }
    public Path clientQuarantine() { return root.resolve("client/quarantine"); }
    public Path remoteIn() { return root.resolve("remote/in"); }
    public Path remoteOut() { return root.resolve("remote/out"); }
}
