#!/usr/bin/env bash
set -Eeuo pipefail

ROOT="${EXCHANGE_ROOT:-./exchange}"
ACTION="${1:-roundtrip}"
mkdir -p "$ROOT/client/in" "$ROOT/client/out" "$ROOT/remote/in" "$ROOT/remote/out"

copy_payloads() {
  local from="$1"
  local to="$2"
  find "$from" -maxdepth 1 -type f -print0 | while IFS= read -r -d '' file; do
    cp -f "$file" "$to/"
  done
}

case "$ACTION" in
  send)
    copy_payloads "$ROOT/client/out" "$ROOT/remote/in"
    ;;
  receive)
    copy_payloads "$ROOT/remote/out" "$ROOT/client/in"
    ;;
  roundtrip)
    copy_payloads "$ROOT/client/out" "$ROOT/remote/in"
    copy_payloads "$ROOT/remote/out" "$ROOT/client/in"
    ;;
  *)
    echo "Usage: $0 [send|receive|roundtrip]" >&2
    exit 2
    ;;
esac
