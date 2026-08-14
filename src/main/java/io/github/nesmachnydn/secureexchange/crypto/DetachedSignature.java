package io.github.nesmachnydn.secureexchange.crypto;

import java.util.Base64;
import java.util.Objects;

public record DetachedSignature(String algorithm, String keyId, String signatureBase64) {
    public DetachedSignature {
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(keyId, "keyId");
        Objects.requireNonNull(signatureBase64, "signatureBase64");
        if (algorithm.isBlank() || keyId.isBlank() || signatureBase64.isBlank()) {
            throw new IllegalArgumentException("Detached-signature fields must not be blank");
        }
    }

    public byte[] signatureBytes() {
        return Base64.getDecoder().decode(signatureBase64);
    }

    public static DetachedSignature fromBytes(String algorithm, String keyId, byte[] signature) {
        return new DetachedSignature(algorithm, keyId, Base64.getEncoder().encodeToString(signature));
    }
}
