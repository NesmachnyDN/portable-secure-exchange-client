# ADR-004: Distribute self-contained platform application images

**Status:** Accepted

## Context

The target workstation should be able to run the client without installing or configuring a machine-wide Java runtime, database server or application server. At the same time, the Java application and local web UI should remain common across supported operating systems.

A Java runtime contains platform-native components, so one binary archive cannot be genuinely native and self-contained for every operating system.

## Decision

Build platform-specific application images with JDK `jpackage --type app-image`. Each image contains:

- a native platform launcher;
- the Spring Boot application JAR;
- a Java runtime generated as part of packaging;
- launcher JVM options, including browser auto-open.

Publish/archive the image as a portable archive rather than requiring an OS-wide installer. Validate each target operating system independently.

The repository currently proves this model on Windows x64 and Linux x64 runners. Packaging CI clears `JAVA_HOME`, starts the native launcher and checks the local HTTP UI before accepting the artifact.

## Consequences

Advantages:
- no machine-wide JRE/JDK is required for the tested distribution;
- no administrative installation procedure is required by the application-image model;
- the application code and UI remain shared across platforms;
- CI can validate the actual launcher/runtime bundle rather than only compiling Java source.

Trade-offs:
- distributions are platform-specific and larger than a plain application JAR;
- each additional/hardened operating system needs an explicit build and compatibility test;
- OS-specific signing, enterprise software-distribution controls and endpoint-security policies remain deployment concerns.
