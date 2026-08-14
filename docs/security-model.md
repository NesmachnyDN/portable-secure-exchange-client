# Security Model

## Security objectives

1. Detect modification or substitution of a file after it has been registered by the client.
2. Provide an extension point for proving file authenticity with detached digital signatures.
3. Keep the application independent from the secure network transport implementation.
4. Bind the local UI to loopback only.
5. Avoid machine-wide installation and administrator privileges in the target deployment model.
6. Keep operational state, certificates and secrets outside source control.

## Implemented controls

- SHA-256 digest for every registered file;
- independent digest persistence in the embedded database;
- quarantine for inbound files with missing or mismatching integrity metadata;
- audit events for staging, successful verification and quarantine;
- filename normalization before writing to controlled directories;
- vendor-neutral `DetachedSignatureProvider` port;
- standard JCA `SHA256withECDSA` reference adapter that streams file content;
- tests proving that file modification, a wrong public key and detached-signature tampering invalidate verification;
- application listener bound to `127.0.0.1`;
- runtime data, keystores and secret-bearing files excluded by `.gitignore`;
- CI publication-safety scan for common credential and corporate-environment markers.

## Key-management boundary

The repository contains no persistent private key, certificate or keystore. Cryptographic tests generate short-lived EC key pairs at runtime. Production certificate discovery, trust-chain validation, revocation checking and private-key access are deployment responsibilities and can be supplied behind the signature port without changing the file-exchange workflow.

The default running application still uses independently persisted SHA-256 metadata for its executable IN/OUT workflow. The JCA signature adapter is a reference extension point, not a claim that production certificate lifecycle management is already implemented.

## Explicit non-goals

- VPN implementation;
- secure transport certificate lifecycle;
- integration with a proprietary crypto provider;
- bundled production signing keys;
- production-grade non-repudiation without a defined trust and certificate lifecycle.
