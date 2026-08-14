package io.github.nesmachnydn.secureexchange.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Objects;

public final class JcaDetachedSignatureProvider implements DetachedSignatureProvider {
    public static final String DEFAULT_ALGORITHM = "SHA256withECDSA";

    private final String algorithm;

    public JcaDetachedSignatureProvider() {
        this(DEFAULT_ALGORITHM);
    }

    public JcaDetachedSignatureProvider(String algorithm) {
        this.algorithm = Objects.requireNonNull(algorithm, "algorithm");
    }

    @Override
    public String algorithm() {
        return algorithm;
    }

    @Override
    public byte[] sign(Path file, PrivateKey privateKey) throws IOException, GeneralSecurityException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initSign(privateKey);
        updateFromFile(signature, file);
        return signature.sign();
    }

    @Override
    public boolean verify(Path file, byte[] detachedSignature, PublicKey publicKey)
            throws IOException, GeneralSecurityException {
        Signature signature = Signature.getInstance(algorithm);
        signature.initVerify(publicKey);
        updateFromFile(signature, file);
        try {
            return signature.verify(detachedSignature);
        } catch (SignatureException malformedSignature) {
            return false;
        }
    }

    private void updateFromFile(Signature signature, Path file) throws IOException, SignatureException {
        try (InputStream input = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = input.read(buffer)) >= 0) {
                signature.update(buffer, 0, read);
            }
        }
    }
}
