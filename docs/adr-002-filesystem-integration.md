# ADR-002: Decouple the client from secure transport through IN/OUT folders

**Status:** Accepted

## Context

The client should not own VPN connectivity, remote routing or the transport certificate lifecycle. An existing secure transport subsystem can move files between organizations.

## Decision

Use controlled `IN` and `OUT` folders as the application integration contract. The transport subsystem reads outbound files and delivers inbound files independently of the client process.

## Consequences

Advantages:
- low coupling between business client and transport technology;
- transport can evolve without changing the client workflow;
- clear operational ownership boundary;
- works in restricted environments where direct application network access is undesirable.

Trade-offs:
- filesystem consistency and duplicate handling require explicit rules;
- integrity and authenticity must be validated independently of transport delivery;
- operational monitoring must correlate application and transport events.
