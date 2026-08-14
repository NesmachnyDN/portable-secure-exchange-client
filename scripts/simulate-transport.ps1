param(
    [ValidateSet('send', 'receive', 'roundtrip')]
    [string]$Action = 'roundtrip',
    [string]$ExchangeRoot = './exchange'
)

$ErrorActionPreference = 'Stop'
$clientIn = Join-Path $ExchangeRoot 'client/in'
$clientOut = Join-Path $ExchangeRoot 'client/out'
$remoteIn = Join-Path $ExchangeRoot 'remote/in'
$remoteOut = Join-Path $ExchangeRoot 'remote/out'
@($clientIn, $clientOut, $remoteIn, $remoteOut) | ForEach-Object { New-Item -ItemType Directory -Force -Path $_ | Out-Null }

function Copy-Payloads([string]$From, [string]$To) {
    Get-ChildItem -Path $From -File | Copy-Item -Destination $To -Force
}

if ($Action -in @('send', 'roundtrip')) { Copy-Payloads $clientOut $remoteIn }
if ($Action -in @('receive', 'roundtrip')) { Copy-Payloads $remoteOut $clientIn }
