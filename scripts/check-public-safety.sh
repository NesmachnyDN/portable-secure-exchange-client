#!/usr/bin/env bash
set -Eeuo pipefail

fail=0
scan_files=$(find . -type f -not -path './.git/*' -not -path './target/*' -not -path './node_modules/*' -not -path './scripts/check-public-safety.sh' -print)

check_pattern() {
  local description="$1"
  local pattern="$2"
  if grep -nEI "$pattern" $scan_files >/tmp/public-safety-match.txt 2>/dev/null; then
    echo "Potential $description found:" >&2
    cat /tmp/public-safety-match.txt >&2
    fail=1
  fi
}

check_pattern 'private key material' '-----BEGIN (RSA |EC |OPENSSH |DSA )?PRIVATE KEY-----'
check_pattern 'cloud or GitHub token' '(gh[pousr]_[A-Za-z0-9_]{20,}|AKIA[0-9A-Z]{16})'
check_pattern 'RFC1918 IPv4 address' '(^|[^0-9])(10\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}|192\.168\.[0-9]{1,3}\.[0-9]{1,3}|172\.(1[6-9]|2[0-9]|3[01])\.[0-9]{1,3}\.[0-9]{1,3})([^0-9]|$)'
check_pattern 'internal/corporate hostname' '([A-Za-z0-9-]+\.)+(corp|internal|intranet)(\.|[:/]|$)'
check_pattern 'credentials embedded in URL' 'https?://[^/@[:space:]]+:[^/@[:space:]]+@'
check_pattern 'historical organization marker' '(VTB|ВТБ|arm[-_ ]?edo|svpc|gos\.armedo)'

exit "$fail"
