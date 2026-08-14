param(
    [string]$AppVersion = '0.1.0'
)

$ErrorActionPreference = 'Stop'
$AppName = 'PortableSecureExchangeClient'
$InputDir = Join-Path 'target' 'jpackage-input'
$OutputDir = Join-Path 'target' 'package'

if (-not (Get-Command jpackage -ErrorAction SilentlyContinue)) {
    throw 'jpackage was not found. Use a full JDK 21+.'
}

$jar = Get-ChildItem -Path 'target' -Filter 'portable-secure-exchange-client-*.jar' -File | Select-Object -First 1
if (-not $jar) {
    throw 'Application JAR not found. Run mvn package first.'
}

Remove-Item -Recurse -Force $InputDir -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force (Join-Path $OutputDir $AppName) -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $InputDir | Out-Null
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null
Copy-Item $jar.FullName (Join-Path $InputDir 'app.jar')

& jpackage `
    --type app-image `
    --name $AppName `
    --app-version $AppVersion `
    --input $InputDir `
    --main-jar app.jar `
    --main-class org.springframework.boot.loader.launch.JarLauncher `
    --dest $OutputDir `
    --java-options '-Dapp.browser.auto-open=true' `
    --java-options '-Dfile.encoding=UTF-8'

if ($LASTEXITCODE -ne 0) {
    throw "jpackage failed with exit code $LASTEXITCODE"
}

Write-Host "Created self-contained application image: $(Join-Path $OutputDir $AppName)"
