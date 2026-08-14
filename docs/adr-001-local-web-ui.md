# ADR-001: Use a local web UI

**Status:** Accepted

## Context

The target client should behave consistently across Windows, mainstream Linux and potentially constrained/specialized operating systems. Requiring a separately installed desktop runtime or maintaining multiple native UI implementations would increase deployment and support cost.

## Decision

Run the application as a local Spring Boot process and render the user interface in the system browser with Vaadin. Bind the HTTP listener to loopback.

## Consequences

Advantages:
- one Java UI implementation;
- consistent rendering model across supported browsers;
- no remote web application dependency;
- application logic, persistence and data remain local.

Trade-offs:
- a supported browser is required;
- local HTTP lifecycle must be managed;
- operating-system packaging remains platform-specific even when the application code is portable.
