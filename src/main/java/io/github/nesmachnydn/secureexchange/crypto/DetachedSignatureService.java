package io.github.nesmachnydn.secureexchange.crypto;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

public final class DetachedSignatureService {
    private final DetachedSignatureProvider provider;

    public DetachedSignatureService(DetachedSignatureProvider provider) {
        this.provider = Objects.requireNonNull(provider, "provider");
    }

    public DetachedSignature sign(Path file, PrivateKey privateKey, String keyId)
            throws IOException, GeneralSecurityException {
        return DetachedSignature.fromBytes(provider.algorithm(), keyId, provider.sign(file, privateKey));
    }

    public boolean verify(Path file, DetachedSignature detachedSignature, PublicKey publicKey)
            throws IOException, GeneralSecurityException {
        if (!provider.algorithm().equals(detachedSignature.algorithm())) {
            return false;
        }
        return provider.verify(file, detachedSignature.signatureBytes(), publicKey);
    }
}
