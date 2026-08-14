package io.github.nesmachnydn.secureexchange.crypto;

import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface DetachedSignatureProvider {
    String algorithm();

    byte[] sign(Path file, PrivateKey privateKey) throws IOException, GeneralSecurityException;

    boolean verify(Path file, byte[] detachedSignature, PublicKey publicKey)
            throws IOException, GeneralSecurityException;
}
