# ADR-003: Isolate digital signatures behind a detached-signature port

**Status:** Accepted

## Context

Digest comparison can detect accidental or deliberate content changes when the trusted digest is stored independently, but a digest alone does not prove who approved the file. Enterprise deployments may also depend on an operating-system or vendor-specific cryptographic provider and externally issued certificates.

## Decision

Keep the file-exchange workflow independent from a concrete cryptographic vendor. Define a `DetachedSignatureProvider` port and provide a standard Java Cryptography Architecture (JCA) reference adapter. The reference implementation uses `SHA256withECDSA` and streams file content directly into the signature operation.

No private key, certificate or keystore is stored in this repository. Tests generate short-lived EC key pairs at runtime only.

## Consequences

Advantages:
- modification of a signed file invalidates the detached signature;
- signatures can be verified with a separately trusted public key;
- vendor/OS-specific providers can be adapted without changing the application workflow;
- public tests demonstrate the security property without shipping credentials.

Trade-offs:
- production certificate discovery, trust validation, revocation and private-key access remain deployment concerns;
- a real deployment must define the signed envelope format and certificate/key lifecycle;
- this reference adapter does not claim production non-repudiation by itself.
