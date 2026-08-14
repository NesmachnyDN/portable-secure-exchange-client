#!/usr/bin/env bash
set -Eeuo pipefail

APP_NAME="PortableSecureExchangeClient"
APP_VERSION="${APP_VERSION:-0.1.0}"
INPUT_DIR="target/jpackage-input"
OUTPUT_DIR="target/package"

command -v jpackage >/dev/null 2>&1 || {
  echo 'jpackage was not found. Use a full JDK 21+.' >&2
  exit 1
}

jar_path="$(find target -maxdepth 1 -type f -name 'portable-secure-exchange-client-*.jar' | head -n 1)"
if [[ -z "$jar_path" ]]; then
  echo 'Application JAR not found. Run mvn package first.' >&2
  exit 1
fi

rm -rf "$INPUT_DIR" "$OUTPUT_DIR/$APP_NAME"
mkdir -p "$INPUT_DIR" "$OUTPUT_DIR"
cp "$jar_path" "$INPUT_DIR/app.jar"

jpackage \
  --type app-image \
  --name "$APP_NAME" \
  --app-version "$APP_VERSION" \
  --input "$INPUT_DIR" \
  --main-jar app.jar \
  --main-class org.springframework.boot.loader.launch.JarLauncher \
  --dest "$OUTPUT_DIR" \
  --java-options '-Dapp.browser.auto-open=true' \
  --java-options '-Dfile.encoding=UTF-8'

echo "Created self-contained application image: $OUTPUT_DIR/$APP_NAME"
