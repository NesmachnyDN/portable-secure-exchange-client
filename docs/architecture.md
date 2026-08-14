# Architecture

## Context

The client is a local application used inside an external organization's infrastructure. It deliberately has no dependency on a bank or remote HTTP API. Its integration contract is the filesystem: `IN` and `OUT` folders are mounted or otherwise exposed inside the client's environment. A separate secure transport subsystem is responsible for moving files across organizational boundaries.

```text
User / local browser
        |
        v
+-------------------------------+
| Portable Secure Exchange      |
| Client                        |
|                               |
| Spring Boot + Vaadin          |
| Embedded persistence          |
| Integrity control             |
| Audit                         |
+-----------+-------------------+
            |
          IN / OUT
            |
            v
+-------------------------------+
| External secure transport     |
| responsibility                |
| VPN / transport certificates  |
+-------------------------------+
```

## Architectural boundaries

- **Presentation:** local web UI rendered in a standard browser to reduce operating-system-specific GUI dependencies.
- **Application:** upload, inbound processing, status and audit workflows.
- **Persistence:** embedded H2 database for local metadata and audit history.
- **Filesystem integration:** stable IN/OUT contract. Network transport is intentionally outside this repository.
- **Integrity:** SHA-256 digest registration and verification detects content changes after the file enters the controlled workflow.
- **Authenticity extension:** a detached-signature port isolates the workflow from concrete cryptographic providers and key stores.

## Integrity and signature model

The executable file-exchange flow uses digest-based integrity control. The digest is stored independently in the local database and also emitted as a sidecar for transport simulation. The database value lets the client detect a file replacement even if the file itself is modified later.

For stronger authenticity, the repository also defines `DetachedSignatureProvider` and a JCA reference adapter. The reference adapter signs file content with `SHA256withECDSA`; tests generate ephemeral EC key pairs and prove that changing the file, changing the signature or verifying with another public key fails. No production private key or certificate is bundled.

A deployment can supply a vendor- or OS-specific signature adapter and certificate lifecycle behind the same port without coupling those concerns to the file-exchange workflow.

## Transport simulator

`scripts/simulate-transport.*` demonstrates the filesystem contract only. It is not intended to simulate VPN, certificate management or a real secure transport product.
