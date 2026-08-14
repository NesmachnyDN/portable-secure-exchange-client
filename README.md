# Portable Secure Exchange Client

<p align="center">
  <img src="docs/images/portable-secure-exchange-client-social-preview.jpg" alt="Portable Secure Exchange Client portfolio cover" width="1000">
</p>

A self-contained, cross-platform client for controlled file exchange using a **local web UI**, **embedded persistence**, **file-integrity verification**, and a deliberately simple **IN/OUT filesystem integration contract**.

The project is an independent portfolio implementation of an enterprise architecture problem: an external organization needs a user-facing client, but the client application should not depend on direct access to remote corporate web resources. A separate secure transport subsystem owns inter-organization delivery, VPN connectivity and transport certificates.

## Why this architecture

The central deployment constraint is simple: the target workstation should not require a machine-wide Java installation, database server, application server or administrator-level installation procedure. The project therefore builds platform-specific self-contained application images with a bundled Java runtime.

The UI is intentionally web-based but **local**. Spring Boot runs on loopback and Vaadin renders the same application UI in a standard browser across supported operating systems.

```text
+---------------- External organization ----------------+
|                                                       |
|  Browser --> localhost:8080                           |
|                 |                                     |
|                 v                                     |
|      Portable Secure Exchange Client                  |
|      - bundled Java runtime                           |
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
- provide a vendor-neutral detached-signature port and standard JCA reference adapter;
- prove by tests that modified content, the wrong public key and a tampered detached signature are rejected;
- simulate the external folder transport without pretending to implement VPN/security transport;
- bind the application UI to `127.0.0.1` only;
- build and smoke-test self-contained Windows x64 and Linux x64 application images with their own Java runtime.

## Integrity, authenticity and transport security

These are separate concerns.

**Application integrity control** detects modification/substitution of file content inside the workflow. The default file-exchange flow registers SHA-256 independently from the file. A separate `DetachedSignatureProvider` extension point demonstrates cryptographic authenticity and tamper detection with a standard JCA implementation. Production certificate/private-key provisioning is intentionally not wired into the default runtime and no private key is stored in this repository.

**Transport security** (VPN, transport certificates and the actual cross-organization delivery product) is an external responsibility and intentionally not implemented here.

## Self-contained distribution

The repository uses JDK `jpackage` application images rather than requiring Java to be installed on the end-user workstation. CI currently validates two distributions:

```text
portable-secure-exchange-client-windows-x64.zip
portable-secure-exchange-client-linux-x64.tar.gz
```

For each platform, CI builds the production application, creates the platform-native app image, clears `JAVA_HOME`, starts the generated native launcher, waits for the loopback UI to respond, and only then archives the distribution as a workflow artifact.

This is an **extract-and-run** distribution model, not an OS-wide installer. Specialized/hardened operating systems remain explicit compatibility targets to validate rather than platforms this repository claims to support without testing. See [Self-contained packaging](docs/packaging.md).

## Technology baseline

- Java 21
- Spring Boot 4.1
- Vaadin 25.2
- Spring Data JPA
- embedded H2
- Maven
- JDK `jpackage`

The project intentionally follows a current Java/Spring/Vaadin baseline rather than reproducing the historical prototype stack.

## Run from source

Development requirements:

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

## Build a self-contained app image

Linux:

```bash
mvn -DskipTests package
./scripts/package-app.sh
./target/package/PortableSecureExchangeClient/bin/PortableSecureExchangeClient
```

Windows PowerShell:

```powershell
mvn -DskipTests package
./scripts/package-app.ps1
.\target\package\PortableSecureExchangeClient\PortableSecureExchangeClient.exe
```

The generated native launcher uses the Java runtime embedded in the application image and opens the local UI in the system browser when desktop integration is available.

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
- [Self-contained packaging](docs/packaging.md)
- [ADR-001: Local web UI](docs/adr-001-local-web-ui.md)
- [ADR-002: Filesystem IN/OUT integration](docs/adr-002-filesystem-integration.md)
- [ADR-003: Detached-signature port](docs/adr-003-detached-signature-port.md)
- [ADR-004: Self-contained platform app images](docs/adr-004-self-contained-app-images.md)

## Roadmap

1. Current: filesystem workflow, integrity registry, audit and local UI.
2. Current: vendor-neutral detached-signature port with a standard JCA reference adapter and tamper tests.
3. Current: self-contained Windows and Linux app images with bundled Java runtimes and native-launcher CI smoke tests.
4. Next: more explicit duplicate/replay handling and recovery semantics.
5. Browserless Vaadin UI tests and end-to-end folder tamper tests.
6. Validate additional target operating systems only where an actual compatibility requirement exists.

## Development approach

**Role:** architecture, requirements, deployment model, integration approach, domain workflow and engineering review.

**Development approach:** AI-assisted implementation with human architecture ownership and code review.

See [ORIGIN.md](ORIGIN.md) for the publication boundary.
