# Security

This is a portfolio/reference implementation, not a production secure-file-transfer product.

Do not commit real certificates, private keys, keystores, credentials or production files. Runtime data and key formats are ignored by Git. The repository CI also performs a lightweight publication-safety scan.

For a real deployment, add threat modelling, authenticated detached signatures, certificate lifecycle management, malware scanning, hardened filesystem permissions, secure logging, backup/recovery controls and environment-specific security assessment.
