# Portable Secure Exchange Client

A zero-install-oriented, cross-platform client for controlled file exchange using a **local web UI**, **embedded persistence**, **file-integrity verification**, and a deliberately simple **IN/OUT filesystem integration contract**.

The project is an independent portfolio implementation of an enterprise architecture problem: an external organization needs a user-facing client, but the client application should not depend on direct access to remote corporate web resources. A separate secure transport subsystem owns inter-organization delivery, VPN connectivity and transport certificates.

## Why this architecture

The central constraint is deployment simplicity: no database server, no application server and no machine-wide Java configuration should be required on the target workstation. The application is designed to be packaged with its own Java runtime in a later packaging increment.

The UI is intentionally web-based but **local**. Spring Boot runs on loopback and Vaadin renders the same application UI in a standard browser across supported operating systems.

```text
+---------------- External organization ----------------+
|                                                       |
|  Browser --> localhost:8080                           |
|                 |                                     |
|                 v                                     |
|      Portable Secure Exchange Client                  |
|      - Spring Boot                                    |
|      - Vaadin local web UI                            |
|      - embedded H2                                    |
|      - SHA-256 integrity registry                     |
|      - audit                                          |
|                 |                                     |
|              IN / OUT                                 |
|                 |                                     |
|                 v                                     |
|      External secure transport (out of scope)         |
+-----------------|-------------------------------------+
                  | VPN / transport certificates
                  v
             remote environment
```

## Current capabilities

- stage a file into the controlled `OUT` folder from the local UI;
- compute and persist SHA-256 before transport;
- retain an outbound archive copy;
- process files arriving in `IN`;
- verify incoming checksum metadata;
- quarantine missing/mismatching integrity data;
- persist transfer status and audit events in embedded H2;
- simulate the external folder transport without pretending to implement VPN/security transport;
- bind the application UI to `127.0.0.1` only.

## Integrity versus transport security

These are separate concerns.

**Application integrity control** detects modification/substitution of file content inside the workflow. The first public increment uses SHA-256 with an independently persisted digest. A later increment will add a detached-signature provider abstraction.

**Transport security** (VPN, transport certificates and the actual cross-organization delivery product) is an external responsibility and intentionally not implemented here.

## Technology baseline

- Java 21
- Spring Boot 4.1
- Vaadin 25.2
- Spring Data JPA
- embedded H2
- Maven

Vaadin 25 is the current recommended line for new projects and requires Java 21+ and Spring Boot 4.1+. The project intentionally follows that current baseline rather than reproducing the historical prototype stack.

## Run from source

Requirements for development:

- Java 21+
- Maven 3.8+
- Node.js 24+ for the production frontend build

```bash
mvn spring-boot:run
```

Then open:

```text
http://127.0.0.1:8080
```

Runtime state is created under:

```text
data/
exchange/
  client/
    in/
    out/
    archive/
    quarantine/
  remote/
    in/
    out/
```

## Simulate the external transport

Linux/macOS:

```bash
./scripts/simulate-transport.sh send
./scripts/simulate-transport.sh receive
```

Windows PowerShell:

```powershell
./scripts/simulate-transport.ps1 send
./scripts/simulate-transport.ps1 receive
```

The simulator copies files only. It intentionally does **not** model VPN or transport cryptography.

## Architecture decisions

- [Architecture overview](docs/architecture.md)
- [Security model](docs/security-model.md)
- [ADR-001: Local web UI](docs/adr-001-local-web-ui.md)
- [ADR-002: Filesystem IN/OUT integration](docs/adr-002-filesystem-integration.md)

## Roadmap

1. Current: filesystem workflow, integrity registry, audit and local UI.
2. Detached digital-signature port with a standard JCA demo implementation.
3. Self-contained `jlink`/`jpackage` distributions for Windows and Linux without machine-wide Java installation.
4. More explicit duplicate/replay handling and recovery semantics.
5. Browserless Vaadin UI tests and tamper-oriented integration tests.

## Development approach

**Role:** architecture, requirements, deployment model, integration approach, domain workflow and engineering review.

**Development approach:** AI-assisted implementation with human architecture ownership and code review.

See [ORIGIN.md](ORIGIN.md) for the publication boundary.
