# Security Model

## Security objectives

1. Detect modification or substitution of a file after it has been registered by the client.
2. Keep the application independent from the secure network transport implementation.
3. Bind the local UI to loopback only.
4. Avoid machine-wide installation and administrator privileges in the target deployment model.
5. Keep operational state, certificates and secrets outside source control.

## Implemented controls

- SHA-256 digest for every registered file;
- independent digest persistence in the embedded database;
- quarantine for inbound files with missing or mismatching integrity metadata;
- audit events for staging, successful verification and quarantine;
- filename normalization before writing to controlled directories;
- application listener bound to `127.0.0.1`;
- runtime data, keystores and secret-bearing files excluded by `.gitignore`;
- CI publication-safety scan for common credential and corporate-environment markers.

## Explicit non-goals of the first increment

- VPN implementation;
- secure transport certificate lifecycle;
- integration with a proprietary crypto provider;
- production-grade non-repudiation.

The next security increment should introduce a `DetachedSignatureProvider` port with a standard JCA demo adapter and no bundled private keys. A production environment can then supply a vendor/OS-specific adapter without changing the file-exchange workflow.
