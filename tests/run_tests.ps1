$ErrorActionPreference = "Stop"
$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
Set-Location $root

function Fail($msg) {
  Write-Host "FAIL: $msg" -ForegroundColor Red
  exit 1
}

if (-not (Test-Path ".\runix.exe")) {
  & .\build.bat
  if ($LASTEXITCODE -ne 0) { Fail "build" }
}

$ver = & .\runix.exe --version
if ($ver -notmatch "runix 0\.0\.1-alpha") { Fail "version: $ver" }
Write-Host "ok version: $ver"

$help = & .\runix.exe help | Out-String
if ($help -notmatch "doctor") { Fail "help missing doctor" }
Write-Host "ok help"

$name = "runix-test-" + [guid]::NewGuid().ToString("N").Substring(0, 8)
$tmp = Join-Path $root $name
& .\runix.exe init $name
if ($LASTEXITCODE -ne 0) { Fail "init" }
if (-not (Test-Path (Join-Path $tmp "src\app.cord"))) { Fail "init missing app.cord" }
if (-not (Test-Path (Join-Path $tmp "runix.json"))) { Fail "init missing runix.json" }
Write-Host "ok init"

$runix = Join-Path $root "runix.exe"
try {
  Push-Location $tmp
  $doctor = & $runix doctor 2>&1 | Out-String
  Write-Host $doctor
  if ($LASTEXITCODE -eq 0) {
    & $runix check
    if ($LASTEXITCODE -ne 0) { Fail "check" }
    Write-Host "ok check"
  } else {
    Write-Host "skip check (cordlang not found)"
  }
} finally {
  Pop-Location
  if (Test-Path $tmp) { Remove-Item -Recurse -Force $tmp -ErrorAction SilentlyContinue }
}

$landName = "runix-test-" + [guid]::NewGuid().ToString("N").Substring(0, 8)
& .\runix.exe init $landName --template landing
if ($LASTEXITCODE -ne 0) { Fail "init landing" }
if (-not (Test-Path (Join-Path (Join-Path $root $landName) "src\pages\HomePage.cord"))) { Fail "landing HomePage" }
Remove-Item -Recurse -Force (Join-Path $root $landName) -ErrorAction SilentlyContinue
Write-Host "ok init landing"

if (Get-Command node -ErrorAction SilentlyContinue) {
  & node "$root\tests\run_npm_cli.mjs"
  if ($LASTEXITCODE -ne 0) { Fail "npm cli" }
} else {
  Write-Host "skip npm cli (node not found)"
}

Write-Host "PASS" -ForegroundColor Green
