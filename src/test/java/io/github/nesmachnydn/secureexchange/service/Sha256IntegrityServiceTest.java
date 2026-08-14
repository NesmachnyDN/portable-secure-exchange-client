package io.github.nesmachnydn.secureexchange.service;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class Sha256IntegrityServiceTest {
    private final Sha256IntegrityService service = new Sha256IntegrityService();

    @Test
    void detectsContentModification() {
        byte[] original = "contract payload".getBytes(StandardCharsets.UTF_8);
        byte[] modified = "contract payload!".getBytes(StandardCharsets.UTF_8);

        assertThat(service.digest(original)).hasSize(64);
        assertThat(service.digest(original)).isNotEqualTo(service.digest(modified));
    }
}
