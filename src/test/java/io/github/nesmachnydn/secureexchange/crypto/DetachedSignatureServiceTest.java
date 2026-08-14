package io.github.nesmachnydn.secureexchange.crypto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.ECGenParameterSpec;

import static org.assertj.core.api.Assertions.assertThat;

class DetachedSignatureServiceTest {
    @TempDir
    Path tempDir;

    private DetachedSignatureService service;
    private KeyPair signingKeyPair;

    @BeforeEach
    void setUp() throws Exception {
        service = new DetachedSignatureService(new JcaDetachedSignatureProvider());
        signingKeyPair = newEcKeyPair();
    }

    @Test
    void verifiesUnchangedFileAndRejectsModifiedContent() throws Exception {
        Path file = tempDir.resolve("payload.txt");
        Files.writeString(file, "approved payload", StandardCharsets.UTF_8);

        DetachedSignature signature = service.sign(file, signingKeyPair.getPrivate(), "demo-client-key");

        assertThat(service.verify(file, signature, signingKeyPair.getPublic())).isTrue();

        Files.writeString(file, "approved payload - modified", StandardCharsets.UTF_8);

        assertThat(service.verify(file, signature, signingKeyPair.getPublic())).isFalse();
    }

    @Test
    void rejectsSignatureCreatedForAnotherKey() throws Exception {
        Path file = tempDir.resolve("payload.txt");
        Files.writeString(file, "approved payload", StandardCharsets.UTF_8);
        DetachedSignature signature = service.sign(file, signingKeyPair.getPrivate(), "demo-client-key");
        KeyPair anotherKeyPair = newEcKeyPair();

        assertThat(service.verify(file, signature, anotherKeyPair.getPublic())).isFalse();
    }

    @Test
    void rejectsTamperedDetachedSignature() throws Exception {
        Path file = tempDir.resolve("payload.txt");
        Files.writeString(file, "approved payload", StandardCharsets.UTF_8);
        DetachedSignature original = service.sign(file, signingKeyPair.getPrivate(), "demo-client-key");
        byte[] tamperedBytes = original.signatureBytes();
        tamperedBytes[tamperedBytes.length - 1] ^= 0x01;
        DetachedSignature tampered = DetachedSignature.fromBytes(
                original.algorithm(), original.keyId(), tamperedBytes);

        assertThat(service.verify(file, tampered, signingKeyPair.getPublic())).isFalse();
    }

    private KeyPair newEcKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("EC");
        generator.initialize(new ECGenParameterSpec("secp256r1"));
        return generator.generateKeyPair();
    }
}
