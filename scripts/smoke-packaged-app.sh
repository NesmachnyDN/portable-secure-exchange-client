#!/usr/bin/env bash
set -Eeuo pipefail

APP="target/package/PortableSecureExchangeClient/bin/PortableSecureExchangeClient"
LOG="target/packaged-app-smoke.log"

if [[ ! -x "$APP" ]]; then
  echo "Packaged launcher not found: $APP" >&2
  exit 1
fi

JAVA_HOME='' "$APP" >"$LOG" 2>&1 &
pid=$!

cleanup() {
  kill "$pid" >/dev/null 2>&1 || true
  wait "$pid" >/dev/null 2>&1 || true
}
trap cleanup EXIT

for _ in $(seq 1 60); do
  if curl --fail --silent --show-error --output /dev/null http://127.0.0.1:8080/; then
    echo 'Packaged application responded on loopback.'
    exit 0
  fi
  if ! kill -0 "$pid" >/dev/null 2>&1; then
    echo 'Packaged application exited before becoming ready.' >&2
    cat "$LOG" >&2
    exit 1
  fi
  sleep 1
done

echo 'Timed out waiting for packaged application.' >&2
cat "$LOG" >&2
exit 1
